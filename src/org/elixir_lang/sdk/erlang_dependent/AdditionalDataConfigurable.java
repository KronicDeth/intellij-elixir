package org.elixir_lang.sdk.erlang_dependent;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkModel;
import com.intellij.openapi.projectRoots.SdkModificator;
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.util.Comparing;
import com.intellij.ui.ListCellRendererWrapper;
import com.intellij.util.ui.JBUI;
import org.elixir_lang.sdk.elixir.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;

import static org.elixir_lang.sdk.elixir.Type.addNewCodePathsFromInternErlangSdk;
import static org.elixir_lang.sdk.elixir.Type.removeCodePathsFromInternalErlangSdk;
import static org.elixir_lang.sdk.erlang_dependent.Type.staticIsValidDependency;

public class AdditionalDataConfigurable implements com.intellij.openapi.projectRoots.AdditionalDataConfigurable {
    private final JLabel internalErlangSdkLabel = new JLabel("Internal Erlang SDK:");
    private final DefaultComboBoxModel<Sdk> internalErlangSdksComboBoxModel = new DefaultComboBoxModel<>();
    private final ComboBox internalErlangSdksComboBox = new ComboBox(internalErlangSdksComboBoxModel);
    private final SdkModel sdkModel;
    private final SdkModel.Listener sdkModelListener;
    private final SdkModificator sdkModificator;
    private Sdk elixirSdk;
    private boolean modified;
    private boolean freeze = false;

    public AdditionalDataConfigurable(@NotNull SdkModel sdkModel, SdkModificator sdkModificator) {
        this.sdkModel = sdkModel;
        this.sdkModificator = sdkModificator;

        sdkModelListener = new SdkModel.Listener() {
            public void sdkAdded(Sdk sdk) {
                if (staticIsValidDependency(sdk)) {
                    addErlangSdk(sdk);
                }
            }

            public void beforeSdkRemove(Sdk sdk) {
                if (staticIsValidDependency(sdk)) {
                    removeErlangSdk(sdk);
                }
            }

            public void sdkChanged(Sdk sdk, String previousName) {
                if (staticIsValidDependency(sdk)) {
                    updateErlangSdkList(sdk, previousName);
                }
            }

            public void sdkHomeSelected(final Sdk sdk, final String newSdkHome) {
                if (staticIsValidDependency(sdk)) {
                    internalErlangSdkUpdate(sdk);
                }
            }
        };
        this.sdkModel.addListener(sdkModelListener);
    }

    private void updateJdkList() {
        internalErlangSdksComboBoxModel.removeAllElements();

        for (Sdk sdk : sdkModel.getSdks()) {
            if (staticIsValidDependency(sdk)) {
                internalErlangSdksComboBoxModel.addElement(sdk);
            }
        }
    }

    public void setSdk(Sdk sdk) {
        elixirSdk = sdk;
    }

    public JComponent createComponent() {
        JPanel wholePanel = new JPanel(new GridBagLayout());

        wholePanel.add(
                internalErlangSdkLabel,
                new GridBagConstraints(
                        0,
                        GridBagConstraints.RELATIVE,
                        1,
                        1,
                        0,
                        1,
                        GridBagConstraints.WEST,
                        GridBagConstraints.NONE,
                        JBUI.emptyInsets(),
                        0,
                        0
                )
        );
        wholePanel.add(
                internalErlangSdksComboBox,
                new GridBagConstraints(
                        1,
                        GridBagConstraints.RELATIVE,
                        1,
                        1,
                        1,
                        1,
                        GridBagConstraints.EAST,
                        GridBagConstraints.HORIZONTAL,
                        JBUI.insets(0, 30, 0, 0),
                        0,
                        0
                )
        );
        internalErlangSdksComboBox.setRenderer(
                new ListCellRendererWrapper() {
                    @Override
                    public void customize(JList list, Object value, int index, boolean selected, boolean hasFocus) {
                        if (value instanceof Sdk) {
                            setText(((Sdk) value).getName());
                        }
                    }
                }
        );
        internalErlangSdksComboBox.addItemListener(itemEvent -> {
            if (!freeze) {
                final int stateChange = itemEvent.getStateChange();
                final Sdk internalErlangSdk = (Sdk) itemEvent.getItem();

                if (stateChange == ItemEvent.DESELECTED) {
                    removeCodePathsFromInternalErlangSdk(elixirSdk, internalErlangSdk, sdkModificator);
                } else if (stateChange == ItemEvent.SELECTED) {
                    addNewCodePathsFromInternErlangSdk(elixirSdk, internalErlangSdk, sdkModificator);
                    modified = true;
                }
            }
        });

        modified = true;

        return wholePanel;
    }

