defmodule TeamCityESpecFormatter do
  @moduledoc false

  use GenServer

  @root_parent_node_id 0

  defstruct description_tree: %{},
            file_by_module: %{}

  def init(_opts) do
    {:ok, %__MODULE__{}}
  end

  # Cannot pattern match on `%ESpec.Example{}` because it won't be defined when this module is required with `-r`

  def handle_cast(
        {:example_finished,
         example = %{
           __struct__: ESpec.Example,
           module: module,
           file: file,
           line: line,
           duration: duration
         }},
        state = %__MODULE__{}
      ) do
    example_state = put_in(state.file_by_module[module], file)
    {ancestors, ancestral_state} = ancestors(example_state, example)
    attributes = attributes(example, ancestors)
    final_state = start_new_test_suite(ancestral_state, ancestors)

    start_test(attributes, file, line)
    test_status(example, attributes)
    finish_test(attributes, duration)

    {:noreply, final_state}
  end

  def handle_cast({:final_result, _examples, _duration}, state) do
    finish_test_suites(state)

    {:noreply, state}
  end

  ## Private Functions

  defp ancestors(state = %__MODULE__{file_by_module: file_by_module}, %{
         __struct__: ESpec.Example,
         context: context_list
       }) do
    {reverse_ancestors, final_file_by_module} =
      Enum.reduce(context_list, {[], file_by_module}, fn
        %{
          __struct__: ESpec.Context,
          module: module,
          line: line,
          description: description
        },
        {acc_ancestors, acc_file_by_module} ->
          updated_file_by_module = Map.put_new(acc_file_by_module, module, module.__info__(:compile)[:source])
          file = Map.fetch!(updated_file_by_module, module)
          ancestor = %{file: file, line: line, description: description}

          {[ancestor | acc_ancestors], updated_file_by_module}

        _, acc ->
          acc
      end)

    {Enum.reverse(reverse_ancestors), %__MODULE__{state | file_by_module: final_file_by_module}}
  end

  defp attributes(%{__struct__: ESpec.Example, description: description}, ancestors) do
    parent_node_id = parent_node_id(ancestors)

    [
      nodeId: "#{parent_node_id}.#{description}",
      name: description,
      parentNodeId: parent_node_id
    ]
  end

  defp camelize(s) do
    [head | tail] = String.split(s, "_")
    "#{head}#{Enum.map(tail, &String.capitalize/1)}"
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
    message_name =
      type
      |> Atom.to_string()
      |> camelize()

    attrs =
      attributes
      |> Enum.map(&format_attribute/1)
      |> Enum.join(" ")

    "##teamcity[#{message_name} #{attrs}]"
  end

  defp format_attribute({k, v}) do
    "#{Atom.to_string(k)}='#{escape_output(v)}'"
  end

  defp parent_node_id(ancestors) when is_list(ancestors) do
    Enum.join([@root_parent_node_id | Enum.map(ancestors, & &1.description)], ".")
  end

  # DO NOT use `flowId` as an attribute.  IDEA ignores flowId and so it can't be used to interleave async test output
  defp put_formatted(type, attributes) do
    type
    |> format(attributes)
    |> IO.puts()
  end

  defp start_new_test_suite(%__MODULE__{description_tree: description_tree}, ancestors)
       when is_list(ancestors) do
    %__MODULE__{
      description_tree: start_new_test_suite(description_tree, @root_parent_node_id, ancestors)
    }
  end

  defp start_new_test_suite(description_tree, _, []) when is_map(description_tree),
    do: description_tree

  defp start_new_test_suite(description_tree, parent_node_id, [
         ancestor = %{description: description} | tail
       ]) do
    node_id = "#{parent_node_id}.#{description}"

    child_tree =
      case Map.fetch(description_tree, description) do
        {:ok, child_tree} ->
          child_tree

        :error ->
          attributes = [
            nodeId: node_id,
            name: description,
            parentNodeId: parent_node_id
          ]

          final_attributes =
            case ancestor do
              %{file: file, line: line} ->
                Keyword.put(attributes, :locationHint, "file://#{file}:#{line}")

              _ ->
                attributes
            end

          put_formatted(:test_suite_started, final_attributes)

          %{}
      end

    Map.put(description_tree, description, start_new_test_suite(child_tree, node_id, tail))
  end

  defp start_test(attributes, file, line) do
    put_formatted(:test_started, Keyword.put(attributes, :locationHint, "file://#{file}:#{line}"))
  end

  defp test_status(example = %{__struct__: ESpec.Example, status: status}, attributes) do
    case status do
      :success ->
        :ok

      :failure ->
        %{error: %{__struct__: ESpec.AssertionError, message: details}} = example
        put_formatted(:test_failed, Keyword.merge(attributes, details: details, message: ""))

      :pending ->
        put_formatted(:test_ignored, attributes)
    end
  end

  defp finish_test(attributes, duration) do
    put_formatted(:test_finished, Keyword.put(attributes, :duration, duration))
  end

  defp finish_test_suites(%__MODULE__{description_tree: description_tree}) do
    finish_test_suites(description_tree, @root_parent_node_id)
  end

  defp finish_test_suites(parent_tree, parent_node_id) when is_map(parent_tree) do
    Enum.each(parent_tree, fn {child_description, child_tree} ->
      node_id = "#{parent_node_id}.#{child_description}"

      finish_test_suites(child_tree, node_id)

      put_formatted(:test_suite_finished,
        nodeId: node_id,
        name: child_description,
        parentNodeId: parent_node_id
      )
    end)
  end
end
