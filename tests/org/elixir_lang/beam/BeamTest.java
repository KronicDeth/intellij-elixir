package org.elixir_lang.beam;

import com.ericsson.otp.erlang.OtpErlangDecodeException;
import org.elixir_lang.beam.chunk.Atoms;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.*;

public class BeamTest {
    @NotNull
    private String ebinDirectory;

    @Test
    public void elixirModule() throws IOException, OtpErlangDecodeException {
        assertModuleName("Elixir.Kernel", "Elixir.Kernel");
    }

    @Test
    public void erlangModule() throws IOException, OtpErlangDecodeException {
        assertModuleName("elixir_interpolation", "elixir_interpolation");
    }

    private void assertModuleName(@NotNull String baseName, @NotNull String moduleName) throws IOException, OtpErlangDecodeException {
                DataInputStream dataInputStream = new DataInputStream(
                new BufferedInputStream(
                        new FileInputStream(ebinDirectory + baseName + ".beam")
                )
        );
        Beam beam = Beam.from(dataInputStream);

        assertNotNull(beam);

        Atoms atoms = beam.atoms();

        assertNotNull(atoms);

        assertEquals(atoms.moduleName(), moduleName);
    }

    @Before
    public void setEbinDirectory() {
        String ebinDirectory = System.getenv("ELIXIR_EBIN_DIRECTORY");

        assertNotNull("ELIXIR_EBIN_DIRECTORY is not set", ebinDirectory);

        this.ebinDirectory = ebinDirectory;
    }
}
