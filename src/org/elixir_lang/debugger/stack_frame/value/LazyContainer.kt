package org.elixir_lang.debugger.stack_frame.value

import com.intellij.xdebugger.frame.XCompositeNode
import com.intellij.xdebugger.frame.XValueChildrenList

interface LazyContainer {
    val childCount: Int
    var nextChildIndexToCompute: Int
    fun computeChild(children: XValueChildrenList, index: Int)
}

fun computeChildren(lazyContainer: LazyContainer, node: XCompositeNode) {
    val nextNextChildIndexToCompute = Math.min(lazyContainer.nextChildIndexToCompute + XCompositeNode.MAX_CHILDREN_TO_SHOW, lazyContainer.childCount)
    val children = XValueChildrenList(nextNextChildIndexToCompute - lazyContainer.nextChildIndexToCompute)

    for (i in lazyContainer.nextChildIndexToCompute until nextNextChildIndexToCompute) {
        lazyContainer.computeChild(children, i)
    }

    lazyContainer.nextChildIndexToCompute = nextNextChildIndexToCompute

    val computedAllChildren = lazyContainer.nextChildIndexToCompute == lazyContainer.childCount

    node.addChildren(children, computedAllChildren)

    if (!computedAllChildren) {
        node.tooManyChildren(lazyContainer.childCount - lazyContainer.nextChildIndexToCompute)
    }
}
