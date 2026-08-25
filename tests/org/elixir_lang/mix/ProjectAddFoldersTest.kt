package org.elixir_lang.mix

import com.intellij.openapi.roots.CompilerModuleExtension
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.ModuleRootModificationUtil
import org.elixir_lang.PlatformTestCase

/**
 * Covers what [Project.addFolders] configures beyond the folder marks themselves.
 */
class ProjectAddFoldersTest : PlatformTestCase() {

    /** URLs of content entries added during a test, cleaned up in [tearDown]. */
    private val addedContentRootUrls = mutableListOf<String>()

    override fun tearDown() {
        try {
            if (addedContentRootUrls.isNotEmpty()) {
                ModuleRootModificationUtil.updateModel(module) { model ->
                    for (entry in model.contentEntries.toList()) {
                        if (entry.url in addedContentRootUrls) {
                            model.removeContentEntry(entry)
                        }
                    }
                }
                addedContentRootUrls.clear()
            }
        } finally {
            super.tearDown()
        }
    }

    /**
     * An Elixir module must not exclude IntelliJ's compiler output: Elixir compiles to `_build`, and
     * the plugin's own project converter exists to strip `<exclude-output/>` from `ELIXIR_MODULE`s -
     * so a module that keeps the flag is offered for "conversion" every time its project is opened.
     *
     * `JavaSettingsSerializer.saveJavaSettings` writes the tag when this property is true, and also
     * when a module has no Java settings at all, which is why [Project.addFolders] sets it rather
     * than leaving it alone.
     */
    fun testDoesNotExcludeCompilerOutput() {
        val root = myFixture.tempDirFixture.findOrCreateDir("mix_app")

        ModuleRootModificationUtil.updateModel(module) { model ->
            // Mirrors JavaModuleBuilder.setupRootModel, which turns this on before
            // ElixirModuleBuilder gets to call addFolders.
            model.getModuleExtension(CompilerModuleExtension::class.java).setExcludeOutput(true)

            Project.addFolders(model, root)
        }
        addedContentRootUrls.add(root.url)

        assertFalse(
            "addFolders must clear exclude-output, or every project the plugin creates is offered" +
                    " for conversion when it is next opened",
            ModuleRootManager.getInstance(module)
                .getModuleExtension(CompilerModuleExtension::class.java)
                .isExcludeOutput
        )
    }
}
