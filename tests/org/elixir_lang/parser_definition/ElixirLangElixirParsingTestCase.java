package org.elixir_lang.parser_definition;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.openapi.vfs.CharsetToolkit;
import org.elixir_lang.intellij_elixir.Quoter;
import org.jetbrains.annotations.NotNull;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.*;

public class ElixirLangElixirParsingTestCase extends ParsingTestCase {
    private enum Parse {
        ERROR("with local and remote error"),
        CORRECT("and quotes correctly");

        /*
         * Fields
         */

        private final String description;

        Parse(String description) {
            this.description = description;
        }

        public String toString() {
            return description;
        }
    }

    /*
     * Test Methods
     */

    public void testEex() {
        assertParsed("lib/eex/lib/eex.ex", Parse.CORRECT);
    }

    public void testEexCompiler() {
        assertParsed("lib/eex/lib/eex/compiler.ex", Parse.CORRECT);
    }

    public void testEexEngine() {
        assertParsed("lib/eex/lib/eex/engine.ex", Parse.CORRECT);
    }

    public void testEexSmartEngine() {
        assertParsed("lib/eex/lib/eex/smart_engine.ex", Parse.CORRECT);
    }

    public void testEexTokenizer() {
        assertParsed("lib/eex/lib/eex/tokenizer.ex", Parse.CORRECT);
    }

    public void testAccess() {
        assertParsed("lib/elixir/lib/access.ex", Parse.CORRECT);
    }

    public void testAgent() {
        assertParsed("lib/elixir/lib/agent.ex", Parse.CORRECT);
    }

    public void testAgentServer() {
        assertParsed("lib/elixir/lib/agent/server.ex", Parse.CORRECT);
    }

    public void testApplication() {
        assertParsed("lib/elixir/lib/application.ex", Parse.CORRECT);
    }

    public void testAtom() {
        assertParsed("lib/elixir/lib/atom.ex", Parse.CORRECT);
    }

    public void testBase() {
        assertParsed("lib/elixir/lib/base.ex", Parse.CORRECT);
    }

    public void testBehaviour() {
        assertParsed("lib/elixir/lib/behaviour.ex", Parse.CORRECT);
    }

    public void testBitwise() {
        assertParsed("lib/elixir/lib/bitwise.ex", Parse.CORRECT);
    }

    public void testCode() {
        assertParsed("lib/elixir/lib/code.ex", Parse.CORRECT);
    }

    public void testCollectable() {
        assertParsed("lib/elixir/lib/collectable.ex", Parse.CORRECT);
    }

    public void testDict() {
        assertParsed("lib/elixir/lib/dict.ex", Parse.CORRECT);
    }

    public void testEnum() {
        assertParsed("lib/elixir/lib/enum.ex", Parse.CORRECT);
    }

    public void testException() {
        assertParsed("lib/elixir/lib/exception.ex", Parse.CORRECT);
    }

    public void testFile() {
        assertParsed("lib/elixir/lib/file.ex", Parse.CORRECT);
    }

    public void testFileStat() {
        assertParsed("lib/elixir/lib/file/stat.ex", Parse.CORRECT);
    }

    public void testFileStream() {
        assertParsed("lib/elixir/lib/file/stream.ex", Parse.CORRECT);
    }

    public void testFloat() {
        assertParsed("lib/elixir/lib/float.ex", Parse.CORRECT);
    }

    public void testGenEvent() {
        assertParsed("lib/elixir/lib/gen_event.ex", Parse.CORRECT);
    }

    public void testGenEventStream() {
        assertParsed("lib/elixir/lib/gen_event/stream.ex", Parse.CORRECT);
    }

    public void testGenServer() {
        assertParsed("lib/elixir/lib/gen_server.ex", Parse.CORRECT);
    }

    public void testHashDict() {
        assertParsed("lib/elixir/lib/hash_dict.ex", Parse.CORRECT);
    }

    public void testHashSet() {
        assertParsed("lib/elixir/lib/hash_set.ex", Parse.CORRECT);
    }

    public void testInspect() {
        assertParsed("lib/elixir/lib/inspect.ex", Parse.CORRECT);
    }

    public void testInspectAlgebra() {
        assertParsed("lib/elixir/lib/inspect/algebra.ex", Parse.CORRECT);
    }

