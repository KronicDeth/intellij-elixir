package org.elixir_lang.psi.impl

import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.usageView.UsageViewUtil
import org.elixir_lang.annotator.Parameter
import org.elixir_lang.psi.ElixirIdentifier
import org.elixir_lang.psi.call.Call
import org.elixir_lang.structure_view.element.CallDefinitionClause
import java.io.File
import javax.swing.Icon

object PresentationImpl {
    @JvmStatic
    fun getPresentation(call: Call): ItemPresentation =
            if (CallDefinitionClause.`is`(call)) {
                CallDefinitionClause.fromCall(call)?.presentation
            } else {
                null
            } ?: getDefaultPresentation(call)

    @JvmStatic
    fun getPresentation(identifier: ElixirIdentifier): ItemPresentation? {
        val parameterizedParameter = Parameter(identifier).let { Parameter.putParameterized(it) }

        return if ((parameterizedParameter.type == Parameter.Type.FUNCTION_NAME ||
                        parameterizedParameter.type == Parameter.Type.MACRO_NAME) &&
                parameterizedParameter.parameterized != null) {
            (parameterizedParameter.parameterized as? Call)?.let {
                CallDefinitionClause.fromCall(it)?.presentation
            }
        } else {
            null
        }
    }

    private fun getDefaultPresentation(call: Call): ItemPresentation {
        val text = UsageViewUtil.createNodeText(call)

        return object : ItemPresentation {
            private val _locationString by lazy { call.locationString() }

            override fun getIcon(b: Boolean): Icon? = call.getIcon(0)
            override fun getLocationString(): String? = _locationString
            override fun getPresentableText(): String? = text
        }
    }
}

private fun Call.locationString(): String = this.containingFile!!.locationString(this.project)

private fun PsiFile.locationString(project: Project): String = this.virtualFile?.locationString(project) ?: this.name

private fun VirtualFile.locationString(project: Project): String =
        this.path.let { path ->
            ProjectRootManager
                    .getInstance(project)
                    .fileIndex
                    .getSourceRootForFile(this)
                    ?.let { sourceRoot ->
                        File(path).relativeToOrSelf(File(sourceRoot.path)).path
                    }
                    ?: path
        }
