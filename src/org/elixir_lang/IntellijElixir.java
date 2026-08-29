package org.elixir_lang;

import com.ericsson.otp.erlang.OtpNode;

import java.io.IOException;

/**
 * Created by kadie.enheduanna.inanna on 12/31/14.
 */
public class IntellijElixir {
    private static OtpNode localNode = null;

    /**
     * Both node names are overridable because Erlang registers a distributed node by name with the
     * machine-wide epmd, which allows one node per name. With the names fixed, two checkouts running
     * tests at once collide: the second quoter exits with "the name ... seems to be in use by another
     * Erlang node", and so would the second test JVM's own node. The build sets these per checkout;
     * unset, both fall back to the literals used before they were configurable.
     */
    public static final String REMOTE_NODE =
            System.getProperty("elixir.quoter.remoteNode", "intellij_elixir@127.0.0.1");

    private static final String LOCAL_NODE =
            System.getProperty("elixir.quoter.localNode", "intellij-elixir@127.0.0.1");

    public static OtpNode getLocalNode() throws IOException {
        if (localNode == null) {
            localNode = new OtpNode(LOCAL_NODE, "intellij_elixir");
        }

        return localNode;
    }
}
