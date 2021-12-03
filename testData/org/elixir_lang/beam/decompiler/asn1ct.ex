# Source code recreated from a .beam file by IntelliJ Elixir
defmodule :asn1ct do

  # Functions

  def add_generated_refed_func(data) do
    case is_function_generated(data) do
      true ->
        :ok
      _ ->
        l = get_gen_state_field(:gen_refed_funcs)
        update_gen_state(:gen_refed_funcs, [data | l])
    end
  end

  def add_tobe_refed_func(data) do
    {name, sI, pattern} = (fn {n, si, p, _} ->
        {n, si, p}
      d ->
        d
    end).(data)
    newData = case sI do
      i when is_integer(i) ->
        (fn d ->
            d
        end).(data)
      _ ->
        (fn {n, _, p} ->
            {n, 0, p}
          {n, _, p, t} ->
            {n, 0, p, t}
        end).(data)
    end
    l = get_gen_state_field(:generated_functions)
    case generated_functions_member(get(:currmod), name, l, pattern) do
      true ->
        :ok
      _ ->
        add_once_tobe_refed_func(newData)
        maybe_rename_function(:tobe_refed, name, pattern)
    end
  end

  def compile(file), do: compile(file, [])

  def compile(file, options0) when is_list(options0) do
    try do
      translate_options(options0)
    catch
      {:throw, error, _} ->
        error
    else
      options1 ->
        options2 = includes(file, options1)
        includes = strip_includes(options2)
        in_process(fn  ->
            compile_proc(file, includes, options2)
        end)
    end
  end

  def compile(file, _OutFile, options) do
    case compile(file, make_erl_options(options)) do
      {:error, _Reason} ->
        :error
      :ok ->
        :ok
      parseRes when is_tuple(parseRes) ->
        :io.format('~p~n', [parseRes])
        :ok
      scanRes when is_list(scanRes) ->
        :io.format('~p~n', [scanRes])
        :ok
    end
  end

  def compile_asn(file, outFile, options), do: compile(:lists.concat([file, '.asn']), outFile, options)

  def compile_asn1(file, outFile, options), do: compile(:lists.concat([file, '.asn1']), outFile, options)

  def compile_py(file, outFile, options), do: compile(:lists.concat([file, '.py']), outFile, options)

  def current_sindex(), do: get_gen_state_field(:current_suffix_index)

  def error(format, args, s) do
    case is_error(s) do
      true ->
        :io.format(format, args)
      false ->
        :ok
    end
  end

  def format_error({:write_error, file, reason}), do: :io_lib.format('writing output file ~s failed: ~s', [file, :file.format_error(reason)])

  def generated_refed_func(name) do
    l = get_gen_state_field(:tobe_refed_funcs)
    newL = :lists.keydelete(name, 1, l)
    update_gen_state(:tobe_refed_funcs, newL)
    l2 = get_gen_state_field(:gen_refed_funcs)
    update_gen_state(:gen_refed_funcs, [name | l2])
  end

  def get_bit_string_format(), do: get(:bit_string_format)

  def get_gen_state_field(field) do
    case read_config_data(:gen_state) do
      :undefined ->
        :undefined
      genState when is_record(genState, :gen_state) ->
        get_gen_state_field(genState, field)
      err ->
        exit({:error, {:asn1, {'false configuration file info', err}}})
    end
  end

  def get_name_of_def(typedef(name: name)), do: name

  def get_name_of_def(classdef(name: name)), do: name

  def get_name_of_def(valuedef(name: name)), do: name

  def get_name_of_def(ptypedef(name: name)), do: name

  def get_name_of_def(pvaluedef(name: name)), do: name

  def get_name_of_def(pvaluesetdef(name: name)), do: name

  def get_name_of_def(pobjectdef(name: name)), do: name

  def get_name_of_def(pobjectsetdef(name: name)), do: name

  def get_name_of_def(_), do: :undefined

  def get_pos_of_def(typedef(pos: pos)), do: pos

  def get_pos_of_def(classdef(pos: pos)), do: pos

  def get_pos_of_def(valuedef(pos: pos)), do: pos

  def get_pos_of_def(ptypedef(pos: pos)), do: pos

  def get_pos_of_def(pvaluedef(pos: pos)), do: pos

  def get_pos_of_def(pvaluesetdef(pos: pos)), do: pos

  def get_pos_of_def(pobjectdef(pos: pos)), do: pos

  def get_pos_of_def(pobjectsetdef(pos: pos)), do: pos

  def get_pos_of_def(unquote(:"Externaltypereference")(pos: pos)), do: pos

  def get_pos_of_def(unquote(:"Externalvaluereference")(pos: pos)), do: pos

  def get_pos_of_def(_), do: :undefined

  def get_tobe_refed_func(name) do
    case get_gen_state_field(:tobe_refed_funcs) do
      l when is_list(l) ->
        case :lists.keysearch(name, 1, l) do
          {_, element} ->
            element
          _ ->
            :undefined
        end
      _ ->
        :undefined
    end
  end

  def is_function_generated(name) do
    case get_gen_state_field(:gen_refed_funcs) do
      l when is_list(l) ->
        :lists.member(name, l)
      _ ->
        false
    end
  end

  def maybe_rename_function(mode, name, pattern), do: ...

  def maybe_saved_sindex(name, pattern) do
    case get_gen_state_field(:generated_functions) do
      [] ->
        false
      l ->
        case generated_functions_member(get(:currmod), name, l) do
          true ->
            l2 = generated_functions_filter(get(:currmod), name, l)
            case :lists.keysearch(pattern, 3, l2) do
              {:value, {_, i, _}} ->
                i
              _ ->
                length(l2)
            end
          _ ->
            false
        end
    end
  end

  def module_info() do
    # body not decompiled
  end

  def module_info(p0) do
    # body not decompiled
  end

  def next_refed_func() do
    case get_gen_state_field(:tobe_refed_funcs) do
      [] ->
        []
      [h | t] ->
        update_gen_state(:tobe_refed_funcs, t)
        h
    end
  end

  def parse_and_save(module, s) do
    options = state(s, :options)
    sourceDir = state(s, :sourcedir)
    includes = for {:i, i} <- options do
      i
    end
    erule = state(s, :erule)
    maps = :lists.member(:maps, options)
    case get_input_file(module, [sourceDir | includes]) do
      {:file, suffixedASN1source} ->
        mtime = :filelib.last_modified(suffixedASN1source)
        case :asn1_db.dbload(module, erule, maps, mtime) do
          :ok ->
            :ok
          :error ->
            parse_and_save1(s, suffixedASN1source, options)
        end
      err when notmaps ->
        case :asn1_db.dbload(module) do
          :ok ->
            warning('could not do a consistency check of the ~p file: no asn1 source file was found.~n', [:lists.concat([module, '.asn1db'])], options)
          :error ->
            :ok
        end
        {:error, {:asn1, :input_file_error, err}}
      err ->
        {:error, {:asn1, :input_file_error, err}}
    end
  end

  def partial_inc_dec_toptype([t | _]) when is_atom(t), do: t

  def partial_inc_dec_toptype([{t, _} | _]) when is_atom(t), do: t

  def partial_inc_dec_toptype([l | _]) when is_list(l), do: partial_inc_dec_toptype(l)

  def partial_inc_dec_toptype(_), do: throw({:error, {'no top type found for partial incomplete decode'}})

  def read_config_data(key) do
    case :asn1ct_table.exists(:asn1_general) do
      false ->
        :undefined
      true ->
        case :asn1ct_table.lookup(:asn1_general, {:asn1_config, key}) do
          [{_, data}] ->
            data
          err ->
            err
        end
    end
  end

  def reset_gen_state(), do: save_gen_state(gen_state())

  def set_current_sindex(index), do: update_gen_state(:current_suffix_index, index)

  def step_in_constructed() do
    case get_gen_state_field(:namelist) do
      [l] when is_list(l) ->
        update_gen_state(:namelist, l)
      _ ->
        :ok
    end
  end

  def test(module), do: test_module(module, [])

  def test(module, [] = options), do: test_module(module, options)

  def test(module, [{:i, _} | _] = options), do: test_module(module, options)

  def test(module, type), do: test_type(module, type, [])

  def test(module, type, [] = options), do: test_type(module, type, options)

  def test(module, type, [{:i, _} | _] = options), do: test_type(module, type, options)

  def test(module, type, value), do: test_value(module, type, value)

  def unset_pos_mod(def) when is_record(def, :typedef), do: typedef(def, pos: :undefined)

  def unset_pos_mod(def) when is_record(def, :classdef), do: classdef(def, pos: :undefined)

  def unset_pos_mod(def) when is_record(def, :valuedef), do: valuedef(def, pos: :undefined, module: :undefined)

  def unset_pos_mod(def) when is_record(def, :ptypedef), do: ptypedef(def, pos: :undefined)

  def unset_pos_mod(def) when is_record(def, :pvaluedef), do: pvaluedef(def, pos: :undefined)

  def unset_pos_mod(def) when is_record(def, :pvaluesetdef), do: pvaluesetdef(def, pos: :undefined)

  def unset_pos_mod(def) when is_record(def, :pobjectdef), do: pobjectdef(def, pos: :undefined)

  def unset_pos_mod(def) when is_record(def, :pobjectsetdef), do: pobjectsetdef(def, pos: :undefined)

  def unset_pos_mod(unquote(:"ComponentType")() = def), do: unquote(:"ComponentType")(def, pos: :undefined)

  def unset_pos_mod(def), do: def

  def update_gen_state(field, data) do
    case get_gen_state() do
      state when is_record(state, :gen_state) ->
        update_gen_state(field, state, data)
      _ ->
        exit({:error, {:asn1, {:internal, 'tried to update nonexistent gen_state', field, data}}})
    end
  end

  def update_namelist(name) do
    case get_gen_state_field(:namelist) do
      [name, rest] ->
        update_gen_state(:namelist, rest)
      [name | rest] ->
        update_gen_state(:namelist, rest)
      [{name, list}] when is_list(list) ->
        update_gen_state(:namelist, list)
      [{name, atom} | rest] when is_atom(atom) ->
        update_gen_state(:namelist, rest)
      other ->
        other
    end
  end

  def use_legacy_types(), do: get(:use_legacy_erlang_types)

  def value(module, type), do: value(module, type, [])

  def value(module, type, includes) do
    in_process(fn  ->
        start(strip_includes(includes))
        case check(module, includes) do
          {:ok, _NewTypes} ->
            get_value(module, type)
          error ->
            error
        end
    end)
  end

  def verbose(format, args, s) do
    case is_verbose(s) do
      true ->
        :io.format(format, args)
      false ->
        :ok
    end
  end

  def vsn(), do: '5.0.15.1'

  def warning(format, args, s) do
    case is_warning(s) do
      true ->
        :io.format('Warning: ' ++ format, args)
      false ->
        :ok
    end
  end

  def warning(format, args, s, reason) do
    case {is_werr(s), is_error(s), is_warning(s)} do
      {true, true, _} ->
        :io.format(format, args)
        throw({:error, reason})
      {false, _, true} ->
        :io.format(format, args)
      _ ->
        :ok
    end
  end

  # Private Functions

  defp unquote(:"-add_once_tobe_refed_func/1-fun-0-")(p0, p1, p2) do
    # body not decompiled
  end

  defp unquote(:"-add_tobe_refed_func/1-fun-0-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-add_tobe_refed_func/1-fun-1-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-add_tobe_refed_func/1-fun-2-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-clean_errors/1-fun-0-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-common_exports/1-fun-0-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-common_exports/1-fun-1-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-compile/2-fun-0-")(p0, p1, p2) do
    # body not decompiled
  end

  defp unquote(:"-compile_set/3-lc$^0/1-0-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-create_partial_decode_gen_info/2-lc$^0/1-0-")(p0, p1) do
    # body not decompiled
  end

  defp unquote(:"-create_pdec_command/4-fun-0-")(p0, p1, p2) do
    # body not decompiled
  end

  defp unquote(:"-delete_double_of_symbol1/2-fun-0-")(p0, p1) do
    # body not decompiled
  end

  defp unquote(:"-delete_double_of_symbol1/2-fun-1-")(p0, p1) do
    # body not decompiled
  end

  defp unquote(:"-delete_double_of_symbol1/2-fun-2-")(p0, p1, p2) do
    # body not decompiled
  end

  defp unquote(:"-discover_dupl_in_mods/5-fun-0-")(p0, p1, p2, p3, p4) do
    # body not decompiled
  end

  defp unquote(:"-exit_if_nameduplicate2/2-fun-0-")(p0, p1) do
    # body not decompiled
  end

  defp unquote(:"-export_all/1-fun-0-")(p0, p1) do
    # body not decompiled
  end

  defp unquote(:"-export_all/1-fun-1-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-fun.abs_listing/1-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-fun.check_pass/1-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-fun.compile_pass/1-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-fun.exit_if_nameduplicate/1-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-fun.generate_pass/1-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-fun.merge_pass/1-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-fun.parse_listing/1-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-fun.parse_pass/1-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-fun.run_tc/3-")(p0, p1, p2) do
    # body not decompiled
  end

  defp unquote(:"-fun.save_pass/1-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-fun.scan_pass/1-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-fun.set_scan_parse_pass/1-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-generated_functions_filter/3-fun-0-")(p0, p1) do
    # body not decompiled
  end

  defp unquote(:"-generated_functions_filter/3-fun-1-")(p0, p1, p2) do
    # body not decompiled
  end

  defp unquote(:"-get_rule/1-lc$^0/1-0-")(p0, p1) do
    # body not decompiled
  end

  defp unquote(:"-get_rule/1-lc$^1/1-1-")(p0, p1, p2, p3) do
    # body not decompiled
  end

  defp unquote(:"-in_process/1-fun-0-")(p0, p1) do
    # body not decompiled
  end

  defp unquote(:"-include_append/2-fun-0-")(p0, p1) do
    # body not decompiled
  end

  defp unquote(:"-include_prepend/2-fun-0-")(p0, p1) do
    # body not decompiled
  end

  defp unquote(:"-make_erl_options/1-fun-0-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-make_erl_options/1-fun-1-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-merge_modules/2-fun-0-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-merge_modules/2-fun-1-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-merge_modules/2-fun-2-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-merge_symbols_from_module/2-fun-0-")(p0, p1) do
    # body not decompiled
  end

  defp unquote(:"-merge_symbols_from_module/2-fun-1-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-parse_and_save/2-lc$^0/1-0-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-pretty2/2-fun-0-")(p0, p1, p2) do
    # body not decompiled
  end

  defp unquote(:"-pretty2/2-fun-1-")(p0, p1, p2) do
    # body not decompiled
  end

  defp unquote(:"-pretty2/2-fun-2-")(p0, p1, p2) do
    # body not decompiled
  end

  defp unquote(:"-pretty2/2-fun-3-")(p0, p1, p2) do
    # body not decompiled
  end

  defp unquote(:"-pretty2/2-fun-4-")(p0, p1, p2) do
    # body not decompiled
  end

  defp unquote(:"-pretty2/2-fun-5-")(p0, p1, p2) do
    # body not decompiled
  end

  defp unquote(:"-print_structured_errors/1-lc$^0/1-0-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-read_config_file/2-lc$^0/1-0-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-remove_asn_flags/1-lc$^0/1-0-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-run_passes/2-fun-0-")(p0, p1, p2) do
    # body not decompiled
  end

  defp unquote(:"-save_automatic_tagged_types/1-fun-0-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-save_imports/1-fun-0-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-save_imports/1-fun-1-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-strip_includes/1-lc$^0/1-0-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-test_module/2-fun-0-")(p0, p1) do
    # body not decompiled
  end

  defp unquote(:"-test_type/3-fun-0-")(p0, p1, p2) do
    # body not decompiled
  end

  defp unquote(:"-test_value/3-fun-0-")(p0, p1, p2) do
    # body not decompiled
  end

  defp unquote(:"-type_check/1-fun-0-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-value/3-fun-0-")(p0, p1, p2) do
    # body not decompiled
  end

  def abs_listing(st(code: {m, _}, outfile: outFile)) do
    pretty2(module(m, :name), outFile ++ '.abs')
    :done
  end

  def add_generated_function(data) do
    l = get_gen_state_field(:generated_functions)
    update_gen_state(:generated_functions, [data | l])
  end

  def add_once_tobe_refed_func(data) do
    tRFL = get_gen_state_field(:tobe_refed_funcs)
    {name, index} = {element(1, data), element(2, data)}
    case :lists.filter(fn {n, i, _} when n == name and i == index ->
        true
      {n, i, _, _} when n == name and i == index ->
        true
      _ ->
        false
    end, tRFL) do
      [] ->
        update_gen_state(:tobe_refed_funcs, [data | tRFL])
      _ ->
        :ok
    end
  end

  def anonymous_dec_command(:undec, :"OPTIONAL"), do: :opt_undec

  def anonymous_dec_command(command, _), do: command

  def check(module, includes) do
    case :asn1_db.dbload(module) do
      :error ->
        {:error, :asn1db_missing_or_out_of_date}
      :ok ->
        m = :asn1_db.dbget(module, :"MODULE")
        typeOrVal = module(m, :typeorval)
        state = state(mname: module(m, :name), module: module(m, typeorval: []), options: includes)
        case :asn1ct_check.check(state, typeOrVal) do
          {:ok, {newTypes, _, _, _, _, _}, _} ->
            {:ok, newTypes}
          {:error, reason} ->
            {:error, reason}
        end
    end
  end

  def check_maps_option(gen(pack: :map)) do
    case get_bit_string_format() do
      :bitstring ->
        :ok
      _ ->
        message1 = 'The \'maps\' option must not be combined with \'compact_bit_string\' or \'legacy_bit_string\''
        exit({:error, {:asn1, message1}})
    end
    case use_legacy_types() do
      false ->
        :ok
      true ->
        message2 = 'The \'maps\' option must not be combined with \'legacy_erlang_types\''
        exit({:error, {:asn1, message2}})
    end
  end

  def check_maps_option(gen()), do: :ok

  def check_pass(st(code: m, file: file, includes: includes, erule: erule, dbfile: dbFile, opts: opts, inputmodules: inputModules) = st) do
    start(includes)
    case :asn1ct_check.storeindb(state(erule: erule, options: opts), m) do
      :ok ->
        module = :asn1_db.dbget(module(m, :name), :"MODULE")
        state = state(mname: module(module, :name), module: module(module, typeorval: []), erule: erule, inputmodules: inputModules, options: opts, sourcedir: :filename.dirname(file))
        case :asn1ct_check.check(state, module(module, :typeorval)) do
          {:error, reason} ->
            {:error, st(st, error: reason)}
          {:ok, newTypeOrVal, genTypeOrVal} ->
            newM = module(module, typeorval: newTypeOrVal)
            :asn1_db.dbput(module(newM, :name), :"MODULE", newM)
            :asn1_db.dbsave(dbFile, module(m, :name))
            verbose('--~p--~n', [{:generated, dbFile}], opts)
            {:ok, st(st, code: {m, genTypeOrVal})}
        end
      {:error, reason} ->
        {:error, st(st, error: reason)}
    end
  end

  def check_tagdefault(modList) do
    case have_same_tagdefault(modList) do
      {true, tagDefault} ->
        tagDefault
      {false, tagDefault} ->
        :asn1ct_table.new(:automatic_tags)
        save_automatic_tagged_types(modList)
        tagDefault
    end
  end

  def clean_errors(errors) when is_list(errors) do
    f = fn {:structured_error, _, _, _} ->
        true
      _ ->
        false
    end
    {structured0, adHoc} = :lists.partition(f, errors)
    structured = :lists.sort(structured0)
    {structured, structured ++ adHoc}
  end

  def clean_errors(adHoc), do: {[], adHoc}

  def cleanup_bit_string_format(), do: erase(:bit_string_format)

  def common_exports(moduleList) do
    case :lists.filter(fn x ->
        element(2, module(x, :exports)) != :all
    end, moduleList) do
      [] ->
        {:exports, :all}
      modsWithExpList ->
        cExports1 = :lists.append(:lists.map(fn x ->
            element(2, module(x, :exports))
        end, modsWithExpList))
        cExports2 = export_all(:lists.subtract(moduleList, modsWithExpList))
        {:exports, cExports1 ++ cExports2}
    end
  end

  def common_imports(iList, inputMNameL) do
    setExternalImportsList = remove_in_set_imports(iList, inputMNameL, [])
    {:imports, remove_import_doubles(setExternalImportsList)}
  end

  def common_passes(), do: [{:iff, :parse, {:pass, :parse_listing, &parse_listing/1}}, {:pass, :check, &check_pass/1}, {:iff, :abs, {:pass, :abs_listing, &abs_listing/1}}, {:pass, :generate, &generate_pass/1}, {:unless, :noobj, {:pass, :compile, &compile_pass/1}}]

  def compare_defs(d1, d2), do: compare_defs2(unset_pos_mod(d1), unset_pos_mod(d2))

  def compare_defs2(d, ^d), do: :equal

  def compare_defs2(_, _), do: :not_equal

  def compile1(file, st(opts: opts) = st0) do
    compiler_verbose(file, opts)
    passes = single_passes()
    base = :filename.rootname(:filename.basename(file))
    outFile = outfile(base, "", opts)
    dbFile = outfile(base, 'asn1db', opts)
    st1 = st(st0, file: file, outfile: outFile, dbfile: dbFile)
    run_passes(passes, st1)
  end

  def compile_pass(st(outfile: outFile, opts: opts0) = st) do
    :asn1_db.dbstop()
    :asn1ct_table.delete([:renamed_defs, :original_imports, :automatic_tags])
    opts = remove_asn_flags(opts0)
    case :c.c(outFile, opts) do
      {:ok, _Module} ->
        {:ok, st}
      _ ->
        {:error, st}
    end
  end

  def compile_proc(file, includes, options) do
    erule = get_rule(options)
    st = st(opts: options, includes: includes, erule: erule)
    case input_file_type(file, includes) do
      {:single_file, suffixedFile} ->
        compile1(suffixedFile, st)
      {:multiple_files_file, setBase, fileName} ->
        case get_file_list(fileName, includes) do
          fileList when is_list(fileList) ->
            compile_set(setBase, fileList, st)
          err ->
            err
        end
      err = {:input_file_error, _Reason} ->
        {:error, err}
    end
  end

  def compile_set(setBase, files, st(opts: opts) = st0) do
    compiler_verbose(files, opts)
    outFile = outfile(setBase, "", opts)
    dbFile = outfile(setBase, 'asn1db', opts)
    inputModules = for f0 <- files do
      unknown_abstract_code
    end
    st = st(st0, file: setBase, files: files, outfile: outFile, dbfile: dbFile, inputmodules: inputModules)
    passes = set_passes()
    run_passes(passes, st)
  end

  def compiler_verbose(what, opts) do
    verbose('Erlang ASN.1 compiler ~s\n', ['5.0.15.1'], opts)
    verbose('Compiling: ~p\n', [what], opts)
    verbose('Options: ~p\n', [opts], opts)
  end

  def concat_sequential(l = [a, b], acc) when is_atom(a) and is_binary(b), do: [l | acc]

  def concat_sequential(l, acc) when is_list(l), do: concat_sequential1(:lists.reverse(l), acc)

  def concat_sequential(a, acc), do: [a | acc]

  def concat_sequential1([], acc), do: acc

  def concat_sequential1([[]], acc), do: acc

  def concat_sequential1([el | restEl], acc) when is_list(el), do: concat_sequential1(restEl, [el | acc])

  def concat_sequential1([:mandatory | restEl], acc), do: concat_sequential1(restEl, [:mandatory | acc])

  def concat_sequential1(l, acc), do: [l | acc]

  def concat_tags(ts, acc) do
    case many_tags(ts) do
      true when is_list(ts) ->
        :lists.reverse(ts) ++ acc
      true ->
        [ts | acc]
      false ->
        [ts | acc]
    end
  end

  def create_partial_decode_gen_info(modName, {^modName, typeLists}) do
    for tL <- typeLists do
      create_partial_decode_gen_info1(modName, tL)
    end
  end

  def create_partial_decode_gen_info(_, []), do: []

  def create_partial_decode_gen_info(_M1, {m2, _}), do: throw({:error, {'wrong module name in asn1 config file', m2}})

  def create_partial_decode_gen_info1(modName, {funcName, typeList}) do
    case typeList do
      [topType | rest] ->
        case :asn1_db.dbget(modName, topType) do
          typedef(typespec: tS) ->
            tagCommand = get_tag_command(tS, :choosen)
            ret = create_pdec_command(modName, get_components(type(tS, :def)), rest, concat_tags(tagCommand, []))
            {funcName, ret}
          _ ->
            throw({:error, {'wrong type list in asn1 config file', typeList}})
        end
      _ ->
        []
    end
  end

  def create_partial_decode_gen_info1(_, _), do: :ok

  def create_partial_inc_decode_gen_info(modName, {mod, [{name, l} | ls]}) when is_list(l) do
    topTypeName = partial_inc_dec_toptype(l)
    [{name, topTypeName, create_partial_inc_decode_gen_info1(modName, topTypeName, {mod, l})} | create_partial_inc_decode_gen_info(modName, {mod, ls})]
  end

  def create_partial_inc_decode_gen_info(_, {_, []}), do: []

  def create_partial_inc_decode_gen_info(_, []), do: []

  def create_partial_inc_decode_gen_info1(modName, topTypeName, {^modName, [_TopType | rest]}) do
    case :asn1_db.dbget(modName, topTypeName) do
      typedef(typespec: tS) ->
        tagCommand = get_tag_command(tS, :mandatory, :mandatory)
        create_pdec_inc_command(modName, get_components(type(tS, :def)), rest, [tagCommand])
      _ ->
        throw({:error, {'wrong type list in asn1 config file', topTypeName}})
    end
  end

  def create_partial_inc_decode_gen_info1(m1, _, {m2, _}) when m1 != m2, do: throw({:error, {'wrong module name in asn1 config file', m2}})

  def create_partial_inc_decode_gen_info1(_, _, tNL), do: throw({:error, {'wrong type list in asn1 config file', tNL}})

  def create_pdec_command(_ModName, _, [], acc) do
    remove_empty_lists = fn [[] | l], res, fun ->
        fun.(l, res, fun)
      [], res, _ ->
        res
      [h | l], res, fun ->
        fun.(l, [h | res], fun)
    end
    remove_empty_lists.(acc, [], remove_empty_lists)
  end

  def create_pdec_command(modName, [unquote(:"ComponentType")(name: c1, typespec: tS) | _Comps], [^c1 | cs], acc) do
    tagCommand = get_tag_command(tS, :choosen)
    create_pdec_command(modName, get_components(type(tS, :def)), cs, concat_tags(tagCommand, acc))
  end

  def create_pdec_command(modName, [unquote(:"ComponentType")(typespec: tS, prop: prop) | comps], [c2 | cs], acc) do
    tagCommand = case prop do
      :mandatory ->
        get_tag_command(tS, :skip)
      _ ->
        get_tag_command(tS, :skip_optional)
    end
    create_pdec_command(modName, comps, [c2 | cs], concat_tags(tagCommand, acc))
  end

  def create_pdec_command(modName, {:"CHOICE", [comp = unquote(:"ComponentType")(name: c1) | _]}, tNL = [c1 | _Cs], acc), do: create_pdec_command(modName, [comp], tNL, acc)

  def create_pdec_command(modName, {:"CHOICE", [unquote(:"ComponentType")() | comps]}, tNL, acc), do: create_pdec_command(modName, {:"CHOICE", comps}, tNL, acc)

  def create_pdec_command(modName, {:"CHOICE", {cs1, cs2}}, tNL, acc) when is_list(cs1) and is_list(cs2), do: create_pdec_command(modName, {:"CHOICE", cs1 ++ cs2}, tNL, acc)

  def create_pdec_command(modName, unquote(:"Externaltypereference")(module: m, type: c1), typeNameList, acc) do
    type(def: def) = get_referenced_type(m, c1)
    create_pdec_command(modName, get_components(def), typeNameList, acc)
  end

  def create_pdec_command(modName, tS = type(def: def), [c1 | cs], acc) do
    case c1 do
      [1] ->
        tagCommand = get_tag_command(tS, :choosen)
        create_pdec_command(modName, def, cs, concat_tags(tagCommand, acc))
      [n] when is_integer(n) ->
        tagCommand = get_tag_command(tS, :skip)
        create_pdec_command(modName, def, [[n - 1] | cs], concat_tags(tagCommand, acc))
      err ->
        throw({:error, {'unexpected error when creating partial decode command', err}})
    end
  end

  def create_pdec_command(_, _, tNL, _), do: throw({:error, {'unexpected error when creating partial decode command', tNL}})

  def create_pdec_inc_command(_ModName, _, [], acc), do: :lists.reverse(acc)

  def create_pdec_inc_command(modName, {comps1, comps2}, tNL, acc) when is_list(comps1) and is_list(comps2), do: create_pdec_inc_command(modName, comps1 ++ comps2, tNL, acc)

  def create_pdec_inc_command(modN, clist, [cL | _Rest], [[]]) when is_list(cL), do: create_pdec_inc_command(modN, clist, cL, [])

  def create_pdec_inc_command(modN, clist, [cL | _Rest], acc) when is_list(cL) do
    innerDirectives = create_pdec_inc_command(modN, clist, cL, [])
    :lists.reverse([innerDirectives | acc])
  end

  def create_pdec_inc_command(modName, cList = [unquote(:"ComponentType")(name: name, typespec: tS, prop: prop) | comps], tNL = [c1 | cs], acc), do: ...

  def create_pdec_inc_command(modName, {:"CHOICE", [unquote(:"ComponentType")(name: c1, typespec: tS, prop: prop) | comps]}, [{^c1, directive} | rest], acc), do: ...

  def create_pdec_inc_command(modName, {:"CHOICE", [unquote(:"ComponentType")(typespec: tS, prop: prop) | comps]}, tNL, acc) do
    tagCommand = get_tag_command(tS, :alt, prop)
    create_pdec_inc_command(modName, {:"CHOICE", comps}, tNL, concat_sequential(tagCommand, acc))
  end

  def create_pdec_inc_command(m, {:"CHOICE", {cs1, cs2}}, tNL, acc) when is_list(cs1) and is_list(cs2), do: create_pdec_inc_command(m, {:"CHOICE", cs1 ++ cs2}, tNL, acc)

  def create_pdec_inc_command(modName, unquote(:"Externaltypereference")(module: m, type: name), tNL, acc) do
    type(def: def) = get_referenced_type(m, name)
    create_pdec_inc_command(modName, get_components(def), tNL, acc)
  end

  def create_pdec_inc_command(_, _, tNL, _), do: throw({:error, {'unexpected error when creating partial decode command', tNL}})

  def delete_double_of_symbol([i | is], acc) do
    symL = unquote(:"SymbolsFromModule")(i, :symbols)
    newSymL = delete_double_of_symbol1(symL, [])
    delete_double_of_symbol(is, [unquote(:"SymbolsFromModule")(i, symbols: newSymL) | acc])
  end

  def delete_double_of_symbol([], acc), do: acc

  def delete_double_of_symbol1([tRef = unquote(:"Externaltypereference")(type: trefName) | rest], acc) do
    newRest = :lists.filter(fn s ->
        case s do
          unquote(:"Externaltypereference")(type: trefName) ->
            false
          _ ->
            true
        end
    end, rest)
    delete_double_of_symbol1(newRest, [tRef | acc])
  end

  def delete_double_of_symbol1([vRef = unquote(:"Externalvaluereference")(value: vName) | rest], acc) do
    newRest = :lists.filter(fn s ->
        case s do
          unquote(:"Externalvaluereference")(value: vName) ->
            false
          _ ->
            true
        end
    end, rest)
    delete_double_of_symbol1(newRest, [vRef | acc])
  end

  def delete_double_of_symbol1([tRef = {unquote(:"Externaltypereference")(type: mRef), unquote(:"Externaltypereference")(type: tRef)} | rest], acc) do
    newRest = :lists.filter(fn s ->
        case s do
          {unquote(:"Externaltypereference")(type: mRef), unquote(:"Externaltypereference")(type: tRef)} ->
            false
          _ ->
            true
        end
    end, rest)
    delete_double_of_symbol1(newRest, [tRef | acc])
  end

  def delete_double_of_symbol1([], acc), do: acc

  def discover_dupl_in_mods(name, def, [m = module(name: n, typeorval: torV) | ms], acc, anyRenamed) do
    fun = fn t, renamedOrDupl ->
        case {get_name_of_def(t), compare_defs(def, t)} do
          {name, :not_equal} ->
            newT = set_name_of_def(n, name, t)
            warn_renamed_def(n, get_name_of_def(newT), name)
            :asn1ct_table.insert(:renamed_defs, {get_name_of_def(newT), name, n})
            {newT, 1 ||| renamedOrDupl}
          {name, :equal} ->
            warn_deleted_def(n, name)
            {[], 2 ||| renamedOrDupl}
          _ ->
            {t, renamedOrDupl}
        end
    end
    {newTorV, newAnyRenamed} = :lists.mapfoldl(fun, anyRenamed, torV)
    discover_dupl_in_mods(name, def, ms, [module(m, typeorval: :lists.flatten(newTorV)) | acc], newAnyRenamed)
  end

  def discover_dupl_in_mods(_, _, [], acc, anyRenamed), do: {acc, anyRenamed}

  def ensure_ext(moduleName, ext) do
    name = :filename.join([moduleName])
    case :filename.extension(name) do
      ext ->
        name
      _ ->
        name ++ ext
    end
  end

  def exit_if_nameduplicate(module(typeorval: torV)), do: exit_if_nameduplicate(torV)

  def exit_if_nameduplicate([]), do: :ok

  def exit_if_nameduplicate([def | rest]) do
    name = get_name_of_def(def)
    exit_if_nameduplicate2(name, rest)
    exit_if_nameduplicate(rest)
  end

  def exit_if_nameduplicate2(name, rest) do
    pred = fn def ->
        case get_name_of_def(def) do
          name ->
            true
          _ ->
            false
        end
    end
    case :lists.any(pred, rest) do
      true ->
        throw({:error, {'more than one definition with same name', name}})
      _ ->
        :ok
    end
  end

  def export_all([]), do: []

  def export_all(moduleList), do: ...

  def finished_warn_prints(), do: put(:warn_duplicate_defs, :undefined)

  def generate({m, codeTuple}, outFile, encodingRule, options) do
    {types, values, ptypes, classes, objects, objectSets} = codeTuple
    code = abst(name: module(m, :name), types: types, values: values, ptypes: ptypes, classes: classes, objects: objects, objsets: objectSets)
    setup_bit_string_format(options)
    setup_legacy_erlang_types(options)
    :asn1ct_table.new(:check_functions)
    gen = init_gen_record(encodingRule, options)
    check_maps_option(gen)
    try do
      specialized_decode_prepare(gen, m)
    catch
      {:throw, {:error, reason}, _} ->
        warning('Error in configuration file: ~n~p~n', [reason], options, 'Error in configuration file')
    end
    :asn1ct_gen.pgen(outFile, gen, code)
    cleanup_bit_string_format()
    erase(:tlv_format)
    erase(:class_default_type)
    :asn1ct_table.delete(:check_functions)
    :ok
  end

  def generate_pass(st(code: code, outfile: outFile, erule: erule, opts: opts) = st0) do
    st = st(st0, code: :undefined)
    generate(code, outFile, erule, opts)
    {:ok, st}
  end

  def generated_functions_filter(_, name, l) when is_atom(name) or is_list(name) do
    :lists.filter(fn {n, _, _} when n == name ->
        true
      _ ->
        false
    end, l)
  end

  def generated_functions_filter(m, unquote(:"Externaltypereference")(module: ^m, type: name), l) do
    removeTType = fn {n, i, [n, p]} when n == name ->
        {n, i, p}
      {unquote(:"Externaltypereference")(module: m1, type: n), i, p} when m1 == m ->
        {n, i, p}
      p ->
        p
    end
    l2 = :lists.map(removeTType, l)
    generated_functions_filter(m, name, l2)
  end

  def generated_functions_member(_M, name, [{^name, _, _} | _]), do: true

  def generated_functions_member(m, unquote(:"Externaltypereference")(module: ^m, type: t), [{unquote(:"Externaltypereference")(module: ^m, type: ^t), _, _} | _]), do: true

  def generated_functions_member(m, unquote(:"Externaltypereference")(module: ^m, type: name), [{^name, _, _} | _]), do: true

  def generated_functions_member(m, name, [_ | t]), do: generated_functions_member(m, name, t)

  def generated_functions_member(_, _, []), do: false

  def generated_functions_member(m, name, l, pattern) do
    case generated_functions_member(m, name, l) do
      true ->
        l2 = generated_functions_filter(m, name, l)
        case :lists.keysearch(pattern, 3, l2) do
          {:value, _} ->
            true
          _ ->
            false
        end
      _ ->
        false
    end
  end

  def get_components(unquote(:"SEQUENCE")(components: {c1, c2})) when is_list(c1) and is_list(c2), do: c1 ++ c2

  def get_components(unquote(:"SEQUENCE")(components: components)), do: components

  def get_components(unquote(:"SET")(components: {c1, c2})) when is_list(c1) and is_list(c2), do: c1 ++ c2

  def get_components(unquote(:"SET")(components: components)), do: components

  def get_components({:"SEQUENCE OF", components}), do: components

  def get_components({:"SET OF", components}), do: components

  def get_components(def), do: def

  def get_config_info(cfgList, infoType) do
    case :lists.keysearch(infoType, 1, cfgList) do
      {:value, {infoType, value}} ->
        value
      false ->
        []
    end
  end

  def get_file_list(file, includes) do
    case :file.open(file, [:read]) do
      {:error, reason} ->
        {:error, {file, :file.format_error(reason)}}
      {:ok, stream} ->
        get_file_list1(stream, :filename.dirname(file), includes, [])
    end
  end

  def get_file_list1(stream, dir, includes, acc) do
    ret = :io.get_line(stream, :"")
    case ret do
      :eof ->
        :ok = :file.close(stream)
        :lists.reverse(acc)
      fileName ->
        suffixedNameList = try do
          input_file_type(:filename.join([dir, :lists.delete(?\n, fileName)]), includes)
        catch
          error -> error
        end
        |> case do
          {:empty_name, []} ->
            []
          {:single_file, name} ->
            [name]
          {:multiple_files_file, _, name} ->
            get_file_list(name, includes)
          _Err ->
            []
        end
        get_file_list1(stream, dir, includes, suffixedNameList ++ acc)
    end
  end

  def get_gen_state(), do: read_config_data(:gen_state)

  def get_gen_state_field(gen_state(active: active), :active), do: active

  def get_gen_state_field(_, :active), do: false

  def get_gen_state_field(gS, :prefix), do: gen_state(gS, :prefix)

  def get_gen_state_field(gS, :inc_tag_pattern), do: gen_state(gS, :inc_tag_pattern)

  def get_gen_state_field(gS, :tag_pattern), do: gen_state(gS, :tag_pattern)

  def get_gen_state_field(gS, :inc_type_pattern), do: gen_state(gS, :inc_type_pattern)

  def get_gen_state_field(gS, :type_pattern), do: gen_state(gS, :type_pattern)

  def get_gen_state_field(gS, :func_name), do: gen_state(gS, :func_name)

  def get_gen_state_field(gS, :namelist), do: gen_state(gS, :namelist)

  def get_gen_state_field(gS, :tobe_refed_funcs), do: gen_state(gS, :tobe_refed_funcs)

  def get_gen_state_field(gS, :gen_refed_funcs), do: gen_state(gS, :gen_refed_funcs)

  def get_gen_state_field(gS, :generated_functions), do: gen_state(gS, :generated_functions)

  def get_gen_state_field(gS, :suffix_index), do: gen_state(gS, :suffix_index)

  def get_gen_state_field(gS, :current_suffix_index), do: gen_state(gS, :current_suffix_index)

  def get_input_file(module, []), do: module

  def get_input_file(module, [i | includes]) do
    try do
      input_file_type(:filename.join([i, module]))
    catch
      error -> error
    end
    |> case do
      {:single_file, fileName} ->
        {:file, fileName}
      _ ->
        get_input_file(module, includes)
    end
  end

  def get_referenced_type(m, name) do
    case :asn1_db.dbget(m, name) do
      typedef(typespec: tS) ->
        case tS do
          type(def: unquote(:"Externaltypereference")(module: m2, type: name2)) ->
            get_referenced_type(m2, name2)
          type() ->
            tS
          _ ->
            throw({:error, {'unexpected element when fetching referenced type', tS}})
        end
      t ->
        throw({:error, {'unexpected element when fetching referenced type', t}})
    end
  end

  def get_rule(options) do
    for rule <- [:ber, :per, :uper, :jer], opt <- options, rule === opt do
      rule
    end
    |> case do
      [rule] ->
        rule
      [rule | _] ->
        rule
      [] ->
        :ber
    end
  end

  def get_tag_command(type(tag: []), _), do: []

  def get_tag_command(type(), :skip), do: :skip

  def get_tag_command(type(tag: tags), :skip_optional) do
    tag = hd(tags)
    [:skip_optional, encode_tag_val(decode_class(tag(tag, :class)), tag(tag, :form), tag(tag, :number))]
  end

  def get_tag_command(type(tag: [tag]), command), do: [command, encode_tag_val(decode_class(tag(tag, :class)), tag(tag, :form), tag(tag, :number))]

  def get_tag_command(t = type(tag: [tag | tags]), command) do
    tC = get_tag_command(type(t, tag: [tag]), command)
    tCs = get_tag_command(type(t, tag: tags), command)
    case many_tags(tCs) do
      true when is_list(tCs) ->
        [tC | tCs]
      _ ->
        [tC, tCs]
    end
  end

  def get_tag_command(type(tag: []), _, _), do: []

  def get_tag_command(type(tag: [tag]), :mandatory, prop) do
    case prop do
      :mandatory ->
        :mandatory
      {:"DEFAULT", _} ->
        [:default, encode_tag_val(decode_class(tag(tag, :class)), tag(tag, :form), tag(tag, :number))]
      _ ->
        [:opt, encode_tag_val(decode_class(tag(tag, :class)), tag(tag, :form), tag(tag, :number))]
    end
  end

  def get_tag_command(type(tag: [tag]), command, prop), do: [anonymous_dec_command(command, prop), encode_tag_val(decode_class(tag(tag, :class)), tag(tag, :form), tag(tag, :number))]

  def get_tag_command(type(tag: tag), command, prop) when is_record(tag, :tag), do: get_tag_command(type(tag: [tag]), command, prop)

  def get_tag_command(t = type(tag: [tag | tags]), command, prop), do: [get_tag_command(type(t, tag: [tag]), command, prop), get_tag_command(type(t, tag: tags), command, prop)]

  def get_value(module, type) do
    case :asn1ct_value.from_type(module, type) do
      {:error, reason} ->
        {:error, reason}
      result ->
        {:ok, result}
    end
  end

  def have_same_tagdefault([module(tagdefault: t) | ms]), do: have_same_tagdefault(ms, {true, t})

  def have_same_tagdefault([], tagDefault), do: tagDefault

  def have_same_tagdefault([module(tagdefault: t) | ms], tDefault = {_, ^t}), do: have_same_tagdefault(ms, tDefault)

  def have_same_tagdefault([module(tagdefault: t1) | ms], {_, t2}), do: have_same_tagdefault(ms, {false, rank_tagdef([t1, t2])})

  def in_process(fun) do
    parent = self()
    pid = spawn_link(fn  ->
        process(parent, fun)
    end)
    receive do
    {^pid, result} ->
        result
      {^pid, class, reason, stack} ->
        sT = try do
          throw(:x)
        catch
          {:throw, :x, stk} ->
            stk
        end
        :erlang.raise(class, reason, stack ++ sT)
    end
  end

  def include_append(dir, options) do
    option_add({:i, dir}, options, fn opts ->
        opts ++ [{:i, dir}]
    end)
  end

  def include_prepend(dir, options) do
    option_add({:i, dir}, options, fn opts ->
        [{:i, dir} | opts]
    end)
  end

  def includes(file, options) do
    options2 = include_append('.', options)
    options3 = include_append(:filename.dirname(file), options2)
    case :proplists.get_value(:outdir, options) do
      :undefined ->
        options3
      outDir ->
        include_prepend(outDir, options3)
    end
  end

  def init_gen_record(encodingRule, options) do
    erule = case encodingRule do
      :uper ->
        :per
      _ ->
        encodingRule
    end
    der = :proplists.get_bool(:der, options)
    jer = :proplists.get_bool(:jer, options) and encodingRule !== :jer
    aligned = encodingRule === :per
    recPrefix = :proplists.get_value(:record_name_prefix, options, "")
    macroPrefix = :proplists.get_value(:macro_name_prefix, options, "")
    pack = case :proplists.get_value(:maps, options, false) do
      true ->
        :map
      false ->
        :record
    end
    gen(erule: erule, der: der, jer: jer, aligned: aligned, rec_prefix: recPrefix, macro_prefix: macroPrefix, pack: pack, options: options)
  end

  def input_file_type([]), do: {:empty_name, []}

  def input_file_type(file), do: ...

  def input_file_type(name, i) do
    case input_file_type(name) do
      {:error, _} ->
        input_file_type2(:filename.basename(name), i)
      err = {:input_file_error, _} ->
        err
      res ->
        res
    end
  end

  def input_file_type2(name, [i | is]) do
    case input_file_type(:filename.join([i, name])) do
      {:error, _} ->
        input_file_type2(name, is)
      err = {:input_file_error, _} ->
        err
      res ->
        res
    end
  end

  def input_file_type2(name, []), do: input_file_type(name)

  def is_asn1_flag(:asn1config), do: true

  def is_asn1_flag(:ber), do: true

  def is_asn1_flag(:compact_bit_string), do: true

  def is_asn1_flag(:debug), do: true

  def is_asn1_flag(:der), do: true

  def is_asn1_flag(:legacy_bit_string), do: true

  def is_asn1_flag({:macro_name_prefix, _}), do: true

  def is_asn1_flag({:n2n, _}), do: true

  def is_asn1_flag(:noobj), do: true

  def is_asn1_flag(:no_ok_wrapper), do: true

  def is_asn1_flag(:optimize), do: true

  def is_asn1_flag(:per), do: true

  def is_asn1_flag({:record_name_prefix, _}), do: true

  def is_asn1_flag(:undec_rec), do: true

  def is_asn1_flag(:uper), do: true

  def is_asn1_flag(:verbose), do: true

  def is_asn1_flag(_), do: false

  def is_error(state(options: opts)), do: is_error(opts)

  def is_error(gen(options: opts)), do: is_error(opts)

  def is_error(o), do: :lists.member(:errors, o) or is_verbose(o)

  def is_verbose(state(options: opts)), do: is_verbose(opts)

  def is_verbose(gen(options: opts)), do: is_verbose(opts)

  def is_verbose(o), do: :lists.member(:verbose, o)

  def is_warning(s) when is_record(s, :state), do: is_warning(state(s, :options))

  def is_warning(o), do: :lists.member(:warnings, o) or is_verbose(o)

  def is_werr(s) when is_record(s, :state), do: is_werr(state(s, :options))

  def is_werr(o), do: :lists.member(:warnings_as_errors, o)

  def legacy_forced_info(opt), do: :io.format('Info: The option \'legacy_erlang_types\' is implied by the \'~s\' option.\n', [opt])

  def make_erl_options(opts) do
    includes = options(opts, :includes)
    defines = options(opts, :defines)
    outdir = options(opts, :outdir)
    warning = options(opts, :warning)
    verbose = options(opts, :verbose)
    specific = options(opts, :specific)
    optimize = options(opts, :optimize)
    outputType = options(opts, :output_type)
    cwd = options(opts, :cwd)
    options = (case verbose do
      true ->
        [:verbose]
      false ->
        []
    end) ++ (case warning do
      0 ->
        []
      _ ->
        [:warnings]
    end) ++ [] ++ (case optimize do
      1 ->
        [:optimize]
      999 ->
        []
      _ ->
        [{:optimize, optimize}]
    end) ++ :lists.map(fn {name, value} ->
        {:d, name, value}
      name ->
        {:d, name}
    end, defines) ++ (case outputType do
      :undefined ->
        [:ber]
      _ ->
        [outputType]
    end)
    options ++ [:errors, {:cwd, cwd}, {:outdir, outdir} | :lists.map(fn dir ->
        {:i, dir}
    end, includes)] ++ specific
  end

  def make_suffix({_, {_, 0, _}}), do: ""

  def make_suffix({_, {_, i, _}}), do: :lists.concat(['_', i])

  def make_suffix(_), do: ""

  def many_tags([:skip]), do: false

  def many_tags([:skip_optional, _]), do: false

  def many_tags([:choosen, _]), do: false

  def many_tags(_), do: true

  def maybe_first_warn_print() do
    case get(:warn_duplicate_defs) do
      :undefined ->
        put(:warn_duplicate_defs, true)
        :io.format('~nDue to multiple occurrences of a definition name in multi-file compiled files:~n')
      _ ->
        :ok
    end
  end

  def maybe_rename_function2(:record, unquote(:"Externaltypereference")(type: name), suffix), do: :lists.concat([name, suffix])

  def maybe_rename_function2(:list, list, suffix), do: :lists.concat([:asn1ct_gen.list2name(list), suffix])

  def maybe_rename_function2(thing, name, suffix) when thing == :atom or thing == :integer or thing == :string, do: :lists.concat([name, suffix])

  def merge_modules(moduleList, commonName) do
    newModuleList = remove_name_collisions(moduleList)
    case :asn1ct_table.size(:renamed_defs) do
      0 ->
        :asn1ct_table.delete(:renamed_defs)
      _ ->
        :ok
    end
    save_imports(newModuleList)
    typeOrVal = :lists.append(:lists.map(fn x ->
        module(x, :typeorval)
    end, newModuleList))
    inputMNameList = :lists.map(fn x ->
        module(x, :name)
    end, newModuleList)
    cExports = common_exports(newModuleList)
    importsModuleNameList = :lists.map(fn x ->
        {module(x, :imports), module(x, :name)}
    end, newModuleList)
    cImports = common_imports(importsModuleNameList, inputMNameList)
    tagDefault = check_tagdefault(newModuleList)
    module(name: commonName, tagdefault: tagDefault, exports: cExports, imports: cImports, typeorval: typeOrVal)
  end

  def merge_pass(st(file: base, code: code) = st) do
    m = merge_modules(code, base)
    {:ok, st(st, code: m)}
  end

  def merge_symbols_from_module([imp | imps], acc) do
    unquote(:"Externaltypereference")(type: modName) = unquote(:"SymbolsFromModule")(imp, :module)
    ifromModName = :lists.filter(fn i ->
        case unquote(:"SymbolsFromModule")(i, :module) do
          unquote(:"Externaltypereference")(type: modName) ->
            true
          unquote(:"Externalvaluereference")(value: modName) ->
            true
          _ ->
            false
        end
    end, imps)
    newImps = :lists.subtract(imps, ifromModName)
    newImp = unquote(:"SymbolsFromModule")(^imp, symbols: :lists.append(:lists.map(fn sL ->
        unquote(:"SymbolsFromModule")(sL, :symbols)
    end, [imp | ifromModName])))
    merge_symbols_from_module(newImps, [newImp | acc])
  end

  def merge_symbols_from_module([], acc), do: :lists.reverse(acc)

  def option_add(option, options, fun) do
    case :lists.member(option, options) do
      true ->
        options
      false ->
        fun.(options)
    end
  end

  def outfile(base, ext, opts) do
    obase = case :lists.keysearch(:outdir, 1, opts) do
      {:value, {:outdir, odir}} ->
        :filename.join(odir, base)
      _NotFound ->
        base
    end
    case ext do
      [] ->
        obase
      _ ->
        :lists.concat([obase, '.', ext])
    end
  end

  def parse_and_save1(state(erule: erule), file, options) do
    ext = :filename.extension(file)
    base = :filename.basename(file, ext)
    dbFile = outfile(base, 'asn1db', options)
    st = st(file: file, dbfile: dbFile, erule: erule)
    passes = parse_and_save_passes()
    run_passes(passes, st)
  end

  def parse_and_save_passes(), do: [{:pass, :scan, &scan_pass/1}, {:pass, :parse, &parse_pass/1}, {:pass, :save, &save_pass/1}]

  def parse_listing(st(code: code, outfile: outFile0) = st) do
    outFile = outFile0 ++ '.parse'
    case :file.write_file(outFile, :io_lib.format('~p\n', [code])) do
      :ok ->
        :done
      {:error, reason} ->
        error = {:write_error, outFile, reason}
        {:error, st(st, error: [{:structured_error, {outFile0, :none}, :asn1ct, error}])}
    end
  end

  def parse_pass(st(file: file, code: tokens) = st) do
    case :asn1ct_parser2.parse(file, tokens) do
      {:ok, m} ->
        {:ok, st(st, code: m)}
      {:error, errors} ->
        {:error, st(st, error: errors)}
    end
  end

  def prepare_bytes(bytes) when is_binary(bytes), do: bytes

  def prepare_bytes(bytes), do: list_to_binary(bytes)

  def pretty2(module, absFile), do: ...

  def print_structured_errors([_ | _] = errors) do
    _ = for {:structured_error, {f, l}, m, e} <- errors do
      :io.format('~ts:~w: ~ts\n', [f, l, m.format_error(e)])
    end
    :ok
  end

  def print_structured_errors(_), do: :ok

  def process(parent, fun) do
    try do
      send(parent, {self(), fun.()})
    catch
      {class, reason, stack} ->
        send(parent, {self(), class, reason, stack})
    end
  end

  def rank_tagdef(l) do
    case :lists.member(:"EXPLICIT", l) do
      true ->
        :"EXPLICIT"
      _ ->
        :"IMPLICIT"
    end
  end

  def read_config_file(gen(options: options), moduleName) do
    name = ensure_ext(moduleName, '.asn1config')
    includes = for {:i, i} <- options do
      i
    end
    read_config_file0(name, ['.' | includes])
  end

  def read_config_file0(name, [d | dirs]) do
    case :file.consult(:filename.join(d, name)) do
      {:ok, cfgList} ->
        cfgList
      {:error, :enoent} ->
        read_config_file0(name, dirs)
      {:error, reason} ->
        error = 'error reading asn1 config file: ' ++ :file.format_error(reason)
        throw({:error, error})
    end
  end

  def read_config_file0(_, []), do: :no_config_file

  def read_config_file_info(moduleName, infoType) when is_atom(infoType) do
    name = ensure_ext(moduleName, '.asn1config')
    cfgList = read_config_file0(name, [])
    get_config_info(cfgList, infoType)
  end

  def remove_asn_flags(options) do
    for x <- options, notis_asn1_flag(x) do
      x
    end
  end

  def remove_import_doubles([]), do: []

  def remove_import_doubles(importList) do
    mergedImportList = merge_symbols_from_module(importList, [])
    delete_double_of_symbol(mergedImportList, [])
  end

  def remove_in_set_imports([{{:imports, impL}, _ModName} | rest], inputMNameL, acc) do
    newImpL = remove_in_set_imports1(impL, inputMNameL, [])
    remove_in_set_imports(rest, inputMNameL, newImpL ++ acc)
  end

  def remove_in_set_imports([], _, acc), do: :lists.reverse(acc)

  def remove_in_set_imports1([i | is], inputMNameL, acc) do
    case unquote(:"SymbolsFromModule")(i, :module) do
      unquote(:"Externaltypereference")(type: mName) ->
        case :lists.member(mName, inputMNameL) do
          true ->
            remove_in_set_imports1(is, inputMNameL, acc)
          false ->
            remove_in_set_imports1(is, inputMNameL, [i | acc])
        end
      _ ->
        remove_in_set_imports1(is, inputMNameL, [i | acc])
    end
  end

  def remove_in_set_imports1([], _, acc), do: :lists.reverse(acc)

  def remove_name_collisions(modules) do
    :asn1ct_table.new(:renamed_defs)
    :lists.foreach(&exit_if_nameduplicate/1, modules)
    remove_name_collisions2(modules, [])
  end

  def remove_name_collisions2([m | ms], acc) do
    typeOrVal = module(m, :typeorval)
    mName = module(m, :name)
    {newM, newMs} = remove_name_collisions2(mName, typeOrVal, ms, [])
    remove_name_collisions2(newMs, [module(m, typeorval: newM) | acc])
  end

  def remove_name_collisions2([], acc) do
    finished_warn_prints()
    acc
  end

  def remove_name_collisions2(modName, [t | ts], ms, acc) do
    name = get_name_of_def(t)
    case discover_dupl_in_mods(name, t, ms, [], 0) do
      {_, 0} ->
        remove_name_collisions2(modName, ts, ms, [t | acc])
      {newMs, 1} ->
        newT = set_name_of_def(modName, name, t)
        warn_renamed_def(modName, get_name_of_def(newT), name)
        :asn1ct_table.insert(:renamed_defs, {get_name_of_def(newT), name, modName})
        remove_name_collisions2(modName, ts, newMs, [newT | acc])
      {newMs, 2} ->
        warn_kept_def(modName, name)
        remove_name_collisions2(modName, ts, newMs, [t | acc])
      {newMs, 2 ||| 1} ->
        warn_kept_def(modName, name)
        remove_name_collisions2(modName, ts, newMs, [t | acc])
    end
  end

  def remove_name_collisions2(_, [], ms, acc), do: {acc, ms}

  def run_passes(passes, st(opts: opts) = st) do
    run = case :lists.member(:time, opts) do
      false ->
        fn _, pass, s ->
            pass.(s)
        end
      true ->
        &run_tc/3
    end
    run_passes_1(passes, st(st, run: run))
  end

  def run_passes_1([{:unless, opt, pass} | passes], st(opts: opts) = st) do
    case :proplists.get_bool(opt, opts) do
      false ->
        run_passes_1([pass | passes], st)
      true ->
        run_passes_1(passes, st)
    end
  end

  def run_passes_1([{:iff, opt, pass} | passes], st(opts: opts) = st) do
    case :proplists.get_bool(opt, opts) do
      true ->
        run_passes_1([pass | passes], st)
      false ->
        run_passes_1(passes, st)
    end
  end

  def run_passes_1([{:pass, name, pass} | passes], st(run: run) = st0) when is_function(pass, 1) do
    try do
      run.(name, pass, st0)
    catch
      {class, error, stk} ->
        :io.format('Internal error: ~p:~p\n~p\n', [class, error, stk])
        {:error, {:internal_error, {class, error}}}
    else
      {:ok, st} ->
        run_passes_1(passes, st)
      {:error, st(error: errors)} ->
        {structured, allErrors} = clean_errors(errors)
        print_structured_errors(structured)
        {:error, allErrors}
      :done ->
        :ok
    end
  end

  def run_passes_1([], _St), do: :ok

  def run_tc(name, fun, st) do
    before0 = statistics(:runtime)
    val = try do
      fun.(st)
    catch
      error -> error
    end
    after0 = statistics(:runtime)
    {before_c, _} = before0
    {after_c, _} = after0
    :io.format('~-31s: ~10.2f s\n', [name, after_c - before_c / 1000])
    val
  end

  def save_automatic_tagged_types([]), do: :done

  def save_automatic_tagged_types([module(tagdefault: :"AUTOMATIC", typeorval: torV) | ms]) do
    fun = fn t ->
        :asn1ct_table.insert(:automatic_tags, {get_name_of_def(t)})
    end
    :lists.foreach(fun, torV)
    save_automatic_tagged_types(ms)
  end

  def save_automatic_tagged_types([_M | ms]), do: save_automatic_tagged_types(ms)

  def save_config(key, info) do
    :asn1ct_table.new_reuse(:asn1_general)
    :asn1ct_table.insert(:asn1_general, {{:asn1_config, key}, info})
  end

  def save_gen_state(genState) when is_record(genState, :gen_state), do: save_config(:gen_state, genState)

  def save_gen_state(:selective_decode, {_, type_component_name_list}) do
    state = case get_gen_state() do
      s when is_record(s, :gen_state) ->
        s
      _ ->
        gen_state()
    end
    stateRec = gen_state(state, type_pattern: type_component_name_list)
    save_config(:gen_state, stateRec)
  end

  def save_gen_state(:selective_decode, _), do: :ok

  def save_gen_state(:exclusive_decode, {_, confList}, partIncTlvTagList) do
    state = case get_gen_state() do
      s when is_record(s, :gen_state) ->
        s
      _ ->
        gen_state()
    end
    stateRec = gen_state(state, inc_tag_pattern: partIncTlvTagList, inc_type_pattern: confList)
    save_config(:gen_state, stateRec)
  end

  def save_gen_state(_, _, _) do
    case get_gen_state() do
      s when is_record(s, :gen_state) ->
        :ok
      _ ->
        save_config(:gen_state, gen_state())
    end
  end

  def save_imports(moduleList) do
    fun = fn m ->
        case module(m, :imports) do
          {_, []} ->
            []
          {_, i} ->
            {module(m, :name), i}
        end
    end
    importsList = :lists.map(fun, moduleList)
    case :lists.flatten(importsList) do
      [] ->
        :ok
      importsList2 ->
        :asn1ct_table.new(:original_imports)
        :lists.foreach(fn x ->
            :asn1ct_table.insert(:original_imports, x)
        end, importsList2)
    end
  end

  def save_pass(st(code: m, erule: erule, opts: opts) = st) do
    :ok = :asn1ct_check.storeindb(state(erule: erule, options: opts), m)
    {:ok, st}
  end

  def scan_pass(st(file: file) = st) do
    case :asn1ct_tok.file(file) do
      {:error, reason} ->
        {:error, st(st, error: reason)}
      tokens when is_list(tokens) ->
        {:ok, st(st, code: tokens)}
    end
  end

  def set_name_of_def(modName, name, oldDef) do
    newName = list_to_atom(:lists.concat([name, modName]))
    case oldDef do
      typedef() ->
        typedef(oldDef, name: newName)
      classdef() ->
        classdef(oldDef, name: newName)
      valuedef() ->
        valuedef(oldDef, name: newName)
      ptypedef() ->
        ptypedef(oldDef, name: newName)
      pvaluedef() ->
        pvaluedef(oldDef, name: newName)
      pvaluesetdef() ->
        pvaluesetdef(oldDef, name: newName)
      pobjectdef() ->
        pobjectdef(oldDef, name: newName)
      pobjectsetdef() ->
        pobjectsetdef(oldDef, name: newName)
    end
  end

  def set_passes(), do: [{:pass, :scan_parse, &set_scan_parse_pass/1}, {:pass, :merge, &merge_pass/1} | common_passes()]

  def set_scan_parse_pass(st(files: files) = st) do
    try do
      l = set_scan_parse_pass_1(files, st)
    {:ok, st(st, code: l)}
    catch
      {:throw, error, _} ->
        {:error, st(st, error: error)}
    end
  end

  def set_scan_parse_pass_1([f | fs], st(file: file) = st) do
    case :asn1ct_tok.file(f) do
      {:error, error} ->
        throw(error)
      tokens when is_list(tokens) ->
        case :asn1ct_parser2.parse(file, tokens) do
          {:ok, m} ->
            [m | set_scan_parse_pass_1(fs, st)]
          {:error, errors} ->
            throw(errors)
        end
    end
  end

  def set_scan_parse_pass_1([], _), do: []

  def setup_bit_string_format(opts) do
    format = case {:lists.member(:compact_bit_string, opts), :lists.member(:legacy_bit_string, opts)} do
      {false, false} ->
        :bitstring
      {true, false} ->
        :compact
      {false, true} ->
        :legacy
      {true, true} ->
        message = 'Contradicting options given: compact_bit_string and legacy_bit_string'
        exit({:error, {:asn1, message}})
    end
    put(:bit_string_format, format)
  end

  def setup_legacy_erlang_types(opts) do
    f = case :lists.member(:legacy_erlang_types, opts) do
      false ->
        case get_bit_string_format() do
          :bitstring ->
            false
          :compact ->
            legacy_forced_info(:compact_bit_string)
            true
          :legacy ->
            legacy_forced_info(:legacy_bit_string)
            true
        end
      true ->
        true
    end
    put(:use_legacy_erlang_types, f)
  end

  def single_passes(), do: [{:pass, :scan, &scan_pass/1}, {:pass, :parse, &parse_pass/1} | common_passes()]

  def special_decode_prepare_1(gen(options: options) = gen, m) do
    modName = case :lists.keyfind(:asn1config, 1, options) do
      {_, mName} ->
        mName
      false ->
        module(m, :name)
    end
    case read_config_file(gen, modName) do
      :no_config_file ->
        :ok
      cfgList ->
        selectedDecode = get_config_info(cfgList, :selective_decode)
        exclusiveDecode = get_config_info(cfgList, :exclusive_decode)
        commandList = create_partial_decode_gen_info(module(m, :name), selectedDecode)
        save_config(:partial_decode, commandList)
        save_gen_state(:selective_decode, selectedDecode)
        commandList2 = create_partial_inc_decode_gen_info(module(m, :name), exclusiveDecode)
        part_inc_tlv_tags = tlv_tags(commandList2)
        save_config(:partial_incomplete_decode, part_inc_tlv_tags)
        save_gen_state(:exclusive_decode, exclusiveDecode, part_inc_tlv_tags)
    end
  end

  def specialized_decode_prepare(gen(erule: :ber, options: options) = gen, m) do
    case :lists.member(:asn1config, options) do
      true ->
        special_decode_prepare_1(gen, m)
      false ->
        :ok
    end
  end

  def specialized_decode_prepare(_, _), do: :ok

  def start(includes) when is_list(includes), do: :asn1_db.dbstart(includes)

  def strip_includes(includes) do
    for {:i, i} <- includes do
      i
    end
  end

  def test_each(module, [type | rest]) do
    case test_type(module, type) do
      {:ok, _Result} ->
        test_each(module, rest)
      error ->
        error
    end
  end

  def test_each(_, []), do: :ok

  def test_module(module, includes) do
    in_process(fn  ->
        start(strip_includes(includes))
        case check(module, includes) do
          {:ok, newTypes} ->
            test_each(module, newTypes)
          error ->
            error
        end
    end)
  end

  def test_type(module, type) do
    case get_value(module, type) do
      {:ok, val} ->
        test_value(module, type, val)
      {:error, reason} ->
        {:error, {:asn1, {:value, reason}}}
    end
  end

  def test_type(module, type, includes) do
    in_process(fn  ->
        start(strip_includes(includes))
        case check(module, includes) do
          {:ok, _NewTypes} ->
            test_type(module, type)
          error ->
            error
        end
    end)
  end

  def test_value(module, type, value) do
    in_process(fn  ->
        try do
          module.encode(type, value)
        catch
          error -> error
        end
        |> case do
          {:ok, bytes} ->
            test_value_decode(module, type, value, bytes)
          bytes when is_binary(bytes) ->
            test_value_decode(module, type, value, bytes)
          error ->
            {:error, {:asn1, {:encode, {{module, type, value}, error}}}}
        end
    end)
  end

  def test_value_decode(module, type, value, bytes) do
    newBytes = prepare_bytes(bytes)
    case module.decode(type, newBytes) do
      {:ok, value} ->
        {:ok, {module, type, value}}
      {:ok, value, <<>>} ->
        {:ok, {module, type, value}}
      value ->
        {:ok, {module, type, value}}
      {value, <<>>} ->
        {:ok, {module, type, value}}
      {:ok, res} ->
        {:error, {:asn1, {:encode_decode_mismatch, {{module, type, value}, res}}}}
      {:ok, res, rest} ->
        {:error, {:asn1, {:encode_decode_mismatch, {{module, type, value}, {res, rest}}}}}
      error ->
        {:error, {:asn1, {{:decode, {module, type, value}, error}}}}
    end
  end

  def tlv_tag(<<cl :: 2, _ :: 1, tagNo :: 5>>) when tagNo < 31, do: cl <<< 16 + tagNo

  def tlv_tag(<<cl :: 2, _ :: 1, 31 :: 5, 0 :: 1, tagNo :: 7>>), do: cl <<< 16 + tagNo

  def tlv_tag(<<cl :: 2, _ :: 1, 31 :: 5, buffer :: binary>>) do
    tagNo = tlv_tag1(buffer, 0)
    cl <<< 16 + tagNo
  end

  def tlv_tag1(<<0 :: 1, partialTag :: 7>>, acc), do: acc <<< 7 ||| partialTag

  def tlv_tag1(<<1 :: 1, partialTag :: 7, buffer :: binary>>, acc), do: tlv_tag1(buffer, acc <<< 7 ||| partialTag)

  def tlv_tags([]), do: []

  def tlv_tags([:mandatory | rest]), do: [:mandatory | tlv_tags(rest)]

  def tlv_tags([[command, tag] | rest]) when is_atom(command) and is_binary(tag), do: [[command, tlv_tag(tag)] | tlv_tags(rest)]

  def tlv_tags([[command, directives] | rest]) when is_atom(command) and is_list(directives), do: [[command, tlv_tags(directives)] | tlv_tags(rest)]

  def tlv_tags([[] | rest]), do: tlv_tags(rest)

  def tlv_tags([{name, topType, l1} | rest]) when is_list(l1) and is_atom(topType), do: [{name, topType, tlv_tags(l1)} | tlv_tags(rest)]

  def tlv_tags([[command, tag, l1] | rest]) when is_list(l1) and is_binary(tag), do: [[command, tlv_tag(tag), tlv_tags(l1)] | tlv_tags(rest)]

  def tlv_tags([[:mandatory | rest]]), do: [[:mandatory | tlv_tags(rest)]]

  def tlv_tags([l = [l1 | _] | rest]) when is_list(l1), do: [tlv_tags(l) | tlv_tags(rest)]

  def translate_options([:ber_bin | t]) do
    :io.format('Warning: The option \'ber_bin\' is now called \'ber\'.\n')
    [:ber | translate_options(t)]
  end

  def translate_options([:per_bin | t]) do
    :io.format('Warning: The option \'per_bin\' is now called \'per\'.\n')
    [:per | translate_options(t)]
  end

  def translate_options([:uper_bin | t]) do
    :io.format('Warning: The option \'uper_bin\' is now called \'uper\'.\n')
    translate_options([:uper | t])
  end

  def translate_options([:nif | t]) do
    :io.format('Warning: The option \'nif\' is no longer needed.\n')
    translate_options(t)
  end

  def translate_options([:optimize | t]) do
    :io.format('Warning: The option \'optimize\' is no longer needed.\n')
    translate_options(t)
  end

  def translate_options([:inline | t]) do
    :io.format('Warning: The option \'inline\' is no longer needed.\n')
    translate_options(t)
  end

  def translate_options([{:inline, _} | _]) do
    :io.format('ERROR: The option {inline,OutputFilename} is no longer supported.\n')
    throw({:error, {:unsupported_option, :inline}})
  end

  def translate_options([h | t]), do: [h | translate_options(t)]

  def translate_options([]), do: []

  def type_check(a) when is_atom(a), do: :atom

  def type_check(l) when is_list(l) do
    pred = fn x when x <= 255 ->
        false
      _ ->
        true
    end
    case :lists.filter(pred, l) do
      [] ->
        :string
      _ ->
        :list
    end
  end

  def type_check(unquote(:"Externaltypereference")()), do: :record

  def update_gen_state(:active, state, data), do: save_gen_state(gen_state(state, active: data))

  def update_gen_state(:prefix, state, data), do: save_gen_state(gen_state(state, prefix: data))

  def update_gen_state(:inc_tag_pattern, state, data), do: save_gen_state(gen_state(state, inc_tag_pattern: data))

  def update_gen_state(:tag_pattern, state, data), do: save_gen_state(gen_state(state, tag_pattern: data))

  def update_gen_state(:inc_type_pattern, state, data), do: save_gen_state(gen_state(state, inc_type_pattern: data))

  def update_gen_state(:type_pattern, state, data), do: save_gen_state(gen_state(state, type_pattern: data))

  def update_gen_state(:func_name, state, data), do: save_gen_state(gen_state(state, func_name: data))

  def update_gen_state(:namelist, state, data), do: save_gen_state(gen_state(state, namelist: data))

  def update_gen_state(:tobe_refed_funcs, state, data), do: save_gen_state(gen_state(state, tobe_refed_funcs: data))

  def update_gen_state(:gen_refed_funcs, state, data), do: save_gen_state(gen_state(state, gen_refed_funcs: data))

  def update_gen_state(:generated_functions, state, data), do: save_gen_state(gen_state(state, generated_functions: data))

  def update_gen_state(:suffix_index, state, data), do: save_gen_state(gen_state(state, suffix_index: data))

  def update_gen_state(:current_suffix_index, state, data), do: save_gen_state(gen_state(state, current_suffix_index: data))

  def warn_deleted_def(modName, defName) do
    maybe_first_warn_print()
    :io.format('NOTICE: The ASN.1 definition in module ~p with name ~p has been deleted in generated module.~n', [modName, defName])
  end

  def warn_kept_def(modName, defName) do
    maybe_first_warn_print()
    :io.format('NOTICE: The ASN.1 definition in module ~p with name ~p has kept its name due to equal definition as duplicate.~n', [modName, defName])
  end

  def warn_renamed_def(modName, newName, oldName) do
    maybe_first_warn_print()
    :io.format('NOTICE: The ASN.1 definition in module ~p with name ~p has been renamed in generated module. New name is ~p.~n', [modName, oldName, newName])
  end
end
