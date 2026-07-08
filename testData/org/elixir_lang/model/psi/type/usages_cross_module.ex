defmodule TypeUsagesCrossModule do
  @type user_<caret>id :: integer()

  @spec fetch(user_id()) :: integer()
  def fetch(value), do: value
end

defmodule TypeUsagesCrossModuleOther do
  @type user_id :: atom()
end
