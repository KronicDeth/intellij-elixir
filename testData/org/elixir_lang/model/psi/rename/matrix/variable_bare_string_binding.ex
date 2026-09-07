defmodule BareStringBindingSites do
  def run do
    "#{renamee = 1}"
    renamee
  end
end
