package org.elixir_lang.intellij_elixir;

import com.ericsson.otp.erlang.*;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiFile;
import org.apache.commons.lang.NotImplementedException;
import org.elixir_lang.ElixirLanguage;
import org.elixir_lang.IntellijElixir;
import org.elixir_lang.psi.ElixirFile;
import org.elixir_lang.psi.impl.ElixirPsiImplUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by luke.imhoff on 12/31/14.
 */
public class Quoter {
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

    public static void assertQuotedCorrectly(PsiFile file) {
        final String text = file.getText();
        final FileViewProvider fileViewProvider = file.getViewProvider();

        try {

            OtpErlangTuple quotedMessage = Quoter.quote(text);
            Quoter.assertMessageReceived(quotedMessage);

            OtpErlangAtom status = (OtpErlangAtom) quotedMessage.elementAt(0);
            String statusString = status.atomValue();

            if (statusString.equals("ok")) {
                OtpErlangMap map = (OtpErlangMap) quotedMessage.elementAt(1);

                OtpErlangAtom codeKey = new OtpErlangAtom("code");
                OtpErlangBinary receivedElixirString = (OtpErlangBinary) map.get(codeKey);
                byte[] receivedBytes = receivedElixirString.binaryValue();
                String receivedText = new String(receivedBytes, "UTF-8");
                assertEquals("code mismatch", text, receivedText);

                OtpErlangAtom quotedKey = new OtpErlangAtom("quoted");
                OtpErlangTuple receivedQuoted = (OtpErlangTuple) map.get(quotedKey);

                ElixirFile root = (ElixirFile) fileViewProvider.getPsi(ElixirLanguage.INSTANCE);
                OtpErlangObject quoted = ElixirPsiImplUtil.quote(root);

                assertEquals(receivedQuoted, quoted);
            } else if (statusString.equals("error")) {
              throw new NotImplementedException();
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

    @NotNull
    public static OtpErlangBinary elixirString(@NotNull String javaString) {
        final byte[] bytes = javaString.getBytes(Charset.forName("UTF-8"));
        return new OtpErlangBinary(bytes);
    }

    public static OtpErlangTuple quote(@NotNull String code) throws IOException, OtpErlangExit, OtpErlangDecodeException {
        final OtpNode otpNode = IntellijElixir.getLocalNode();
        final OtpMbox otpMbox = otpNode.createMbox();

        OtpErlangObject quoteMessage = Quoter.quoteMessage(code, otpMbox.self());
        otpMbox.send(REMOTE_NAME, IntellijElixir.REMOTE_NODE, quoteMessage);

        return (OtpErlangTuple) otpMbox.receive(TIMEOUT_IN_MILLISECONDS);
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
