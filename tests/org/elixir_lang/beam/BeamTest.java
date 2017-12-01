package org.elixir_lang.beam;

import com.ericsson.otp.erlang.OtpErlangDecodeException;
import com.intellij.openapi.util.Condition;
import com.intellij.util.containers.ContainerUtil;
import org.elixir_lang.beam.chunk.Atoms;
import org.elixir_lang.beam.chunk.CallDefinitions;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.*;
import java.util.stream.Stream;

import static org.elixir_lang.psi.call.name.Function.DEF;
import static org.elixir_lang.psi.call.name.Function.DEFP;
import static org.junit.Assert.*;

public class BeamTest {
    @NotNull
    @SuppressWarnings("NullableProblems")
    private String ebinDirectory;

    @Test
    public void elixirModule() throws IOException, OtpErlangDecodeException {
        Beam beam = beam("Elixir.Kernel");

        assertNotNull(beam);

        Atoms atoms = beam.atoms();

        assertNotNull(atoms);

        assertEquals("Elixir.Kernel", atoms.moduleName());

        long callDefinitionCount = beam.callDefinitionsStream().mapToInt(CallDefinitions::size).sum();

        assertTrue("There are no callDefinitions", callDefinitionCount > 0);

        SortedSet<MacroNameArity> macroNameAritySortedSet =
                CallDefinitions.macroNameAritySortedSet(beam.callDefinitionsStream(), atoms);

        assertEquals("There are nameless callDefinitions", callDefinitionCount, macroNameAritySortedSet.size());

        Stream<MacroNameArity> macroNameArityStream = macroNameAritySortedSet.stream().filter(
                macroNameArity -> "node".equals(macroNameArity.name)
        );

        MacroNameArity[] macroNameArities = macroNameArityStream.toArray(MacroNameArity[]::new);

        assertEquals(2, macroNameArities.length);

        assertEquals(0, (int) macroNameArities[0].arity);
        assertEquals(1, (int) macroNameArities[1].arity);
    }

    @Test
    public void erlangModule() throws IOException, OtpErlangDecodeException {
        Beam beam = beam("elixir_interpolation");

        assertNotNull(beam);

        Atoms atoms = beam.atoms();

        assertNotNull(atoms);

        assertEquals("elixir_interpolation", atoms.moduleName());

        long callDefinitionCount = beam.callDefinitionsStream().mapToInt(CallDefinitions::size).sum();

        assertTrue("There are no callDefinitions", callDefinitionCount > 0);

        SortedSet<MacroNameArity> macroNameAritySortedSet =
                CallDefinitions.macroNameAritySortedSet(beam.callDefinitionsStream(), atoms);

        assertEquals("There are nameless callDefinitions", callDefinitionCount, macroNameAritySortedSet.size());

        List<MacroNameArity> extracts = ContainerUtil.filter(
                macroNameAritySortedSet,
                macroNameArity -> "extract".equals(macroNameArity.name)
        );

        assertEquals(2, extracts.size());

        MacroNameArity firstExtract = extracts.get(0);

        assertEquals(DEF, firstExtract.macro);
        assertEquals(6, (int) firstExtract.arity);

        MacroNameArity secondExtract = extracts.get(1);

        assertEquals(DEFP, secondExtract.macro);
        assertEquals(8, (int) secondExtract.arity);
    }

    private Beam beam(@NotNull String baseName) throws IOException, OtpErlangDecodeException {
        String path = ebinDirectory + baseName + ".beam";
        DataInputStream dataInputStream = new DataInputStream(
                new BufferedInputStream(
                        new FileInputStream(path)
                )
        );

        return Beam.from(dataInputStream, path);
    }

    @Before
    public void setEbinDirectory() {
        String ebinDirectory = System.getenv("ELIXIR_EBIN_DIRECTORY");

        assertNotNull("ELIXIR_EBIN_DIRECTORY is not set", ebinDirectory);

        this.ebinDirectory = ebinDirectory;
    }
}
