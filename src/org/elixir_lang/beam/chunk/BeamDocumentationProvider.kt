package org.elixir_lang.beam.chunk

import com.intellij.openapi.vfs.VirtualFile
import org.elixir_lang.beam.Beam
import org.elixir_lang.beam.Beam.Companion.from
import org.elixir_lang.beam.chunk.beam_documentation.BeamDoc
import java.util.*

/***
 * Decompiles given file and provides moduledocs / function docs
 */
class BeamDocumentationProvider {

    fun getFunctionDocs(virtualFile: VirtualFile, functionName: String, arity: Int): List<BeamDoc>? {
        return getFunctionDocs(Optional.ofNullable(from(virtualFile)), functionName, arity)
    }

    private fun getFunctionDocs(beamOptional: Optional<Beam>, functionName: String, arity: Int): List<BeamDoc>? {
        if (beamOptional.isPresent) {
            val beam: Beam = beamOptional.get()
            val atoms = beam.atoms()
            if (atoms != null) {
                val moduleName = atoms.moduleName()

                if (moduleName != null) {
                    return beam.beamDocumentation()?.docs?.getFunctionDocsOrSimilar(functionName, arity)
                }
            }
        }
        return null
    }

    fun getModuleDocs(virtualFile: VirtualFile): String? {
        return getModuleDocs(Optional.ofNullable(from(virtualFile)))
    }

    private fun getModuleDocs(beamOptional: Optional<Beam>): String? {
        if (beamOptional.isPresent) {
            val beam: Beam = beamOptional.get()
            val atoms = beam.atoms()
            if (atoms != null) {
                val moduleName = atoms.moduleName()

                if (moduleName != null) {
                    return beam.beamDocumentation()?.moduleDocs?.englishDocs
                }
            }
        }
        return null
    }
}