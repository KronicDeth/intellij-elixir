package org.elixir_lang.beam.chunk.atoms

import com.intellij.ui.components.JBScrollPane
import org.elixir_lang.beam.chunk.Atoms

class ScrollPane(atoms: Atoms?): JBScrollPane(Table(atoms))
