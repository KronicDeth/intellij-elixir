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

import com.ericsson.otp.erlang.OtpErlangRef
import com.intellij.xdebugger.frame.XCompositeNode
import com.intellij.xdebugger.frame.XValueChildrenList

class Ref(term: OtpErlangRef) : Presentable<OtpErlangRef>(term) {
    override val hasChildren = true

    override fun computeChildren(node: XCompositeNode) {
        val ids = term.ids()
        val children = XValueChildrenList(1 + ids.size)
        children.add("node", term.node())

        for (i in ids.indices) {
            children.add("id$i", ids[i])
        }

        node.addChildren(children, true)
    }
}
