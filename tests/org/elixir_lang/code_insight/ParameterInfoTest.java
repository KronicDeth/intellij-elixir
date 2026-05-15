package org.elixir_lang.code_insight;

import com.intellij.lang.parameterInfo.CreateParameterInfoContext;
import com.intellij.testFramework.utils.parameterInfo.MockCreateParameterInfoContext;
import com.intellij.testFramework.utils.parameterInfo.MockParameterInfoUIContext;
import org.elixir_lang.PlatformTestCase;
import org.elixir_lang.psi.Arguments;
import org.elixir_lang.psi.call.Call;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class ParameterInfoTest extends PlatformTestCase {

    public void testBareHeadPreferredOverImplementationClauses() {
        myFixture.configureByFiles("bare_head_usage.ex", "bare_head_declaration.ex");

        ParameterInfo handler = new ParameterInfo();
        CreateParameterInfoContext context = new MockCreateParameterInfoContext(myFixture.getEditor(), myFixture.getFile());
        Arguments args = handler.findElementForParameterInfo(context);

        assertNotNull("Should find Arguments element at caret", args);

        handler.showParameterInfo(args, context);
        Object[] items = context.getItemsToShow();

        assertNotNull("Should have parameter info items", items);
        assertEquals("Multiple clause heads should be deduplicated to one entry", 1, items.length);

        // Render the single item and verify it shows the bare head's canonical parameters
        Call call = assertInstanceOf(items[0], Call.class);
        MockParameterInfoUIContext<Arguments> uiContext = new MockParameterInfoUIContext<>(args);
        uiContext.setCurrentParameterIndex(0);
        handler.updateUI(call, uiContext);

        assertEquals("enumerable, nth, fun", uiContext.getText());
    }

    public void testDifferentAritiesPreservedAsSeparateHints() {
        myFixture.configureByFiles("different_arity_usage.ex", "different_arity_declaration.ex");

        ParameterInfo handler = new ParameterInfo();
        CreateParameterInfoContext context = new MockCreateParameterInfoContext(myFixture.getEditor(), myFixture.getFile());
        Arguments args = handler.findElementForParameterInfo(context);

        assertNotNull("Should find Arguments element at caret", args);

        handler.showParameterInfo(args, context);
        Object[] items = context.getItemsToShow();

        assertNotNull("Should have parameter info items", items);
        assertEquals("Different arities (process/1, process/2) should produce separate hints", 2, items.length);

        // Render both items and collect their parameter text
        Set<String> paramTexts = Arrays.stream(items)
                .map(item -> {
                    Call call = assertInstanceOf(item, Call.class);
                    MockParameterInfoUIContext<Arguments> uiContext = new MockParameterInfoUIContext<>(args);
                    uiContext.setCurrentParameterIndex(0);
                    handler.updateUI(call, uiContext);
                    return uiContext.getText();
                })
                .collect(Collectors.toSet());

        assertTrue("Should include process/1 parameters", paramTexts.contains("item"));
        assertTrue("Should include process/2 parameters", paramTexts.contains("item, opts"));
    }

    /*
     * Protected Instance Methods
     */

    @Override
    protected String getTestDataPath() {
        return "testData/org/elixir_lang/code_insight/parameter_info";
    }
}
