# Regenerates snippets.jsonl and NOTICE.md beside this script: the source strings Elixir's own parser, tokenizer,
# formatter and normalizer tests hand to the parser, for every elixir-lang/elixir corpus in
# .github/ci-versions.json. Run it from the repository root with the Elixir mise.toml pins (1.18 or later, for
# JSON):
#
#     mise exec -- elixir testData/org/elixir_lang/parser_definition/elixir_snippets/generate.exs
#
# Each release's tests are read by that release's own Elixir, through `mise exec`, since a later Elixir can
# reject what an earlier release's tests contain; so every pair in the declaration must be installed in mise.
unless Version.match?(System.version(), ">= 1.18.0") do
  raise "generate.exs needs Elixir 1.18 or later for JSON; this is #{System.version()}"
end

here = __DIR__
declaration_path = List.first(System.argv()) || Path.expand("../../../../../.github/ci-versions.json", here)
extractor = Path.join(here, "extract.exs")
elixir_git = "https://github.com/elixir-lang/elixir"

declaration = declaration_path |> File.read!() |> JSON.decode!()
beam = declaration["beam"]

releases =
  [beam["baseline"] | beam["additional"] || []]
  |> Enum.flat_map(fn pair ->
    for %{"git" => ^elixir_git, "sha" => sha} <- pair["corpus"] || [], do: {pair["elixir"], pair["otp"], sha}
  end)
  |> Enum.uniq_by(fn {elixir, _, _} -> elixir end)
  |> Enum.sort_by(fn {elixir, _, _} -> elixir end, &(Version.compare(&1, &2) != :gt))

if releases == [], do: raise("#{declaration_path} declares no #{elixir_git} corpus")

{:ok, _} = Application.ensure_all_started(:inets)
{:ok, _} = Application.ensure_all_started(:ssl)

download = fn sha ->
  url = ~c"https://codeload.github.com/elixir-lang/elixir/tar.gz/#{sha}"

  ssl = [
    verify: :verify_peer,
    cacerts: :public_key.cacerts_get(),
    customize_hostname_check: [match_fun: :public_key.pkix_verify_hostname_match_fun(:https)]
  ]

  case :httpc.request(:get, {url, []}, [ssl: ssl, timeout: 300_000], body_format: :binary) do
    {:ok, {{_, 200, _}, _, body}} -> body
    other -> raise "could not download #{url}: #{inspect(other)}"
  end
end

scratch = Path.join(System.tmp_dir!(), "elixir_snippets_#{System.unique_integer([:positive])}")

extracted =
  try do
    for {elixir, otp, sha} <- releases do
      {:ok, entries} = :erl_tar.extract({:binary, download.(sha)}, [:compressed, :memory])
      root = Path.join(scratch, elixir)

      notice =
        Enum.find_value(entries, fn {name, content} ->
          if name |> List.to_string() |> Path.split() |> tl() == ["NOTICE"], do: content
        end)

      for {name, content} <- entries,
          [_top | path] = name |> List.to_string() |> Path.split(),
          List.starts_with?(path, ["lib", "elixir", "test", "elixir"]) do
        destination = Path.join([root | path])
        File.mkdir_p!(Path.dirname(destination))
        File.write!(destination, content)
      end

      output = Path.join(scratch, "#{elixir}.term")
      tests = Path.join(root, "lib/elixir/test/elixir")

      case System.cmd("mise", ["exec", "elixir@#{elixir}", "erlang@#{otp}", "--", "elixir", extractor, tests, output],
             stderr_to_stdout: true
           ) do
        {_, 0} -> :ok
        {log, status} -> raise "extract.exs under Elixir #{elixir} / OTP #{otp} exited #{status}:\n#{log}"
      end

      {elixir, sha, notice, output |> File.read!() |> :erlang.binary_to_term()}
    end
  after
    File.rm_rf!(scratch)
  end

