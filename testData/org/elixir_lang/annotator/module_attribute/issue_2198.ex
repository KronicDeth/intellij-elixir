defmodule Schema do
  alias Schema

  defmacro def_schema(fields) do
    quote bind_quoted: [fields: fields] do
      defstruct Keyword.keys(fields)

      @type t :: %__MODULE__{
                   unquote_splicing(Enum.map(fields, &Schema.spec_field/1))
                 }
    end
  end

  def spec_field(_) do
  end
end
