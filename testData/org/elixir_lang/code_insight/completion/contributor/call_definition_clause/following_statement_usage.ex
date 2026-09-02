defmodule Prefix.FollowingStatementUsage do
  alias Prefix.FollowingStatementDeclaration

  def hello do
    FollowingStatementDeclaration.<caret>
    :world
  end
end
