defmodule Calcinator.Alembic.Document do
  @doc """
  Converts an error `reason` from that isn't a standard format (such as those from the backing store) to a
  500 Internal Server Error JSONAPI error, but with `id` set to `id` that is also used in `Logger.error`, so that
  `reason`, which should remain private to limit implementation disclosures that could lead to security issues.

  ## Log Messages

  ```
  id=UUIDv4 reason=inspect(reason)
  ```

  """
  @spec error_reason(term) :: Document.t
  def error_reason(reason) do
    reason
    |> Error.error_reason()
    |> Error.to_document()
  end
end
