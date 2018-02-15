/*
 * Copyright 2012-2014 Sergey Ignatov
 * Copyright 2017 Jake Becker
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
import org.elixir_lang.debugger.XValuePresentation;
import org.jetbrains.annotations.NotNull;

public final class XValueFactory {
  private XValueFactory() {
  }

  @NotNull
  public static XValue create(OtpErlangObject object) {
    if (object instanceof OtpErlangLong || object instanceof OtpErlangDouble) {
      return new NumericXValue(object);
    }
    if (object instanceof OtpErlangAtom) {
      return new AtomXValue((OtpErlangAtom) object);
    }
    if (object instanceof OtpErlangPid) {
      return new PidXValue((OtpErlangPid) object);
    }
    if (object instanceof OtpErlangPort) {
      return new PortXValue((OtpErlangPort) object);
    }
    if (object instanceof OtpErlangRef) {
      return new RefXValue((OtpErlangRef) object);
    }
    if (object instanceof OtpErlangTuple) {
      return new TupleXValue((OtpErlangTuple) object);
    }
    if (object instanceof OtpErlangString) {
      if (XValuePresentation.isPrintable((OtpErlangString)object)) {
        return new CharListXValue((OtpErlangString) object);
      } else {
        return new ListXValue(new OtpErlangList(((OtpErlangString) object).stringValue()));
      }
    }
    if (object instanceof OtpErlangList) {
      return new ListXValue((OtpErlangList) object);
    }
    if (object instanceof OtpErlangBitstr) {
      if (XValuePresentation.toUtf8String((OtpErlangBitstr)object) != null) {
        return new StringXValue((OtpErlangBitstr) object);
      } else {
        return new BitStringXValue((OtpErlangBitstr) object);
      }
    }
    if (object instanceof OtpErlangMap) {
      return new MapXValue((OtpErlangMap) object);
    }
    return new PrimitiveXValueBase<>(object);
  }
}
