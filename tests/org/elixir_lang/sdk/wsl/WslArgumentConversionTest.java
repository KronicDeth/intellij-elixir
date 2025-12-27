package org.elixir_lang.sdk.wsl;

import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.testFramework.ServiceContainerUtil;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static org.elixir_lang.sdk.wsl.WslCompatServiceKt.getWslCompat;

/**
 * Unit tests for WSL UNC path conversion in command line arguments.
 *
 * <p>Tests verify that embedded Windows UNC paths in wsl$ or wsl.localhost format
 * are correctly converted to POSIX paths when running commands in WSL.</p>
 */
public class WslArgumentConversionTest extends BasePlatformTestCase {

    private static final String WSL_WORK_DIR = "\\\\wsl$\\Ubuntu\\home\\user\\project";

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // Replace the real WslCompatService with MockWslCompatService for testing
        MockWslCompatService mockService = new MockWslCompatService();
        ServiceContainerUtil.registerOrReplaceServiceInstance(
            ApplicationManager.getApplication(),
            WslCompatService.class,
            mockService,
            getTestRootDisposable()
        );
    }

    /**
     * Test that simple argument with embedded WSL path is converted.
     */
    public void testConvertSimpleArgument() {
        String input = "--path=\\\\wsl$\\Ubuntu\\home\\user";
        String expected = "--path=/home/user";

        assertSingleArgumentConverted(input, expected);
    }

    /**
     * Test conversion with forward slashes in UNC path.
     */
    public void testConvertForwardSlashUncPath() {
        String input = "--path=//wsl$/Ubuntu/home/user";
        String expected = "--path=/home/user";

        assertSingleArgumentConverted(input, expected);
    }

    /**
     * Test conversion with wsl.localhost format.
     */
    public void testConvertWslLocalhostFormat() {
        String input = "--path=\\\\wsl.localhost\\Ubuntu\\home\\user";
        String expected = "--path=/home/user";

        String wslLocalWorkDir = "\\\\wsl.localhost\\Ubuntu\\home\\user\\project";
        assertSingleArgumentConverted(wslLocalWorkDir, input, expected);
    }

    /**
     * Test conversion of multiple paths in single argument.
     */
    public void testConvertMultiplePathsInArgument() {
        String input = "--map=\\\\wsl$\\Ubuntu\\home\\user\\dir1:\\\\wsl$\\Ubuntu\\home\\user\\dir2";
        String expected = "--map=/home/user/dir1:/home/user/dir2";

        assertSingleArgumentConverted(input, expected);
    }

    /**
     * Test conversion of standalone path argument.
     */
    public void testConvertStandalonePathArgument() {
        String input = "\\\\wsl$\\Ubuntu\\home\\user\\file.txt";
        String expected = "/home/user/file.txt";

        assertSingleArgumentConverted(input, expected);
    }

    /**
     * Test that paths with spaces are handled correctly.
     */
    public void testConvertPathWithSpaces() {
        String input = "--path=\\\\wsl$\\Ubuntu\\home\\user\\my documents";
        String expected = "--path=/home/user/my documents";

        assertSingleArgumentConverted(input, expected);
    }

    /**
     * Test that multiple arguments are all converted.
     */
    public void testConvertMultipleArguments() {
        WslCompatService service = getWslCompat();
        GeneralCommandLine commandLine = new GeneralCommandLine();

        commandLine.setExePath("\\\\wsl$\\Ubuntu\\usr\\bin\\elixir");
        commandLine.setWorkDirectory(WSL_WORK_DIR);
        commandLine.addParameter("--path=\\\\wsl$\\Ubuntu\\home\\user");
        commandLine.addParameter("--config=\\\\wsl$\\Ubuntu\\etc\\config");
        commandLine.addParameter("run");

        ProcessBuilder processBuilder = toProcessBuilder(commandLine);
        service.convertProcessBuilderArgumentsForWsl(processBuilder, commandLine);

        List<String> params = processBuilder.command();
        assertEquals(4, params.size());
        assertEquals("--path=/home/user", params.get(1));
        assertEquals("--config=/etc/config", params.get(2));
        assertEquals("run", params.get(3));
    }

    /**
     * Test that non-WSL arguments are not modified.
     */
    public void testDoNotConvertNonWslArguments() {
        WslCompatService service = getWslCompat();
        GeneralCommandLine commandLine = new GeneralCommandLine();

        commandLine.setExePath("\\\\wsl$\\Ubuntu\\usr\\bin\\elixir");
        commandLine.setWorkDirectory(WSL_WORK_DIR);
        String[] inputs = {"--verbose", "--path=/local/path", "run"};
        for (String input : inputs) {
            commandLine.addParameter(input);
        }

        ProcessBuilder processBuilder = toProcessBuilder(commandLine);
        service.convertProcessBuilderArgumentsForWsl(processBuilder, commandLine);

        List<String> params = processBuilder.command().stream().skip(1).toList();
        assertEquals(Arrays.asList(inputs), params);
    }

    /**
     * Test that conversion is skipped when working directory is not in WSL.
     */
    public void testSkipConversionForNonWslWorkingDirectory() {
        String expected = "--path=\\\\wsl$\\Ubuntu\\home\\user";
        // Should remain unchanged

        WslCompatService service = getWslCompat();
        GeneralCommandLine commandLine = new GeneralCommandLine();

        // Non-WSL working directory
        commandLine.setWorkDirectory("/tmp/project");
        commandLine.addParameter(expected);

        ProcessBuilder processBuilder = toProcessBuilder(commandLine);
        service.convertProcessBuilderArgumentsForWsl(processBuilder, commandLine);

        List<String> params = processBuilder.command();
        assertEquals(expected, params.get(1));
    }

    /**
     * Test that mixed path separators are handled.
     */
    public void testConvertMixedPathSeparators() {
        String input = "--path=\\\\wsl$\\Ubuntu/home/user";
        String expected = "--path=/home/user";

        assertSingleArgumentConverted(input, expected);
    }

    /**
     * Test conversion stops at invalid Windows path characters.
     */
    public void testConvertStopsAtInvalidCharacters() {
        String input = "--path=\\\\wsl$\\Ubuntu\\home\\user|other";
        String expected = "--path=/home/user|other";

        assertSingleArgumentConverted(input, expected);
    }

    /**
     * Test that paths embedded in JSON are converted.
     */
    public void testConvertPathsInJson() {
        String input = "{\"path\":\"\\\\wsl$\\Ubuntu\\home\\user\"}";
        String expected = "{\"path\":\"/home/user\"}";

        assertSingleArgumentConverted(input, expected);
    }

    /**
     * Test that empty parameter list is handled gracefully.
     */
    public void testConvertEmptyParameterList() {
        WslCompatService service = getWslCompat();
        GeneralCommandLine commandLine = new GeneralCommandLine();

        commandLine.setExePath("\\\\wsl$\\Ubuntu\\usr\\bin\\elixir");
        commandLine.setWorkDirectory(WSL_WORK_DIR);
        // No parameters

        ProcessBuilder processBuilder = toProcessBuilder(commandLine);
        service.convertProcessBuilderArgumentsForWsl(processBuilder, commandLine);

        List<String> params = processBuilder.command();
        assertEquals(1, params.size());
    }

    /**
     * Test case-insensitive matching of wsl$ and wsl.localhost.
     */
    public void testConvertCaseInsensitive() {
        String input = "--path=\\\\WSL$\\Ubuntu\\home\\user";
        String expected = "--path=/home/user";

        String wslWorkDirUppercase = "\\\\WSL$\\Ubuntu\\home\\user\\project";
        assertSingleArgumentConverted(wslWorkDirUppercase, input, expected);
    }

    /**
     * Test that distribution mismatch prevents conversion.
     */
    public void testDoNotConvertDifferentDistribution() {
        String expected = "--path=\\\\wsl$\\Debian\\home\\user";
        // Should remain unchanged

        // Executable is in Ubuntu, but argument references Debian
        assertSingleArgumentConverted(expected, expected);
    }

    /**
     * Test conversion of Windows drive path with forward slashes.
     */
    public void testConvertWindowsDrivePathWithForwardSlashes() {
        String input = "--path=C:/Users/steve/project";
        String expected = "--path=/mnt/c/Users/steve/project";

        assertSingleArgumentConverted(input, expected);
    }

    /**
     * Test conversion of Windows drive path with backslashes.
     */
    public void testConvertWindowsDrivePathWithBackslashes() {
        String input = "--path=C:\\Users\\steve\\project";
        String expected = "--path=/mnt/c/Users/steve/project";

        assertSingleArgumentConverted(input, expected);
    }

    /**
     * Test conversion of different drive letters.
     */
    public void testConvertDifferentDriveLetters() {
        String input = "--src=D:/data --dest=E:\\backup";
        String expected = "--src=/mnt/d/data --dest=/mnt/e/backup";

        assertSingleArgumentConverted(input, expected);
    }

    /**
     * Test conversion of multiple Windows drive paths in single argument.
     */
    public void testConvertMultipleWindowsDrivePaths() {
        String input = "--map=C:/source:D:/target";
        String expected = "--map=/mnt/c/source:/mnt/d/target";

        assertSingleArgumentConverted(input, expected);
    }

    /**
     * Test conversion of mixed WSL UNC and Windows drive paths.
     */
    public void testConvertMixedWslUncAndDrivePaths() {
        String input = "--wsl=\\\\wsl$\\Ubuntu\\home\\user --win=C:/Users/steve";
        String expected = "--wsl=/home/user --win=/mnt/c/Users/steve";

        assertSingleArgumentConverted(input, expected);
    }

    /**
     * Test conversion of Windows drive path with mixed separators.
     */
    public void testConvertWindowsDrivePathMixedSeparators() {
        String input = "C:\\Users/steve\\Documents/file.txt";
        String expected = "/mnt/c/Users/steve/Documents/file.txt";

        assertSingleArgumentConverted(input, expected);
    }

    /**
     * Test that Windows drive paths stop at invalid characters.
     */
    public void testConvertWindowsDrivePathStopsAtInvalidChars() {
        String input = "--path=C:/Users/steve|other";
        String expected = "--path=/mnt/c/Users/steve|other";

        assertSingleArgumentConverted(input, expected);
    }

    /**
     * Test conversion of Windows drive paths in environment variables.
     */
    public void testConvertWindowsDrivePathsInEnvironment() {
        WslCompatService service = getWslCompat();
        GeneralCommandLine commandLine = new GeneralCommandLine();

        commandLine.setExePath("\\\\wsl$\\Ubuntu\\usr\\bin\\elixir");
        commandLine.setWorkDirectory(WSL_WORK_DIR);
        commandLine.getEnvironment().put("MYPATH", "C:/Users/steve/bin");
        commandLine.getEnvironment().put("DATADIR", "D:\\data\\files");

        ProcessBuilder processBuilder = toProcessBuilder(commandLine);
        service.convertProcessBuilderArgumentsForWsl(processBuilder, commandLine);

        assertEquals("/mnt/c/Users/steve/bin", processBuilder.environment().get("MYPATH"));
        assertEquals("/mnt/d/data/files", processBuilder.environment().get("DATADIR"));
    }

    /**
     * Test that standalone Windows drive path is converted.
     */
    public void testConvertStandaloneWindowsDrivePath() {
        String input = "C:/Program Files/app.exe";
        String expected = "/mnt/c/Program Files/app.exe";

        assertSingleArgumentConverted(input, expected);
    }

    /**
     * Test lowercase and uppercase drive letters are handled.
     */
    public void testConvertDriveLetterCaseInsensitive() {
        WslCompatService service = getWslCompat();
        GeneralCommandLine commandLine = new GeneralCommandLine();

        commandLine.setExePath("\\\\wsl$\\Ubuntu\\usr\\bin\\elixir");
        commandLine.setWorkDirectory(WSL_WORK_DIR);
        commandLine.addParameter("c:/lowercase");
        commandLine.addParameter("C:/uppercase");

        ProcessBuilder processBuilder = toProcessBuilder(commandLine);
        service.convertProcessBuilderArgumentsForWsl(processBuilder, commandLine);

        List<String> params = processBuilder.command();
        assertEquals(3, params.size());
        assertEquals("/mnt/c/lowercase", params.get(1));
        assertEquals("/mnt/c/uppercase", params.get(2));
    }

    /**
     * Test that exePath is converted when it's a WSL UNC path.
     */
    public void testConvertExePath() {
        WslCompatService service = getWslCompat();
        GeneralCommandLine commandLine = new GeneralCommandLine();

        String exePath = "\\\\wsl$\\Ubuntu\\usr\\bin\\elixir";
        commandLine.setExePath(exePath);
        commandLine.setWorkDirectory(WSL_WORK_DIR);
        commandLine.addParameter("--version");

        ProcessBuilder processBuilder = toProcessBuilder(commandLine);
        service.convertProcessBuilderArgumentsForWsl(processBuilder, commandLine);

        assertEquals("/usr/bin/elixir", processBuilder.command().getFirst());
        assertEquals("--version", processBuilder.command().get(1));
    }

    /**
     * Test that conversion is skipped when exePath is not WSL.
     */
    public void testSkipConversionWhenExePathNotWsl() {
        WslCompatService service = getWslCompat();
        GeneralCommandLine commandLine = new GeneralCommandLine();

        String exePath = "/usr/local/bin/elixir";
        String originalParam = "\\\\wsl$\\Ubuntu\\home\\user\\file.txt";

        commandLine.setExePath(exePath);
        commandLine.setWorkDirectory(WSL_WORK_DIR);
        commandLine.addParameter(originalParam);

        ProcessBuilder processBuilder = toProcessBuilder(commandLine);
        service.convertProcessBuilderArgumentsForWsl(processBuilder, commandLine);

        // Nothing should be converted because exePath is not WSL UNC
        assertEquals(exePath, processBuilder.command().getFirst());
        assertEquals(originalParam, processBuilder.command().get(1));
    }

    /**
     * Test that conversion is skipped when exePath and workDir are from different distributions.
     */
    public void testSkipConversionWhenDistributionMismatch() {
        WslCompatService service = getWslCompat();
        GeneralCommandLine commandLine = new GeneralCommandLine();

        String exePath = "\\\\wsl$\\Ubuntu\\usr\\bin\\elixir";
        String workDir = "\\\\wsl$\\Debian\\home\\user\\project";
        String originalParam = "--path=\\\\wsl$\\Ubuntu\\home\\user";

        commandLine.setExePath(exePath);
        commandLine.setWorkDirectory(workDir);
        commandLine.addParameter(originalParam);

        ProcessBuilder processBuilder = toProcessBuilder(commandLine);
        service.convertProcessBuilderArgumentsForWsl(processBuilder, commandLine);

        // Nothing should be converted because distributions don't match
        assertEquals(exePath, processBuilder.command().getFirst());
        assertEquals(originalParam, processBuilder.command().get(1));
    }

    /**
     * Test that exePath with forward slashes is converted.
     */
    public void testConvertExePathWithForwardSlashes() {
        WslCompatService service = getWslCompat();
        GeneralCommandLine commandLine = new GeneralCommandLine();

        String exePath = "//wsl$/Ubuntu/usr/bin/elixir";
        commandLine.setExePath(exePath);
        commandLine.setWorkDirectory(WSL_WORK_DIR);

        ProcessBuilder processBuilder = toProcessBuilder(commandLine);
        service.convertProcessBuilderArgumentsForWsl(processBuilder, commandLine);

        assertEquals("/usr/bin/elixir", processBuilder.command().getFirst());
    }

    /**
     * Test that exePath with wsl.localhost format is converted.
     */
    public void testConvertExePathWslLocalhost() {
        WslCompatService service = getWslCompat();
        GeneralCommandLine commandLine = new GeneralCommandLine();

        String exePath = "\\\\wsl.localhost\\Ubuntu\\usr\\bin\\elixir";
        String workDir = "\\\\wsl.localhost\\Ubuntu\\home\\user\\project";

        commandLine.setExePath(exePath);
        commandLine.setWorkDirectory(workDir);
        commandLine.addParameter("--version");

        ProcessBuilder processBuilder = toProcessBuilder(commandLine);
        service.convertProcessBuilderArgumentsForWsl(processBuilder, commandLine);

        assertEquals("/usr/bin/elixir", processBuilder.command().getFirst());
    }

    /**
     * Test that Windows drive path in exePath is converted.
     */
    public void testConvertExePathWindowsDrive() {
        WslCompatService service = getWslCompat();
        GeneralCommandLine commandLine = new GeneralCommandLine();

        String exePath = "\\\\wsl$\\Ubuntu\\mnt\\c\\tools\\elixir.bat";
        commandLine.setExePath(exePath);
        commandLine.setWorkDirectory(WSL_WORK_DIR);

        ProcessBuilder processBuilder = toProcessBuilder(commandLine);
        service.convertProcessBuilderArgumentsForWsl(processBuilder, commandLine);

        assertEquals("/mnt/c/tools/elixir.bat", processBuilder.command().getFirst());
    }

    // Helper methods

    /**
     * Helper to test single argument conversion with default WSL working directory.
     */
    private void assertSingleArgumentConverted(String input, String expected) {
        assertSingleArgumentConverted(WSL_WORK_DIR, input, expected);
    }

    /**
     * Helper to test single argument conversion with custom working directory.
     */
    private void assertSingleArgumentConverted(String workDir, String input, String expected) {
        WslCompatService service = getWslCompat();


        // Set exePath to match the working directory distribution for consistency
        // Extract distribution from workDir and create a matching exePath
        String exePath;
        if (workDir.contains("Ubuntu")) {
            exePath = workDir.substring(0, workDir.lastIndexOf('\\')) + "\\usr\\bin\\elixir";
        } else if (workDir.contains("Debian")) {
            exePath = workDir.substring(0, workDir.lastIndexOf('\\')) + "\\usr\\bin\\elixir";
        } else {
            exePath = "\\\\wsl$\\Ubuntu\\usr\\bin\\elixir";
        }
        GeneralCommandLine commandLine = new GeneralCommandLine();
        commandLine.setExePath(exePath);
        commandLine.setWorkDirectory(workDir);
        commandLine.addParameter(input);
        ProcessBuilder processBuilder = toProcessBuilder(commandLine);

        service.convertProcessBuilderArgumentsForWsl(processBuilder, commandLine);

        List<String> params = processBuilder.command();
        assertEquals(2, params.size());
        assertEquals(expected, params.getLast());
    }

    // This is a small cheat to access the process builder without actually running the command line in tests
    private ProcessBuilder toProcessBuilder(GeneralCommandLine commandLine) {
        try {
            Method method = commandLine.getClass().getDeclaredMethod("toProcessBuilder", List.class);
            method.setAccessible(true);
            return (ProcessBuilder) method.invoke(commandLine, commandLine.getCommandLineList(null));
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