    public void testInteger() {
        assertParsed("lib/elixir/lib/integer.ex", Parse.CORRECT);
    }

    public void testIo() {
        assertParsed("lib/elixir/lib/io.ex", Parse.CORRECT);
    }

    public void testIoAnsi() {
        assertParsed("lib/elixir/lib/io/ansi.ex", Parse.CORRECT);
    }

    public void testIoAnsiDocs() {
        assertParsed("lib/elixir/lib/io/ansi/docs.ex", Parse.CORRECT);
    }

    public void testIoStream() {
        assertParsed("lib/elixir/lib/io/stream.ex", Parse.CORRECT);
    }

    public void testKernel() {
        assertParsed("lib/elixir/lib/kernel.ex", Parse.CORRECT);
    }

    public void testKernelCli() {
        assertParsed("lib/elixir/lib/kernel/cli.ex", Parse.CORRECT);
    }

    public void testKernelErrorHandler() {
        assertParsed("lib/elixir/lib/kernel/error_handler.ex", Parse.CORRECT);
    }

    public void testKernelLexicalTracker() {
        assertParsed("lib/elixir/lib/kernel/lexical_tracker.ex", Parse.CORRECT);
    }

    public void testKernelParallelCompiler() {
        assertParsed("lib/elixir/lib/kernel/parallel_compiler.ex", Parse.CORRECT);
    }

    public void testKernelParallelRequire() {
        assertParsed("lib/elixir/lib/kernel/parallel_require.ex", Parse.CORRECT);
    }

    public void testKernelSpecialForms() {
        assertParsed("lib/elixir/lib/kernel/special_forms.ex", Parse.CORRECT);
    }

    public void testKernelTypespec() {
        assertParsed("lib/elixir/lib/kernel/typespec.ex", Parse.CORRECT);
    }

    public void testKeyword() {
        assertParsed("lib/elixir/lib/keyword.ex", Parse.CORRECT);
    }

    public void testList() {
        assertParsed("lib/elixir/lib/list.ex", Parse.CORRECT);
    }

    public void testListChars() {
        assertParsed("lib/elixir/lib/list/chars.ex", Parse.CORRECT);
    }

    public void testMacro() {
        assertParsed("lib/elixir/lib/macro.ex", Parse.CORRECT);
    }

    public void testMacroEnv() {
        assertParsed("lib/elixir/lib/macro/env.ex", Parse.CORRECT);
    }

    public void testMap() {
        assertParsed("lib/elixir/lib/map.ex", Parse.CORRECT);
    }

    public void testMapSet() {
        assertParsed("lib/elixir/lib/map_set.ex", Parse.CORRECT);
    }

    public void testModule() {
        assertParsed("lib/elixir/lib/module.ex", Parse.CORRECT);
    }

    public void testModuleLocalsTracker() {
        assertParsed("lib/elixir/lib/module/locals_tracker.ex", Parse.CORRECT);
    }

    public void testNode() {
        assertParsed("lib/elixir/lib/node.ex", Parse.CORRECT);
    }

    public void testOptionParser() {
        assertParsed("lib/elixir/lib/option_parser.ex", Parse.CORRECT);
    }

    public void testPath() {
        assertParsed("lib/elixir/lib/path.ex", Parse.CORRECT);
    }

    public void testPort() {
        assertParsed("lib/elixir/lib/port.ex", Parse.CORRECT);
    }

    public void testProcess() {
        assertParsed("lib/elixir/lib/process.ex", Parse.CORRECT);
    }

    public void testProtocol() {
        assertParsed("lib/elixir/lib/protocol.ex", Parse.CORRECT);
    }

    public void testRange() {
        assertParsed("lib/elixir/lib/range.ex", Parse.CORRECT);
    }

    public void testRecord() {
        assertParsed("lib/elixir/lib/record.ex", Parse.CORRECT);
    }

    public void testRecordExtractor() {
        assertParsed("lib/elixir/lib/record/extractor.ex", Parse.CORRECT);
    }

    public void testRegex() {
        assertParsed("lib/elixir/lib/regex.ex", Parse.CORRECT);
    }

    public void testSet() {
        assertParsed("lib/elixir/lib/set.ex", Parse.CORRECT);
    }

    public void testStream() {
        assertParsed("lib/elixir/lib/stream.ex", Parse.CORRECT);
    }

