package org.elixir_lang.beam.chunk

import com.intellij.openapi.vfs.VirtualFile
import org.elixir_lang.beam.Beam
import org.elixir_lang.beam.Beam.Companion.from
import org.elixir_lang.beam.Decompiler
import java.util.*

/***
 * Decompiles given file and provides moduledocs / function docs
 */
class BeamDocumentationProvider {

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
                    return beam.beamDocumentation()?.moduledoc
                }
            }
        }
        return null
    }
}