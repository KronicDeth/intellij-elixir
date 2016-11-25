# https://github.com/lixhq/teamcity-exunit-formatter
defmodule TeamCityExUnitFormatter do
  @moduledoc false

  use GenEvent

  def formatter(_color, msg), do: msg

  def init(opts) do
    config = %{
      seed: opts[:seed],
      trace: opts[:trace],
      width: 80,
      tests_counter: 0,
      failures_counter: 0,
      skipped_counter: 0,
      invalids_counter: 0
    }
    {:ok, config}
  end

  def handle_event({:case_started, %ExUnit.TestCase{name: name}}, config) do
    IO.puts format :test_suite_started, name: name
    {:ok, config}
  end

  def handle_event({:case_finished, %ExUnit.TestCase{name: name}}, config) do
    IO.puts format :test_suite_finished, name: name
    {:ok, config}
  end

  def handle_event({:test_started, %ExUnit.Test{name: name, case: the_case, tags: tags}}, config) do
    IO.puts format :test_started, name: "#{the_case}.#{name}", locationHint: "file://#{tags[:file]}:#{tags[:line]}"
    {:ok, config}
  end

  def handle_event({:test_finished, %ExUnit.Test{name: name, case: the_case, time: time, state: {:failed, {_, reason, _} = failed}} = test}, config) do
    formatted = ExUnit.Formatter.format_test_failure(test, failed, config.failures_counter + 1, config.width, &formatter/2)
    IO.puts format :test_failed, name: "#{the_case}.#{name}", message: inspect(reason), details: formatted
    IO.puts format :test_finished, name: "#{the_case}.#{name}", duration: div(time, 1000)
    {:ok, %{config | tests_counter: config.tests_counter + 1,
                     failures_counter: config.failures_counter + 1}}
  end

  def handle_event({:test_finished, %ExUnit.Test{name: name, case: the_case, time: time, state: {:failed, failed}} = test}, config) when is_list(failed) do
    formatted = ExUnit.Formatter.format_test_failure(test, failed, config.failures_counter + 1, config.width, &formatter/2)
    message = Enum.map_join(failed, "", fn {_kind, reason, _stack} -> inspect(reason) end)
    IO.puts format :test_failed, name: "#{the_case}.#{name}", message: message, details: formatted
    IO.puts format :test_finished, name: "#{the_case}.#{name}", duration: div(time, 1000)
    {:ok, %{config | tests_counter: config.tests_counter + 1,
                     failures_counter: config.failures_counter + 1}}
  end

  def handle_event({:test_finished, %ExUnit.Test{name: name, case: the_case, state: {:skip, _}}}, config) do
    IO.puts format :test_ignored, name: "#{the_case}.#{name}"
    IO.puts format :test_finished, name: "#{the_case}.#{name}"
    {:ok, %{config | tests_counter: config.tests_counter + 1,
                     skipped_counter: config.skipped_counter + 1}}
  end

  def handle_event({:test_finished, %ExUnit.Test{name: name, case: the_case, time: time}}, config) do
    IO.puts format :test_finished, name: "#{the_case}.#{name}", duration: div(time, 1000)
    {:ok, config}
  end

  def handle_event(_, config) do
    {:ok, config}
  end

  defp format(type, attributes) do
    attrs = attributes
            |> Enum.map(&format_attribute/1)
            |> Enum.join(" ")
    messageName = camelize Atom.to_string(type)
    "##teamcity[#{messageName} #{attrs}]"
  end

  defp format_attribute({k, v}) do
    "#{Atom.to_string k}='#{escape_output v}'"
  end

  defp camelize(s) do
    [head | tail] = String.split s, "_"
    "#{head}#{Enum.map tail, &String.capitalize/1}"
  end

   #Must escape certain characters, see: https://confluence.jetbrains.com/display/TCD9/Build+Script+Interaction+with+TeamCity
  defp escape_output(s) when not is_binary(s) do escape_output("#{s}") end
  defp escape_output(s) do
    s
      |> String.replace("|", "||")
      |> String.replace("'", "|'")
      |> String.replace("\n", "|n")
      |> String.replace("\r", "|r")
      #|> String.replace(~r/u([0-9a-f]{4})/i, "|0x\\1")
      #|> String.replace(~r/\x{([0-9a-f]{4})}/ui, "|0x\\1")
      |> String.replace("[", "|[")
      |> String.replace("]", "|]")
  end
end