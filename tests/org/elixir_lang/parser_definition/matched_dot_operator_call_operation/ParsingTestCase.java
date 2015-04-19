package org.elixir_lang.parser_definition.matched_dot_operator_call_operation;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.psi.*;
import org.elixir_lang.ElixirLanguage;
import org.elixir_lang.ElixirParserDefinition;
import org.elixir_lang.intellij_elixir.Quoter;
import org.elixir_lang.psi.impl.ElixirPsiImplUtil;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by luke.imhoff on 8/7/14.
 */
@org.junit.Ignore("abstract")
public abstract class ParsingTestCase extends org.elixir_lang.parser_definition.ParsingTestCase {
    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/matched_dot_operator_call_operation";
    }
}
