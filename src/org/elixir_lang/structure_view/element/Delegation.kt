package org.elixir_lang.structure_view.element;

import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.ElementDescriptionLocation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.usageView.UsageViewTypeLocation;
import org.elixir_lang.navigation.item_presentation.Parent;
import org.elixir_lang.psi.ElixirAccessExpression;
import org.elixir_lang.psi.ElixirList;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.structure_view.element.modular.Modular;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.elixir_lang.psi.call.name.Function.DEFDELEGATE;
import static org.elixir_lang.psi.call.name.Module.KERNEL;
import static org.elixir_lang.psi.impl.PsiElementImplKt.stripAccessExpression;
import static org.elixir_lang.psi.impl.call.CallImplKt.finalArguments;
import static org.elixir_lang.psi.impl.call.CallImplKt.keywordArgument;

public class Delegation extends Element<Call>  {
    /*
     * Fields
     */

    @NotNull
    private final List<TreeElement> childList = new ArrayList<TreeElement>();
    @NotNull
    private final Modular modular;

    /*
     * Static Methods
     */

    public static List<Call> callDefinitionHeadCallList(Call defdelegateCall) {
        List<Call> callDefinitionHeadCallList = null;

        PsiElement[] finalArguments = finalArguments(defdelegateCall);

        assert finalArguments != null;
        assert finalArguments.length > 0;

        PsiElement firstFinalArgument = finalArguments[0];

        if (firstFinalArgument instanceof ElixirAccessExpression) {
            PsiElement accessExpressionChild = stripAccessExpression(firstFinalArgument);

            if (accessExpressionChild instanceof ElixirList) {
                ElixirList list = (ElixirList) accessExpressionChild;

                Call[] listCalls = PsiTreeUtil.getChildrenOfType(list, Call.class);
                callDefinitionHeadCallList = filterCallDefinitionHeadCallList(listCalls);
            }
        } else if (firstFinalArgument instanceof Call) {
            Call call = (Call) firstFinalArgument;

            callDefinitionHeadCallList = filterCallDefinitionHeadCallList(call);
        }

        if (callDefinitionHeadCallList == null) {
            callDefinitionHeadCallList = Collections.emptyList();
        }

        return callDefinitionHeadCallList;
    }

    @NotNull
    public static List<Call> filterCallDefinitionHeadCallList(Call... calls) {
        List<Call> callList = Collections.emptyList();

        if (calls != null) {
            callList = new ArrayList<Call>(calls.length);

            for (Call call : calls) {
                if (CallDefinitionHead.Companion.is(call)) {
                    callList.add(call);
                }
            }
        }

        return callList;
    }

    public static String elementDescription(Call call, ElementDescriptionLocation location) {
        String elementDescription = null;

        if (location == UsageViewTypeLocation.INSTANCE) {
            elementDescription = "delegation";
        }

        return elementDescription;
    }

    public static boolean is(Call call) {
        return call.isCalling(KERNEL, DEFDELEGATE, 2);
    }

    /*
     * Constructors
     */

    public Delegation(@NotNull Modular modular, @NotNull Call call) {
        super(call);
        this.modular = modular;
    }

    /*
     *
     * Instance Methods
     *
     */

    /*
     * Public Instance Methods
     */

    /**
     * If {@code true}, when delegated, the first argument passed to the delegated function will be relocated to the end
     * of the arguments when dispatched to the target.
     *
     * @return defaults to {@code false} and when keyword argument is not parsable as boolean.
     */
    public boolean appendFirst() {
        PsiElement keywordValue = keywordArgument(navigationItem, "append_first");
        boolean appendFirst = false;

        if (keywordValue != null) {
            String keywordValueText = keywordValue.getText();

            if (keywordValueText.equals("true")) {
                appendFirst = true;
            }
        }

        return appendFirst;
    }

    /**
     * The value of the {@code :as} keyword argument
     *
     * @return text of the {@code :as} keyword value
     */
    @Nullable
    public String as() {
        return keywordArgumentText("as");
    }

    @NotNull
    public List<Call> callDefinitionHeadCallList() {
        return callDefinitionHeadCallList(navigationItem);
    }

    public void definition(CallDefinition callDefinition) {
      childList.add(callDefinition);
    }

    /**
     * The calls defined by this delegation
     *
     * @return the list of {@link CallDefinition} elements;
     */
    @NotNull
    @Override
    public TreeElement[] getChildren() {
        return childList.toArray(new TreeElement[childList.size()]);
    }

    /**
     * Returns the presentation of the tree element.
     *
     * @return the element presentation.
     */
    @NotNull
    @Override
    public ItemPresentation getPresentation() {
        Parent parent = (Parent) modular.getPresentation();
        String location = parent.getLocatedPresentableText();

        return new org.elixir_lang.navigation.item_presentation.Delegation(
                location,
                to(),
                as(),
                appendFirst()
        );
    }

    /**
     * The value of the {@code :to} keyword argument
     *
     * @return text of the {@code :to} keyword value
     */
    @Nullable
    public String to() {
        return keywordArgumentText("to");
    }

    /*
     * Private Instance Methods
     */

    /**
     * The text of the {@code keywordValueText} keyword argument.
     *
     * @param keywordValueText the text of the keyword value
     * @return the {@code PsiElement.getText()} of the keyword value if the keyword argument exists of the
     *   {@link Element#navigationItem}; {@code null} if the keyword argument does not exist.
     */
    @Nullable
    private String keywordArgumentText(@NotNull final String keywordValueText) {
        PsiElement keywordValue = keywordArgument(navigationItem, keywordValueText);
        String text = null;

        if (keywordValue != null) {
            text = keywordValue.getText();
        }

        return text;
    }

}
