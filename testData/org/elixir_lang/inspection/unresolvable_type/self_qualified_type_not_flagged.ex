defmodule SelfQualifiedType do
  @type t :: %__MODULE__{field: term()}
  defstruct [:field]

  @spec build(__MODULE__.t()) :: :ok
  def build(_struct), do: :ok
end
