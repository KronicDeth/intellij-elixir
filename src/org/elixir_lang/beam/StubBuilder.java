package org.elixir_lang.beam;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.stubs.BinaryFileStubBuilder;
import com.intellij.psi.stubs.PsiFileStub;
import com.intellij.psi.stubs.Stub;
import com.intellij.util.indexing.FileContent;
import org.elixir_lang.beam.psi.BeamFileImpl;
import org.jetbrains.annotations.Nullable;

public class StubBuilder implements BinaryFileStubBuilder {
    private static final Logger LOGGER = Logger.getInstance(StubBuilder.class);
    private static final int STUB_VERSION = 0;

    /**
     * @param file a .beam file
     * @return {@code true} (accepts all files because it is only registered for BEAM file type
     */
    @Override
    public boolean acceptsFile(VirtualFile file) {
        return true;
    }

    @Nullable
    @Override
    public Stub buildStubTree(FileContent fileContent) {
        byte[] content = fileContent.getContent();

        PsiFileStub<?> stub = BeamFileImpl.buildFileStub(content);

        if (stub == null) {
            LOGGER.info("No stub built for file " + fileContent);
        }

        return stub;
    }

    @Override
    public int getStubVersion() {
        return STUB_VERSION;
    }
}
