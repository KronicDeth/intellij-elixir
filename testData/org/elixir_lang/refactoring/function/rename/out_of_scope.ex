defmodule ScopeA do
  def shared_<caret>fun do
    :a
  end

  def use_a do
    shared_fun()
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
