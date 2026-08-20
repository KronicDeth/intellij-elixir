defmodule CasePattern do
  def run(value) do
    case value do
      {:ok, fresh} -> fresh
      _ -> nil
    end
  end
end
