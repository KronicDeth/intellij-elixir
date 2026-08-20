defmodule Renamee do
  def hello, do: :world
end

defmodule DirectClient do
  alias Renamee
  require Renamee
  import Renamee
  use Renamee

  def call do
    Renamee.hello()
  end
end
