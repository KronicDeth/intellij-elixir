defmodule SpecNamedArgLabelSameNameAsType do
  @type my_id :: non_neg_integer()

  @spec do_it(my_id :: my_id()) :: :ok
  def do_it(_value), do: :ok
end
