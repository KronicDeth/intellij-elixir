# Source code recreated from a .beam file by IntelliJ Elixir
defmodule Module do
  @moduledoc ~S'''
  Provides functions to deal with modules during compilation time.

  It allows a developer to dynamically add, delete and register
  attributes, attach documentation and so forth.

  After a module is compiled, using many of the functions in
  this module will raise errors, since it is out of their scope
  to inspect runtime data. Most of the runtime data can be inspected
  via the [`__info__/1`](`c:Module.__info__/1`) function attached to
  each compiled module.

  ## Module attributes

  Each module can be decorated with one or more attributes. The following ones
  are currently defined by Elixir:

  ### `@after_compile`

  A hook that will be invoked right after the current module is compiled.
  Accepts a module or a `{module, function_name}`. See the "Compile callbacks"
  section below.

  ### `@before_compile`

  A hook that will be invoked before the module is compiled.
  Accepts a module or a `{module, function_or_macro_name}` tuple.
  See the "Compile callbacks" section below.

  ### `@behaviour`

  Note the British spelling!

  Behaviours can be referenced by modules to ensure they implement
  required specific function signatures defined by `@callback`.

  For example, you could specify a `URI.Parser` behaviour as follows:

      defmodule URI.Parser do
        @doc "Defines a default port"
        @callback default_port() :: integer

        @doc "Parses the given URL"
        @callback parse(uri_info :: URI.t()) :: URI.t()
      end

  And then a module may use it as:

      defmodule URI.HTTP do
        @behaviour URI.Parser
        def default_port(), do: 80
        def parse(info), do: info
      end

  If the behaviour changes or `URI.HTTP` does not implement
  one of the callbacks, a warning will be raised.

  For detailed documentation, see the
  [behaviour typespec documentation](typespecs.md#behaviours).

  ### `@impl`

  To aid in the correct implementation of behaviours, you may optionally declare
  `@impl` for implemented callbacks of a behaviour. This makes callbacks
  explicit and can help you to catch errors in your code. The compiler will warn
  in these cases:

    * if you mark a function with `@impl` when that function is not a callback.

    * if you don't mark a function with `@impl` when other functions are marked
      with `@impl`. If you mark one function with `@impl`, you must mark all
      other callbacks for that behaviour as `@impl`.

  `@impl` works on a per-context basis. If you generate a function through a macro
  and mark it with `@impl`, that won't affect the module where that function is
  generated in.

  `@impl` also helps with maintainability by making it clear to other developers
  that the function is implementing a callback.

  Using `@impl`, the example above can be rewritten as:

      defmodule URI.HTTP do
        @behaviour URI.Parser

        @impl true
        def default_port(), do: 80

        @impl true
        def parse(info), do: info
      end

  You may pass either `false`, `true`, or a specific behaviour to `@impl`.

      defmodule Foo do
        @behaviour Bar
        @behaviour Baz

        # Will warn if neither Bar nor Baz specify a callback named bar/0.
        @impl true
        def bar(), do: :ok

        # Will warn if Baz does not specify a callback named baz/0.
        @impl Baz
        def baz(), do: :ok
      end

  The code is now more readable, as it is now clear which functions are
  part of your API and which ones are callback implementations. To reinforce this
  idea, `@impl true` automatically marks the function as `@doc false`, disabling
  documentation unless `@doc` is explicitly set.

  ### `@compile`

  Defines options for module compilation. This is used to configure
  both Elixir and Erlang compilers, as any other compilation pass
  added by external tools. For example:

      defmodule MyModule do
        @compile {:inline, my_fun: 1}

        def my_fun(arg) do
          to_string(arg)
        end
      end

  Multiple uses of `@compile` will accumulate instead of overriding
  previous ones. See the "Compile options" section below.

  ### `@deprecated`

  Provides the deprecation reason for a function. For example:

      defmodule Keyword do
        @deprecated "Use Kernel.length/1 instead"
        def size(keyword) do
          length(keyword)
        end
      end

  The Mix compiler automatically looks for calls to deprecated modules
  and emit warnings during compilation.

  Using the `@deprecated` attribute will also be reflected in the
  documentation of the given function and macro. You can choose between
  the `@deprecated` attribute and the documentation metadata to provide
  hard-deprecations (with warnings) and soft-deprecations (without warnings):

  This is a soft-deprecation as it simply annotates the documentation
  as deprecated:

      @doc deprecated: "Use Kernel.length/1 instead"
      def size(keyword)

  This is a hard-deprecation as it emits warnings and annotates the
  documentation as deprecated:

      @deprecated "Use Kernel.length/1 instead"
      def size(keyword)

  Currently `@deprecated` only supports functions and macros. However
  you can use the `:deprecated` key in the annotation metadata to
  annotate the docs of modules, types and callbacks too.

  We recommend using this feature with care, especially library authors.
  Deprecating code always pushes the burden towards library users. We
  also recommend for deprecated functionality to be maintained for long
  periods of time, even after deprecation, giving developers plenty of
  time to update (except for cases where keeping the deprecated API is
  undesired, such as in the presence of security issues).

  ### `@doc` and `@typedoc`

  Provides documentation for the entity that follows the attribute.
  `@doc` is to be used with a function, macro, callback, or
  macrocallback, while `@typedoc` with a type (public or opaque).

  Accepts a string (often a heredoc) or `false` where `@doc false` will
  make the entity invisible to documentation extraction tools like
  [`ExDoc`](https://hexdocs.pm/ex_doc/). For example:

      defmodule MyModule do
        @typedoc "This type"
        @typedoc since: "1.1.0"
        @type t :: term

        @doc "Hello world"
        @doc since: "1.1.0"
        def hello do
          "world"
        end

        @doc """
        Sums `a` to `b`.
        """
        def sum(a, b) do
          a + b
        end
      end

  As can be seen in the example above, `@doc` and `@typedoc` also accept
  a keyword list that serves as a way to provide arbitrary metadata
  about the entity. Tools like [`ExDoc`](https://hexdocs.pm/ex_doc/) and
  `IEx` may use this information to display annotations. A common use
  case is `since` that may be used to annotate in which version the
  function was introduced.

  As illustrated in the example, it is possible to use these attributes
  more than once before an entity. However, the compiler will warn if
  used twice with binaries as that replaces the documentation text from
  the preceding use. Multiple uses with keyword lists will merge the
  lists into one.

  Note that since the compiler also defines some additional metadata,
  there are a few reserved keys that will be ignored and warned if used.
  Currently these are: `:opaque` and `:defaults`.

  Once this module is compiled, this information becomes available via
  the `Code.fetch_docs/1` function.

  ### `@dialyzer`

  Defines warnings to request or suppress when using a version of
  `:dialyzer` that supports module attributes.

  Accepts an atom, a tuple, or a list of atoms and tuples. For example:

      defmodule MyModule do
        @dialyzer {:nowarn_function, my_fun: 1}

        def my_fun(arg) do
          M.not_a_function(arg)
        end
      end

  For the list of supported warnings, see
  [`:dialyzer` module](http://www.erlang.org/doc/man/dialyzer.html).

  Multiple uses of `@dialyzer` will accumulate instead of overriding
  previous ones.

  ### `@external_resource`

  Specifies an external resource for the current module.

  Sometimes a module embeds information from an external file. This
  attribute allows the module to annotate which external resources
  have been used.

  Tools may use this information to ensure the module is recompiled
  in case any of the external resources change, see for example:
  [`mix compile.elixir`](https://hexdocs.pm/mix/Mix.Tasks.Compile.Elixir.html).

  If the external resource does not exist, the module still has
  a dependency on it, causing the module be recompiled as soon
  as the file is added.

  ### `@file`

  Changes the filename used in stacktraces for the function or macro that
  follows the attribute, such as:

      defmodule MyModule do
        @doc "Hello world"
        @file "hello.ex"
        def hello do
          "world"
        end
      end

  ### `@moduledoc`

  Provides documentation for the current module.

      defmodule MyModule do
        @moduledoc """
        A very useful module.
        """
        @moduledoc authors: ["Alice", "Bob"]
      end

  Accepts a string (often a heredoc) or `false` where `@moduledoc false`
  will make the module invisible to documentation extraction tools like
  [`ExDoc`](https://hexdocs.pm/ex_doc/).

  Similarly to `@doc` also accepts a keyword list to provide metadata
  about the module. For more details, see the documentation of `@doc`
  above.

  Once this module is compiled, this information becomes available via
  the `Code.fetch_docs/1` function.

  ### `@on_definition`

  A hook that will be invoked when each function or macro in the current
  module is defined. Useful when annotating functions.

  Accepts a module or a `{module, function_name}` tuple. See the
  "Compile callbacks" section below.

  ### `@on_load`

  A hook that will be invoked whenever the module is loaded.

  Accepts the function name (as an atom) of a function in the current module or
  `{function_name, 0}` tuple where `function_name` is the name of a function in
  the current module. The function must be public and have an arity of 0 (no
  arguments). If the function does not return `:ok`, the loading of the module
  will be aborted. For example:

      defmodule MyModule do
        @on_load :load_check

        def load_check do
          if some_condition() do
            :ok
          else
            :abort
          end
        end

        def some_condition do
          false
        end
      end

  Modules compiled with HiPE would not call this hook.

  ### `@vsn`

  Specify the module version. Accepts any valid Elixir value, for example:

      defmodule MyModule do
        @vsn "1.0"
      end

  ### Typespec attributes

  The following attributes are part of typespecs and are also built-in in
  Elixir:

    * `@type` - defines a type to be used in `@spec`
    * `@typep` - defines a private type to be used in `@spec`
    * `@opaque` - defines an opaque type to be used in `@spec`
    * `@spec` - provides a specification for a function
    * `@callback` - provides a specification for a behaviour callback
    * `@macrocallback` - provides a specification for a macro behaviour callback
    * `@optional_callbacks` - specifies which behaviour callbacks and macro
      behaviour callbacks are optional
    * `@impl` - declares an implementation of a callback function or macro

  For detailed documentation, see the [typespec documentation](typespecs.md).

  ### Custom attributes

  In addition to the built-in attributes outlined above, custom attributes may
  also be added. Custom attributes are expressed using the `@/1` operator followed
  by a valid variable name. The value given to the custom attribute must be a valid
  Elixir value:

      defmodule MyModule do
        @custom_attr [some: "stuff"]
      end

  For more advanced options available when defining custom attributes, see
  `register_attribute/3`.

  ## Compile callbacks

  There are three callbacks that are invoked when functions are defined,
  as well as before and immediately after the module bytecode is generated.

  ### `@after_compile`

  A hook that will be invoked right after the current module is compiled.

  Accepts a module or a `{module, function_name}` tuple. The function
  must take two arguments: the module environment and its bytecode.
  When just a module is provided, the function is assumed to be
  `__after_compile__/2`.

  Callbacks will run in the order they are registered.

  #### Example

      defmodule MyModule do
        @after_compile __MODULE__

        def __after_compile__(env, _bytecode) do
          IO.inspect(env)
        end
      end

  ### `@before_compile`

  A hook that will be invoked before the module is compiled.

  Accepts a module or a `{module, function_or_macro_name}` tuple. The
  function/macro must take one argument: the module environment. If
  it's a macro, its returned value will be injected at the end of the
  module definition before the compilation starts.

  When just a module is provided, the function/macro is assumed to be
  `__before_compile__/1`.

  Callbacks will run in the order they are registered. Any overridable
  definition will be made concrete before the first callback runs.
  A definition may be made overridable again in another before compile
  callback and it will be made concrete one last time after all callbacks
  run.

  *Note*: unlike `@after_compile`, the callback function/macro must
  be placed in a separate module (because when the callback is invoked,
  the current module does not yet exist).

  #### Example

      defmodule A do
        defmacro __before_compile__(_env) do
          quote do
            def hello, do: "world"
          end
        end
      end

      defmodule B do
        @before_compile A
      end

      B.hello()
      #=> "world"

  ### `@on_definition`

  A hook that will be invoked when each function or macro in the current
  module is defined. Useful when annotating functions.

  Accepts a module or a `{module, function_name}` tuple. The function
  must take 6 arguments:

    * the module environment
    * the kind of the function/macro: `:def`, `:defp`, `:defmacro`, or `:defmacrop`
    * the function/macro name
    * the list of quoted arguments
    * the list of quoted guards
    * the quoted function body

  If the function/macro being defined has multiple clauses, the hook will
  be called for each clause.

  Unlike other hooks, `@on_definition` will only invoke functions and
  never macros. This is to avoid `@on_definition` callbacks from
  redefining functions that have just been defined in favor of more
  explicit approaches.

  When just a module is provided, the function is assumed to be
  `__on_definition__/6`.

  #### Example

      defmodule Hooks do
        def on_def(_env, kind, name, args, guards, body) do
          IO.puts("Defining #{kind} named #{name} with args:")
          IO.inspect(args)
          IO.puts("and guards")
          IO.inspect(guards)
          IO.puts("and body")
          IO.puts(Macro.to_string(body))
        end
      end

      defmodule MyModule do
        @on_definition {Hooks, :on_def}

        def hello(arg) when is_binary(arg) or is_list(arg) do
          "Hello" <> to_string(arg)
        end

        def hello(_) do
          :ok
        end
      end

  ## Compile options

  The `@compile` attribute accepts different options that are used by both
  Elixir and Erlang compilers. Some of the common use cases are documented
  below:

    * `@compile :debug_info` - includes `:debug_info` regardless of the
      corresponding setting in `Code.get_compiler_option/1`

    * `@compile {:debug_info, false}` - disables `:debug_info` regardless
      of the corresponding setting in `Code.get_compiler_option/1`

    * `@compile {:inline, some_fun: 2, other_fun: 3}` - inlines the given
      name/arity pairs. Inlining is applied locally, calls from another
      module are not affected by this option

    * `@compile {:autoload, false}` - disables automatic loading of
      modules after compilation. Instead, the module will be loaded after
      it is dispatched to

    * `@compile {:no_warn_undefined, Mod}` or
      `@compile {:no_warn_undefined, {Mod, fun, arity}}` - does not warn if
      the given module or the given `Mod.fun/arity` are not defined


  '''

  # Functions

  @doc false
  def __get_attribute__(module, key, line) do
    # body not decompiled
  end

  def __info__(p0) do
    # body not decompiled
  end

  @doc false
  def __put_attribute__(module, key, value, line) do
    # body not decompiled
  end

  def add_doc(p0, p1, p2, p3, p4) do
    # body not decompiled
  end


  @deprecated """
  Use @doc instead
  """

  @doc false
  def add_doc(module, line, kind, arg, signature \\ [], doc) do
    # body not decompiled
  end

  def behaviour_info(p0) do
    # body not decompiled
  end

  @doc false
  def check_behaviours_and_impls(env, set, bag, all_definitions) do
    # body not decompiled
  end

  @doc false
  def compile_definition_attributes(env, kind, name, args, guards, body) do
    # body not decompiled
  end

  @doc ~S"""
  Concatenates a list of aliases and returns a new alias.

  ## Examples

      iex> Module.concat([Foo, Bar])
      Foo.Bar

      iex> Module.concat([Foo, "Bar"])
      Foo.Bar


  """
  def concat(list) do
    # body not decompiled
  end

  @doc ~S"""
  Concatenates two aliases and returns a new alias.

  ## Examples

      iex> Module.concat(Foo, Bar)
      Foo.Bar

      iex> Module.concat(Foo, "Bar")
      Foo.Bar


  """
  def concat(left, right) do
    # body not decompiled
  end

  @doc ~S"""
  Creates a module with the given name and defined by
  the given quoted expressions.

  The line where the module is defined and its file **must**
  be passed as options.

  It returns a tuple of shape `{:module, module, binary, term}`
  where `module` is the module name, `binary` is the module
  bytecode and `term` is the result of the last expression in
  `quoted`.

  Similar to `Kernel.defmodule/2`, the binary will only be
  written to disk as a `.beam` file if `Module.create/3` is
  invoked in a file that is currently being compiled.

  ## Examples

      contents =
        quote do
          def world, do: true
        end

      Module.create(Hello, contents, Macro.Env.location(__ENV__))

      Hello.world()
      #=> true

  ## Differences from `defmodule`

  `Module.create/3` works similarly to `Kernel.defmodule/2`
  and return the same results. While one could also use
  `defmodule` to define modules dynamically, this function
  is preferred when the module body is given by a quoted
  expression.

  Another important distinction is that `Module.create/3`
  allows you to control the environment variables used
  when defining the module, while `Kernel.defmodule/2`
  automatically uses the environment it is invoked at.

  """
  def create(module, quoted, opts) do
    # body not decompiled
  end

  @doc ~S"""
  Checks if the module defines the given function or macro.

  Use `defines?/3` to assert for a specific type.

  This function can only be used on modules that have not yet been compiled.
  Use `Kernel.function_exported?/3` and `Kernel.macro_exported?/3` to check for
  public functions and macros respectively in compiled modules.

  Note that `defines?` returns false for functions and macros that have
  been defined but then marked as overridable and no other implementation
  has been provided. You can check the overridable status by calling
  `overridable?/2`.

  ## Examples

      defmodule Example do
        Module.defines?(__MODULE__, {:version, 0}) #=> false
        def version, do: 1
        Module.defines?(__MODULE__, {:version, 0}) #=> true
      end


  """
  def defines?(module, tuple) do
    # body not decompiled
  end

  @doc ~S"""
  Checks if the module defines a function or macro of the
  given `kind`.

  `kind` can be any of `:def`, `:defp`, `:defmacro`, or `:defmacrop`.

  This function can only be used on modules that have not yet been compiled.
  Use `Kernel.function_exported?/3` and `Kernel.macro_exported?/3` to check for
  public functions and macros respectively in compiled modules.

  ## Examples

      defmodule Example do
        Module.defines?(__MODULE__, {:version, 0}, :def) #=> false
        def version, do: 1
        Module.defines?(__MODULE__, {:version, 0}, :def) #=> true
      end


  """
  def defines?(module, tuple, def_kind) do
    # body not decompiled
  end

  @doc ~S"""
  Checks if the current module defines the given type (private, opaque or not).

  This function is only available for modules being compiled.

  """
  def defines_type?(module, definition) do
    # body not decompiled
  end

  @doc ~S"""
  Returns all functions and macros defined in `module`.

  It returns a list with all defined functions and macros, public and private,
  in the shape of `[{name, arity}, ...]`.

  This function can only be used on modules that have not yet been compiled.
  Use the `c:Module.__info__/1` callback to get the public functions and macros in
  compiled modules.

  ## Examples

      defmodule Example do
        def version, do: 1
        defmacrop test(arg), do: arg
        Module.definitions_in(__MODULE__) #=> [{:version, 0}, {:test, 1}]
      end


  """
  def definitions_in(module) do
    # body not decompiled
  end

  @doc ~S"""
  Returns all functions defined in `module`, according
  to its kind.

  This function can only be used on modules that have not yet been compiled.
  Use the `c:Module.__info__/1` callback to get the public functions and macros in
  compiled modules.

  ## Examples

      defmodule Example do
        def version, do: 1
        Module.definitions_in(__MODULE__, :def)  #=> [{:version, 0}]
        Module.definitions_in(__MODULE__, :defp) #=> []
      end


  """
  def definitions_in(module, kind) do
    # body not decompiled
  end

  @doc ~S"""
  Deletes the module attribute that matches the given key.

  It returns the deleted attribute value (or `nil` if nothing was set).

  ## Examples

      defmodule MyModule do
        Module.put_attribute(__MODULE__, :custom_threshold_for_lib, 10)
        Module.delete_attribute(__MODULE__, :custom_threshold_for_lib)
      end


  """
  def delete_attribute(module, key) do
    # body not decompiled
  end

  def eval_quoted(p0, p1) do
    # body not decompiled
  end

  def eval_quoted(p0, p1, p2) do
    # body not decompiled
  end

  @doc ~S"""
  Evaluates the quoted contents in the given module's context.

  A list of environment options can also be given as argument.
  See `Code.eval_string/3` for more information.

  Raises an error if the module was already compiled.

  ## Examples

      defmodule Foo do
        contents =
          quote do
            def sum(a, b), do: a + b
          end

        Module.eval_quoted(__MODULE__, contents)
      end

      Foo.sum(1, 2)
      #=> 3

  For convenience, you can pass any `Macro.Env` struct, such
  as  `__ENV__/0`, as the first argument or as options. Both
  the module and all options will be automatically extracted
  from the environment:

      defmodule Foo do
        contents =
          quote do
            def sum(a, b), do: a + b
          end

        Module.eval_quoted(__ENV__, contents)
      end

      Foo.sum(1, 2)
      #=> 3

  Note that if you pass a `Macro.Env` struct as first argument
  while also passing `opts`, they will be merged with `opts`
  having precedence.

  """
  def eval_quoted(module_or_env, quoted, binding \\ [], opts \\ []) do
    # body not decompiled
  end

  def get_attribute(p0, p1) do
    # body not decompiled
  end

  @doc ~S"""
  Gets the given attribute from a module.

  If the attribute was marked with `accumulate` with
  `Module.register_attribute/3`, a list is always returned.
  `nil` is returned if the attribute has not been marked with
  `accumulate` and has not been set to any value.

  The `@` macro compiles to a call to this function. For example,
  the following code:

      @foo

  Expands to something akin to:

      Module.get_attribute(__MODULE__, :foo)

  This function can only be used on modules that have not yet been compiled.
  Use the `c:Module.__info__/1` callback to get all persisted attributes, or
  `Code.fetch_docs/1` to retrieve all documentation related attributes in
  compiled modules.

  ## Examples

      defmodule Foo do
        Module.put_attribute(__MODULE__, :value, 1)
        Module.get_attribute(__MODULE__, :value) #=> 1

        Module.get_attribute(__MODULE__, :value, :default) #=> 1
        Module.get_attribute(__MODULE__, :not_found, :default) #=> :default

        Module.register_attribute(__MODULE__, :value, accumulate: true)
        Module.put_attribute(__MODULE__, :value, 1)
        Module.get_attribute(__MODULE__, :value) #=> [1]
      end


  """
  def get_attribute(module, key, default \\ nil) do
    # body not decompiled
  end

  @doc ~S"""
  Checks if the given attribute has been defined.

  An attribute is defined if it has been registered with `register_attribute/3`
  or assigned a value. If an attribute has been deleted with `delete_attribute/2`
  it is no longer considered defined.

  This function can only be used on modules that have not yet been compiled.

  ## Examples

      defmodule MyModule do
        @value 1
        Module.register_attribute(__MODULE__, :other_value)
        Module.put_attribute(__MODULE__, :another_value, 1)

        Module.has_attribute?(__MODULE__, :value) #=> true
        Module.has_attribute?(__MODULE__, :other_value) #=> true
        Module.has_attribute?(__MODULE__, :another_value) #=> true

        Module.has_attribute?(__MODULE__, :undefined) #=> false

        Module.delete_attribute(__MODULE__, :value)
        Module.has_attribute?(__MODULE__, :value) #=> false
      end


  """
  def has_attribute?(module, key) do
    # body not decompiled
  end

  @doc ~S"""
  Makes the given functions in `module` overridable.

  An overridable function is lazily defined, allowing a
  developer to customize it. See `Kernel.defoverridable/1` for
  more information and documentation.

  Once a function or a macro is marked as overridable, it will
  no longer be listed under `definitions_in/1` or return true
  when given to `defines?/2` until another implementation is
  given.

  """
  def make_overridable(module, tuples) do
    # body not decompiled
  end

  def module_info() do
    # body not decompiled
  end

  def module_info(p0) do
    # body not decompiled
  end

  @doc ~S"""
  Checks if a module is open.

  A module is "open" if it is currently being defined and its attributes and
  functions can be modified.

  """
  def open?(module) do
    # body not decompiled
  end

  @doc ~S"""
  Returns `true` if `tuple` in `module` is marked as overridable.

  """
  def overridable?(module, tuple) do
    # body not decompiled
  end

  @doc ~S"""
  Puts a module attribute with `key` and `value` in the given `module`.

  ## Examples

      defmodule MyModule do
        Module.put_attribute(__MODULE__, :custom_threshold_for_lib, 10)
      end


  """
  def put_attribute(module, key, value) do
    # body not decompiled
  end

  @doc ~S"""
  Registers an attribute.

  By registering an attribute, a developer is able to customize
  how Elixir will store and accumulate the attribute values.

  ## Options

  When registering an attribute, two options can be given:

    * `:accumulate` - several calls to the same attribute will
      accumulate instead of overriding the previous one. New attributes
      are always added to the top of the accumulated list.

    * `:persist` - the attribute will be persisted in the Erlang
      Abstract Format. Useful when interfacing with Erlang libraries.

  By default, both options are `false`.

  ## Examples

      defmodule MyModule do
        Module.register_attribute(__MODULE__, :custom_threshold_for_lib, accumulate: true)

        @custom_threshold_for_lib 10
        @custom_threshold_for_lib 20
        @custom_threshold_for_lib #=> [20, 10]
      end


  """
  def register_attribute(module, attribute, options) do
    # body not decompiled
  end

  @doc ~S"""
  Concatenates a list of aliases and returns a new alias only if the alias
  was already referenced.

  If the alias was not referenced yet, fails with `ArgumentError`.
  It handles charlists, binaries and atoms.

  ## Examples

      iex> Module.safe_concat([Module, Unknown])
      ** (ArgumentError) argument error

      iex> Module.safe_concat([List, Chars])
      List.Chars


  """
  def safe_concat(list) do
    # body not decompiled
  end

  @doc ~S"""
  Concatenates two aliases and returns a new alias only if the alias was
  already referenced.

  If the alias was not referenced yet, fails with `ArgumentError`.
  It handles charlists, binaries and atoms.

  ## Examples

      iex> Module.safe_concat(Module, Unknown)
      ** (ArgumentError) argument error

      iex> Module.safe_concat(List, Chars)
      List.Chars


  """
  def safe_concat(left, right) do
    # body not decompiled
  end

  @doc ~S"""
  Copies the given spec as a callback.

  Returns `true` if there is such a spec and it was copied as a callback.
  If the function associated to the spec has documentation defined prior to
  invoking this function, the docs are copied too.

  """
  def spec_to_callback(module, definition) do
    # body not decompiled
  end

  @doc ~S"""
  Splits the given module name into binary parts.

  `module` has to be an Elixir module, as `split/1` won't work with Erlang-style
  modules (for example, `split(:lists)` raises an error).

  `split/1` also supports splitting the string representation of Elixir modules
  (that is, the result of calling `Atom.to_string/1` with the module name).

  ## Examples

      iex> Module.split(Very.Long.Module.Name.And.Even.Longer)
      ["Very", "Long", "Module", "Name", "And", "Even", "Longer"]
      iex> Module.split("Elixir.String.Chars")
      ["String", "Chars"]


  """
  def split(module) do
    # body not decompiled
  end

  # Private Functions

  defp unquote(:"-check_behaviours/2-fun-0-")(p0, p1, p2, p3, p4) do
    # body not decompiled
  end

  defp unquote(:"-check_behaviours/2-fun-1-")(p0, p1, p2) do
    # body not decompiled
  end

  defp unquote(:"-check_callbacks/3-fun-0-")(p0, p1, p2, p3) do
    # body not decompiled
  end

  defp unquote(:"-check_impls/4-fun-0-")(p0, p1) do
    # body not decompiled
  end

  defp unquote(:"-check_impls/4-fun-1-")(p0, p1, p2) do
    # body not decompiled
  end

  defp unquote(:"-check_impls/4-fun-2-")(p0, p1, p2, p3, p4) do
    # body not decompiled
  end

  defp unquote(:"-impl_behaviours/6-fun-0-")(p0, p1, p2) do
    # body not decompiled
  end

  defp unquote(:"-known_callbacks/1-fun-0-")(p0, p1) do
    # body not decompiled
  end

  defp unquote(:"-make_overridable/2-fun-0-")(p0, p1) do
    # body not decompiled
  end

  defp unquote(:"-make_overridable/2-fun-1-")(p0, p1) do
    # body not decompiled
  end

  defp unquote(:"-make_overridable/2-fun-2-")(p0, p1, p2) do
    # body not decompiled
  end

  defp unquote(:"-simplify_arg/3-fun-0-")(p0, p1) do
    # body not decompiled
  end

  defp unquote(:"-warn_missing_impls/4-fun-0-")(p0, p1, p2, p3, p4) do
    # body not decompiled
  end

  defp add_callback(p0, p1, p2, p3, p4) do
    # body not decompiled
  end

  defp add_defaults_count(p0, p1) do
    # body not decompiled
  end

  defp args_count(p0, p1, p2) do
    # body not decompiled
  end

  defp assert_not_compiled!(p0, p1) do
    # body not decompiled
  end

  defp assert_not_compiled!(p0, p1, p2) do
    # body not decompiled
  end

  defp assert_not_compiled_message(p0, p1, p2) do
    # body not decompiled
  end

  defp assert_not_readonly!(p0, p1) do
    # body not decompiled
  end

  defp attribute_stack(p0, p1) do
    # body not decompiled
  end

  defp autogenerated_key(p0, p1) do
    # body not decompiled
  end

  defp bag_lookup_element(p0, p1, p2) do
    # body not decompiled
  end

  defp behaviour_callbacks_for_impls(p0, p1, p2) do
    # body not decompiled
  end

  defp behaviour_info(p0, p1) do
    # body not decompiled
  end

  defp build_signature(p0, p1) do
    # body not decompiled
  end

  defp callbacks_for_impls(p0, p1) do
    # body not decompiled
  end

  defp check_behaviours(p0, p1) do
    # body not decompiled
  end

  defp check_callbacks(p0, p1, p2) do
    # body not decompiled
  end

  defp check_impls(p0, p1, p2, p3) do
    # body not decompiled
  end

  defp check_module_for_overridable(p0, p1) do
    # body not decompiled
  end

  defp compile_deprecated(p0, p1, p2, p3, p4, p5) do
    # body not decompiled
  end

  defp compile_doc(p0, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11) do
    # body not decompiled
  end

  defp compile_doc_meta(p0, p1, p2, p3, p4) do
    # body not decompiled
  end

  defp compile_impl(p0, p1, p2, p3, p4, p5, p6, p7) do
    # body not decompiled
  end

  defp data_tables_for(p0) do
    # body not decompiled
  end

  defp deprecated_reason(p0, p1, p2) do
    # body not decompiled
  end

  defp deprecated_reasons(p0, p1, p2, p3) do
    # body not decompiled
  end

  defp doc_key(p0) do
    # body not decompiled
  end

  defp expand_key(p0, p1) do
    # body not decompiled
  end

  defp expand_keys(p0, p1, p2) do
    # body not decompiled
  end

  defp format_callback(p0, p1, p2) do
    # body not decompiled
  end

  defp format_definition(p0) do
    # body not decompiled
  end

  defp format_definition(p0, p1) do
    # body not decompiled
  end

  defp format_impl_warning(p0, p1, p2) do
    # body not decompiled
  end

  defp get_doc_info(p0, p1) do
    # body not decompiled
  end

  defp get_doc_meta(p0, p1) do
    # body not decompiled
  end

  defp impl_behaviours(p0, p1, p2, p3, p4) do
    # body not decompiled
  end

  defp impl_behaviours(p0, p1, p2, p3, p4, p5) do
    # body not decompiled
  end

  defp known_callbacks(p0) do
    # body not decompiled
  end

  defp merge_signature(p0, p1, p2) do
    # body not decompiled
  end

  defp merge_signatures(p0, p1, p2) do
    # body not decompiled
  end

  defp missing_impl_in_context?(p0, p1, p2) do
    # body not decompiled
  end

  defp normalize_macro_or_function_callback(p0) do
    # body not decompiled
  end

  defp preprocess_attribute(p0, p1) do
    # body not decompiled
  end

  defp preprocess_doc_meta(p0, p1, p2, p3) do
    # body not decompiled
  end

  defp protocol?(p0) do
    # body not decompiled
  end

  defp put_attribute(p0, p1, p2, p3, p4, p5) do
    # body not decompiled
  end

  defp reverse_values(p0, p1) do
    # body not decompiled
  end

  defp simplify_arg(p0, p1, p2) do
    # body not decompiled
  end

  defp simplify_args(p0, p1, p2, p3) do
    # body not decompiled
  end

  defp simplify_module_name(p0) do
    # body not decompiled
  end

  defp simplify_var(p0, p1) do
    # body not decompiled
  end

  defp split(p0, p1) do
    # body not decompiled
  end

  defp validate_doc_meta(p0, p1) do
    # body not decompiled
  end

  defp warn_missing_impls(p0, p1, p2, p3) do
    # body not decompiled
  end
end
