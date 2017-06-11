defmodule Calcinator.Resources.Ecto.Repo do
  defmacro __using__([]) do
    quote do
      defoverridable [
        allow_sandbox_access: 1,
        changeset: 1,
        changeset: 2,
        delete: 2,
        full_associations: 1,
        get: 2,
        insert: 2,
        list: 1,
        sandboxed?: 0,
        update: 2,
        update: 3
      ]
    end
  end
end
