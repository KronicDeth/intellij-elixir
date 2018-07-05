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

package org.elixir_lang.debugger.stack_frame.value

import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import com.intellij.xdebugger.frame.XCompositeNode
import com.intellij.xdebugger.frame.XValueChildrenList

internal class Mapping(private val key: OtpErlangObject, private val value: OtpErlangObject) :
        Presentable<OtpErlangTuple>(OtpErlangTuple(arrayOf(key, value))) {
    override val hasChildren: Boolean = true

    override fun computeChildren(node: XCompositeNode) {
        val children = XValueChildrenList(2)
        children.add("key", key)
        children.add("value", value)
        node.addChildren(children, true)
    }
}
