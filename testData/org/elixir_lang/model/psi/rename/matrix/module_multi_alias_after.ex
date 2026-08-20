defmodule Grouped.Fresh do
  def hello, do: :world
end

defmodule Grouped.Sibling do
  def hi, do: :there
end

defmodule MultiAliasClient do
  alias Grouped.{Fresh, Sibling}

  def call do
    {Fresh.hello(), Sibling.hi()}
  end
end
