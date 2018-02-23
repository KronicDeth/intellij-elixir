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

package org.elixir_lang.debugger.stack_frame.value

import com.ericsson.otp.erlang.*
import com.intellij.xdebugger.frame.XValue
import org.elixir_lang.debugger.stack_frame.value.list.Improper
import org.elixir_lang.debugger.stack_frame.value.list.Proper

object Factory {
    @JvmStatic
    fun create(term: OtpErlangObject): XValue =
            when (term) {
                is OtpErlangLong, is OtpErlangDouble -> Numeric(term)
                is OtpErlangAtom -> Atom(term)
                is OtpErlangPid -> Pid(term)
                is OtpErlangPort -> Port(term)
                is OtpErlangRef -> Ref(term)
                is OtpErlangTuple -> Tuple(term)
                is OtpErlangString ->
                    if (Presentation.isPrintable(term)) {
                        CharList(term)
                    } else {
                        Proper(OtpErlangList(term.stringValue()))
                    }
                is OtpErlangList ->
                    if (term.isProper) {
                        Proper(term)
                    } else {
                        Improper(term)
                    }

                is OtpErlangBitstr ->
                    if (Presentation.toUtf8String(term) != null) {
                        String(term)
                    } else {
                        BitString(term)
                    }
                else ->
                    if (term is OtpErlangMap) {
                        Map(term)
                    } else {
                        PrimitiveBase(term)
                    }
            }
}
