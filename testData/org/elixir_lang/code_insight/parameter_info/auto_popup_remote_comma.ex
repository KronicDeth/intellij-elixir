defmodule ParameterInfo.AutoPopupRemoteComma do
  def run do
    ParameterInfo.Remote.reduce(1<caret>)
  end
end
