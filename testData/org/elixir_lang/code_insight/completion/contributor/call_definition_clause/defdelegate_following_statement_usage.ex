defmodule Prefix.DefdelegateFollowingStatementUsage do
  alias Prefix.DefdelegateDeclaration

  def hello do
    DefdelegateDeclaration.<caret>
    :world
  end
end
