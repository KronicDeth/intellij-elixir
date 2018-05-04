defmodule Variable do
  def function() do
    var<caret>iable = Application.get_env(:variable, :key)

    :ok
  end
end
