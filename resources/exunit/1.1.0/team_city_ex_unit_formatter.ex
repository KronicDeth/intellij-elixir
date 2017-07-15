# Originally based on https://github.com/lixhq/teamcity-exunit-formatter, but it did not work for parallel tests: IDEA
# does not honor flowId, so needed to use the nodeId/parentNodeIde system
#
# nodeId and parentNodeId system is documented in
# https://intellij-support.jetbrains.com/hc/en-us/community/posts/115000389550/comments/115000330464
defmodule TeamCityExUnitFormatter do
  @moduledoc false

  use GenEvent

  # Functions

  ## GenEvent Callbacks

  def handle_event(event, state) do
    updated_state = TeamCityExUnitFormatting.put_event(state, event)

    {:ok, updated_state}
  end

  def init(opts), do: {:ok, TeamCityExUnitFormatting.new(opts)}
end
