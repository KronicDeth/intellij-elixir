defmodule MyNamespace.Referenced do
  use Ecto.Schema

  schema "" do
    field "name", :string
  end

  def changeset(params) do
    %__MODULE__{}
    |> cast(params, ~w(name))
    |> validate_required(:name)
  end
end