    public void testStreamReducers() {
        assertParsed("lib/elixir/lib/stream/reducers.ex", Parse.CORRECT);
    }

    public void testString() {
        assertParsed("lib/elixir/lib/string.ex", Parse.CORRECT);
    }

    public void testStringChars() {
        assertParsed("lib/elixir/lib/string/chars.ex", Parse.CORRECT);
    }

    public void testStringIo() {
        assertParsed("lib/elixir/lib/string_io.ex", Parse.CORRECT);
    }

    public void testSupervisor() {
        assertParsed("lib/elixir/lib/supervisor.ex", Parse.CORRECT);
    }

    public void testSupervisorDefault() {
        assertParsed("lib/elixir/lib/supervisor/default.ex", Parse.CORRECT);
    }

    public void testSupervisorSpec() {
        assertParsed("lib/elixir/lib/supervisor/spec.ex", Parse.CORRECT);
    }

    public void testSystem() {
        assertParsed("lib/elixir/lib/system.ex", Parse.CORRECT);
    }

    public void testTask() {
        assertParsed("lib/elixir/lib/task.ex", Parse.CORRECT);
    }

    public void testTaskSupervised() {
        assertParsed("lib/elixir/lib/task/supervised.ex", Parse.CORRECT);
    }

    public void testTaskSupervisor() {
        assertParsed("lib/elixir/lib/task/supervisor.ex", Parse.CORRECT);
    }

    public void testTuple() {
        assertParsed("lib/elixir/lib/tuple.ex", Parse.CORRECT);
    }

    public void testUri() {
        assertParsed("lib/elixir/lib/uri.ex", Parse.CORRECT);
    }

    public void testVersion() {
        assertParsed("lib/elixir/lib/version.ex", Parse.CORRECT);
    }

    public void testCompileSample() {
        assertParsed("lib/elixir/test/elixir/fixtures/compile_sample.ex", Parse.CORRECT);
    }

    public void testParallelCompilerBar() {
        assertParsed("lib/elixir/test/elixir/fixtures/parallel_compiler/bar.ex", Parse.CORRECT);
    }

    public void testParallelCompilerBat() {
        assertParsed("lib/elixir/test/elixir/fixtures/parallel_compiler/bat.ex", Parse.ERROR);
    }

    public void testParallelCompilerFoo() {
        assertParsed("lib/elixir/test/elixir/fixtures/parallel_compiler/foo.ex", Parse.CORRECT);
    }

    public void testParallelDeadlockBar() {
        assertParsed("lib/elixir/test/elixir/fixtures/parallel_deadlock/bar.ex", Parse.CORRECT);
    }

    public void testParallelDeadlockFoo() {
        assertParsed("lib/elixir/test/elixir/fixtures/parallel_deadlock/foo.ex", Parse.CORRECT);
    }

    public void testParallelStructBar() {
        assertParsed("lib/elixir/test/elixir/fixtures/parallel_struct/bar.ex", Parse.CORRECT);
    }

    public void testParallelStructFoo() {
        assertParsed("lib/elixir/test/elixir/fixtures/parallel_struct/foo.ex", Parse.CORRECT);
    }

    public void testWarningsSample() {
        assertParsed("lib/elixir/test/elixir/fixtures/warnings_sample.ex", Parse.CORRECT);
    }

    public void testUnicodeUnicode() {
        assertParsed("lib/elixir/unicode/unicode.ex", Parse.CORRECT);
    }

    public void testExUnit() {
        assertParsed("lib/ex_unit/lib/ex_unit.ex", Parse.CORRECT);
    }

    public void testExUnitAssertions() {
        assertParsed("lib/ex_unit/lib/ex_unit/assertions.ex", Parse.CORRECT);
    }

    public void testExUnitCallbacks() {
        assertParsed("lib/ex_unit/lib/ex_unit/callbacks.ex", Parse.CORRECT);
    }

    public void testExUnitCaptureIo() {
        assertParsed("lib/ex_unit/lib/ex_unit/capture_io.ex", Parse.CORRECT);
    }

    public void testExUnitCaptureLog() {
        assertParsed("lib/ex_unit/lib/ex_unit/capture_log.ex", Parse.CORRECT);
    }