    private void internalErlangSdkUpdate(@NotNull final Sdk sdk) {
        SdkAdditionalData sdkAdditionalData = (SdkAdditionalData) sdk.getSdkAdditionalData();
        Sdk erlangSdk;

        if (sdkAdditionalData != null) {
            erlangSdk = sdkAdditionalData.getErlangSdk();
        } else {
            erlangSdk = null;
        }

        if (erlangSdk == null || internalErlangSdksComboBoxModel.getIndexOf(erlangSdk) == -1) {
            internalErlangSdksComboBoxModel.addElement(erlangSdk);
        } else {
            internalErlangSdksComboBoxModel.setSelectedItem(erlangSdk);
        }
    }

    public boolean isModified() {
        return modified;
    }

    public void apply() throws ConfigurationException {
        writeInternalErlangSdk((Sdk) internalErlangSdksComboBox.getSelectedItem());
        modified = false;
    }

    private void writeInternalErlangSdk(@Nullable Sdk erlangSdk) {
        writeInternalErlangSdk(elixirSdk.getSdkModificator(), erlangSdk);
    }

    private void writeInternalErlangSdk(@NotNull SdkModificator sdkModificator, @Nullable Sdk erlangSdk) {
        SdkAdditionalData sdkAdditionData = new SdkAdditionalData(
                erlangSdk,
                elixirSdk
        );
        sdkModificator.setSdkAdditionalData(sdkAdditionData);
        ApplicationManager.getApplication().runWriteAction(sdkModificator::commitChanges);
        ((ProjectJdkImpl) elixirSdk).resetVersionString();
    }

    public void reset() {
        freeze = true;
        updateJdkList();
        freeze = false;

        if (elixirSdk != null && elixirSdk.getSdkAdditionalData() instanceof SdkAdditionalData) {
            final SdkAdditionalData sdkAdditionalData = (SdkAdditionalData) elixirSdk.getSdkAdditionalData();
            final Sdk erlangSdk = sdkAdditionalData.getErlangSdk();

            if (erlangSdk != null) {
                for (int i = 0; i < internalErlangSdksComboBoxModel.getSize(); i++) {
                    if (Comparing.strEqual(
                            internalErlangSdksComboBoxModel.getElementAt(i).getName(),
                            erlangSdk.getName()
                    )) {
                        internalErlangSdksComboBox.setSelectedIndex(i);
                        break;
                    }
                }
            }

            modified = false;
        }
    }

    public void disposeUIResources() {
        sdkModel.removeListener(sdkModelListener);
    }

    private void addErlangSdk(final Sdk sdk) {
        internalErlangSdksComboBoxModel.addElement(sdk);
    }

    private void removeErlangSdk(final Sdk sdk) {
        if (internalErlangSdksComboBoxModel.getSelectedItem().equals(sdk)) {
            modified = true;
        }

        internalErlangSdksComboBoxModel.removeElement(sdk);
    }

    private void updateErlangSdkList(Sdk sdk, String previousName) {
        final Sdk[] sdks = sdkModel.getSdks();

        for (Sdk currentSdk : sdks) {
            if (currentSdk.getSdkType() instanceof Type) {
                final SdkAdditionalData sdkAdditionalData = (SdkAdditionalData) currentSdk.getSdkAdditionalData();
                final Sdk erlangSdk = sdkAdditionalData.getErlangSdk();

                if (erlangSdk != null && Comparing.equal(erlangSdk.getName(), previousName)) {
                    sdkAdditionalData.setErlangSdk(sdk);
                }
            }
        }
        updateJdkList();
    }
}
