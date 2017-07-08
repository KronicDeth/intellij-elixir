defmodule One do
  @moduledoc """
  One
    Two
   Three
  """

  def charlist do
    to_string '''
    One
      Two
     Three
    '''
  end

  def interpolated_charlist_sigil_heredoc do
    to_string ~c'''
    One
      Two
     Three
    '''
  end

  def interpolated_regex_heredoc do
    Regex.source ~r"""
    One
      Two
     Three
    """
  end

  def interpolated_sigil_heredoc do
    convert ~x"""
    One
      Two
     Three
    """
  end

  def interpolated_string_sigil_heredoc do
    to_charlist ~s"""
    One
      Two
     Three
    """
  end

  def interpolated_words_heredoc do
    Enum.count ~w"""
    One
      Two
     Three
    """
  end

  def literal_charlist_sigil_heredoc do
    to_string ~C'''
    One
      Two
     Three
    '''
  end

  def literal_regex_heredoc do
    Regex.source ~R"""
    One
      Two
     Three
    """
  end

  def literal_sigil_heredoc do
    convert ~X"""
    One
      Two
     Three
    """
  end

  def literal_string_sigil_heredoc do
    to_charlist ~S"""
    One
      Two
     Three
    """
  end

  def literal_words_heredoc do
    Enum.count ~W"""
    One
      Two
     Three
    """
  end
end
