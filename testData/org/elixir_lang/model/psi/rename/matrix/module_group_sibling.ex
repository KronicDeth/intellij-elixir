defmodule One.Two do
end

defmodule One.Two.Three do
  def a, do: :a
end

defmodule One.Four do
  def b, do: :b
end

defmodule DeepGroupClient do
  alias One.{Two.Three, Four}

  def call do
    {Three.a(), Four.b()}
  end
end
