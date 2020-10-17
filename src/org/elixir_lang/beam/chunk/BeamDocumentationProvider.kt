package org.elixir_lang.beam.chunk

import com.intellij.openapi.vfs.VirtualFile
import org.elixir_lang.beam.Beam
import org.elixir_lang.beam.Beam.Companion.from
import org.elixir_lang.beam.chunk.beam_documentation.Doc
import org.elixir_lang.beam.chunk.beam_documentation.FunctionMetadata
import java.util.*

/***
 * Decompiles given file and provides moduledocs / function docs
 */
class BeamDocumentationProvider {

    fun getFunctionDocs(virtualFile: VirtualFile, functionName: String, arity: Int): List<Doc>? {
        return getFunctionDocs(Optional.ofNullable(from(virtualFile)), functionName, arity)
    }

    private fun getFunctionDocs(beamOptional: Optional<Beam>, functionName: String, arity: Int): List<Doc>? {
        if (beamOptional.isPresent) {
            val beam: Beam = beamOptional.get()
            val atoms = beam.atoms()
            if (atoms != null) {
                val moduleName = atoms.moduleName()

                if (moduleName != null) {
                    return beam.documentation()?.docs?.getFunctionDocsOrSimilar(functionName, arity)
                }
            }
        }
        return null
    }

    fun getFunctionAttributes(virtualFile: VirtualFile, functionName: String, arity: Int): List<FunctionMetadata>? {
        return getFunctionAttributes(Optional.ofNullable(from(virtualFile)), functionName, arity)
    }

    private fun getFunctionAttributes(beamOptional: Optional<Beam>, functionName: String, arity: Int): List<FunctionMetadata>? {
        if (beamOptional.isPresent) {
            val beam: Beam = beamOptional.get()
            val atoms = beam.atoms()
            if (atoms != null) {
                val moduleName = atoms.moduleName()

                if (moduleName != null) {
                    return beam.documentation()?.docs?.getFunctionMetadataOrSimilar(functionName, arity)
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
                    return beam.documentation()?.moduleDocs?.englishDocs
                }
            }
        }
        return null
    }
}