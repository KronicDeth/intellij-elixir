# Originally based on https://github.com/lixhq/teamcity-exunit-formatter, but it did not work for parallel tests: IDEA
# does not honor flowId, so needed to use the nodeId/parentNodeIde system
#
# nodeId and parentNodeId system is documented in
# https://intellij-support.jetbrains.com/hc/en-us/community/posts/115000389550/comments/115000330464
defmodule TeamCityExUnitFormatting do
  # Constants

  @root_parent_node_id 0

  # Struct

  defstruct failures_counter: 0,
            invalids_counter: 0,
            seed: nil,
            skipped_counter: 0,
            tests_counter: 0,
            trace: false,
            width: 80

  # Functions

  def new(opts) do
    %__MODULE__{
      seed: opts[:seed],
      trace: opts[:trace]
    }
  end

  def put_event(state = %__MODULE__{}, {:case_finished, test_case = %ExUnit.TestCase{}}) do
    put_formatted(:test_suite_finished, attributes(test_case))

    state
  end

  def put_event(state = %__MODULE__{}, {:case_started, test_case = %ExUnit.TestCase{}}) do
    put_formatted(:test_suite_started, attributes(test_case))

    state
  end

  def put_event(state = %__MODULE__{}, {:suite_finished, _run_us, _load_us}), do: state

  def put_event(state = %__MODULE__{}, {:suite_started, opts}) do
    seed = opts[:seed]

    IO.puts("Suite started with seed #{seed}")

    %__MODULE__{state | seed: seed, trace: opts[:trace]}
  end

  def put_event(
        state = %__MODULE{
          failures_counter: failures_counter,
          tests_counter: tests_counter,
          width: width
        },
        {
          :test_finished,
          test = %ExUnit.Test{
            logs: logs,
            state:
              failed = {
                :failed,
                {_, reason, _}
              },
            time: time
          }
        }
      ) do
    updated_failures_counter = failures_counter + 1
    attributes = attributes(test)

    formatted_failure =
      ExUnit.Formatter.format_test_failure(
        test,
        failed,
        updated_failures_counter,
        width,
        &formatter/2
      )

    details = IO.iodata_to_binary([formatted_failure, format_logs(logs)])

    put_formatted(
      :test_failed,
      Keyword.merge(
        attributes,
        details: details,
        message: ""
      )
    )

    put_formatted(
      :test_finished,
      Keyword.merge(
        attributes,
        duration: div(time, 1000)
      )
    )

    %{
      state
      | tests_counter: tests_counter + 1,
        failures_counter: updated_failures_counter
    }
  end

  def put_event(
        state = %__MODULE__{
          failures_counter: failures_counter,
          width: width,
          tests_counter: tests_counter
        },
        {:test_finished, test = %ExUnit.Test{state: {:failed, failed}, time: time}}
      )
      when is_list(failed) do
    updated_failures_counter = failures_counter + 1
    attributes = attributes(test)

    formatted_failure =
      ExUnit.Formatter.format_test_failure(
        test,
        failed,
        updated_failures_counter,
        width,
        &formatter/2
      )

    details = IO.iodata_to_binary([formatted_failure, format_logs(logs)])

    put_formatted(
      :test_failed,
      Keyword.merge(
        attributes,
        details: formatted_failure,
        message: ""
      )
    )

    put_formatted(
      :test_finished,
      Keyword.merge(
        attributes,
        duration: div(time, 1000)
      )
    )

    %{
      state
      | tests_counter: tests_counter + 1,
        failures_counter: updated_failures_counter
    }
  end

  def put_event(
        state = %__MODULE__{
          tests_counter: tests_counter,
          skipped_counter: skipped_counter
        },
        {:test_finished, test = %ExUnit.Test{state: {:skip, _}}}
      ) do
    attributes = attributes(test)

    put_formatted(:test_ignored, attributes)
    put_formatted(:test_finished, attributes)

    %{
      state
      | tests_counter: tests_counter + 1,
        skipped_counter: skipped_counter + 1
    }
  end

  def put_event(state = %__MODULE__{}, {
        :test_finished,
        test = %ExUnit.Test{
          time: time
        }
      }) do
    put_formatted(
      :test_finished,
      test
      |> attributes()
      |> Keyword.merge(duration: div(time, 1000))
    )

    state
  end

  def put_event(state = %__MODULE__{}, {:test_started, test = %ExUnit.Test{tags: tags}}) do
    put_formatted(
      :test_started,
      test
      |> attributes()
      |> Keyword.merge(locationHint: "file://#{tags[:file]}:#{tags[:line]}")
    )

    state
  end

  def put_event(state = %__MODULE__{}, event) do
    IO.warn(
      "#{inspect(__MODULE__)} does not know how to process event (#{inspect(event)}).  " <>
        "Please report this message to https://github.com/KronicDeth/intellij-elixir/issues/new."
    )

    state
  end

  ## Private Functions

  defp attributes(test_or_test_case) do
    [
      nodeId: nodeId(test_or_test_case),
      name: name(test_or_test_case),
      parentNodeId: parentNodeId(test_or_test_case)
    ]
  end

  defp camelize(s) do
    [head | tail] = String.split(s, "_")
    "#{head}#{Enum.map(tail, &String.capitalize/1)}"
  end

  defp colorize(escape, string) do
    [escape, string, :reset]
    |> IO.ANSI.format_fragment(true)
    |> IO.iodata_to_binary()
  end

  # Must escape certain characters
  # see: https://confluence.jetbrains.com/display/TCD9/Build+Script+Interaction+with+TeamCity
  defp escape_output(s) when not is_binary(s), do: escape_output("#{s}")

  defp escape_output(s) do
    s
    |> String.replace("|", "||")
    |> String.replace("'", "|'")
    |> String.replace("\n", "|n")
    |> String.replace("\r", "|r")
    |> String.replace("[", "|[")
    |> String.replace("]", "|]")
  end

  defp format(type, attributes) do
    messageName =
      type
      |> Atom.to_string()
      |> camelize()

    attrs =
      attributes
      |> Enum.map(&format_attribute/1)
      |> Enum.join(" ")

    "##teamcity[#{messageName} #{attrs}]"
  end

  defp format_attribute({k, v}) do
    "#{Atom.to_string(k)}='#{escape_output(v)}'"
  end

  defp format_case_name(case_name) do
    case_name
    |> to_string()
    |> String.replace(~r/\bElixir\./, "")
  end

  defp format_logs(""), do: ""

  defp format_logs(logs) do
    indent = "\n     "
    indented_logs = String.replace(logs, "\n", indent)
    [indent, "The following output was logged:", indent | indented_logs]
  end

  defp formatter(:diff_enabled?, _), do: true

  defp formatter(:error_info, msg), do: colorize(:red, msg)

  defp formatter(:extra_info, msg), do: colorize(:cyan, msg)

  defp formatter(:location_info, msg), do: colorize([:bright, :black], msg)

  defp formatter(:diff_delete, msg), do: colorize(:red, msg)

  defp formatter(:diff_delete_whitespace, msg), do: colorize(IO.ANSI.color_background(2, 0, 0), msg)

  defp formatter(:diff_insert, msg), do: colorize(:green, msg)

  defp formatter(:diff_insert_whitespace, msg), do: colorize(IO.ANSI.color_background(0, 2, 0), msg)

  defp formatter(:blame_diff, msg), do: colorize(:red, msg)

  defp formatter(_, msg), do: msg

  defp name(test = %ExUnit.Test{name: name}) do
    named_captures =
      Regex.named_captures(
        ~r|test doc at (?<module>.+)\.(?<function>\w+)/(?<arity>\d+) \((?<count>\d+)\)|,
        to_string(name)
      )

    name(test, named_captures)
  end

  defp name(%ExUnit.TestCase{name: name}), do: format_case_name(name)

  defp name(%ExUnit.Test{name: name}, nil), do: to_string(name)

  defp name(%ExUnit.Test{case: case_name}, %{
         "arity" => arity,
         "count" => count,
         "function" => function,
         "module" => module
       }) do
    name = "#{function}/#{arity} doc (#{count})"

    if module <> "Test" == format_case_name(case_name) do
      name
    else
      "#{module}.#{name}"
    end
  end

  defp nodeId(%ExUnit.Test{case: case_name, name: name}), do: "#{case_name}.#{name}"
  defp nodeId(%ExUnit.TestCase{name: name}), do: name

  defp parentNodeId(%ExUnit.Test{case: case_name}), do: case_name
  defp parentNodeId(%ExUnit.TestCase{}), do: @root_parent_node_id

  # DO NOT use `flowId` as an attribute.  IDEA ignores flowId and so it can't be used to interleave async test output
  defp put_formatted(type, attributes) do
    type
    |> format(attributes)
    |> IO.puts()
  end
end
