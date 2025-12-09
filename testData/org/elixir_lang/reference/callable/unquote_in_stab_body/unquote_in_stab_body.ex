defmodule StabBody do
  @moduledoc false

  @callback docs_uri() :: binary()
  defmacro __using__(opts) do
    docs_uri =
        quote do
            unquote(opts[:docs_uri])
        end

    quote do
      @moduledoc unquote(module_doc)
      unquote(docs_uri)
    end
  end
end
