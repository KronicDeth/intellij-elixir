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

public final class Factory {
  private Factory() {
  }

  @NotNull
  public static XValue create(OtpErlangObject object) {
    if (object instanceof OtpErlangLong || object instanceof OtpErlangDouble) {
      return new Numeric(object);
    }
    if (object instanceof OtpErlangAtom) {
      return new Atom((OtpErlangAtom) object);
    }
    if (object instanceof OtpErlangPid) {
      return new Pid((OtpErlangPid) object);
    }
    if (object instanceof OtpErlangPort) {
      return new Port((OtpErlangPort) object);
    }
    if (object instanceof OtpErlangRef) {
      return new Ref((OtpErlangRef) object);
    }
    if (object instanceof OtpErlangTuple) {
      return new Tuple((OtpErlangTuple) object);
    }
    if (object instanceof OtpErlangString) {
      if (XValuePresentation.isPrintable((OtpErlangString)object)) {
        return new CharList((OtpErlangString) object);
      } else {
        return new List(new OtpErlangList(((OtpErlangString) object).stringValue()));
      }
    }
    if (object instanceof OtpErlangList) {
      return new List((OtpErlangList) object);
    }
    if (object instanceof OtpErlangBitstr) {
      if (XValuePresentation.toUtf8String((OtpErlangBitstr)object) != null) {
        return new String((OtpErlangBitstr) object);
      } else {
        return new BitString((OtpErlangBitstr) object);
      }
    }
    if (object instanceof OtpErlangMap) {
      return new Map((OtpErlangMap) object);
    }
    return new PrimitiveBase<>(object);
  }
}
