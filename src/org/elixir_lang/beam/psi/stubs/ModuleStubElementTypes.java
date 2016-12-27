package org.elixir_lang.beam.psi.stubs;

import com.intellij.lang.ASTNode;
import org.elixir_lang.beam.psi.ModuleElement;
import org.jetbrains.annotations.NotNull;

// See com.intellij.psi.impl.java.stubs.JavaStubElementTypes
public interface ModuleStubElementTypes {
    /**
     * See {@link com.intellij.psi.impl.java.stubs.JavaStubElementTypes#CLASS}
     */
    ModuleElementType MODULE = new ModuleElementType("Module") {
        @NotNull
        @Override
        public ASTNode createCompositeNode() {
            return new ModuleElement(this);
        }
    };

}
