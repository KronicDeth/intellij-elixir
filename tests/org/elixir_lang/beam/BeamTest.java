package org.elixir_lang.beam;

import com.ericsson.otp.erlang.OtpErlangDecodeException;
import com.intellij.openapi.util.Condition;
import com.intellij.util.containers.ContainerUtil;
import org.elixir_lang.beam.chunk.Atoms;
import org.elixir_lang.beam.chunk.Exports;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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

        Exports exports = beam.exports();

        assertNotNull(exports);

        int exportCount = exports.size();

        assertTrue("There are no exports", exportCount > 0);

        SortedSet<MacroNameArity> macroNameAritySortedSet = exports.macroNameAritySortedSet(atoms);

        assertEquals("There are nameless exports", exportCount, macroNameAritySortedSet.size());

        List<MacroNameArity> nodes = ContainerUtil.filter(
                macroNameAritySortedSet,
                new Condition<MacroNameArity>() {
                    @Override
                    public boolean value(MacroNameArity macroNameArity) {
                        return "node".equals(macroNameArity.name);
                    }
                }
        );

        assertEquals(nodes.size(), 2);

        assertEquals(0, (int) nodes.get(0).arity);
        assertEquals(1, (int) nodes.get(1).arity);
    }

    @Test
    public void erlangModule() throws IOException, OtpErlangDecodeException {
        Beam beam = beam("elixir_interpolation");

        assertNotNull(beam);

        Atoms atoms = beam.atoms();

        assertNotNull(atoms);

        assertEquals("elixir_interpolation", atoms.moduleName());

        Exports exports = beam.exports();

        assertNotNull(exports);

        int exportCount = exports.size();

        assertTrue("There are no exports", exportCount > 0);

        SortedSet<MacroNameArity> macroNameAritySortedSet = exports.macroNameAritySortedSet(atoms);

        assertEquals("There are nameless exports", exportCount, macroNameAritySortedSet.size());

        List<MacroNameArity> extracts = ContainerUtil.filter(
                macroNameAritySortedSet,
                new Condition<MacroNameArity>() {
                    @Override
                    public boolean value(MacroNameArity macroNameArity) {
                        return "extract".equals(macroNameArity.name);
                    }
                }
        );

        assertEquals(1, extracts.size());

        assertEquals(6, (int) extracts.get(0).arity);
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
