package org.elixir_lang.sdk;

import com.intellij.openapi.projectRoots.SdkModificator;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import static org.elixir_lang.sdk.HomePath.eachEbinPath;

public class Type {
    private Type() {
    }

    public static void addCodePaths(@NotNull SdkModificator sdkModificator) {
        eachEbinPath(
                sdkModificator.getHomePath(),
                ebin -> {
                    VirtualFile virtualFile = LocalFileSystem
                            .getInstance()
                            .findFileByIoFile(ebin.toFile());

                    if (virtualFile != null) {
                        sdkModificator.addRoot(virtualFile, OrderRootType.CLASSES);
                    }
                }
        );
    }
}
