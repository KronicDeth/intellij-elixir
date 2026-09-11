# Run by generate.exs under each release's own Elixir, which is the only one sure to read that release's tests:
#
#     elixir extract.exs <lib/elixir/test/elixir> <output file>
#
# Writes, as an Erlang term, every literal source string the tests hand to the parser, with where it came from.
# Keep it to what Elixir 1.11 has.
[root, output] = System.argv()
# Path.wildcard reads a backslash as an escape
root = String.replace(root, "\\", "/")

files =
  ([
     "kernel/parser_test.exs",
     "kernel/string_tokenizer_test.exs",
     "kernel/errors_test.exs",
     "kernel/diagnostics_test.exs",
     "code_test.exs"
   ] ++
     Path.wildcard(Path.join(root, "code_formatter/*_test.exs")) ++
     Path.wildcard(Path.join(root, "code_normalizer/*_test.exs")))
  |> Enum.map(&Path.relative_to(&1, root))
  |> Enum.filter(&File.exists?(Path.join(root, &1)))

# Argument positions holding source, by helper.
local = %{parse!: [0], assert_syntax_error: [1], assert_same: [0], assert_format: [0, 1], assert_eval_raise: [2]}

remote = %{
  string_to_quoted: [0],
  string_to_quoted!: [0],
  string_to_quoted_with_comments: [0],
  string_to_quoted_with_comments!: [0],
  format_string!: [0],
  eval_string: [0],
  compile_string: [0]
}

literal = fn
  s when is_binary(s) -> {:ok, s}
  {:sigil_S, _, [{:<<>>, _, [s]}, _]} when is_binary(s) -> {:ok, s}
  {:sigil_s, _, [{:<<>>, _, [s]}, _]} when is_binary(s) -> {:ok, Macro.unescape_string(s)}
  {:sigil_C, _, [{:<<>>, _, [s]}, _]} when is_binary(s) -> {:ok, s}
  {:sigil_c, _, [{:<<>>, _, [s]}, _]} when is_binary(s) -> {:ok, Macro.unescape_string(s)}
  [c | _] = charlist when is_integer(c) -> if Enum.all?(charlist, &is_integer/1), do: {:ok, List.to_string(charlist)}, else: :skip
  _ -> :skip
end

literal? = fn arg -> literal.(arg) != :skip end

extracted =
  Enum.map(files, fn file ->
    text = File.read!(Path.join(root, file))
    quoted = Code.string_to_quoted!(text)

    spdx =
      text
      |> String.split("\n")
      |> Enum.map(&String.trim_trailing/1)
      |> Enum.take_while(&(&1 == "" or String.starts_with?(&1, "#")))
      |> Enum.filter(&String.starts_with?(&1, "# SPDX-"))

    {_, defined} =
      Macro.prewalk(quoted, MapSet.new(), fn
        {kind, _, [{:when, _, [{name, _, _} | _]} | _]} = node, acc when kind in [:def, :defp, :defmacro, :defmacrop] ->
          {node, MapSet.put(acc, name)}

        {kind, _, [{name, _, _} | _]} = node, acc when kind in [:def, :defp, :defmacro, :defmacrop] and is_atom(name) ->
          {node, MapSet.put(acc, name)}

        node, acc ->
          {node, acc}
      end)

    collect = fn name, meta, args, indexes, acc ->
      Enum.reduce(indexes, acc, fn index, acc ->
        case literal.(Enum.at(args, index)) do
          {:ok, source} -> [{file, meta[:line], Atom.to_string(name), source} | acc]
          :skip -> acc
        end
      end)
    end

    {_, {snippets, unknown}} =
      Macro.prewalk(quoted, {[], []}, fn
        {:|>, _, [lhs, {{:., _, [{:__aliases__, _, [:Code]}, name]}, meta, _}]} = node, {snippets, unknown}
        when is_map_key(remote, name) ->
          {node, {collect.(name, meta, [lhs], [0], snippets), unknown}}

        {{:., _, [{:__aliases__, _, [:Code]}, name]}, meta, args} = node, {snippets, unknown}
        when is_list(args) and is_map_key(remote, name) ->
          {node, {collect.(name, meta, args, Map.fetch!(remote, name), snippets), unknown}}

        {name, meta, args} = node, {snippets, unknown} when is_atom(name) and is_list(args) and is_map_key(local, name) ->
          {node, {collect.(name, meta, args, Map.fetch!(local, name), snippets), unknown}}

        # A helper the test file defines for itself and calls with a literal string may be handing source to
        # the parser under a name the table above does not know.
        {name, meta, args} = node, {snippets, unknown} when is_atom(name) and is_list(args) ->
          if MapSet.member?(defined, name) and Enum.any?(args, literal?) do
            {node, {snippets, [{file, meta[:line], Atom.to_string(name)} | unknown]}}
          else
            {node, {snippets, unknown}}
          end

        node, acc ->
          {node, acc}
      end)

    %{file: file, spdx: spdx, snippets: Enum.reverse(snippets), unknown: Enum.reverse(unknown)}
  end)

File.write!(output, :erlang.term_to_binary(extracted))
