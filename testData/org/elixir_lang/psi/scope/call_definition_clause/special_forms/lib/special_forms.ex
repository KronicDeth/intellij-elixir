defmodule Kernel.SpecialForms do
  defmacro __ENV__, do: error!([])
  defmacro __MODULE__, do: error!([])
  defmacro __DIR__, do: error!([])
  defmacro __CALLER__, do: error!([])
  defmacro __STACKTRACE__, do: error!([])
end
