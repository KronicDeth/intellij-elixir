defmodule TeamCityExUnitFormatter do
  @moduledoc false

  use GenServer

  # Functions

  ## GenServer Callbacks

  def handle_cast(event, state) do
    updated_state = TeamCityExUnitFormatting.put_event(state, event)

    {:noreply, updated_state}
  end

  def init(opts), do: {:ok, TeamCityExUnitFormatting.new(opts)}
end
