# IntelliJ must be spelled Intellij so that task can be invoked as `intellij_elixir.debug` since inflector can't be
# changed in mix
defmodule Mix.Tasks.IntellijElixir.Debug do
  use Mix.Task

  def run(_) do
    Mix.Task.run("loadconfig")
    # ensures that project and dep beams are available to be interpreted
    Mix.Task.run("compile", [])

    IntelliJElixir.Debugged.start()
  end
end
