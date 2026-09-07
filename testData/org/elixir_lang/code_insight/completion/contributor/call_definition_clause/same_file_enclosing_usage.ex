defmodule Outer do
  def before_function(a), do: a

  def run do
    Outer.<caret>
  end

  def after_function(a), do: a

  defdelegate delegated(a), to: Enum
end
