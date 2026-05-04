defmodule ServiceClient do
  def start_link(args), do: GenServer.start_link(__MODULE__, args)
end

defmodule ServiceClientTest do
  def test_child_spec do
    assert %{start: {ServiceClient, :start_<caret>link, _args}} = ServiceClient.child_spec([])
  end
end
