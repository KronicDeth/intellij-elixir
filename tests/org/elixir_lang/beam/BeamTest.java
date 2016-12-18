package org.elixir_lang.beam;

import com.ericsson.otp.erlang.OtpErlangDecodeException;
import com.intellij.openapi.util.Pair;
import gnu.trove.THashSet;
import org.elixir_lang.beam.chunk.Atoms;
import org.elixir_lang.beam.chunk.Exports;
import org.elixir_lang.beam.chunk.exports.Export;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

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

        Pair<SortedMap<String, SortedMap<Integer, Export>>, SortedSet<Export>> exportByArityByNameNamelessExportSet =
                exports.exportByArityByName(atoms);

        assertNotNull(exportByArityByNameNamelessExportSet);

        Set<Export> namelessExportSet = exportByArityByNameNamelessExportSet.second;

        assertTrue("There are nameless exports", namelessExportSet.isEmpty());

        SortedMap<String, SortedMap<Integer, Export>> exportByArityByName = exportByArityByNameNamelessExportSet.first;

        // a name with multiple arities
        SortedMap<Integer, Export> exportByArity = exportByArityByName.get("node");

        assertNotNull(exportByArity);

        Set<Integer> expectedAritySet = new THashSet<Integer>();
        expectedAritySet.add(0);
        expectedAritySet.add(1);

        assertEquals("node arities do not match", expectedAritySet, exportByArity.keySet());
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

        Pair<SortedMap<String, SortedMap<Integer, Export>>, SortedSet<Export>> exportByArityByNameNamelessExportSet =
                exports.exportByArityByName(atoms);

        assertNotNull(exportByArityByNameNamelessExportSet);

        Set<Export> namelessExportSet = exportByArityByNameNamelessExportSet.second;

        assertTrue("There are nameless exports", namelessExportSet.isEmpty());

        SortedMap<String, SortedMap<Integer, Export>> exportByArityByName = exportByArityByNameNamelessExportSet.first;

        Map<Integer, Export> exportByArity = exportByArityByName.get("extract");

        assertNotNull(exportByArity);

        Export export = exportByArity.get(6);

        assertNotNull(export);
    }

    private Beam beam(@NotNull String baseName) throws IOException, OtpErlangDecodeException {
        DataInputStream dataInputStream = new DataInputStream(
                new BufferedInputStream(
                        new FileInputStream(ebinDirectory + baseName + ".beam")
                )
        );

        return Beam.from(dataInputStream);
    }

    @Before
    public void setEbinDirectory() {
        String ebinDirectory = System.getenv("ELIXIR_EBIN_DIRECTORY");

        assertNotNull("ELIXIR_EBIN_DIRECTORY is not set", ebinDirectory);

        this.ebinDirectory = ebinDirectory;
    }
}
