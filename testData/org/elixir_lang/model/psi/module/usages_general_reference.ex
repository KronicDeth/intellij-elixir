defmodule MyApp.Modul<caret>e do
end

defmodule MyApp.Supervisor do
  def init(_arg) do
    Supervisor.init([MyApp.Module])
  end
end
