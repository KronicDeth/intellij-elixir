defmodule TypeGotoDeclaration do
  @type user_id :: integer()

  @spec id(user_<caret>id()) :: user_id()
  def id(value), do: value
end
