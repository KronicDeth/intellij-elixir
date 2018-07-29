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

import com.ericsson.otp.erlang.OtpErlangLong
import com.ericsson.otp.erlang.OtpErlangObject
import com.intellij.xdebugger.frame.XCompositeNode
import com.intellij.xdebugger.frame.XValueChildrenList

/**
 * A [Presentable] that can compute some of its children lazily if the [childCount] exceeds [XCompositeNode.MAX_CHILDREN_TO_SHOW].
 */
abstract class LazyParent<out T : OtpErlangObject>(term: T, override val childCount: Int = 0) :
        LazyContainer,  Presentable<T>(term) {
    final override val hasChildren: Boolean by lazy { childCount > 0 }

    override fun computeChildren(node: XCompositeNode) {
        computeChildren(this, node)
    }

    override var nextChildIndexToCompute: Int = 0
}

fun XValueChildrenList.add(name: kotlin.String, numericChild: Long) {
    add(name, OtpErlangLong(numericChild))
}