    public void testExUnitCase() {
        assertParsed("lib/ex_unit/lib/ex_unit/case.ex", Parse.CORRECT);
    }

    public void testExUnitCaseTemplate() {
        assertParsed("lib/ex_unit/lib/ex_unit/case_template.ex", Parse.CORRECT);
    }

    public void testExUnitCliFormatter() {
        assertParsed("lib/ex_unit/lib/ex_unit/cli_formatter.ex", Parse.CORRECT);
    }

    public void testExUnitDocTest() {
        assertParsed("lib/ex_unit/lib/ex_unit/doc_test.ex", Parse.CORRECT);
    }

    public void testExUnitEventManager() {
        assertParsed("lib/ex_unit/lib/ex_unit/event_manager.ex", Parse.CORRECT);
    }

    public void testExUnitFilters() {
        assertParsed("lib/ex_unit/lib/ex_unit/filters.ex", Parse.CORRECT);
    }

    public void testExUnitFormatter() {
        assertParsed("lib/ex_unit/lib/ex_unit/formatter.ex", Parse.CORRECT);
    }

    public void testExUnitOnExitHandler() {
        assertParsed("lib/ex_unit/lib/ex_unit/on_exit_handler.ex", Parse.CORRECT);
    }

    public void testExUnitRunner() {
        assertParsed("lib/ex_unit/lib/ex_unit/runner.ex", Parse.CORRECT);
    }

    public void testExUnitRunnerStats() {
        assertParsed("lib/ex_unit/lib/ex_unit/runner_stats.ex", Parse.CORRECT);
    }

    public void testExUnitServer() {
        assertParsed("lib/ex_unit/lib/ex_unit/server.ex", Parse.CORRECT);
    }

    public void testIex() {
        assertParsed("lib/iex/lib/iex.ex", Parse.CORRECT);
    }

    public void testIexApp() {
        assertParsed("lib/iex/lib/iex/app.ex", Parse.CORRECT);
    }

    public void testIexAutocomplete() {
        assertParsed("lib/iex/lib/iex/autocomplete.ex", Parse.CORRECT);
    }

    public void testIexCli() {
        assertParsed("lib/iex/lib/iex/cli.ex", Parse.CORRECT);
    }

    public void testIexConfig() {
        assertParsed("lib/iex/lib/iex/config.ex", Parse.CORRECT);
    }

    public void testIexEvaluator() {
        assertParsed("lib/iex/lib/iex/evaluator.ex", Parse.CORRECT);
    }

    public void testIexHelpers() {
        assertParsed("lib/iex/lib/iex/helpers.ex", Parse.CORRECT);
    }

    public void testIexHistory() {
        assertParsed("lib/iex/lib/iex/history.ex", Parse.CORRECT);
    }

    public void testIexIntrospection() {
        assertParsed("lib/iex/lib/iex/introspection.ex", Parse.CORRECT);
    }

    public void testIexRemsh() {
        assertParsed("lib/iex/lib/iex/remsh.ex", Parse.CORRECT);
    }

    public void testIexServer() {
        assertParsed("lib/iex/lib/iex/server.ex", Parse.CORRECT);
    }

    public void testLogger() {
        assertParsed("lib/logger/lib/logger.ex", Parse.CORRECT);
    }

    public void testLoggerApp() {
        assertParsed("lib/logger/lib/logger/app.ex", Parse.CORRECT);
    }

    public void testLoggerBackendsConsole() {
        assertParsed("lib/logger/lib/logger/backends/console.ex", Parse.CORRECT);
    }

    public void testLoggerConfig() {
        assertParsed("lib/logger/lib/logger/config.ex", Parse.CORRECT);
    }

    public void testLoggerErrorHandler() {
        assertParsed("lib/logger/lib/logger/error_handler.ex", Parse.CORRECT);
    }

    public void testLoggerFormatter() {
        assertParsed("lib/logger/lib/logger/formatter.ex", Parse.CORRECT);
    }

    public void testLoggerTranslator() {
        assertParsed("lib/logger/lib/logger/translator.ex", Parse.CORRECT);
    }

    public void testLoggerUtils() {
        assertParsed("lib/logger/lib/logger/utils.ex", Parse.CORRECT);
    }

    public void testLoggerWatcher() {
        assertParsed("lib/logger/lib/logger/watcher.ex", Parse.CORRECT);
    }

