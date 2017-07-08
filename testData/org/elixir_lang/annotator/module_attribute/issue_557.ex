defmodule Calcinator.View do
  @callback show_relationship(related, %{optional(:params) => params, optional(:related) => related,}) when related :: struct | nil
end
