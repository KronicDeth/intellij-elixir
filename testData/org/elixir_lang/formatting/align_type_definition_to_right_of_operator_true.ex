defmodule Calcinator.Resources.Page do
  defstruct ~w(number size)a

  @type t :: %__MODULE__{
               number: pos_integer,
               size: pos_integer
             }
end
