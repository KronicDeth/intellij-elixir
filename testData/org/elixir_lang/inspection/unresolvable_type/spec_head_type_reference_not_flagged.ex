defmodule SpecHeadTypeReference do
  @type config :: map()
  @type opts :: map()

  @spec check(config, opts) :: :ok
  def check(_config, _opts), do: :ok
end
