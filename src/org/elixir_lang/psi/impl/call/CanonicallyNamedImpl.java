package org.elixir_lang.psi.impl.call;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.util.containers.SmartHashSet;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.call.StubBased;
import org.elixir_lang.structure_view.element.modular.Implementation;
import org.elixir_lang.structure_view.element.modular.Module;
import org.elixir_lang.structure_view.element.modular.Protocol;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import static org.elixir_lang.psi.stub.type.call.Stub.isModular;
import static org.elixir_lang.structure_view.element.CallDefinitionClause.enclosingModularMacroCall;
import static org.elixir_lang.structure_view.element.modular.Implementation.forNameCollection;

public class CanonicallyNamedImpl {

    @Nullable
    public static String canonicalName(@NotNull StubBased stubBased) {
        String canonicalName;

        if (isModular(stubBased)) {
            String canonicalNameSuffix = null;

            if (Implementation.is(stubBased)) {
                String protocolName = Implementation.protocolName(stubBased);
                PsiElement forNameElement = Implementation.forNameElement(stubBased);
                String forName = null;

                if (forNameElement != null) {
                    forName = forNameElement.getText();
                }

                canonicalNameSuffix = StringUtil.notNullize(protocolName, "?") +
                        "." +
                        StringUtil.notNullize(forName, "?");
            } else if (Module.is(stubBased) || Protocol.is(stubBased)) {
                canonicalNameSuffix = org.elixir_lang.navigation.item_presentation.modular.Module.presentableText(
                        stubBased
                );
            }

            Call enclosing = enclosingModularMacroCall(stubBased);

            if (enclosing instanceof StubBased) {
                StubBased enclosingStubBased = (StubBased) enclosing;
                String canonicalNamePrefix = enclosingStubBased.canonicalName();

                canonicalName = StringUtil.notNullize(canonicalNamePrefix, "?") +
                        "." +
                        StringUtil.notNullize(canonicalNameSuffix, "?");
            } else {
                canonicalName = StringUtil.notNullize(canonicalNameSuffix, "?");
            }
        } else {
            canonicalName = stubBased.getName();
        }

        return canonicalName;
    }


    @NotNull
    public static Set<String> canonicalNameSet(@NotNull StubBased stubBased) {
        Set<String> canonicalNameSet;

        if (isModular(stubBased)) {
            Set<String> canonicalNameSuffixSet;

            if (Implementation.is(stubBased)) {
                String maybeProtocolName = Implementation.protocolName(stubBased);
                PsiElement forNameElement = Implementation.forNameElement(stubBased);
                Collection<String> maybeForNameCollection = null;

                if (forNameElement != null) {
                    maybeForNameCollection = forNameCollection(forNameElement);
                }

                String protocolName = StringUtil.notNullize(maybeProtocolName, "?");

                if (maybeForNameCollection == null) {
                    canonicalNameSuffixSet = Collections.singleton(protocolName + ".?");
                } else {
                    canonicalNameSuffixSet = new SmartHashSet<>(maybeForNameCollection.size());

                    for (@Nullable String maybeForName : maybeForNameCollection) {
                        String canonicalName = protocolName + "." + StringUtil.notNullize(maybeForName, "?");
                        canonicalNameSuffixSet.add(canonicalName);
                    }
                }
            } else if (Module.Companion.is(stubBased) || Protocol.is(stubBased)) {
                canonicalNameSuffixSet = Collections.singleton(
                        org.elixir_lang.navigation.item_presentation.modular.Module.presentableText(stubBased)
                );
            } else {
                canonicalNameSuffixSet = Collections.singleton("?");
            }

            Call enclosing = enclosingModularMacroCall(stubBased);

            if (enclosing instanceof StubBased) {
                StubBased enclosingStubBased = (StubBased) enclosing;
                Set<String> canonicalNamePrefixSet = enclosingStubBased.canonicalNameSet();

                canonicalNameSet = new SmartHashSet<>(
                        canonicalNamePrefixSet.size() * canonicalNameSuffixSet.size()
                );

                for (String canonicalNamePrefix : canonicalNamePrefixSet) {
                    for (String canonicalNameSuffix : canonicalNameSuffixSet) {
                        canonicalNameSet.add(
                                canonicalNamePrefix + "." + canonicalNameSuffix
                        );
                    }
                }
            } else {
                canonicalNameSet = canonicalNameSuffixSet;
            }
        } else {
            String canonicalName = stubBased.getName();

            if (canonicalName != null) {
                canonicalNameSet = Collections.singleton(canonicalName);
            } else {
                canonicalNameSet = Collections.emptySet();
            }
        }

        return canonicalNameSet;
    }
}
