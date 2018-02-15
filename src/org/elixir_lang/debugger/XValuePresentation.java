/*
 * Copyright 2012-2014 Sergey Ignatov
 * Copyright 2017 Jake Becker
 * Copyright 2017 Luke Imhoff
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.elixir_lang.debugger;

import com.ericsson.otp.erlang.*;
import org.elixir_lang.utils.ElixirModulesUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.util.Map;

public class XValuePresentation extends com.intellij.xdebugger.frame.presentation.XValuePresentation {
  private final OtpErlangObject myValue;

  public XValuePresentation(OtpErlangObject value) {
    myValue = value;
  }

  private static void renderObject(OtpErlangObject o, XValueTextRenderer renderer) {
    if (o instanceof OtpErlangMap) {
      renderMap((OtpErlangMap) o, renderer);
    } else if (o instanceof OtpErlangAtom) {
      renderAtom((OtpErlangAtom) o, renderer);
    } else if (o instanceof OtpErlangTuple) {
      renderTuple((OtpErlangTuple) o, renderer);
    } else if (o instanceof OtpErlangList) {
      renderList((OtpErlangList) o, renderer);
    } else if (o instanceof OtpErlangBitstr) {
      renderBitstr((OtpErlangBitstr) o, renderer);
    } else if (o instanceof OtpErlangString) {
      renderErlangString((OtpErlangString) o, renderer);
    } else if (o instanceof OtpErlangDouble || o instanceof OtpErlangLong) {
      renderer.renderNumericValue(o.toString());
    } else {
      renderer.renderValue(o.toString());
    }
  }

  private static void renderMap(OtpErlangMap map, XValueTextRenderer renderer) {
    renderer.renderSpecialSymbol("%");
    if (isStruct(map)) {
      String structType = structType(map);

      assert structType != null;

      renderer.renderKeywordValue(structType);
    }
    renderer.renderSpecialSymbol("{");

    boolean first = true;
    boolean symbolKeys = hasSymbolKeys(map);

    for (final Map.Entry<OtpErlangObject, OtpErlangObject> e : map.entrySet()) {
      OtpErlangObject key = e.getKey();
      if (!(isStruct(map) && key instanceof OtpErlangAtom && ((OtpErlangAtom) key).atomValue().equals("__struct__"))) {
        if (first) {
          first = false;
        } else {
          renderer.renderSpecialSymbol(", ");
        }

        if (symbolKeys) {
          assert key instanceof OtpErlangAtom;

          renderer.renderKeywordValue(((OtpErlangAtom) key).atomValue());
          renderer.renderKeywordValue(": ");
        } else {
          renderObject(key, renderer);
          renderer.renderSpecialSymbol(" => ");
        }

        renderObject(e.getValue(), renderer);
      }
    }
    renderer.renderSpecialSymbol("}");
  }

  private static void renderAtom(OtpErlangAtom atom, XValueTextRenderer renderer) {
    renderer.renderKeywordValue(ElixirModulesUtil.INSTANCE.erlangModuleNameToElixir(atom.atomValue()));
  }

  private static void renderTuple(OtpErlangTuple tuple, XValueTextRenderer renderer) {
    int i;

    renderer.renderSpecialSymbol("{");
    for (i = 0; i < tuple.arity(); i++) {
      if (i > 0) {
        renderer.renderSpecialSymbol(", ");
      }
      renderObject(tuple.elementAt(i), renderer);
    }

    renderer.renderSpecialSymbol("}");
  }

  private static void renderList(OtpErlangList list, XValueTextRenderer renderer) {
    int i;

    renderer.renderSpecialSymbol("[");
    for (i = 0; i < list.arity(); i++) {
      if (i > 0) {
        renderer.renderSpecialSymbol(", ");
      }
      renderObject(list.elementAt(i), renderer);
    }

    // Improper lists have a lastTail
    OtpErlangObject lastTail = list.getLastTail();

    if (lastTail != null) {
      // Improper lists need to render the head tail joiner, `|`, explicitly
      renderer.renderSpecialSymbol(" | ");
      renderObject(lastTail, renderer);
    }

    renderer.renderSpecialSymbol("]");
  }

  private static void renderBitstr(OtpErlangBitstr bitstr, XValueTextRenderer renderer) {
    String utf8String = toUtf8String(bitstr);
    if (utf8String != null) {
      renderer.renderStringValue(utf8String);
    } else {
      renderer.renderSpecialSymbol("<<");
      boolean first = true;
      for (byte b : bitstr.binaryValue()) {
        if (!first) renderer.renderSpecialSymbol(", ");
        renderer.renderValue("" + ((int) b & 0xFF));
        first = false;
      }

      if (bitstr.pad_bits() > 0) {
        renderer.renderSpecialSymbol("::size(" + (8 - bitstr.pad_bits()) + ")");
      }

      renderer.renderSpecialSymbol(">>");
    }
  }

  public static boolean isPrintable(OtpErlangString s) {
    String str = s.toString();
    for (int i = 0; i < str.length(); i++) {
      char c = str.charAt(i);
      if (!isPrintable(c)) return false;
    }
    return true;
  }

  private static boolean isPrintable(char c) {
    return (32 <= c && c <= 126)
      || c == '\n' || c == '\r' || c == '\t' || c == 11 /* vertical tab */ || c == '\b' || c == '\f'
      || c == 27 /* esc */ || c == 7; /* bell */
  }

  private static void renderErlangString(OtpErlangString str, XValueTextRenderer renderer) {
    if (isPrintable(str)) {
      renderer.renderSpecialSymbol("'");
      renderer.renderValue(str.stringValue());
      renderer.renderSpecialSymbol("'");
    } else {
      renderObject(new OtpErlangList(str.stringValue()), renderer);
    }
  }

  @Nullable
  public static String toUtf8String(OtpErlangBitstr bitstr) {
    if (bitstr.pad_bits() > 0) return null;
    try {
      return Charset.availableCharsets().get("UTF-8").newDecoder().decode(ByteBuffer.wrap(bitstr.binaryValue())).toString();
    } catch (CharacterCodingException e) {
      return null;
    }
  }

  @Nullable
  private static String structType(OtpErlangMap map) {
    OtpErlangObject structValue = map.get(new OtpErlangAtom("__struct__"));
    if (structValue instanceof OtpErlangAtom) {
      return ElixirModulesUtil.INSTANCE.erlangModuleNameToElixir(((OtpErlangAtom) structValue).atomValue());
    } else {
      return null;
    }
  }

  private static boolean isStruct(OtpErlangMap map) {
    return structType(map) != null;
  }

  public static boolean hasSymbolKeys(OtpErlangMap map) {
    for (OtpErlangObject key : map.keys()) {
      if (!(key instanceof OtpErlangAtom) || ((OtpErlangAtom) key).atomValue().startsWith("Elixir.")) return false;
    }
    return true;
  }

  @Override
  public void renderValue(@NotNull XValueTextRenderer renderer) {
    XValuePresentation.renderObject(myValue, renderer);
  }
}
