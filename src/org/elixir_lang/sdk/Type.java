package org.elixir_lang.sdk;

import com.intellij.openapi.projectRoots.SdkModificator;
import com.intellij.openapi.roots.JavadocOrderRootType;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.function.Consumer;

import static org.elixir_lang.sdk.HomePath.eachEbinPath;
import static org.elixir_lang.sdk.ProcessOutput.isSmallIde;

public class Type {
    private Type() {
    }

    public static void addCodePaths(@NotNull SdkModificator sdkModificator) {
        eachEbinPath(
                sdkModificator.getHomePath(),
                ebin -> ebinPathChainVirtualFile(
                        ebin, virtualFile -> sdkModificator.addRoot(virtualFile, OrderRootType.CLASSES)
                )
        );
    }

    public static void ebinPathChainVirtualFile(@NotNull Path ebinPath, Consumer<VirtualFile> virtualFileConsumer) {
        VirtualFile virtualFile = LocalFileSystem
                .getInstance()
                .findFileByIoFile(ebinPath.toFile());

        if (virtualFile != null) {
            virtualFileConsumer.accept(virtualFile);
        }
    }

    @Nullable
    public static OrderRootType documentationRootType() {
        OrderRootType rootType;

        if (isSmallIde()) {
            rootType = null;
        } else {
            rootType = JavadocOrderRootType.getInstance();
        }

        return rootType;
    }
}
