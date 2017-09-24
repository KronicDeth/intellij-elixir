package org.elixir_lang.sdk;

import com.intellij.openapi.projectRoots.SdkModificator;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.function.Consumer;

import static org.elixir_lang.sdk.HomePath.eachEbinPath;

public class Type {
    private Type() {
    }

    public static void ebinPathChainVirtualFile(@NotNull Path ebinPath, Consumer<VirtualFile> virtualFileConsumer) {
        VirtualFile virtualFile = LocalFileSystem
                .getInstance()
                .findFileByIoFile(ebinPath.toFile());

        if (virtualFile != null) {
            virtualFileConsumer.accept(virtualFile);
        }
    }

    public static void addCodePaths(@NotNull SdkModificator sdkModificator) {
        eachEbinPath(
                sdkModificator.getHomePath(),
                ebin -> ebinPathChainVirtualFile(
                        ebin, virtualFile -> sdkModificator.addRoot(virtualFile, OrderRootType.CLASSES)
                )
        );
    }
}
