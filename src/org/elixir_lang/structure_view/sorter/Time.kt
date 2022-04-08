package org.elixir_lang.structure_view.sorter

import com.intellij.ide.util.treeView.smartTree.ActionPresentation
import com.intellij.ide.util.treeView.smartTree.ActionPresentationData
import com.intellij.icons.AllIcons
import com.intellij.ide.util.treeView.smartTree.Sorter
import org.apache.commons.lang.NotImplementedException
import org.elixir_lang.Timed
import org.jetbrains.annotations.NonNls
import java.util.Comparator

/**
 * Sorts element by their `#time()`, which indicates whether the element works at
 */
class Time : Sorter {
    /*
     * Constructors
     */
    /**
     * Returns the comparator used for comparing nodes in the tree.
     *
     * @return the comparator for comparing nodes.
     */
    override fun getComparator(): Comparator<*> {
        return Comparator<Any?> { o1, o2 ->
            val comparison: Int = if (o1 is Timed && o2 is Timed) {
                val time1 = o1.time
                val time2 = o2.time
                if (time1 === time2) {
                    0
                } else if (time1 === org.elixir_lang.semantic.call.definition.clause.Time.COMPILE && time2 === org.elixir_lang.semantic.call.definition.clause.Time.RUN) {
                    -1
                } else if (time1 === org.elixir_lang.semantic.call.definition.clause.Time.RUN && time2 === org.elixir_lang.semantic.call.definition.clause.Time.COMPILE) {
                    1
                } else {
                    throw NotImplementedException("Only COMPILE and RUN time are expected")
                }
            } else if (o1 is Timed && o2 !is Timed) {
                when (o1.time) {
                    org.elixir_lang.semantic.call.definition.clause.Time.COMPILE -> -1
                    org.elixir_lang.semantic.call.definition.clause.Time.RUN -> 1
                    else -> throw NotImplementedException("Only COMPILE and RUN time are expected")
                }
            } else if (o1 !is Timed && o2 is Timed) {
                when (o2.time) {
                    org.elixir_lang.semantic.call.definition.clause.Time.COMPILE -> 1
                    org.elixir_lang.semantic.call.definition.clause.Time.RUN -> -1
                    else -> throw NotImplementedException("Only COMPILE and RUN time are expected")
                }
            } else {
                assert(o1 !is Timed && o2 !is Timed)
                0
            }
            comparison
        }
    }

    /**
     * Returns a unique identifier for the action.
     *
     * @return the action identifier.
     */
    override fun getName(): String = TIME_SORTER_ID!!

    /**
     * Returns the presentation for the action.
     *
     * @return the action presentation.
     * @see ActionPresentationData.ActionPresentationData
     */
    override fun getPresentation(): ActionPresentation = ActionPresentationData(
            "Sort by Time",
            "Sort into Compile Time, mixed, and Runtime groups",  // TODO make an icon that is arrow + (compile + run) on side like SortByType does with type symbols
            AllIcons.ObjectBrowser.SortByType
    )

    override fun isVisible(): Boolean = true

    companion object {
        val INSTANCE: Sorter = Time()
        val TIME_SORTER_ID: @NonNls String? = "TIME_COMPARATOR"
    }
}
