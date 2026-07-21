defmodule One.Two do
end

defmodule One.Two.Fresh do
  def a, do: :a
end

defmodule One.Four do
  def b, do: :b
end

defmodule DeepGroupClient do
  alias One.{Two.Fresh, Four}

  def call do
    {Fresh.a(), Four.b()}
  end
end
