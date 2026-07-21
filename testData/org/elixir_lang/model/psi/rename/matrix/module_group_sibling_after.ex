defmodule One.Two do
end

defmodule One.Two.Three do
  def a, do: :a
end

defmodule One.Fresh do
  def b, do: :b
end

defmodule DeepGroupClient do
  alias One.{Two.Three, Fresh}

  def call do
    {Three.a(), Fresh.b()}
  end
end
