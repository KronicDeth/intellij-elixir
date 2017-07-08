defmodule Calcinator.Resources do
  @callback get(id, query_options) :: {:ok, struct} |
                                      {:error, :not_found} |
                                      {:error, :ownership} |
                                      {:error, :timeout} |
                                      {:error, reason :: term}
end
