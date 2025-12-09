defmodule AnonymousFunStabBody do
  @moduledoc false

  @callback docs_uri() :: binary()
  defmacro __using__(opts) do
    fn ->
      quote do
        unquote(docs_uri)
        unquote(other_var)
      end
    end
  end
end
