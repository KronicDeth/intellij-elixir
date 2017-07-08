defmodule Calcinator.Alembic.Error do
  def bad_gateway do
    %Error{
      status: "502",
      title: "Bad Gateway"
    }
  end
end