    public void testMix() {
        assertParsed("lib/mix/lib/mix.ex", Parse.CORRECT);
    }

    public void testMixArchive() {
        assertParsed("lib/mix/lib/mix/archive.ex", Parse.CORRECT);
    }

    public void testMixCli() {
        assertParsed("lib/mix/lib/mix/cli.ex", Parse.CORRECT);
    }

    public void testMixCompilersElixir() {
        assertParsed("lib/mix/lib/mix/compilers/elixir.ex", Parse.CORRECT);
    }

    public void testMixCompilersErlang() {
        assertParsed("lib/mix/lib/mix/compilers/erlang.ex", Parse.CORRECT);
    }

    public void testMixConfig() {
        assertParsed("lib/mix/lib/mix/config.ex", Parse.CORRECT);
    }

    public void testMixConfigAgent() {
        assertParsed("lib/mix/lib/mix/config/agent.ex", Parse.CORRECT);
    }

    public void testMixDep() {
        assertParsed("lib/mix/lib/mix/dep.ex", Parse.CORRECT);
    }

    public void testMixDepConverger() {
        assertParsed("lib/mix/lib/mix/dep/converger.ex", Parse.CORRECT);
    }

    public void testMixDepFetcher() {
        assertParsed("lib/mix/lib/mix/dep/fetcher.ex", Parse.CORRECT);
    }

    public void testMixDepLoader() {
        assertParsed("lib/mix/lib/mix/dep/loader.ex", Parse.CORRECT);
    }

    public void testMixDepLock() {
        assertParsed("lib/mix/lib/mix/dep/lock.ex", Parse.CORRECT);
    }

    public void testMixDepUmbrella() {
        assertParsed("lib/mix/lib/mix/dep/umbrella.ex", Parse.CORRECT);
    }

    public void testMixExceptions() {
        assertParsed("lib/mix/lib/mix/exceptions.ex", Parse.CORRECT);
    }

    public void testMixGenerator() {
        assertParsed("lib/mix/lib/mix/generator.ex", Parse.CORRECT);
    }

    public void testMixHex() {
        assertParsed("lib/mix/lib/mix/hex.ex", Parse.CORRECT);
    }

    public void testMixLocal() {
        assertParsed("lib/mix/lib/mix/local.ex", Parse.CORRECT);
    }

    public void testMixProject() {
        assertParsed("lib/mix/lib/mix/project.ex", Parse.CORRECT);
    }

    public void testMixProjectStack() {
        assertParsed("lib/mix/lib/mix/project_stack.ex", Parse.CORRECT);
    }

    public void testMixRebar() {
        assertParsed("lib/mix/lib/mix/rebar.ex", Parse.CORRECT);
    }

    public void testMixPublicKey() {
        assertParsed("lib/mix/lib/mix/public_key.ex", Parse.CORRECT);
    }

    public void testMixRemoteConverger() {
        assertParsed("lib/mix/lib/mix/remote_converger.ex", Parse.CORRECT);
    }

    public void testMixScm() {
        assertParsed("lib/mix/lib/mix/scm.ex", Parse.CORRECT);
    }

    public void testMixScmGit() {
        assertParsed("lib/mix/lib/mix/scm/git.ex", Parse.CORRECT);
    }

    public void testMixScmPath() {
        assertParsed("lib/mix/lib/mix/scm/path.ex", Parse.CORRECT);
    }

    public void testMixShell() {
        assertParsed("lib/mix/lib/mix/shell.ex", Parse.CORRECT);
    }

    public void testMixShellIo() {
        assertParsed("lib/mix/lib/mix/shell/io.ex", Parse.CORRECT);
    }

    public void testMixShellProcess() {
        assertParsed("lib/mix/lib/mix/shell/process.ex", Parse.CORRECT);
    }

    public void testMixState() {
        assertParsed("lib/mix/lib/mix/state.ex", Parse.CORRECT);
    }

    public void testMixTask() {
        assertParsed("lib/mix/lib/mix/task.ex", Parse.CORRECT);
    }

    public void testMixTasksAppStart() {
        assertParsed("lib/mix/lib/mix/tasks/app.start.ex", Parse.CORRECT);
    }

    public void testMixTasksArchiveBuild() {
        assertParsed("lib/mix/lib/mix/tasks/archive.build.ex", Parse.CORRECT);
    }

