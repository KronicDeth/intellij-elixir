defmodule AliasAs.Renamee do
  def hello, do: :world
end

defmodule AliasAsClient do
  alias AliasAs.Renamee, as: R

  def call do
    R.hello()
  end
end
