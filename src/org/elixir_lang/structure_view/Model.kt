package org.elixir_lang.structure_view

import com.intellij.ide.structureView.StructureViewModel.ElementInfoProvider
import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.structureView.TextEditorBasedStructureViewModel
import com.intellij.ide.util.treeView.smartTree.NodeProvider
import com.intellij.ide.util.treeView.smartTree.Sorter
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.util.concurrency.annotations.RequiresReadLock
import org.elixir_lang.psi.*
import org.elixir_lang.psi.call.Call
import org.elixir_lang.structure_view.element.*
import org.elixir_lang.structure_view.element.structure.Field
import org.elixir_lang.structure_view.element.structure.FieldWithDefaultValue
import org.elixir_lang.structure_view.node_provider.Used
import org.elixir_lang.structure_view.sorter.Time
import org.elixir_lang.structure_view.sorter.Visibility
import java.util.*

class Model(elixirFile: ElixirFile, editor: Editor?) : TextEditorBasedStructureViewModel(editor, elixirFile), ElementInfoProvider {
    override fun getNodeProviders(): Collection<NodeProvider<*>> = NODE_PROVIDERS

    public override fun getPsiFile(): ElixirFile = super.getPsiFile() as ElixirFile

    override fun getSorters(): Array<Sorter> = arrayOf(
            Time.INSTANCE,
            Visibility.INSTANCE,
            Sorter.ALPHA_SORTER
    )

    /**
     * Returns the list of PSI element classes which are shown as structure view elements.
     * When determining the current editor element, the PSI tree is walked up until an element
     * matching one of these classes is found.
     *
     * @return the list of classes
     */
    override fun getSuitableClasses(): Array<Class<*>> = arrayOf(
            Call::class.java,
            ElixirAtom::class.java,
            QuotableKeywordPair::class.java
    )

    override fun isAlwaysShowsPlus(element: StructureViewTreeElement): Boolean = false
    override fun isAlwaysLeaf(element: StructureViewTreeElement): Boolean = false

    override fun isSuitable(element: PsiElement): Boolean = if (super.isSuitable(element)) {
        // calls can be nested in calls, so need to check for sure
        when (element) {
            is Call -> Companion.isSuitable(element)
            is ElixirAtom -> Field.`is`(element)
            is QuotableKeywordPair -> FieldWithDefaultValue.`is`(element)
            else -> false
        }
    } else {
        false
    }

    /**
     * Returns the root element of the structure view tree.
     *
     * @return the structure view root.
     */
    override fun getRoot(): StructureViewTreeElement = File(psiFile)

    companion object {
        private val NODE_PROVIDERS: Collection<NodeProvider<*>> = Arrays.asList<NodeProvider<*>>(Used())

        @RequiresReadLock
        fun isSuitable(call: Call?): Boolean =
                ChildCall.isSuitable(call!!, ResolveState.initial())
    }
}
