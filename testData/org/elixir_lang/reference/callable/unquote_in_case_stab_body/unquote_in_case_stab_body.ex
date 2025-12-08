defmodule CaseStabBody do
  @moduledoc false

  @callback docs_uri() :: binary()
  defmacro __using__(opts) do
    case :ok do
      :ok ->
        quote do
          unquote(docs_uri)
          unquote(other_var)
        end
    end
  end
end
