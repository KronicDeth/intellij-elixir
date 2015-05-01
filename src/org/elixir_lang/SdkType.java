package org.elixir_lang;

import com.intellij.openapi.projectRoots.*;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.Version;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.List;

public class SdkType extends com.intellij.openapi.projectRoots.SdkType {
    @NotNull
    public static SdkType getInstance() {
        return com.intellij.openapi.projectRoots.SdkType.findInstance(SdkType.class);
    }

    public SdkType() {
        super("Elixir SDK");
    }

    @Nullable
    @Override
    public AdditionalDataConfigurable createAdditionalDataConfigurable(SdkModel sdkModel, SdkModificator sdkModificator) {
        return null;
    }

    @Override
    public String getPresentableName() {
        return "Elixir SDK";
    }

    @Override
    public boolean isValidSdkHome(String path) {
        File bin = new File(path, "bin").getAbsoluteFile();
        File elixir = new File(bin, "elixir");
        File elixirc = new File(bin, "elixirc");
        File iex = new File(bin, "iex");
        File mix = new File(bin, "mix");

        return elixir.canExecute() && elixirc.canExecute() && iex.canExecute() && mix.canExecute();
    }

    @Nullable
    @Override
    public String getVersionString(String sdkHome) {
        List<String> parts = FileUtil.splitPath(sdkHome);

        return parts.get(parts.size() - 1);
    }

    @Override
    public void saveAdditionalData(SdkAdditionalData additionalData, Element additional) {

    }

    @Nullable
    @Override
    public String suggestHomePath() {
        Iterator<String> iterator = suggestHomePaths().iterator();
        String suggestedHomePath = null;

        if (iterator.hasNext()) {
            suggestedHomePath = iterator.next();
        }

        return suggestedHomePath;
    }

    @Override
    public Collection<String> suggestHomePaths() {
        return homePathByVersion().values();
    }

    @Override
    public String suggestSdkName(String currentSdkName, String sdkHome) {
        return "Elixir " + getVersionString(sdkHome);
    }

    @Override
    public void setupSdkPaths(@NotNull Sdk sdk) {
        SdkModificator sdkModificator = sdk.getSdkModificator();

        String sdkHome = sdkModificator.getHomePath();
        File lib = new File(sdkHome, "lib");

        for (File library : lib.listFiles()) {
            File beams = new File(library, "ebin");

            if (beams.isDirectory()) {
                VirtualFile libraryVirtualFile = LocalFileSystem.getInstance().findFileByIoFile(library);

                if (libraryVirtualFile != null) {
                    sdkModificator.addRoot(libraryVirtualFile, OrderRootType.CLASSES);
                }
            }
        }

        sdkModificator.commitChanges();
    }

    /*
     * Private
     */

    /**
     * Map of home paths to versions in descending version order so that newer versions are favored.
     *
     * @return
     */
    private Map<Version, String> homePathByVersion() {
        Map<Version, String> homePathByVersion = new TreeMap<Version, String>(
                new Comparator<Version>() {
                    @Override
                    public int compare(Version version1, Version version2) {
                        // compare version2 to version1 to produce descending instead of ascending order.
                        return version2.compareTo(version1);
                    }
                }
        );

        if (SystemInfo.isMac) {
            File homebrewRoot = new File("/usr/local/Cellar/elixir");

            if (homebrewRoot.isDirectory()) {
                for (File child : homebrewRoot.listFiles()) {
                    if (child.isDirectory()) {
                        String versionString = child.getName();
                        String[] versionParts = versionString.split("\\.", 3);
                        int major = Integer.parseInt(versionParts[0]);
                        int minor = Integer.parseInt(versionParts[1]);
                        int bugfix = Integer.parseInt(versionParts[2]);
                        Version version = new Version(major, minor, bugfix);

                        homePathByVersion.put(version, child.getAbsolutePath());
                    }
                }
            }
        }

        return homePathByVersion;
    }
}
