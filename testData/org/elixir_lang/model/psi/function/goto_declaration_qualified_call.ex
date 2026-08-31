defmodule GotoDeclarationQualifiedCall do
  def run(struct, params) do
    GotoDeclarationCapture.Referenced.change<caret>set(struct, params)
  end
end
