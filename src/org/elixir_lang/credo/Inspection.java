package org.elixir_lang.credo;

import com.google.common.base.Charsets;
import com.intellij.analysis.AnalysisScope;
import com.intellij.codeInsight.daemon.impl.AnnotationHolderImpl;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInspection.*;
import com.intellij.codeInspection.reference.*;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.ProcessOutput;
import com.intellij.execution.util.ExecUtil;
import com.intellij.lang.ExternalLanguageAnnotators;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationSession;
import com.intellij.lang.annotation.ExternalAnnotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.containers.ContainerUtil;
import gnu.trove.THashMap;
import gnu.trove.THashSet;
import org.elixir_lang.ElixirLanguage;
import org.elixir_lang.jps.builder.ParametersList;
import org.elixir_lang.mix.runner.MixRunningStateUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Paths;
import java.util.*;

import static org.elixir_lang.credo.Annotator.lineListToIssueList;

public class Inspection extends GlobalInspectionTool {
    public static final String SHORT_NAME = "Credo";

    private static void put(@NotNull Map<String, Set<String>> pathSetByWorkingDirectory,
                            @NotNull Set<String> workingDirectorySet) {
        for (String workingDirectory : workingDirectorySet) {
            put(pathSetByWorkingDirectory, workingDirectory, workingDirectory);
        }
    }

    private static void put(@NotNull Map<String, Set<String>> pathSetByWorkingDirectory,
                            @NotNull Set<String> workingDirectorySet,
                            @NotNull String path) {
        for (String workingDirectory : workingDirectorySet) {
            put(pathSetByWorkingDirectory, workingDirectory, path);
        }
    }

    private static void put(@NotNull Map<String, Set<String>> pathSetByWorkingDirectory,
                            @NotNull String workingDirectory,
                            @NotNull String path) {
        pathSetByWorkingDirectory
                .computeIfAbsent(workingDirectory, key -> new THashSet<>())
                .add(path);
    }

    @NotNull
    private static GeneralCommandLine generalCommandLine(@NotNull GeneralCommandLine workingDirectoryGeneralCommandLine,
                                                         @NotNull Project project,
                                                         @NotNull ParametersList mixParametersList) {
        return MixRunningStateUtil.commandLine(
                workingDirectoryGeneralCommandLine,
                project,
                new ParametersList(),
                mixParametersList
        );
    }

    private static List<Annotator.Issue> runInspection(@NotNull Project project,
                                                       @NotNull String workingDirectory,
                                                       @NotNull Set<String> pathSet) {
        List<Annotator.Issue> issueList;

        try {
            ProcessOutput processOutput = ExecUtil.execAndGetOutput(
                    generalCommandLine(workingDirectory, project, pathSet)
            );

            issueList = lineListToIssueList(processOutput.getStdoutLines());
        } catch (ExecutionException executionException) {
            issueList = Collections.emptyList();
        }

        return issueList;
    }

    @NotNull
    private static GeneralCommandLine generalCommandLine(@NotNull String workingDirectory,
                                                         @NotNull Project project,
                                                         @NotNull ParametersList mixParametersList) {
        GeneralCommandLine workingDirectoryGeneralCommandLine = new GeneralCommandLine().withCharset(Charsets.UTF_8);
        workingDirectoryGeneralCommandLine.withWorkDirectory(workingDirectory);

        return generalCommandLine(
                workingDirectoryGeneralCommandLine,
                project,
                mixParametersList
        );
    }

    @NotNull
    private static GeneralCommandLine generalCommandLine(@NotNull String workingDirectory,
                                                         @NotNull Project project,
                                                         @NotNull Set<String> pathSet) {
        return generalCommandLine(workingDirectory, project, mixParameterList(pathSet));
    }

    @NotNull
    private static ParametersList mixParametersList() {
        ParametersList parametersList = new ParametersList();
        parametersList.add("credo");

        return parametersList;
    }

    @NotNull
    private static ParametersList mixParameterList(@NotNull Set<String> pathSet) {
        ParametersList parametersList = mixParametersList();
        parametersList.add("--format");
        parametersList.add("flycheck");

        for (String path : pathSet) {
            parametersList.add(path);
        }

        return parametersList;
    }

    @NotNull
    private static Set<String> workingDirectorySet(@NotNull Module module) {
        Set<String> workingDirectorySet = null;

        for (VirtualFile virtualFile : ModuleRootManager.getInstance(module).getContentRoots()) {
            if (virtualFile.findChild("mix.exs") != null) {
                if (workingDirectorySet == null) {
                    workingDirectorySet = new THashSet<>();
                }

                workingDirectorySet.add(virtualFile.getPath());
            }
        }

        if (workingDirectorySet == null) {
            workingDirectorySet = workingDirectorySet(module.getProject());
        }

        return workingDirectorySet;
    }

    @NotNull
    private static Set<String> workingDirectorySet(@NotNull Project project) {
        return Collections.singleton(project.getBasePath());
    }

