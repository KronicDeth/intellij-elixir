defmodule CasePattern do
  def run(value) do
    case value do
      {:ok, renamee} -> renamee
      _ -> nil
    end
  end
end
