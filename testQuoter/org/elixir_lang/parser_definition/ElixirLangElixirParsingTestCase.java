package org.elixir_lang.parser_definition;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.io.FileUtilRt;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class ElixirLangElixirParsingTestCase extends ParsingTestCase {
    /*
     * Test Methods
     */

    public void testEex() {
        assertParsed("lib/eex/lib/eex.ex");
    }

    public void testEexCompiler() {
        assertParsed("lib/eex/lib/eex/compiler.ex");
    }

    public void testEexEngine() {
        assertParsed("lib/eex/lib/eex/engine.ex");
    }

    public void testEexSmartEngine() {
        assertParsed("lib/eex/lib/eex/smart_engine.ex");
    }

    public void testEexTokenizer() {
        assertParsed("lib/eex/lib/eex/tokenizer.ex");
    }

    public void testAccess() {
        assertParsed("lib/elixir/lib/access.ex");
    }

    public void testAgent() {
        assertParsed("lib/elixir/lib/agent.ex");
    }

    public void testAgentServer() {
        assertParsed("lib/elixir/lib/agent/server.ex");
    }

    public void testApplication() {
        assertParsed("lib/elixir/lib/application.ex");
    }

    public void testAtom() {
        assertParsed("lib/elixir/lib/atom.ex");
    }

    public void testBase() {
        assertParsed("lib/elixir/lib/base.ex");
    }

    public void testBehaviour() {
        assertParsed("lib/elixir/lib/behaviour.ex");
    }

    public void testBitwise() {
        assertParsed("lib/elixir/lib/bitwise.ex");
    }

    public void testCode() {
        assertParsed("lib/elixir/lib/code.ex");
    }

    public void testCollectable() {
        assertParsed("lib/elixir/lib/collectable.ex");
    }

    public void testDict() {
        assertParsed("lib/elixir/lib/dict.ex");
    }

    public void testEnum() {
        assertParsed("lib/elixir/lib/enum.ex");
    }

    public void testException() {
        assertParsed("lib/elixir/lib/exception.ex");
    }

    public void testFile() {
        assertParsed("lib/elixir/lib/file.ex");
    }

    public void testFileStat() {
        assertParsed("lib/elixir/lib/file/stat.ex");
    }

    public void testFileStream() {
        assertParsed("lib/elixir/lib/file/stream.ex");
    }

    public void testFloat() {
        assertParsed("lib/elixir/lib/float.ex");
    }

    public void testGenEvent() {
        assertParsed("lib/elixir/lib/gen_event.ex");
    }

    public void testGenEventStream() {
        assertParsed("lib/elixir/lib/gen_event/stream.ex");
    }

    public void testHashDict() {
        assertParsed("lib/elixir/lib/hash_dict.ex");
    }

    public void testHashSet() {
        assertParsed("lib/elixir/lib/hash_set.ex");
    }

    public void testInspect() {
        assertParsed("lib/elixir/lib/inspect.ex");
    }

    public void testInspectAlgebra() {
        assertParsed("lib/elixir/lib/inspect/algebra.ex");
    }

    public void testInteger() {
        assertParsed("lib/elixir/lib/integer.ex");
    }

    public void testIo() {
        assertParsed("lib/elixir/lib/io.ex");
    }

    public void testIoAnsi() {
        assertParsed("lib/elixir/lib/io/ansi.ex");
    }

    public void testIoAnsiDocs() {
        assertParsed("lib/elixir/lib/io/ansi/docs.ex");
    }

    public void testIoStream() {
        assertParsed("lib/elixir/lib/io/stream.ex");
    }

    public void testKernelCli() {
        assertParsed("lib/elixir/lib/kernel/cli.ex");
    }

    public void testKernelErrorHandler() {
        assertParsed("lib/elixir/lib/kernel/error_handler.ex");
    }

    public void testKernelLexicalTracker() {
        assertParsed("lib/elixir/lib/kernel/lexical_tracker.ex");
    }

    public void testKernelParallelCompiler() {
        assertParsed("lib/elixir/lib/kernel/parallel_compiler.ex");
    }

    public void testKernelParallelRequire() {
        assertParsed("lib/elixir/lib/kernel/parallel_require.ex");
    }

    public void testKernelSpecialForms() {
        assertParsed("lib/elixir/lib/kernel/special_forms.ex");
    }

    public void testKernelTypespec() {
        assertParsed("lib/elixir/lib/kernel/typespec.ex");
    }

    public void testKeyword() {
        assertParsed("lib/elixir/lib/keyword.ex");
    }

    public void testList() {
        assertParsed("lib/elixir/lib/list.ex");
    }

    public void testListChars() {
        assertParsed("lib/elixir/lib/list/chars.ex");
    }

    public void testMacro() {
        assertParsed("lib/elixir/lib/macro.ex");
    }

    public void testMacroEnv() {
        assertParsed("lib/elixir/lib/macro/env.ex");
    }

    public void testMap() {
        assertParsed("lib/elixir/lib/map.ex");
    }

    public void testMapSet() {
        assertParsed("lib/elixir/lib/map_set.ex");
    }

    public void testModule() {
        assertParsed("lib/elixir/lib/module.ex");
    }

    public void testModuleLocalsTracker() {
        assertParsed("lib/elixir/lib/module/locals_tracker.ex");
    }

    public void testNode() {
        assertParsed("lib/elixir/lib/node.ex");
    }

    public void testOptionParser() {
        assertParsed("lib/elixir/lib/option_parser.ex");
    }

    public void testPath() {
        assertParsed("lib/elixir/lib/path.ex");
    }

    public void testPort() {
        assertParsed("lib/elixir/lib/port.ex");
    }

    public void testProcess() {
        assertParsed("lib/elixir/lib/process.ex");
    }

    public void testProtocol() {
        assertParsed("lib/elixir/lib/protocol.ex");
    }

    public void testRange() {
        assertParsed("lib/elixir/lib/range.ex");
    }

    public void testRecord() {
        assertParsed("lib/elixir/lib/record.ex");
    }

    public void testRecordExtractor() {
        assertParsed("lib/elixir/lib/record/extractor.ex");
    }

    public void testRegex() {
        assertParsed("lib/elixir/lib/regex.ex");
    }

    public void testSet() {
        assertParsed("lib/elixir/lib/set.ex");
    }

    public void testStream() {
        assertParsed("lib/elixir/lib/stream.ex");
    }

    public void testStreamReducers() {
        assertParsed("lib/elixir/lib/stream/reducers.ex");
    }

    public void testString() {
        assertParsed("lib/elixir/lib/string.ex");
    }

    public void testStringChars() {
        assertParsed("lib/elixir/lib/string/chars.ex");
    }

    public void testStringIo() {
        assertParsed("lib/elixir/lib/string_io.ex");
    }

    public void testSupervisor() {
        assertParsed("lib/elixir/lib/supervisor.ex");
    }

    public void testSupervisorDefault() {
        assertParsed("lib/elixir/lib/supervisor/default.ex");
    }

    public void testSupervisorSpec() {
        assertParsed("lib/elixir/lib/supervisor/spec.ex");
    }

    public void testSystem() {
        assertParsed("lib/elixir/lib/system.ex");
    }

    public void testTask() {
        assertParsed("lib/elixir/lib/task.ex");
    }

    public void testTaskSupervised() {
        assertParsed("lib/elixir/lib/task/supervised.ex");
    }

    public void testTaskSupervisor() {
        assertParsed("lib/elixir/lib/task/supervisor.ex");
    }

    public void testTuple() {
        assertParsed("lib/elixir/lib/tuple.ex");
    }

    public void testUri() {
        assertParsed("lib/elixir/lib/uri.ex");
    }

    public void testVersion() {
        assertParsed("lib/elixir/lib/version.ex");
    }

    public void testCompileSample() {
        assertParsed("lib/elixir/test/elixir/fixtures/compile_sample.ex");
    }

    public void testUnicodeUnicode() {
        assertParsed("lib/elixir/unicode/unicode.ex");
    }

    public void testExUnit() {
        assertParsed("lib/ex_unit/lib/ex_unit.ex");
    }

    public void testExUnitAssertions() {
        assertParsed("lib/ex_unit/lib/ex_unit/assertions.ex");
    }

    public void testExUnitCallbacks() {
        assertParsed("lib/ex_unit/lib/ex_unit/callbacks.ex");
    }

    public void testExUnitCaptureIo() {
        assertParsed("lib/ex_unit/lib/ex_unit/capture_io.ex");
    }

    public void testExUnitCaptureLog() {
        assertParsed("lib/ex_unit/lib/ex_unit/capture_log.ex");
    }

    public void testExUnitCase() {
        assertParsed("lib/ex_unit/lib/ex_unit/case.ex");
    }

    public void testExUnitCaseTemplate() {
        assertParsed("lib/ex_unit/lib/ex_unit/case_template.ex");
    }

    public void testExUnitCliFormatter() {
        assertParsed("lib/ex_unit/lib/ex_unit/cli_formatter.ex");
    }

    public void testExUnitEventManager() {
        assertParsed("lib/ex_unit/lib/ex_unit/event_manager.ex");
    }

    public void testExUnitFilters() {
        assertParsed("lib/ex_unit/lib/ex_unit/filters.ex");
    }

    public void testExUnitFormatter() {
        assertParsed("lib/ex_unit/lib/ex_unit/formatter.ex");
    }

    public void testExUnitOnExitHandler() {
        assertParsed("lib/ex_unit/lib/ex_unit/on_exit_handler.ex");
    }

    public void testExUnitRunner() {
        assertParsed("lib/ex_unit/lib/ex_unit/runner.ex");
    }

    public void testExUnitRunnerStats() {
        assertParsed("lib/ex_unit/lib/ex_unit/runner_stats.ex");
    }

    public void testExUnitServer() {
        assertParsed("lib/ex_unit/lib/ex_unit/server.ex");
    }

    public void testIex() {
        assertParsed("lib/iex/lib/iex.ex");
    }

    public void testIexApp() {
        assertParsed("lib/iex/lib/iex/app.ex");
    }

    public void testIexCli() {
        assertParsed("lib/iex/lib/iex/cli.ex");
    }

    public void testIexConfig() {
        assertParsed("lib/iex/lib/iex/config.ex");
    }

    public void testIexEvaluator() {
        assertParsed("lib/iex/lib/iex/evaluator.ex");
    }

    public void testIexHelpers() {
        assertParsed("lib/iex/lib/iex/helpers.ex");
    }

    public void testIexHistory() {
        assertParsed("lib/iex/lib/iex/history.ex");
    }

    public void testIexServer() {
        assertParsed("lib/iex/lib/iex/server.ex");
    }

    public void testLogger() {
        assertParsed("lib/logger/lib/logger.ex");
    }

    public void testLoggerApp() {
        assertParsed("lib/logger/lib/logger/app.ex");
    }

    public void testLoggerBackendsConsole() {
        assertParsed("lib/logger/lib/logger/backends/console.ex");
    }

    public void testLoggerConfig() {
        assertParsed("lib/logger/lib/logger/config.ex");
    }

    public void testLoggerFormatter() {
        assertParsed("lib/logger/lib/logger/formatter.ex");
    }

    public void testLoggerTranslator() {
        assertParsed("lib/logger/lib/logger/translator.ex");
    }

    public void testLoggerUtils() {
        assertParsed("lib/logger/lib/logger/utils.ex");
    }

    public void testLoggerWatcher() {
        assertParsed("lib/logger/lib/logger/watcher.ex");
    }

    public void testMix() {
        assertParsed("lib/mix/lib/mix.ex");
    }

    public void testMixCli() {
        assertParsed("lib/mix/lib/mix/cli.ex");
    }

    public void testMixCompilersErlang() {
        assertParsed("lib/mix/lib/mix/compilers/erlang.ex");
    }

    public void testMixConfig() {
        assertParsed("lib/mix/lib/mix/config.ex");
    }

    public void testMixDep() {
        assertParsed("lib/mix/lib/mix/dep.ex");
    }

    public void testMixDepConverger() {
        assertParsed("lib/mix/lib/mix/dep/converger.ex");
    }

    public void testMixDepFetcher() {
        assertParsed("lib/mix/lib/mix/dep/fetcher.ex");
    }

    public void testMixDepLock() {
        assertParsed("lib/mix/lib/mix/dep/lock.ex");
    }

    public void testMixDepUmbrella() {
        assertParsed("lib/mix/lib/mix/dep/umbrella.ex");
    }

    public void testMixExceptions() {
        assertParsed("lib/mix/lib/mix/exceptions.ex");
    }

    public void testMixGenerator() {
        assertParsed("lib/mix/lib/mix/generator.ex");
    }

    public void testMixHex() {
        assertParsed("lib/mix/lib/mix/hex.ex");
    }

    public void testMixLocal() {
        assertParsed("lib/mix/lib/mix/local.ex");
    }

    public void testMixProject() {
        assertParsed("lib/mix/lib/mix/project.ex");
    }

    public void testMixRebar() {
        assertParsed("lib/mix/lib/mix/rebar.ex");
    }

    public void testMixPublicKey() {
        assertParsed("lib/mix/lib/mix/public_key.ex");
    }

    public void testMixRemoteConverger() {
        assertParsed("lib/mix/lib/mix/remote_converger.ex");
    }

    public void testMixScm() {
        assertParsed("lib/mix/lib/mix/scm.ex");
    }

    public void testMixScmGit() {
        assertParsed("lib/mix/lib/mix/scm/git.ex");
    }

    public void testMixScmPath() {
        assertParsed("lib/mix/lib/mix/scm/path.ex");
    }

    public void testMixShell() {
        assertParsed("lib/mix/lib/mix/shell.ex");
    }

    public void testMixShellIo() {
        assertParsed("lib/mix/lib/mix/shell/io.ex");
    }

    public void testMixShellProcess() {
        assertParsed("lib/mix/lib/mix/shell/process.ex");
    }

    public void testMixState() {
        assertParsed("lib/mix/lib/mix/state.ex");
    }

    public void testMixTask() {
        assertParsed("lib/mix/lib/mix/task.ex");
    }

    public void testMixTasksAppStart() {
        assertParsed("lib/mix/lib/mix/tasks/app.start.ex");
    }

    public void testMixTasksArchiveBuild() {
        assertParsed("lib/mix/lib/mix/tasks/archive.build.ex");
    }

    public void testMixTasksArchive() {
        assertParsed("lib/mix/lib/mix/tasks/archive.ex");
    }

    public void testMixTasksArchiveInstall() {
        assertParsed("lib/mix/lib/mix/tasks/archive.install.ex");
    }

    public void testMixTasksArchiveUninstall() {
        assertParsed("lib/mix/lib/mix/tasks/archive.uninstall.ex");
    }

    public void testMixTasksClean() {
        assertParsed("lib/mix/lib/mix/tasks/clean.ex");
    }

    public void testMixTasksCmd() {
        assertParsed("lib/mix/lib/mix/tasks/cmd.ex");
    }

    public void testMixTasksCompileAll() {
        assertParsed("lib/mix/lib/mix/tasks/compile.all.ex");
    }

    public void testMixTasksCompileApp() {
        assertParsed("lib/mix/lib/mix/tasks/compile.app.ex");
    }

    public void testMixTasksCompileElixir() {
        assertParsed("lib/mix/lib/mix/tasks/compile.elixir.ex");
    }

    public void testMixTasksCompileErlang() {
        assertParsed("lib/mix/lib/mix/tasks/compile.erlang.ex");
    }

    public void testMixTasksCompile() {
        assertParsed("lib/mix/lib/mix/tasks/compile.ex");
    }

    public void testMixTasksCompileLeex() {
        assertParsed("lib/mix/lib/mix/tasks/compile.leex.ex");
    }

    public void testMixTasksCompileYecc() {
        assertParsed("lib/mix/lib/mix/tasks/compile.yecc.ex");
    }

    public void testMixTasksDepsCompile() {
        assertParsed("lib/mix/lib/mix/tasks/deps.compile.ex");
    }

    public void testMixTasksDeps() {
        assertParsed("lib/mix/lib/mix/tasks/deps.ex");
    }

    public void testMixTasksDepsGet() {
        assertParsed("lib/mix/lib/mix/tasks/deps.get.ex");
    }

    public void testMixTasksDepsUnlock() {
        assertParsed("lib/mix/lib/mix/tasks/deps.unlock.ex");
    }

    public void testMixTasksDepsUpdate() {
        assertParsed("lib/mix/lib/mix/tasks/deps.update.ex");
    }

    public void testMixTasksDo() {
        assertParsed("lib/mix/lib/mix/tasks/do.ex");
    }

    public void testMixTasksEscriptBuild() {
        assertParsed("lib/mix/lib/mix/tasks/escript.build.ex");
    }

    public void testMixTasksHelp() {
        assertParsed("lib/mix/lib/mix/tasks/help.ex");
    }

    public void testMixTasksIex() {
        assertParsed("lib/mix/lib/mix/tasks/iex.ex");
    }

    public void testMixTasksLoadconfig() {
        assertParsed("lib/mix/lib/mix/tasks/loadconfig.ex");
    }

    public void testMixTasksLoadpaths() {
        assertParsed("lib/mix/lib/mix/tasks/loadpaths.ex");
    }

    public void testMixTasksLocal() {
        assertParsed("lib/mix/lib/mix/tasks/local.ex");
    }

    public void testMixTasksLocalHex() {
        assertParsed("lib/mix/lib/mix/tasks/local.hex.ex");
    }

    public void testMixTasksLocalPublicKeys() {
        assertParsed("lib/mix/lib/mix/tasks/local.public_keys.ex");
    }

    public void testMixTasksLocalRebar() {
        assertParsed("lib/mix/lib/mix/tasks/local.rebar.ex");
    }

    public void testMixTasksNew() {
        assertParsed("lib/mix/lib/mix/tasks/new.ex");
    }

    public void testMixTasksProfileFprof() {
        assertParsed("lib/mix/lib/mix/tasks/profile.fprof.ex");
    }

    public void testMixTasksRun() {
        assertParsed("lib/mix/lib/mix/tasks/run.ex");
    }

    public void testMixTasksTest() {
        assertParsed("lib/mix/lib/mix/tasks/test.ex");
    }

    public void testMixTasksServer() {
        assertParsed("lib/mix/lib/mix/tasks_server.ex");
    }

    public void testMixUtils() {
        assertParsed("lib/mix/lib/mix/utils.ex");
    }

    public void testDepsStatusCustomRawRepoLibRawRepo() {
        assertParsed("lib/mix/test/fixtures/deps_status/custom/raw_repo/lib/raw_repo.ex");
    }

    public void testNoMixfileLibA() {
        assertParsed("lib/mix/test/fixtures/no_mixfile/lib/a.ex");
    }

    public void testNoMixfileLibB() {
        assertParsed("lib/mix/test/fixtures/no_mixfile/lib/b.ex");
    }

    public void testUmbrellaDepDepsUmbrellaAppsBarLibBar() {
        assertParsed("lib/mix/test/fixtures/umbrella_dep/deps/umbrella/apps/bar/lib/bar.ex");
    }

    public void testUmbrellaDepDepsUmbrellaAppsFooLibFoo() {
        assertParsed("lib/mix/test/fixtures/umbrella_dep/deps/umbrella/apps/foo/lib/foo.ex");
    }

    /*
     * Protected Instance Methods
     */

    @Override
    @NotNull
    protected String getTestDataPath() {
        return System.getenv("ELIXIR_LANG_ELIXIR_PATH");
    }

    /*
     * Private Instance Methods
     */

    private void assertParsed(String relativePath) {
        File rootFile = new File(getTestDataPath());
        File absoluteFile = new File(rootFile, relativePath);

        assertParsed(absoluteFile);
    }

    private void assertParsed(File absoluteFile) {
        // inlines part of com.intellij.testFramework.ParsingTestCase#doTest(boolean)
        try {
            String text = FileUtil.loadFile(absoluteFile, "UTF-8", true).trim();

            String nameWithoutExtension = FileUtilRt.getNameWithoutExtension(absoluteFile.toString());
            myFile = createPsiFile(nameWithoutExtension, text);
            ensureParsed(myFile);
            toParseTreeText(myFile, skipSpaces(), includeRanges());
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }

        assertWithoutLocalError();
        assertQuotedCorrectly();
    }
}
