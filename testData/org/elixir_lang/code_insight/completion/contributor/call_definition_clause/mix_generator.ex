# Source code recreated from a .beam file by IntelliJ Elixir
defmodule Mix.Generator do
  @moduledoc ~S"""
  Conveniences for working with paths and generating content.

  """

  # Macros

  @doc ~S"""
  Embeds a template given by `contents` into the current module.

  It will define a private function with the `name` followed by
  `_template` that expects assigns as arguments.

  This function must be invoked passing a keyword list.
  Each key in the keyword list can be accessed in the
  template using the `@` macro.

  For more information, check `EEx.SmartEngine`.

  ## Examples

      defmodule Mix.Tasks.MyTask do
        require Mix.Generator
        Mix.Generator.embed_template(:log, "Log: <%= @log %>")
      end


  """
  defmacro embed_template(name, contents) do
    {:__block__, [], [{:=, [], [{:contents, [line: 181], Mix.Generator}, contents]}, {:=, [], [{:name, [line: 181], Mix.Generator}, name]}, {:__block__, [], [{:=, [], [{:contents, [], Mix.Generator}, {:case, [], [{:contents, [], Mix.Generator}, [do: [{:"->", [], [[[from_file: {:file, [], Mix.Generator}]], {:__block__, [], [{:@, [context: Mix.Generator, import: Kernel], [{:file, [context: Mix.Generator], [{:file, [], Mix.Generator}]}]}, {{:".", [], [{:__aliases__, [alias: false], [:"File"]}, :read!]}, [], [{:file, [], Mix.Generator}]}]}]}, {:"->", [], [[{:when, [], [{:c, [], Mix.Generator}, {:is_binary, [context: Mix.Generator, import: Kernel], [{:c, [], Mix.Generator}]}]}], {:__block__, [], [{:@, [context: Mix.Generator, import: Kernel], [{:file, [context: Mix.Generator], [{{{:".", [], [{:__ENV__, [], Mix.Generator}, :file]}, [no_parens: true], []}, {:+, [context: Mix.Generator, import: Kernel], [{{:".", [], [{:__ENV__, [], Mix.Generator}, :line]}, [no_parens: true], []}, 1]}}]}]}, {:c, [], Mix.Generator}]}]}, {:"->", [], [[{:_, [], Mix.Generator}], {:raise, [context: Mix.Generator, import: Kernel], [{:__aliases__, [alias: false], [:"ArgumentError"]}, "expected string or from_file: file"]}]}]]]}]}, {:require, [context: Mix.Generator], [{:__aliases__, [alias: false], [:"EEx"]}]}, {:=, [], [{:source, [], Mix.Generator}, {:<>, [context: Mix.Generator, import: Kernel], ["<% _ = assigns %>", {:contents, [], Mix.Generator}]}]}, {{:".", [], [{:__aliases__, [alias: false], [:"EEx"]}, :function_from_string]}, [], [:defp, {{:".", [], [:erlang, :binary_to_atom]}, [], [{:"<<>>", [], [{:::, [], [{{:".", [], [Kernel, :to_string]}, [], [{:name, [], Mix.Generator}]}, {:binary, [], Mix.Generator}]}, "_template"]}, :utf8]}, {:source, [], Mix.Generator}, [:assigns]]}]}]}
  end

  @doc ~S"""
  Embeds a text given by `contents` into the current module.

  It will define a private function with the `name` followed by
  `_text` that expects no arguments.

  ## Examples

      defmodule Mix.Tasks.MyTask do
        require Mix.Generator
        Mix.Generator.embed_text(:error, "There was an error!")
      end


  """
  defmacro embed_text(name, contents) do
    {:__block__, [], [{:=, [], [{:contents, [line: 218], Mix.Generator}, contents]}, {:=, [], [{:name, [line: 218], Mix.Generator}, name]}, {:__block__, [], [{:=, [], [{:contents, [], Mix.Generator}, {:case, [], [{:contents, [], Mix.Generator}, [do: [{:"->", [], [[[from_file: {:f, [], Mix.Generator}]], {{:".", [], [{:__aliases__, [alias: false], [:"File"]}, :read!]}, [], [{:f, [], Mix.Generator}]}]}, {:"->", [], [[{:when, [], [{:c, [], Mix.Generator}, {:is_binary, [context: Mix.Generator, import: Kernel], [{:c, [], Mix.Generator}]}]}], {:c, [], Mix.Generator}]}, {:"->", [], [[{:_, [], Mix.Generator}], {:raise, [context: Mix.Generator, import: Kernel], [{:__aliases__, [alias: false], [:"ArgumentError"]}, "expected string or from_file: file"]}]}]]]}]}, {:defp, [context: Mix.Generator, import: Kernel], [{{:unquote, [], [{{:".", [], [:erlang, :binary_to_atom]}, [], [{:"<<>>", [], [{:::, [], [{{:".", [], [Kernel, :to_string]}, [], [{:name, [], Mix.Generator}]}, {:binary, [], Mix.Generator}]}, "_text"]}, :utf8]}]}, [context: Mix.Generator], []}, [do: {:unquote, [], [{:contents, [], Mix.Generator}]}]]}]}]}
  end

  # Functions

  def __info__(p0) do
    # body not decompiled
  end

  def copy_file(x0, x1) do
    super(x0, x1, [])
  end

  @doc ~S"""
  Copies `source` to `target`.

  If `target` already exists and the contents are not the same,
  it asks for user confirmation.

  ## Options

    * `:force` - forces copying without a shell prompt
    * `:quiet` - does not log command output

  ## Examples

      iex> Mix.Generator.copy_file("source/gitignore", ".gitignore")
      * creating .gitignore
      true


  """
  def copy_file(source, target, options) do
    create_file(target, File.read!(source), options)
  end

  def copy_template(x0, x1, x2) do
    super(x0, x1, x2, [])
  end

  @doc ~S"""
  Evaluates and copy templates at `source` to `target`.

  The template in `source` is evaluated with the given `assigns`.

  If `target` already exists and the contents are not the same,
  it asks for user confirmation.

  ## Options

    * `:force` - forces copying without a shell prompt
    * `:quiet` - does not log command output

  ## Examples

      iex> assigns = [project_path: "/Users/joe/newproject"]
      iex> Mix.Generator.copy_template("source/gitignore", ".gitignore", assigns)
      * creating .gitignore
      true


  """
  def copy_template(source, target, assigns, options) do
    create_file(target, EEx.eval_file(source, assigns: assigns), options)
  end

  def create_directory(x0) do
    super(x0, [])
  end

  @doc ~S"""
  Creates a directory if one does not exist yet.

  This function does nothing if the given directory already exists; in this
  case, it still logs the directory creation.

  ## Options

    * `:quiet` - does not log command output

  ## Examples

      iex> Mix.Generator.create_directory("path/to/dir")
      * creating path/to/dir
      true


  """
  def create_directory(path, options) do
    (
      log(:green, "creating", Path.relative_to_cwd(path), options)
      File.mkdir_p!(path)
      true
      )
  end

  def create_file(x0, x1) do
    super(x0, x1, [])
  end

  @doc ~S"""
  Creates a file with the given contents.

  If the file already exists and the contents are not the same,
  it asks for user confirmation.

  ## Options

    * `:force` - forces creation without a shell prompt
    * `:quiet` - does not log command output

  ## Examples

      iex> Mix.Generator.create_file(".gitignore", "_build\ndeps\n")
      * creating .gitignore
      true


  """
  def create_file(path, contents, opts) do
    (
      log(:green, :creating, Path.relative_to_cwd(path), opts)
      if(opts[:force] || x) do
        false
      else
        File.mkdir_p!(Path.dirname(path))
        File.write!(path, contents)
        true
      end
      )
  end

  def module_info() do
    # body not decompiled
  end

  def module_info(p0) do
    # body not decompiled
  end

  @doc ~S"""
  Prompts the user to overwrite the file if it exists.

  Returns false if the file exists and the user forbade
  to override it. Returns true otherwise.

  """
  def overwrite?(path) do
    if(File.exists?(path)) do
      true
    else
      full = Path.expand(path)
      Mix.shell().yes?(<<Path.relative_to_cwd(full)::binary(), " already exists, overwrite?"::binary()>>)
    end
  end

  @doc ~S"""
  Prompts the user to overwrite the file if it exists.

  The contents are compared to avoid asking the user to
  override if the contents did not change. Returns false
  if the file exists and the content is the same or the
  user forbade to override it. Returns true otherwise.

  """
  def overwrite?(path, contents) do
    case(File.read(path)) do
      {:ok, binary} ->
        case(binary == :erlang.iolist_to_binary(contents)) do
          false ->
            full = Path.expand(path)
            Mix.shell().yes?(<<Path.relative_to_cwd(full)::binary(), " already exists, overwrite?"::binary()>>)
          true ->
            false
        end
      _ ->
        true
    end
  end

  # Private Functions

  defp log(color, command, message, opts) do
    if(opts[:quiet]) do
      Mix.shell().info([color, <<"* "::binary(), String.Chars.to_string(command)::binary(), " "::binary()>>, :reset, message])
    else
      nil
    end
  end
end
