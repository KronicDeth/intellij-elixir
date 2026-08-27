package org.elixir_lang.console;

import com.intellij.openapi.project.Project;
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
     * An Erlang dependency's {@code src} is ordinary project content and resolves like any other
     * file, while OTP's own sources live under an Erlang SDK's {@code src} directories, which are
     * not registered as source roots today, so {@code gen_server.erl} is accepted here and then
     * finds nothing.
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
     * <p>Requires a read action for the {@link FilenameIndex} lookup, and indexes to be built. Both
     * are the platform's to provide: it runs console filters inside
     * {@code ReadAction.nonBlocking}, and skips a filter that is not {@code DumbAware} while
     * indexing.
     */
    @NotNull
    @RequiresReadLock
    static Collection<VirtualFile> resolve(@NotNull Project project, @NotNull String path) {
        ThreadingAssertions.assertReadAccess();

        VirtualFile asIsFile = pathToVirtualFile(path);

        if (asIsFile != null) {
            return Collections.singleton(asIsFile);
        }

        String basePath = project.getBasePath();
        VirtualFile projectBasedFile = null;

        if (basePath != null) {
            String projectBasedPath;

            if (path.startsWith(basePath)) {
                projectBasedPath = path;
            } else {
                projectBasedPath = new File(basePath, path).getAbsolutePath();
            }

            projectBasedFile = pathToVirtualFile(projectBasedPath);
        }

        Collection<VirtualFile> virtualFileCollection = null;

        if (projectBasedFile != null) {
            virtualFileCollection = Collections.singleton(projectBasedFile);
        } else {
            Matcher filenameMatcher = PATTERN_FILENAME.matcher(path);

            if (filenameMatcher.find()) {
                String filename = filenameMatcher.group(1);
                GlobalSearchScope projectScope = ProjectScope.getProjectScope(project);
                virtualFileCollection = suffixMatches(project, path, filename, projectScope);

                if (virtualFileCollection.isEmpty()) {
                    GlobalSearchScope libraryScope = ProjectScope.getLibrariesScope(project);

                    virtualFileCollection = suffixMatches(project, path, filename, libraryScope);
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
        String normalizedPath = path.replace(File.separatorChar, '/');

        return LocalFileSystem.getInstance().findFileByPath(normalizedPath);
    }

    @NotNull
    private static Collection<VirtualFile> suffixMatches(@NotNull Project project,
                                                         @NotNull String path,
                                                         @NotNull String basename,
                                                         @NotNull GlobalSearchScope scope) {
        List<VirtualFile> suffixedVirtualFiles = new ArrayList<>();
        Collection<VirtualFile> projectFilesWithBaseName = FilenameIndex.getVirtualFilesByName(basename, scope);

        for (VirtualFile projectFileWithBaseName : projectFilesWithBaseName) {
            String virtualFilePath = projectFileWithBaseName.getPath();

            if (virtualFilePath.endsWith(path)) {
                suffixedVirtualFiles.add(projectFileWithBaseName);
            }
        }

        return suffixedVirtualFiles;
    }
}
