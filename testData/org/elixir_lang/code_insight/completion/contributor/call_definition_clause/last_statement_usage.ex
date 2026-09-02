defmodule Prefix.LastStatementUsage do
  alias Prefix.FollowingStatementDeclaration

  def hello do
    :world
    FollowingStatementDeclaration.<caret>
  end
end
