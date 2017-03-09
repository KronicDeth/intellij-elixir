/*
 * Copyright 2012-2014 Sergey Ignatov
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

package org.elixir_lang.debugger.xdebug.xvalue;

import com.ericsson.otp.erlang.*;
import com.intellij.xdebugger.frame.XValue;
import org.elixir_lang.debugger.ElixirXValuePresentation;
import org.jetbrains.annotations.NotNull;

public final class ElixirXValueFactory {
  private ElixirXValueFactory() {
  }

  @NotNull
  public static XValue create(OtpErlangObject object) {
    if (object instanceof OtpErlangLong || object instanceof OtpErlangDouble) {
      return new ElixirNumericXValue(object);
    }
    if (object instanceof OtpErlangAtom) {
      return new ElixirAtomXValue((OtpErlangAtom) object);
    }
    if (object instanceof OtpErlangPid) {
      return new ElixirPidXValue((OtpErlangPid) object);
    }
    if (object instanceof OtpErlangPort) {
      return new ElixirPortXValue((OtpErlangPort) object);
    }
    if (object instanceof OtpErlangRef) {
      return new ElixirRefXValue((OtpErlangRef) object);
    }
    if (object instanceof OtpErlangTuple) {
      return new ElixirTupleXValue((OtpErlangTuple) object);
    }
    if (object instanceof OtpErlangString) {
      if (ElixirXValuePresentation.isPrintable((OtpErlangString)object)) {
        return new ElixirCharListXValue((OtpErlangString) object);
      } else {
        return new ElixirListXValue(new OtpErlangList(((OtpErlangString) object).stringValue()));
      }
    }
    if (object instanceof OtpErlangList) {
      return new ElixirListXValue((OtpErlangList) object);
    }
    if (object instanceof OtpErlangBitstr) {
      if (ElixirXValuePresentation.toUtf8String((OtpErlangBitstr)object) != null) {
        return new ElixirStringXValue((OtpErlangBitstr) object);
      } else {
        return new ElixirBitStringXValue((OtpErlangBitstr) object);
      }
    }
    if (object instanceof OtpErlangMap) {
      return new ElixirMapXValue((OtpErlangMap) object);
    }
    return new ElixirPrimitiveXValueBase<>(object);
  }
}
