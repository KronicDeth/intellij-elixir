package org.elixir_lang.intellij_elixir;

import com.ericsson.otp.erlang.*;
import com.intellij.psi.PsiFile;
import org.elixir_lang.IntellijElixir;
import org.elixir_lang.psi.impl.ElixirPsiImplUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by luke.imhoff on 12/31/14.
 */
public class Quoter {
    public static final OtpErlangAtom CODE_KEY = new OtpErlangAtom("code");
    public static final OtpErlangAtom QUOTED_KEY = new OtpErlangAtom("quoted");
    /* remote name is Elixir.IntellijElixir.Quoter because all aliases in Elixir look like atoms prefixed with
       with Elixir. from erlang's perspective. */
    public static final String REMOTE_NAME = "Elixir.IntellijElixir.Quoter";
    public static final int TIMEOUT_IN_MILLISECONDS = 50;

    public static void assertMessageReceived(OtpErlangObject message) {
        assertNotNull(
                "did not receive message from " + REMOTE_NAME + "@" + IntellijElixir.REMOTE_NODE + ".  Make sure it is running",
                message
        );
    }

    public static void assertError(PsiFile file) {
        final String text = file.getText();

        try {
            OtpErlangTuple quotedMessage = Quoter.quote(text);
            Quoter.assertMessageReceived(quotedMessage);

            OtpErlangAtom status = (OtpErlangAtom) quotedMessage.elementAt(0);
            String statusString = status.atomValue();

            assertEquals(statusString, "error");
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        } catch (OtpErlangDecodeException e) {
            throw new RuntimeException(e);
        } catch (OtpErlangExit e) {
            throw new RuntimeException(e);
        }
    }

    public static void assertCodeEquals(String expectedCode, OtpErlangMap quotedMessageMap) {
        String actualCode = code(quotedMessageMap);
        assertEquals("code mismatch", expectedCode, actualCode);
    }

    public static void assertQuotedCorrectly(PsiFile file) {
        final String text = file.getText();

        try {
            OtpErlangTuple quotedMessage = Quoter.quote(text);
            Quoter.assertMessageReceived(quotedMessage);

            OtpErlangAtom status = (OtpErlangAtom) quotedMessage.elementAt(0);
            String statusString = status.atomValue();
            OtpErlangMap map = (OtpErlangMap) quotedMessage.elementAt(1);
            assertCodeEquals(text, map);
            OtpErlangObject expectedQuoted = quoted(map);

            if (statusString.equals("ok")) {
                OtpErlangObject actualQuoted = ElixirPsiImplUtil.quote(file);
                assertEquals(expectedQuoted, actualQuoted);
            } else if (statusString.equals("error")) {
                OtpErlangTuple error = (OtpErlangTuple) expectedQuoted;

                OtpErlangLong line = (OtpErlangLong) error.elementAt(0);

                OtpErlangBinary messageBinary = (OtpErlangBinary) error.elementAt(1);
                String message = javaString(messageBinary);

                OtpErlangBinary tokenBinary = (OtpErlangBinary) error.elementAt(2);
                String token = javaString(tokenBinary);

                throw new AssertionError(
                        "intellij_elixir returned " + message + " on line " + line + " due to " + token  +
                                ", use assertQuotesAroundError if error is expect in Elixir natively, " +
                                "but not in intellij-elixir plugin"
                );
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        } catch (OtpErlangDecodeException e) {
            throw new RuntimeException(e);
        } catch (OtpErlangExit e) {
            throw new RuntimeException(e);
        }
    }

    @Contract(pure = true)
    @NotNull
    public static String code(@NotNull OtpErlangMap quotedMessageMap) {
        OtpErlangBinary actualElixirString = (OtpErlangBinary) quotedMessageMap.get(CODE_KEY);
        byte[] actualBytes = actualElixirString.binaryValue();

        return new String(actualBytes, Charset.forName("UTF-8"));
    }

    @NotNull
    public static OtpErlangBinary elixirString(@NotNull String javaString) {
        final byte[] bytes = javaString.getBytes(Charset.forName("UTF-8"));
        return new OtpErlangBinary(bytes);
    }

    @NotNull
    public static String javaString(@NotNull OtpErlangBinary elixirString) {
        byte[] bytes = elixirString.binaryValue();
        return new String(bytes, Charset.forName("UTF-8"));
    }

    public static OtpErlangTuple quote(@NotNull String code) throws IOException, OtpErlangExit, OtpErlangDecodeException {
        final OtpNode otpNode = IntellijElixir.getLocalNode();
        final OtpMbox otpMbox = otpNode.createMbox();

        OtpErlangObject quoteMessage = Quoter.quoteMessage(code, otpMbox.self());
        otpMbox.send(REMOTE_NAME, IntellijElixir.REMOTE_NODE, quoteMessage);

        return (OtpErlangTuple) otpMbox.receive(TIMEOUT_IN_MILLISECONDS);
    }

    @NotNull
    public static OtpErlangObject quoted(@NotNull OtpErlangMap quotedMessageMap) {
        return quotedMessageMap.get(QUOTED_KEY);
    }

    public static OtpErlangObject quoteMessage(final String text, final OtpErlangPid self) {
        final OtpErlangAtom[] messageKeys = new OtpErlangAtom[]{
                new OtpErlangAtom("quote"),
                new OtpErlangAtom("for")
        };
        final OtpErlangObject[] messageValues = new OtpErlangObject[]{
                elixirString(text),
                self
        };

        return new OtpErlangMap(messageKeys, messageValues);
    }
}
