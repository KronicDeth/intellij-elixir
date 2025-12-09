defmodule BracketOp do
  @moduledoc false

  @callback docs_uri() :: binary()
  defmacro __using__(opts) do
    quote do
      # Unquote with bracket operation in one of the unquoted values
      unquote(docs_uri)
      unquote(opts[:other_key])
    end
  end
end
