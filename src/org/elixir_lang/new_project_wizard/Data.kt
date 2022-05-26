package org.elixir_lang.new_project_wizard

import com.intellij.openapi.observable.properties.ObservableMutableProperty
import com.intellij.openapi.projectRoots.Sdk

// Based on [IntelliJNewProjectWizardData](https://github.com/JetBrains/intellij-community/blob/efe1a8b3636421ecd3925b8f7e616b3da9bbc268/java/idea-ui/src/com/intellij/ide/projectWizard/generators/IntelliJNewProjectWizardData.kt#L7-L20)
interface Data {
    val sdkProperty: ObservableMutableProperty<Sdk?>
    val mixNewAppProperty: ObservableMutableProperty<String>
    val mixNewModuleProperty: ObservableMutableProperty<String>
    val mixNewSupProperty: ObservableMutableProperty<Boolean>
    val mixNewUmbrellaProperty: ObservableMutableProperty<Boolean>

    var sdk: Sdk?
    var mixNewApp: String
    var mixNewModule: String
    var mixNewSup: Boolean
    var mixNewUmbrella: Boolean
}
