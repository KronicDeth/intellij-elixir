package org.elixir_lang.mix

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project

class MissingSdk(project: Project, module: Module?) :
        Exception("${module?.name ?: "unknown"} module in ${project.name} project is missing an Elixir SDK") {
    constructor(module: Module) : this(module.project, module)
}
