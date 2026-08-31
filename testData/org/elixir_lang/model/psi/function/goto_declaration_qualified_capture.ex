defmodule GotoDeclarationQualifiedCapture do
  def run do
    &GotoDeclarationCapture.Referenced.change<caret>set/2
  end
end
