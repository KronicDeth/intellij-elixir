defmodule MyNamespace.Local do
  use Ecto.Schema

  schema "" do
    field "name", :string
  end

  def changeset(params) do
    %__MODULE__{}
    |> <caret>changeset(params)
  end

  def changeset(data, params) do
    data
    |> cast(params, ~w(name))
    |> validate_required(:name)
  end
end