    public void testMixTasksArchive() {
        assertParsed("lib/mix/lib/mix/tasks/archive.ex", Parse.CORRECT);
    }

    public void testMixTasksArchiveInstall() {
        assertParsed("lib/mix/lib/mix/tasks/archive.install.ex", Parse.CORRECT);
    }

    public void testMixTasksArchiveUninstall() {
        assertParsed("lib/mix/lib/mix/tasks/archive.uninstall.ex", Parse.CORRECT);
    }

    public void testMixTasksClean() {
        assertParsed("lib/mix/lib/mix/tasks/clean.ex", Parse.CORRECT);
    }

    public void testMixTasksCmd() {
        assertParsed("lib/mix/lib/mix/tasks/cmd.ex", Parse.CORRECT);
    }

    public void testMixTasksCompileAll() {
        assertParsed("lib/mix/lib/mix/tasks/compile.all.ex", Parse.CORRECT);
    }

    public void testMixTasksCompileApp() {
        assertParsed("lib/mix/lib/mix/tasks/compile.app.ex", Parse.CORRECT);
    }

    public void testMixTasksCompileElixir() {
        assertParsed("lib/mix/lib/mix/tasks/compile.elixir.ex", Parse.CORRECT);
    }

    public void testMixTasksCompileErlang() {
        assertParsed("lib/mix/lib/mix/tasks/compile.erlang.ex", Parse.CORRECT);
    }

    public void testMixTasksCompile() {
        assertParsed("lib/mix/lib/mix/tasks/compile.ex", Parse.CORRECT);
    }

    public void testMixTasksCompileLeex() {
        assertParsed("lib/mix/lib/mix/tasks/compile.leex.ex", Parse.CORRECT);
    }

    public void testMixTasksCompileProtocols() {
        assertParsed("lib/mix/lib/mix/tasks/compile.protocols.ex", Parse.CORRECT);
    }

    public void testMixTasksCompileYecc() {
        assertParsed("lib/mix/lib/mix/tasks/compile.yecc.ex", Parse.CORRECT);
    }

    public void testMixTasksDepsCheck() {
        assertParsed("lib/mix/lib/mix/tasks/deps.check.ex", Parse.CORRECT);
    }

    // TODO re-enable once travis-ci run elixir with https://github.com/elixir-lang/elixir/commit/3e52ed0fbbc09a156e6ea180baff3b89a8da183e so intellij_elixir matches intellij-elixir behavior
    /*
    public void testMixTasksDepsClean() {
        assertParsed("lib/mix/lib/mix/tasks/deps.clean.ex", Parse.CORRECT);
    }
    */

    public void testMixTasksDepsCompile() {
        assertParsed("lib/mix/lib/mix/tasks/deps.compile.ex", Parse.CORRECT);
    }

    public void testMixTasksDeps() {
        assertParsed("lib/mix/lib/mix/tasks/deps.ex", Parse.CORRECT);
    }

    public void testMixTasksDepsGet() {
        assertParsed("lib/mix/lib/mix/tasks/deps.get.ex", Parse.CORRECT);
    }

    public void testMixTasksDepsLoadpaths() {
        assertParsed("lib/mix/lib/mix/tasks/deps.loadpaths.ex", Parse.CORRECT);
    }

    public void testMixTasksDepsUnlock() {
        assertParsed("lib/mix/lib/mix/tasks/deps.unlock.ex", Parse.CORRECT);
    }

    public void testMixTasksDepsUpdate() {
        assertParsed("lib/mix/lib/mix/tasks/deps.update.ex", Parse.CORRECT);
    }

    public void testMixTasksDo() {
        assertParsed("lib/mix/lib/mix/tasks/do.ex", Parse.CORRECT);
    }

    public void testMixTasksEscriptBuild() {
        assertParsed("lib/mix/lib/mix/tasks/escript.build.ex", Parse.CORRECT);
    }

    public void testMixTasksHelp() {
        assertParsed("lib/mix/lib/mix/tasks/help.ex", Parse.CORRECT);
    }

    public void testMixTasksIex() {
        assertParsed("lib/mix/lib/mix/tasks/iex.ex", Parse.CORRECT);
    }

