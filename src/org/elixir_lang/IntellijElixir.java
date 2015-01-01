package org.elixir_lang;

import com.ericsson.otp.erlang.OtpNode;

import java.io.IOException;

/**
 * Created by luke.imhoff on 12/31/14.
 */
public class IntellijElixir {
    private static OtpNode localNode = null;
    public static final String REMOTE_NODE = "intellij_elixir";

    public static OtpNode getLocalNode() throws IOException {
        if (localNode == null) {
            localNode = new OtpNode("intellij-elixir");
        }

        return localNode;
    }
}
