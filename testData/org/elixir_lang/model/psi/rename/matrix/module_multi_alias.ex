defmodule Grouped.Renamee do
  def hello, do: :world
end

defmodule Grouped.Sibling do
  def hi, do: :there
end

defmodule MultiAliasClient do
  alias Grouped.{Renamee, Sibling}

  def call do
    {Renamee.hello(), Sibling.hi()}
  end
end