    public void testMixTasksLoadconfig() {
        assertParsed("lib/mix/lib/mix/tasks/loadconfig.ex", Parse.CORRECT);
    }

    public void testMixTasksLoadpaths() {
        assertParsed("lib/mix/lib/mix/tasks/loadpaths.ex", Parse.CORRECT);
    }

    public void testMixTasksLocal() {
        assertParsed("lib/mix/lib/mix/tasks/local.ex", Parse.CORRECT);
    }

    public void testMixTasksLocalHex() {
        assertParsed("lib/mix/lib/mix/tasks/local.hex.ex", Parse.CORRECT);
    }

    public void testMixTasksLocalPublicKeys() {
        assertParsed("lib/mix/lib/mix/tasks/local.public_keys.ex", Parse.CORRECT);
    }

    public void testMixTasksLocalRebar() {
        assertParsed("lib/mix/lib/mix/tasks/local.rebar.ex", Parse.CORRECT);
    }

    public void testMixTasksNew() {
        assertParsed("lib/mix/lib/mix/tasks/new.ex", Parse.CORRECT);
    }

    public void testMixTasksProfileFprof() {
        assertParsed("lib/mix/lib/mix/tasks/profile.fprof.ex", Parse.CORRECT);
    }

    public void testMixTasksRun() {
        assertParsed("lib/mix/lib/mix/tasks/run.ex", Parse.CORRECT);
    }

    public void testMixTasksTest() {
        assertParsed("lib/mix/lib/mix/tasks/test.ex", Parse.CORRECT);
    }

    public void testMixTasksServer() {
        assertParsed("lib/mix/lib/mix/tasks_server.ex", Parse.CORRECT);
    }

    public void testMixUtils() {
        assertParsed("lib/mix/lib/mix/utils.ex", Parse.CORRECT);
    }

    public void testArchiveLibLocalSample() {
        assertParsed("lib/mix/test/fixtures/archive/lib/local.sample.ex", Parse.CORRECT);
    }

    public void testDepsStatusCustomRawRepoLibRawRepo() {
        assertParsed("lib/mix/test/fixtures/deps_status/custom/raw_repo/lib/raw_repo.ex", Parse.CORRECT);
    }

    public void testEscripttestLibEscripttest() {
        assertParsed("lib/mix/test/fixtures/escripttest/lib/escripttest.ex", Parse.CORRECT);
    }

    public void testNoMixfileLibA() {
        assertParsed("lib/mix/test/fixtures/no_mixfile/lib/a.ex", Parse.CORRECT);
    }

    public void testNoMixfileLibB() {
        assertParsed("lib/mix/test/fixtures/no_mixfile/lib/b.ex", Parse.CORRECT);
    }

    public void testNoMixfileLibC() {
        assertParsed("lib/mix/test/fixtures/no_mixfile/lib/c.ex", Parse.CORRECT);
    }

    public void testUmbrellaDepDepsUmbrellaAppsBarLibBar() {
        assertParsed("lib/mix/test/fixtures/umbrella_dep/deps/umbrella/apps/bar/lib/bar.ex", Parse.CORRECT);
    }

    public void testUmbrellaDepDepsUmbrellaAppsFooLibFoo() {
        assertParsed("lib/mix/test/fixtures/umbrella_dep/deps/umbrella/apps/foo/lib/foo.ex", Parse.CORRECT);
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

    private void assertParsed(String relativePath, Parse parse) {
        File rootFile = new File(getTestDataPath());
        File absoluteFile = new File(rootFile, relativePath);

        assertParsed(absoluteFile, parse);
    }

    private void assertParsed(File absoluteFile, Parse parse) {
        // inlines part of com.intellij.testFramework.ParsingTestCase#doTest(boolean)
        try {
            String text = FileUtil.loadFile(absoluteFile, CharsetToolkit.UTF8, true).trim();

            String nameWithoutExtension = FileUtilRt.getNameWithoutExtension(absoluteFile.toString());
            myFile = createPsiFile(nameWithoutExtension, text);
            ensureParsed(myFile);
            toParseTreeText(myFile, skipSpaces(), includeRanges());
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }

        switch (parse) {
            case CORRECT:
                assertWithoutLocalError();
                assertQuotedCorrectly();
                break;
            case ERROR:
                assertWithLocalError();
                Quoter.assertError(myFile);
        }
    }
}
