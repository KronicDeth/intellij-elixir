package org.elixir_lang.mix

import org.elixir_lang.run.Configuration

class MissingWorkingDirectory(configuration: Configuration) :
        Exception("${configuration.name} needs a working directory to derive the module when the module is not set explicitly")
