defmodule ParameterInfo.AutoPopupRemoteOpeningParenthesis do
  def run do
    ParameterInfo.Remote.reduce<caret>
  end
end
