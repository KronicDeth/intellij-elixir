defmodule Calcinator.Alembic.Error do
  def bad_gateway do
    %Error{
      status: "502",
      title: "Bad Gateway"
    }
  end

  def not_found do
    %Error{status: "404", title: "Not Found"}
  end
end
