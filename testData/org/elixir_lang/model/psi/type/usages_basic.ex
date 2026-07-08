defmodule TypeUsagesBasic do
  @type user_<caret>id :: integer()

  @spec fetch(user_id()) :: {:ok, user_id()}
  def fetch(value), do: {:ok, value}
end