    @NotNull
    private static Annotator annotator() {
        List<ExternalAnnotator> externalAnnotatorList = ExternalLanguageAnnotators.INSTANCE.allForLanguage(ElixirLanguage.INSTANCE);
        Annotator annotator = null;

        for (ExternalAnnotator element : externalAnnotatorList) {
            if (element instanceof Annotator) {
                annotator = (Annotator) element;
                break;
            }
        }

        //noinspection ConstantConditions
        return annotator;
    }

    // See ExternalAnnotatorInspectionVisitor#toLocalQuickFixes
    @NotNull
    private static ProblemDescriptor[] convertToProblemDescriptors(@NotNull final List<Annotation> annotations,
                                                                   @NotNull final PsiFile file) {
        if (annotations.isEmpty()) {
            return ProblemDescriptor.EMPTY_ARRAY;
        }

        List<ProblemDescriptor> problems = ContainerUtil.newArrayListWithCapacity(annotations.size());
        IdentityHashMap<IntentionAction, LocalQuickFix> quickFixMappingCache = ContainerUtil.newIdentityHashMap();
        for (Annotation annotation : annotations) {
            if (annotation.getSeverity() == HighlightSeverity.INFORMATION ||
                    annotation.getStartOffset() == annotation.getEndOffset() && !annotation.isAfterEndOfLine()) {
                continue;
            }

            final PsiElement startElement;
            final PsiElement endElement;
            if (annotation.getStartOffset() == annotation.getEndOffset() && annotation.isAfterEndOfLine()) {
                startElement = endElement = file.findElementAt(annotation.getEndOffset() - 1);
            } else {
                startElement = file.findElementAt(annotation.getStartOffset());
                endElement = file.findElementAt(annotation.getEndOffset() - 1);
            }
            if (startElement == null || endElement == null) {
                continue;
            }

            LocalQuickFix[] quickFixes = toLocalQuickFixes(annotation.getQuickFixes(), quickFixMappingCache);
            ProblemDescriptor descriptor = new ProblemDescriptorBase(startElement,
                    endElement,
                    annotation.getMessage(),
                    quickFixes,
                    ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                    annotation.isAfterEndOfLine(),
                    null,
                    true,
                    false);
            problems.add(descriptor);
        }

        return problems.toArray(new ProblemDescriptor[problems.size()]);
    }

    // See ExternalAnnotatorInspectionVisitor#toLocalQuickFixes
    @NotNull
    private static LocalQuickFix[] toLocalQuickFixes(
            @Nullable List<Annotation.QuickFixInfo> fixInfos,
            @NotNull IdentityHashMap<IntentionAction, LocalQuickFix> quickFixMappingCache
    ) {
        LocalQuickFix[] localQuickFixes;

        if (fixInfos == null || fixInfos.isEmpty()) {
            localQuickFixes = LocalQuickFix.EMPTY_ARRAY;
        } else {
            localQuickFixes = new LocalQuickFix[fixInfos.size()];
            int i = 0;

            for (Annotation.QuickFixInfo fixInfo : fixInfos) {
                IntentionAction intentionAction = fixInfo.quickFix;
                final LocalQuickFix fix;

                if (intentionAction instanceof LocalQuickFix) {
                    fix = (LocalQuickFix) intentionAction;
                } else {
                    LocalQuickFix lqf = quickFixMappingCache.get(intentionAction);

                    if (lqf == null) {
                        lqf = new ExternalAnnotatorInspectionVisitor.LocalQuickFixBackedByIntentionAction(intentionAction);
                        quickFixMappingCache.put(intentionAction, lqf);
                    }

                    fix = lqf;
                }

                localQuickFixes[i++] = fix;
            }
        }

        return localQuickFixes;
    }

    private static void addProblemElement(@NotNull ProblemDescriptionsProcessor problemDescriptionsProcessor,
                                          @NotNull LocalFileSystem localFileSystem,
                                          @NotNull PsiManager psiManager,
                                          @NotNull RefManager refManager,
                                          @NotNull ExternalAnnotator<PsiFile, List<Annotator.Issue>> externalAnnotator,
                                          @NotNull String fullPath,
                                          @NotNull List<Annotator.Issue> issueList) {
        VirtualFile virtualFile = localFileSystem.findFileByPath(fullPath);

        addProblemElement(
                problemDescriptionsProcessor,
                psiManager,
                refManager,
                externalAnnotator,
                virtualFile,
                issueList
        );
    }

    private static void addProblemElement(@NotNull ProblemDescriptionsProcessor problemDescriptionsProcessor,
                                          @NotNull PsiManager psiManager,
                                          @NotNull RefManager refManager,
                                          @NotNull ExternalAnnotator<PsiFile, List<Annotator.Issue>> externalAnnotator,
                                          @Nullable VirtualFile virtualFile,
                                          @NotNull List<Annotator.Issue> issueList) {
        if (virtualFile != null) {
            PsiFile psiFile = psiManager.findFile(virtualFile);

            addProblemElement(problemDescriptionsProcessor, refManager, externalAnnotator, psiFile, issueList);
        }
    }

