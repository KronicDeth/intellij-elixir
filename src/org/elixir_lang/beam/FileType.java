package org.elixir_lang.beam;

import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;

public class FileType implements com.intellij.openapi.fileTypes.FileType {
    public static final FileType INSTANCE = new FileType();

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "beam";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Bogdan/Björn's Erlang Abstract Machine file";
    }

    @NotNull
    @Override
    public Icon getIcon() {
        return Icons.FILE;
    }

    /**
     * Returns the character set for the specified file.
     *
     * @param file    The file for which the character set is requested.
     * @param content bytes in the {@code file}
     * @return null because it's a binary file
     */
    @Nullable
    @Override
    public String getCharset(@NotNull VirtualFile file, @NotNull byte[] content) {
        return null;
    }

    @NotNull
    @Override
    public String getName() {
        return "BEAM";
    }

    /**
     * <a href="http://beam-wisdoms.clau.se/en/latest/indepth-beam-file.html">Beam files</a> are based on the
     * <a href="https://en.wikipedia.org/wiki/Interchange_File_Format">IFF format</a>.
     *
     * @return true
     */
    @Override
    public boolean isBinary() {
        return true;
    }

    /**
     * {@code .beam} files are only meant for decompilation.
     *
     * @return true
     */
    @Override
    public boolean isReadOnly() {
        return true;
    }

    @NotNull
    @Override
    public String toString() {
        return "BEAM";
    }
}
