defmodule Calcinator.Resources do
  @type t ::
          :ok
          | :not_found
          | :ownership
          | :timeout
          | :unknown
end
