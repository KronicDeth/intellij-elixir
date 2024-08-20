package org.elixir_lang.sdk.erlang_dependent;

import com.intellij.openapi.projectRoots.SdkModificator;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

public  interface SdkModificatorRootTypeConsumer {
    void consume(@NotNull SdkModificator sdkModificator,
                 @NotNull VirtualFile[] configuredRoots,
                 @NotNull VirtualFile expandedInternalRoot,
                 @NotNull OrderRootType type);
}
