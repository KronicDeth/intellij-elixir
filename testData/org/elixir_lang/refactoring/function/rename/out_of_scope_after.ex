defmodule ScopeA do
  def renamed_fun do
    :a
  end

  def use_a do
    renamed_fun()
  end
end

defmodule ScopeB do
  def shared_fun do
    :b
  end

  def use_b do
    shared_fun()
  end
end
