package org.elixir_lang.parser_definition;

import com.ericsson.otp.erlang.*;
import com.intellij.openapi.fileEditor.impl.LoadTextUtil;
import com.intellij.psi.FileViewProvider;
import com.intellij.testFramework.LightVirtualFile;
import org.apache.commons.lang.NotImplementedException;
import org.elixir_lang.ElixirLanguage;
import org.elixir_lang.psi.ElixirFile;
import org.elixir_lang.psi.impl.ElixirPsiImplUtil;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by luke.imhoff on 9/17/14.
 */
public class AtomParsingTestCase extends ParsingTestCase {
    public void testDoubleQuoted() {
        doTest(true);
    }

    public void testLiteral() {
        doTest(true);
        assertQuotedCorrectly();
    }

    public void testOperator() {
        doTest(true);
    }

    public void testSingleQuoted() {
        doTest(true);
    }

    protected void assertQuotedCorrectly() {
        String name = getTestName(false);
        try {
            String text = loadFile(name + "." + myFileExt);
            myFile = createPsiFile(name, text);
            ensureParsed(myFile);
            assertEquals("light virtual file text mismatch", text, ((LightVirtualFile)myFile.getVirtualFile()).getContent().toString());
            assertEquals("virtual file text mismatch", text, LoadTextUtil.loadText(myFile.getVirtualFile()));

            final FileViewProvider fileViewProvider = myFile.getViewProvider();
            assertEquals("doc text mismatch", text, fileViewProvider.getDocument().getText());
            assertEquals("psi text mismatch", text, myFile.getText());

            ElixirFile root = (ElixirFile) fileViewProvider.getPsi(ElixirLanguage.INSTANCE);

            OtpErlangObject quoted = ElixirPsiImplUtil.quote(root);
            OtpNode otpNode = new OtpNode("intellij-elixir");
            OtpMbox otpMbox = otpNode.createMbox();

            final byte[] bytes = text.getBytes(Charset.forName("UTF-8"));
            OtpErlangBinary elixirString = new OtpErlangBinary(bytes);

            OtpErlangAtom[] messageKeys = new OtpErlangAtom[]{
                    new OtpErlangAtom("quote"),
                    new OtpErlangAtom("for")
            };
            OtpErlangObject[] messageValues = new OtpErlangObject[]{
                    elixirString,
                    otpMbox.self()
            };
            OtpErlangMap quoteMessage = new OtpErlangMap(messageKeys, messageValues);
            /* remote name is Elixir.IntellijElixir.Quoter because all aliases in Elixir look like atoms prefixed with
               with Elixir. from erlang's perspective. */
            final String remoteName = "Elixir.IntellijElixir.Quoter";
            final String remoteNode = "intellij_elixir";
            otpMbox.send(remoteName, remoteNode, quoteMessage);

            long timeoutInMilliseconds = 1000;
            OtpErlangTuple quotedMessage = (OtpErlangTuple) otpMbox.receive(timeoutInMilliseconds);

            assertNotNull(
                    "did not receive message from " + remoteName + "@" + remoteNode + ".  Make sure it is running",
                    quotedMessage
            );

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

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/atom_parsing_test_case";
    }
}
