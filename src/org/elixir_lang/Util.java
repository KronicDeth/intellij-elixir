package org.elixir_lang;

import com.ericsson.otp.erlang.OtpErlangAtom;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangTuple;
import com.google.common.collect.AbstractIterator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.indexing.FileBasedIndex;
import org.elixir_lang.psi.*;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Iterator;

public class Util {
    /**
     * Iterator of calls in all Elixir file in project.
     *
     * @param project The project whose Elixir files to search for calls.
     * @return Iterator of calls in all Elixir file in project.
     */
    public static Iterator<Quotable> callIterator(Project project) {
        final Iterator<ElixirFile> elixirFileIterator = elixirFileIterator(project);

        return new AbstractIterator<Quotable>() {
            private Iterator<Quotable> quotableIterator = null;

            @Override
            protected Quotable computeNext() {
                while (true) {
                    if (quotableIterator == null || !quotableIterator.hasNext()) {
                        if (elixirFileIterator.hasNext()) {
                            nextQuotableIterator();
                        } else {
                            return endOfData();
                        }
                    } else {
                        return quotableIterator.next();
                    }
                }
            }

            /**
             * Private Instance Methods
             */

            private void nextQuotableIterator() {
                ElixirFile elixirFile = elixirFileIterator.next();

                Collection<Quotable> children = PsiTreeUtil.findChildrenOfAnyType(
                        elixirFile,
                        AtUnqualifiedNoParenthesesCall.class,
                        Call.class,
                        DotCall.class,
                        QualifiedNoArgumentsCall.class,
                        QualifiedNoParenthesesCall.class,
                        QualifiedParenthesesCall.class,
                        UnqualifiedNoArgumentsCall.class,
                        UnqualifiedNoParenthesesCall.class,
                        UnqualifiedParenthesesCall.class
                );

                quotableIterator = children.iterator();
            }

        };
    }

    /**
     * Iterator of calls with the given, unqualified name
     *
     * @param project The project whose Elixir files to search for calls
     * @param name name of function called without Module qualifier
     */
    public static Iterator<Quotable> callIterator(Project project, final String name) {
        final Iterator<Quotable> callIterator = callIterator(project);

        return new AbstractIterator<Quotable>() {
            @Override
            protected Quotable computeNext() {
                while (callIterator.hasNext()) {
                    Quotable call = callIterator.next();

                    OtpErlangTuple quotedCall = (OtpErlangTuple) call.quote();
                    OtpErlangObject quotedIdentifier = quotedCall.elementAt(0);

                    // Can't handle other forms yet
                    assert quotedIdentifier instanceof OtpErlangAtom;

                    OtpErlangAtom identifierAtom = (OtpErlangAtom) quotedIdentifier;

                    if (identifierAtom.atomValue().equals(name)) {
                        return call;
                    }
                }

                return endOfData();
            }
        };
    }

    /**
     * Iterator of ElixirFiles in project.
     *
     * @param project
     * @return
     */
    public static Iterator<ElixirFile> elixirFileIterator(final Project project) {
        Collection<VirtualFile> virtualFileCollection = FileBasedIndex.getInstance().getContainingFiles(
                FileTypeIndex.NAME,
                ElixirFileType.INSTANCE,
                GlobalSearchScope.allScope(project)
        );
        final Iterator<VirtualFile> virtualFileIterator = virtualFileCollection.iterator();

        return new AbstractIterator<ElixirFile>() {
            @Override
            protected ElixirFile computeNext() {
                while (virtualFileIterator.hasNext()) {
                    VirtualFile virtualFile = virtualFileIterator.next();
                    ElixirFile elixirFile = (ElixirFile) PsiManager.getInstance(project).findFile(virtualFile);

                    if (elixirFile != null) {
                        return elixirFile;
                    }
                }

                return endOfData();
            }
        };
    }

    @Nullable
    public static ModuleDefinition findModuleDefinition(Project project, String fullyQualifiedName) {
        ModuleDefinition found = null;

        for (Iterator<ModuleDefinition> moduleDefinitionIterator = moduleDefinitionIterator(project); moduleDefinitionIterator.hasNext();) {
            ModuleDefinition moduleDefinition = moduleDefinitionIterator.next();
            String moduleFullyQualifiedName = moduleDefinition.fullyQualifiedName();

            if (moduleFullyQualifiedName.equals(fullyQualifiedName)) {
                found = moduleDefinition;

                break;
            }
        }

        return found;
    }

    public static Iterator<ModuleDefinition> moduleDefinitionIterator(Project project) {
        final Iterator<Quotable> defmoduleIterator = callIterator(project, "defmodule");

        return new AbstractIterator<ModuleDefinition>() {
            @Override
            protected ModuleDefinition computeNext() {
                while (defmoduleIterator.hasNext()) {
                    Quotable defmodule = defmoduleIterator.next();

                    return new ModuleDefinition(defmodule);
                }

                return endOfData();
            }
        };
    }

}
