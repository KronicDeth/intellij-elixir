defmodule MyApp.ConnCase do
  use ExUnit.CaseTemplate

  using do
    quote do
      alias MyApp.Repo
    end
  end
end
