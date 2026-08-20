defmodule AliasAs.Fresh do
  def hello, do: :world
end

defmodule AliasAsClient do
  alias AliasAs.Fresh, as: R

  def call do
    R.hello()
  end
end