    private static void addProblemElement(@NotNull ProblemDescriptionsProcessor problemDescriptionsProcessor,
                                          @NotNull RefManager refManager,
                                          @NotNull ExternalAnnotator<PsiFile, List<Annotator.Issue>> externalAnnotator,
                                          @Nullable PsiFile psiFile,
                                          @NotNull List<Annotator.Issue> issueList) {
        if (psiFile != null) {
            ProblemDescriptor[] problemDescriptors = problemDescriptors(externalAnnotator, psiFile, issueList);
            RefElement refElement = refManager.getReference(psiFile);
            problemDescriptionsProcessor.addProblemElement(refElement, problemDescriptors);
        }
    }

    private static ProblemDescriptor[] problemDescriptors(
            @NotNull ExternalAnnotator<PsiFile, List<Annotator.Issue>> externalAnnotator,
            @NotNull PsiFile psiFile,
            @NotNull List<Annotator.Issue> issueList
    ) {
        return ReadAction.compute(() -> {
            AnnotationSession annotationSession = new AnnotationSession(psiFile);
            AnnotationHolderImpl annotationHolder = new AnnotationHolderImpl(annotationSession, true);
            externalAnnotator.apply(psiFile, issueList, annotationHolder);

            return convertToProblemDescriptors(annotationHolder, psiFile);
        });
    }

    @NotNull
    private Map<String, Set<String>> pathSetByWorkingDirectory(@NotNull GlobalInspectionContext globalContext) {
        Map<String, Set<String>> pathSetByWorkingDirectory = new THashMap<>();

        globalContext.getRefManager().iterate(new RefVisitor() {
            @Override
            public void visitElement(@NotNull RefEntity refEntity) {
                if (globalContext.shouldCheck(refEntity, Inspection.this)) {
                    if (refEntity instanceof RefModule) {
                        RefModule refModule = (RefModule) refEntity;

                        put(pathSetByWorkingDirectory, workingDirectorySet(refModule.getModule()));
                    } else if (refEntity instanceof RefProject) {
                        String workingDirectory = globalContext.getProject().getBasePath();
                        put(pathSetByWorkingDirectory, Collections.singleton(workingDirectory));
                    } else if (refEntity instanceof RefFile) {
                        RefFile refFile = (RefFile) refEntity;
                        PsiFile psiFile = refFile.getElement();

                        Set<String> workingDirectorySet;
                        Module module = ModuleUtilCore.findModuleForPsiElement(refFile.getElement());

                        if (module != null) {
                            workingDirectorySet = workingDirectorySet(module);
                        } else {
                            workingDirectorySet = workingDirectorySet(psiFile.getProject());
                        }

                        put(pathSetByWorkingDirectory, workingDirectorySet, psiFile.getVirtualFile().getPath());
                    } else if (refEntity instanceof RefDirectory) {
                        RefDirectory refDirectory = (RefDirectory) refEntity;
                        RefModule refModule = refDirectory.getModule();
                        Set<String> workingDirectorySet;

                        if (refModule != null) {
                            workingDirectorySet = workingDirectorySet(refModule.getModule());
                        } else {
                            workingDirectorySet = workingDirectorySet(refDirectory.getElement().getProject());
                        }

                        put(
                                pathSetByWorkingDirectory,
                                workingDirectorySet,
                                refDirectory.getElement().getContainingFile().getVirtualFile().getPath()
                        );
                    }
                }
            }
        });

        return pathSetByWorkingDirectory;
    }

    @Override
    public void runInspection(@NotNull AnalysisScope scope,
                              @NotNull InspectionManager manager,
                              @NotNull GlobalInspectionContext globalContext,
                              @NotNull ProblemDescriptionsProcessor problemDescriptionsProcessor) {
        Map<String, Set<String>> pathSetByWorkingDirectory = pathSetByWorkingDirectory(globalContext);
        Map<String, List<Annotator.Issue>> issueListByFullPath = new THashMap<>();

        for (Map.Entry<String, Set<String>> entry : pathSetByWorkingDirectory.entrySet()) {
            String workingDirectory = entry.getKey();

            List<Annotator.Issue> issueList = runInspection(manager.getProject(), workingDirectory, entry.getValue());

            for (Annotator.Issue issue : issueList) {
                String fullPath = Paths.get(workingDirectory, issue.path).toString();
                issueListByFullPath.computeIfAbsent(fullPath, key -> new ArrayList<>()).add(issue);
            }
        }

        LocalFileSystem localFileSystem = LocalFileSystem.getInstance();
        PsiManager psiManager = PsiManager.getInstance(globalContext.getProject());
        RefManager refManager = globalContext.getRefManager();
        ExternalAnnotator<PsiFile, List<Annotator.Issue>> externalAnnotator = annotator();

        for (Map.Entry<String, List<Annotator.Issue>> entry : issueListByFullPath.entrySet()) {
            addProblemElement(
                    problemDescriptionsProcessor,
                    localFileSystem,
                    psiManager,
                    refManager,
                    externalAnnotator,
                    entry.getKey(),
                    entry.getValue()
            );
        }
    }

    @Override
    public boolean isGraphNeeded() {
        return true;
    }

    @Override
    public boolean worksInBatchModeOnly() {
        return true;
    }
}