# The origin recorded for a snippet is its first appearance: the oldest release, then file and position.
snippets =
  Enum.reduce(extracted, %{}, fn {elixir, sha, _notice, files}, acc ->
    for %{snippets: found} <- files, {file, line, helper, source} <- found, reduce: acc do
      acc ->
        hash = :crypto.hash(:sha256, source) |> Base.encode16(case: :lower) |> binary_part(0, 16)

        case acc do
          %{^hash => %{"source" => ^source} = snippet} ->
            releases = if elixir in snippet["releases"], do: snippet["releases"], else: snippet["releases"] ++ [elixir]
            %{acc | hash => %{snippet | "releases" => releases}}

          %{^hash => _} ->
            raise "two snippets share the hash #{hash}"

          _ ->
            origin = %{
              "release" => elixir,
              "commit" => sha,
              "file" => "lib/elixir/test/elixir/#{file}",
              "line" => line,
              "helper" => helper
            }

            Map.put(acc, hash, %{"hash" => hash, "origin" => origin, "releases" => [elixir], "source" => source})
        end
    end
  end)

File.write!(
  Path.join(here, "snippets.jsonl"),
  snippets |> Map.values() |> Enum.sort_by(& &1["hash"]) |> Enum.map(&[JSON.encode!(&1), ?\n])
)

group_releases = fn pairs ->
  pairs
  |> Enum.group_by(fn {_elixir, value} -> value end, fn {elixir, _value} -> elixir end)
  |> Enum.sort_by(fn {_value, elixirs} -> hd(elixirs) end, &(Version.compare(&1, &2) != :gt))
end

spdx =
  for {elixir, _sha, _notice, files} <- extracted, %{file: file, spdx: [_ | _] = lines} <- files do
    {elixir, {file, lines}}
  end
  |> group_releases.()

notices = extracted |> Enum.flat_map(fn {elixir, _, notice, _} -> if notice, do: [{elixir, notice}], else: [] end) |> group_releases.()

indent = fn text -> text |> String.trim_trailing() |> String.split("\n") |> Enum.map_join("\n", &String.trim_trailing("    " <> &1)) end

File.write!(Path.join(here, "NOTICE.md"), [
  """
  # Snippets from Elixir's tests

  `snippets.jsonl` holds, one JSON object per line, the source strings that Elixir's own parser, tokenizer,
  formatter and normalizer tests hand to the parser. They come from #{elixir_git}, which is
  licensed under the Apache License, Version 2.0, the licence of this repository (`LICENSE.md`).

  It was changed as follows: `generate.exs` extracted each snippet from a string literal, sigil or charlist in
  a test file, interpreted the escapes of `~s` and `~c` sigils, and kept one copy of each snippet across the
  releases below. Each line records the snippet's `hash`, the `releases` whose tests contain it, and its
  `origin`: the release, commit, file, line and helper where it first appears.

  ## Sources

  | Release | Commit |
  |---|---|
  """,
  Enum.map(extracted, fn {elixir, sha, _, _} -> "| #{elixir} | [`#{sha}`](#{elixir_git}/tree/#{sha}) |\n" end),
  "\n## Copyright and licence notices of the source files\n",
  Enum.map(spdx, fn {{file, lines}, elixirs} ->
    "\n`lib/elixir/test/elixir/#{file}`, #{Enum.join(elixirs, ", ")}:\n\n#{indent.(Enum.join(lines, "\n"))}\n"
  end),
  Enum.map(notices, fn {notice, elixirs} ->
    "\n## NOTICE, #{Enum.join(elixirs, ", ")}\n\n#{indent.(notice)}\n"
  end)
])

for {elixir, _sha, _notice, files} <- extracted do
  IO.puts("#{elixir}: " <> Enum.map_join(files, ", ", fn %{file: file, snippets: found} -> "#{file} #{length(found)}" end))
end

unknown =
  for {elixir, _sha, _notice, files} <- extracted, %{unknown: calls} <- files, {file, _line, helper} <- calls do
    {elixir, {file, helper}}
  end
  |> group_releases.()

unless unknown == [] do
  IO.puts("\nHelpers defined in the tests and called with a literal string, which extract.exs does not read:")

  for {{file, helper}, elixirs} <- unknown do
    IO.puts("  #{file} #{helper}: #{elixirs |> Enum.uniq() |> Enum.join(", ")} (#{length(elixirs)} calls)")
  end
end

IO.puts("\n#{map_size(snippets)} distinct snippets")
