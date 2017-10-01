package org.elixir_lang.beam;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.stubs.BinaryFileStubBuilder;
import com.intellij.psi.stubs.Stub;
import com.intellij.util.indexing.FileContent;
import org.elixir_lang.beam.psi.BeamFileImpl;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class StubBuilder implements BinaryFileStubBuilder {
    private static final Logger LOGGER = Logger.getInstance(StubBuilder.class);
    private static final int STUB_VERSION = 1;

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

        Optional<Stub> stubOptional = BeamFileImpl.buildFileStub(content, fileContent.getFile().getPath());
        Stub stub;

        if (stubOptional.isPresent()) {
            stub = stubOptional.get();
        } else {
            LOGGER.info("No stub built for file " + fileContent);
            stub = null;
        }

        return stub;
    }

    @Override
    public int getStubVersion() {
        return STUB_VERSION;
    }
}
