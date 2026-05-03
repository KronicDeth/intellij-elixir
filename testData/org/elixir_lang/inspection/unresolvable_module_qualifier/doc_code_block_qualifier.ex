defmodule IcWeb.ModelCase do
  @moduledoc """
  Helper for returning list of errors in model when passed certain data.

  ## Examples

      iex> errors_on(%User{}, %{password: "password"})
      [password: "is unsafe", name: "is blank"]

      iex> changeset = User.changeset(%User{}, password: "password")
      iex> {:password, "is unsafe"} in changeset.errors
      true
  """

  def errors_on(model, data) do
    model.__struct__.changeset(model, data).errors
  end
end
