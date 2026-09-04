package org.elixir_lang.console;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import com.intellij.util.concurrency.ThreadingAssertions;
import com.intellij.util.concurrency.annotations.RequiresReadLock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Resolves a source path printed in console output to the file or files it names.
 *
 * <p>Shared by {@link FileReferenceFilter}, which reads the formatted {@code (app) path:line:}
 * frames, and {@link LiteralStackTraceFilter}, which reads the {@code [file: ..., line: ...]}
 * keyword list. The two differ only in how they find a path; what a path means is the same for
 * both, so it lives here rather than in either of them.
 */
final class SourceFileResolver {
    /**
     * The extensions a console path may name. Elixir's own sources and templates, plus Erlang,
     * since a stack trace mixes them freely - an OTP frame reports {@code gen_server.erl} beside
     * Elixir frames, and both are the same kind of thing to a reader following a trace.
     *
     * <p>Accepting an extension is not the same as resolving it - that depends on what is indexed.
     * An Erlang dependency's {@code src} is ordinary project content; OTP's own sources come from an
     * Erlang SDK's {@code src} directories, which it registers as source roots. An install that
     * ships no sources at all resolves neither: Elixir's own {@code src/elixir.erl} and friends are
     * paths recorded when the release was built, with no file on disk to open.
     */
    private static final Pattern PATTERN_FILENAME =
            Pattern.compile("[/\\\\]?([^/\\\\]*?\\.(?:heex|leex|eex|exs|erl|ex))$");

    private SourceFileResolver() {
    }

    /**
     * The file or files {@code path} could name, best candidate first: the path as written, then the
     * path taken as project-relative, then any project file whose name matches and whose own path
     * ends with {@code path}, then the same search widened to libraries.
     *
     * <p>Empty when nothing matches - a path naming a file nothing indexed can be found for, or one
     * whose extension {@link #PATTERN_FILENAME} does not accept.
     *
     * <p>Requires a read action, which the platform provides by running console filters inside
     * {@code ReadAction.nonBlocking}. It does not require indexes to be built: the
     * {@link FilenameIndex} lookup answers during dumb mode rather than throwing, returning whatever
     * has been indexed so far, which is why both filters are {@code DumbAware}.
     */
    @NotNull
    @RequiresReadLock
    static Collection<VirtualFile> resolve(@NotNull Project project, @NotNull String path) {
        ThreadingAssertions.assertReadAccess();

        // Every comparison below is between a path from console output and a path from the project
        // model or the VFS, which are separate sources, so both sides are normalised first. A
        // compile error printed as `lib\my_app\foo.ex` otherwise matches nothing: the VFS holds
        // `/` and neither `startsWith` nor `endsWith` knows a separator from any other character.
        String systemIndependentPath = FileUtil.toSystemIndependentName(path);

        VirtualFile asIsFile = pathToVirtualFile(systemIndependentPath);

        if (asIsFile != null) {
            return Collections.singleton(asIsFile);
        }

        String basePath = project.getBasePath();
        VirtualFile projectBasedFile = null;

        if (basePath != null) {
            String systemIndependentBasePath = FileUtil.toSystemIndependentName(basePath);
            String projectBasedPath;

            if (systemIndependentPath.startsWith(systemIndependentBasePath)) {
                projectBasedPath = systemIndependentPath;
            } else {
                projectBasedPath = FileUtil.toSystemIndependentName(
                        new File(basePath, systemIndependentPath).getAbsolutePath()
                );
            }

            projectBasedFile = pathToVirtualFile(projectBasedPath);
        }

        Collection<VirtualFile> virtualFileCollection = null;

        if (projectBasedFile != null) {
            virtualFileCollection = Collections.singleton(projectBasedFile);
        } else {
            Matcher filenameMatcher = PATTERN_FILENAME.matcher(systemIndependentPath);

            if (filenameMatcher.find()) {
                String filename = filenameMatcher.group(1);
                GlobalSearchScope projectScope = ProjectScope.getProjectScope(project);
                virtualFileCollection = suffixMatches(project, systemIndependentPath, filename, projectScope);

                if (virtualFileCollection.isEmpty()) {
                    GlobalSearchScope libraryScope = ProjectScope.getLibrariesScope(project);

                    virtualFileCollection = suffixMatches(project, systemIndependentPath, filename, libraryScope);
                }
            }
        }

        if (virtualFileCollection == null) {
            virtualFileCollection = Collections.emptySet();
        }

        return virtualFileCollection;
    }

    @Nullable
    private static VirtualFile pathToVirtualFile(@NotNull String path) {
        return LocalFileSystem.getInstance().findFileByPath(FileUtil.toSystemIndependentName(path));
    }

    @NotNull
    private static Collection<VirtualFile> suffixMatches(@NotNull Project project,
                                                         @NotNull String path,
                                                         @NotNull String basename,
                                                         @NotNull GlobalSearchScope scope) {
        List<VirtualFile> suffixedVirtualFiles = new ArrayList<>();
        Collection<VirtualFile> projectFilesWithBaseName = FilenameIndex.getVirtualFilesByName(basename, scope);

        for (VirtualFile projectFileWithBaseName : projectFilesWithBaseName) {
            // The caller normalised `path`; a VirtualFile's own path is system-independent already,
            // but saying so costs nothing and survives someone passing a raw path in.
            String virtualFilePath = FileUtil.toSystemIndependentName(projectFileWithBaseName.getPath());

            if (virtualFilePath.endsWith(path)) {
                suffixedVirtualFiles.add(projectFileWithBaseName);
            }
        }

        return suffixedVirtualFiles;
    }
}
