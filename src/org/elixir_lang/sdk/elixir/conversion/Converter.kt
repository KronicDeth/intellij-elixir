package org.elixir_lang.sdk.elixir.conversion

import com.intellij.conversion.ProjectConverter
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState.NON_MODAL
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.SdkModificator
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.vfs.VirtualFileManager
import org.elixir_lang.sdk.elixir.Type
import org.elixir_lang.sdk.elixir.Type.*
import org.elixir_lang.sdk.erlang_dependent.SdkAdditionalData
import java.nio.file.Paths

private fun commitChanges(sdkModificator: SdkModificator) =
    ApplicationManager.getApplication().invokeAndWait(
            { ApplicationManager.getApplication().runWriteAction { sdkModificator.commitChanges() } },
            NON_MODAL
    )

private fun isElixir(sdk: Sdk): Boolean = sdk.sdkType is Type

private fun isClassPathConversionNeeded(homePath: String, sdkModificator: SdkModificator): Boolean =
    isPathConversionNeeded(oldClassPathPath(homePath), sdkModificator, OrderRootType.CLASSES)

private fun isPathConversionNeeded(oldPathPath: String,
                                   sdkModificator: SdkModificator,
                                   orderRootType: OrderRootType): Boolean =
    sdkModificator.getRoots(orderRootType).any { virtualFile ->
        virtualFile.path == oldPathPath
    }

private fun isSourcePathConversionNeeded(homePath: String, sdkModificator: SdkModificator): Boolean =
    isPathConversionNeeded(oldSourcePathPath(homePath), sdkModificator, OrderRootType.SOURCES)

private const val ELIXIR_LANG_DOT_ORG_DOCS_URL = "http://elixir-lang.org/docs/stable/elixir/"

private fun isDocumentationPathConversionNeeded(sdkModificator: SdkModificator): Boolean =
    documentationRootType()?.let { documentationRootType ->
        sdkModificator.getRoots(documentationRootType).any { documentationPath ->
            documentationPath.url == ELIXIR_LANG_DOT_ORG_DOCS_URL
        }
    } == true

private fun isErlangSdkConversionNeeded(sdk: Sdk): Boolean =
        (sdk.sdkAdditionalData as? SdkAdditionalData)?.erlangSdk == null

private fun isConversionNeeded(sdk: Sdk): Boolean =
        (isErlangSdkConversionNeeded(sdk) && defaultErlangSdk() != null) ||
                sdk.sdkModificator.let { sdkModificator ->
                    isDocumentationPathConversionNeeded(sdkModificator) ||
                            sdk.homePath?.let { homePath ->
                                isClassPathConversionNeeded(homePath, sdkModificator) ||
                                        isSourcePathConversionNeeded(homePath, sdkModificator)
                            } == true
                }

fun oldClassPathPath(homePath: String): String {
    return Paths.get(homePath, "lib").toString()
}

fun oldSourcePathPath(homePath: String): String {
    return Paths.get(homePath, "lib").toString()
}

private fun putDefaultErlangSdk(elixirSdk: Sdk): Sdk? {
    val sdkModificator = elixirSdk.sdkModificator
    val defaultErlangSdk = configureInternalErlangSdk(elixirSdk, sdkModificator)

    commitChanges(sdkModificator)

    return defaultErlangSdk
}

private fun sdks(): Sequence<Sdk> = ProjectJdkTable.getInstance().allJdks.asSequence().filter(::isElixir)

private fun updateClassPaths(sdkModificator: SdkModificator, homePath: String): Boolean {
    val oldClassPathPath = oldClassPathPath(homePath)
    var modified = false

    for (classPath in sdkModificator.getRoots(OrderRootType.CLASSES)) {
        val classPathPath = classPath.path

        if (classPathPath == oldClassPathPath) {
            sdkModificator.removeRoot(classPath, OrderRootType.CLASSES)
            org.elixir_lang.sdk.Type.addCodePaths(sdkModificator)
            modified = true
        }
    }

    return modified
}

private fun updateDocumentationPaths(sdkModificator: SdkModificator): Boolean {
    val documentationRootType = documentationRootType()
    var modified = false

    if (documentationRootType != null) {
        val elixirLangDotOrgDocsUrl = "http://elixir-lang.org/docs/stable/elixir/"

        for (documentationPath in sdkModificator.getRoots(documentationRootType)) {
            if (documentationPath.url == elixirLangDotOrgDocsUrl) {
                VirtualFileManager
                        .getInstance()
                        .findFileByUrl(elixirLangDotOrgDocsUrl)
                        ?.let { elixirLangDotOrgDocsUrlVirtualFile ->
                            sdkModificator.removeRoot(elixirLangDotOrgDocsUrlVirtualFile, documentationRootType)
                        }
                addDocumentationPaths(sdkModificator)
                modified = true
            }
        }
    }

    return modified
}

private fun updateRoots(sdk: Sdk) {
    val homePath = sdk.homePath

    if (homePath != null) {
        val sdkModificator = sdk.sdkModificator
        val modified = updateClassPaths(sdkModificator, homePath) ||
                updateDocumentationPaths(sdkModificator) ||
                updateSourcePaths(sdkModificator, homePath)

        if (modified) {
            ApplicationManager.getApplication().invokeAndWait(
                    { ApplicationManager.getApplication().runWriteAction { sdkModificator.commitChanges() } },
                    NON_MODAL
            )
        }
    }
}

private fun updateSourcePaths(sdkModificator: SdkModificator, homePath: String): Boolean {
    val oldSourcePathPath = oldSourcePathPath(homePath)
    var modified = false

    for (classPath in sdkModificator.getRoots(OrderRootType.SOURCES)) {
        val classPathPath = classPath.path

        if (classPathPath == oldSourcePathPath) {
            sdkModificator.removeRoot(classPath, OrderRootType.SOURCES)
            addSourcePaths(sdkModificator)
            modified = true
        }
    }

    return modified
}

class Converter: ProjectConverter() {
    override fun isConversionNeeded(): Boolean = sdks().any { sdk -> isConversionNeeded(sdk) }
    override fun preProcessingFinished() {
        super.preProcessingFinished()

        sdks().forEach { sdk ->
            updateRoots(sdk)
            putDefaultErlangSdk(sdk)
        }
    }
}
