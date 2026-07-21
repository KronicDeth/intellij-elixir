defmodule Fresh do
  def hello, do: :world
end

defmodule DirectClient do
  alias Fresh
  require Fresh
  import Fresh
  use Fresh

  def call do
    Fresh.hello()
  end
end
