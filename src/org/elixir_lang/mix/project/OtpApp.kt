package org.elixir_lang.mix.project

import com.ericsson.otp.erlang.OtpErlangAtom
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.util.Computable
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFileFactory
import org.elixir_lang.ElixirScriptFileType
import org.elixir_lang.NameArity
import org.elixir_lang.psi.ElixirAccessExpression
import org.elixir_lang.psi.ElixirFile
import org.elixir_lang.psi.ElixirList
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.keywordValue
import org.elixir_lang.semantic.File
import org.elixir_lang.semantic.call.definition.Clause
import org.elixir_lang.semantic.call.definition.clause.Time
import org.elixir_lang.semantic.semantic
import java.io.IOException
import java.nio.file.Paths

private fun app(appMixFile: VirtualFile): String =
    try {
        app(appMixFile, text(appMixFile))
    } catch (e: IOException) {
        appFromPath(appMixFile)
    }

private fun app(appMixFile: VirtualFile, appMixFileText: String): String {
    val elixirFile = elixirFile(appMixFileText)
    val appList = appList(elixirFile)

    return if (appList.isEmpty()) {
        appFromPath(appMixFile)
    } else {
        appList.first()
    }
}

private fun appList(elixirFile: ElixirFile): List<String> {
    val modulars = elixirFile.semantic?.let { it as? File }?.modulars.orEmpty()

    return modulars
        .asSequence()
        .flatMap { it.callDefinitions.asSequence() }
        .filter { definition ->
            definition.time == Time.RUN && definition.nameArityInterval?.contains(NameArity("project", 0)) == true
        }
        .flatMap { definition ->
            definition.clauses.asSequence()
        }
        .map(Clause::psiElement)
        .filterIsInstance<Call>()
        .flatMap { projectCallDefinition ->
            projectCallDefinition.doBlock?.stab?.stabBody?.children?.asSequence() ?: emptySequence()
        }
        .filterIsInstance<ElixirAccessExpression>()
        .mapNotNull(ElixirAccessExpression::getList)
        .mapNotNull(ElixirList::getKeywords)
        .mapNotNull { keywordList ->
            keywordList.keywordValue("app")
        }
        .map { keywordValue ->
            keywordValue.quote()
        }
        .filterIsInstance<OtpErlangAtom>()
        .map(OtpErlangAtom::atomValue)
        .toList()
}

private fun appFromPath(appMixFile: VirtualFile): String = Paths.get(appMixFile.path).parent.fileName.toString()

fun <T> computeReadAction(computable: Computable<T>): T =
    ApplicationManager.getApplication().runReadAction(computable)

private fun elixirFile(text: String): ElixirFile = computeReadAction {
    val defaultProject = ProjectManager.getInstance().defaultProject

    PsiFileFactory
        .getInstance(defaultProject)
        .createFileFromText("mix.exs", ElixirScriptFileType.INSTANCE, text) as ElixirFile
}

private fun text(virtualFile: VirtualFile): String = computeReadAction {
    VfsUtil.loadText(virtualFile)
}

class OtpApp(val root: VirtualFile, appMixFile: VirtualFile) {
    val deps = mutableSetOf<String>()
    var ideaModuleFile: VirtualFile? = null
    var module: Module? = null
    val name = app(appMixFile)

    override fun toString(): String = "$name ($root)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        val that = other as OtpApp

        return name == that.name && root == that.root
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + root.hashCode()
        return result
    }
}
