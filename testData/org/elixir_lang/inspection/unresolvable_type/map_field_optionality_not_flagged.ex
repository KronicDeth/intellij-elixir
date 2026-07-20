defmodule MapFieldOptionality do
  @type t :: %{
          required(atom()) => non_neg_integer(),
          optional(binary()) => integer()
        }

  @spec record(report :: term()) :: %{required(atom()) => non_neg_integer()}
  def record(report), do: %{report => 0}
end
