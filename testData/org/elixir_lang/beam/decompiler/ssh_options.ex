# Source code recreated from a .beam file by IntelliJ Elixir
defmodule :ssh_options do

  # Types

  @type private_options :: %{required(:socket_options) => socket_options(), required(:internal_options) => internal_options(), optional(option_key()) => any()}

  # Private Types

  @typep accept_callback :: ((peerName :: charlist(), fingerprint() -> boolean()) | (peerName :: charlist(), port :: :inet.port_number(), fingerprint() -> boolean()))

  @typep accept_hosts :: (boolean() | accept_callback() | {hashAlgoSpec :: fp_digest_alg(), accept_callback()})

  @typep alg_entry :: ({:kex, [kex_alg()]} | {:public_key, [pubkey_alg()]} | {:cipher, double_algs(cipher_alg())} | {:mac, double_algs(mac_alg())} | {:compression, double_algs(compression_alg())})

  @typep algs_list :: [alg_entry()]

  @typep auth_methods_common_option :: {:auth_methods, charlist()}

  @typep authentication_client_options :: ({:user, charlist()} | {:password, charlist()})

  @typep authentication_daemon_options :: (:ssh_file.system_dir_daemon_option() | {:auth_method_kb_interactive_data, prompt_texts()} | {:user_passwords, [{userName :: charlist(), pwd :: charlist()}]} | {:pk_check_user, boolean()} | {:password, charlist()} | {:pwdfun, (pwdfun_2() | pwdfun_4())})

  @typep callbacks_daemon_options :: ({:failfun, (user :: charlist(), peerAddress :: :inet.ip_address(), reason :: term() -> _)} | {:connectfun, (user :: charlist(), peerAddress :: :inet.ip_address(), method :: charlist() -> _)})

  @typep cipher_alg :: (:"3des-cbc" | :"AEAD_AES_128_GCM" | :"AEAD_AES_256_GCM" | :"aes128-cbc" | :"aes128-ctr" | :"aes128-gcm@openssh.com" | :"aes192-ctr" | :"aes192-cbc" | :"aes256-cbc" | :"aes256-ctr" | :"aes256-gcm@openssh.com" | :"chacha20-poly1305@openssh.com")

  @typep client_option :: (:ssh_file.pubkey_passphrase_client_options() | host_accepting_client_options() | authentication_client_options() | diffie_hellman_group_exchange_client_option() | connect_timeout_client_option() | recv_ext_info_client_option() | opaque_client_options() | :gen_tcp.connect_option() | common_option())

  @typep client_options :: [client_option()]

  @typep common_option :: (:ssh_file.user_dir_common_option() | profile_common_option() | max_idle_time_common_option() | key_cb_common_option() | disconnectfun_common_option() | unexpectedfun_common_option() | ssh_msg_debug_fun_common_option() | rekey_limit_common_option() | id_string_common_option() | pref_public_key_algs_common_option() | preferred_algorithms_common_option() | modify_algorithms_common_option() | auth_methods_common_option() | inet_common_option() | fd_common_option())

  @typep common_options :: [common_option()]

  @typep compression_alg :: (:none | :zlib | :"zlib@openssh.com")

  @typep connect_timeout_client_option :: {:connect_timeout, timeout()}

  @typep daemon_option :: (subsystem_daemon_option() | shell_daemon_option() | exec_daemon_option() | ssh_cli_daemon_option() | tcpip_tunnel_out_daemon_option() | tcpip_tunnel_in_daemon_option() | authentication_daemon_options() | diffie_hellman_group_exchange_daemon_option() | negotiation_timeout_daemon_option() | hello_timeout_daemon_option() | hardening_daemon_options() | callbacks_daemon_options() | send_ext_info_daemon_option() | opaque_daemon_options() | :gen_tcp.listen_option() | common_option())

  @typep daemon_options :: [daemon_option()]

  @typep deprecated_exec_opt :: (function() | mod_fun_args())

  @typep diffie_hellman_group_exchange_client_option :: {:dh_gex_limits, {min :: pos_integer(), i :: pos_integer(), max :: pos_integer()}}

  @typep diffie_hellman_group_exchange_daemon_option :: ({:dh_gex_groups, ([explicit_group()] | explicit_group_file() | ssh_moduli_file())} | {:dh_gex_limits, {min :: pos_integer(), max :: pos_integer()}})

  @typep disconnectfun_common_option :: {:disconnectfun, (reason :: term() -> (:void | any()))}

  @typep double_algs :: ([({:client2server, [algType]} | {:server2client, [algType]})] | [algType])

  @typep error :: {:error, {:eoptions, any()}}

  @typep exec_daemon_option :: {:exec, exec_spec()}

  @typep exec_fun :: (unquote(:"exec_fun/1")() | unquote(:"exec_fun/2")() | unquote(:"exec_fun/3")())

  @typep unquote(:"exec_fun/1") :: (cmd :: charlist() -> exec_result())

  @typep unquote(:"exec_fun/2") :: (cmd :: charlist(), user :: charlist() -> exec_result())

  @typep unquote(:"exec_fun/3") :: (cmd :: charlist(), user :: charlist(), clientAddr :: ip_port() -> exec_result())

  @typep exec_result :: ({:ok, result :: term()} | {:error, reason :: term()})

  @typep exec_spec :: ({:direct, exec_fun()} | :disabled | deprecated_exec_opt())

  @typep explicit_group :: {size :: pos_integer(), g :: pos_integer(), p :: pos_integer()}

  @typep explicit_group_file :: {:file, charlist()}

  @typep fd_common_option :: {:fd, :gen_tcp.socket()}

  @typep fingerprint :: (charlist() | [charlist()])

  @typep fp_digest_alg :: (:md5 | :crypto.sha1() | :crypto.sha2())

  @typep hardening_daemon_options :: ({:max_sessions, pos_integer()} | {:max_channels, pos_integer()} | {:parallel_login, boolean()} | {:minimal_remote_max_packet_size, pos_integer()})

  @typep hello_timeout_daemon_option :: {:hello_timeout, timeout()}

  @typep host :: (charlist() | :inet.ip_address() | :loopback)

  @typep host_accepting_client_options :: ({:silently_accept_hosts, accept_hosts()} | {:user_interaction, boolean()} | {:save_accepted_host, boolean()} | {:quiet_mode, boolean()})

  @typep id_string_common_option :: {:id_string, (charlist() | :random | {:random, nmin :: pos_integer(), nmax :: pos_integer()})}

  @typep inet_common_option :: {:inet, (:inet | :inet6)}

  @typep internal_options :: :ssh_options.private_options()

  @typep ip_port :: {:inet.ip_address(), :inet.port_number()}

  @typep kb_int_fun_3 :: (peer :: ip_port(), user :: charlist(), service :: charlist() -> kb_int_tuple())

  @typep kb_int_fun_4 :: (peer :: ip_port(), user :: charlist(), service :: charlist(), state :: any() -> kb_int_tuple())

  @typep kb_int_tuple :: {name :: charlist(), instruction :: charlist(), prompt :: charlist(), echo :: boolean()}

  @typep kex_alg :: (:"diffie-hellman-group-exchange-sha1" | :"diffie-hellman-group-exchange-sha256" | :"diffie-hellman-group1-sha1" | :"diffie-hellman-group14-sha1" | :"diffie-hellman-group14-sha256" | :"diffie-hellman-group16-sha512" | :"diffie-hellman-group18-sha512" | :"curve25519-sha256" | :"curve25519-sha256@libssh.org" | :"curve448-sha512" | :"ecdh-sha2-nistp256" | :"ecdh-sha2-nistp384" | :"ecdh-sha2-nistp521")

  @typep key_cb_common_option :: {:key_cb, module :: (atom() | {module :: atom(), opts :: [term()]})}

  @typep limit_bytes :: (non_neg_integer() | :infinity)

  @typep limit_time :: (pos_integer() | :infinity)

  @typep mac_alg :: (:"AEAD_AES_128_GCM" | :"AEAD_AES_256_GCM" | :"hmac-sha1" | :"hmac-sha1-etm@openssh.com" | :"hmac-sha1-96" | :"hmac-sha2-256" | :"hmac-sha2-512" | :"hmac-sha2-256-etm@openssh.com" | :"hmac-sha2-512-etm@openssh.com")

  @typep max_idle_time_common_option :: {:idle_time, timeout()}

  @typep mod_args :: {module :: atom(), args :: []}

  @typep mod_fun_args :: {module :: atom(), function :: atom(), args :: []}

  @typep modify_algorithms_common_option :: {:modify_algorithms, modify_algs_list()}

  @typep modify_algs_list :: [({:append, algs_list()} | {:prepend, algs_list()} | {:rm, algs_list()})]

  @typep negotiation_timeout_daemon_option :: {:negotiation_timeout, timeout()}

  @typep opaque_client_options :: ({:keyboard_interact_fun, (name :: iodata(), instruction :: iodata(), prompts :: [{prompt :: iodata(), echo :: boolean()}] -> [response :: iodata()])} | opaque_common_options())

  @typep opaque_common_options :: ({:transport, {atom(), atom(), atom()}} | {:vsn, {non_neg_integer(), non_neg_integer()}} | {:tstflg, [term()]} | :ssh_file.user_dir_fun_common_option() | {:max_random_length_padding, non_neg_integer()})

  @typep opaque_daemon_options :: ({:infofun, function()} | opaque_common_options())

  @typep open_socket :: :gen_tcp.socket()

  @typep option_class :: (:internal_options | :socket_options | :user_options)

  @typep option_declaration :: %{required(:class) => (:user_option | :undoc_user_option), required(:chk) => (any() -> (boolean() | {true, any()})), optional(:default) => any()}

  @typep option_declarations :: %{required(option_key()) => option_declaration()}

  @typep option_in :: (:proplists.property() | :proplists.proplist())

  @typep option_key :: atom()

  @typep pref_public_key_algs_common_option :: {:pref_public_key_algs, [pubkey_alg()]}

  @typep preferred_algorithms_common_option :: {:preferred_algorithms, algs_list()}

  @typep profile_common_option :: {:profile, atom()}

  @typep prompt_texts :: (kb_int_tuple() | kb_int_fun_3() | kb_int_fun_4())

  @typep pubkey_alg :: (:"ecdsa-sha2-nistp256" | :"ecdsa-sha2-nistp384" | :"ecdsa-sha2-nistp521" | :"ssh-ed25519" | :"ssh-ed448" | :"rsa-sha2-256" | :"rsa-sha2-512" | :"ssh-dss" | :"ssh-rsa")

  @typep pwdfun_2 :: (user :: charlist(), password :: (charlist() | :pubkey) -> boolean())

  @typep pwdfun_4 :: (user :: charlist(), password :: (charlist() | :pubkey), peerAddress :: ip_port(), state :: any() -> (boolean() | :disconnect | {boolean(), newState :: any()}))

  @typep recv_ext_info_client_option :: {:recv_ext_info, boolean()}

  @typep rekey_limit_common_option :: {:rekey_limit, bytes :: (limit_bytes() | {minutes :: limit_time(), bytes :: limit_bytes()})}

  @typep role :: (:client | :server)

  @typep send_ext_info_daemon_option :: {:send_ext_info, boolean()}

  @typep shell_daemon_option :: {:shell, shell_spec()}

  @typep shell_fun :: (unquote(:"shell_fun/1")() | unquote(:"shell_fun/2")())

  @typep unquote(:"shell_fun/1") :: (user :: charlist() -> pid())

  @typep unquote(:"shell_fun/2") :: (user :: charlist(), peerAddr :: :inet.ip_address() -> pid())

  @typep shell_spec :: (mod_fun_args() | shell_fun() | :disabled)

  @typep socket_options :: [(:gen_tcp.connect_option() | :gen_tcp.listen_option())]

  @typep ssh_cli_daemon_option :: {:ssh_cli, (mod_args() | :no_cli)}

  @typep ssh_moduli_file :: {:ssh_moduli_file, charlist()}

  @typep ssh_msg_debug_fun_common_option :: {:ssh_msg_debug_fun, (:ssh.connection_ref(), alwaysDisplay :: boolean(), msg :: binary(), languageTag :: binary() -> any())}

  @typep subsystem_daemon_option :: {:subsystems, subsystem_specs()}

  @typep subsystem_spec :: {name :: charlist(), mod_args()}

  @typep subsystem_specs :: [subsystem_spec()]

  @typep tcpip_tunnel_in_daemon_option :: {:tcpip_tunnel_in, boolean()}

  @typep tcpip_tunnel_out_daemon_option :: {:tcpip_tunnel_out, boolean()}

  @typep unexpectedfun_common_option :: {:unexpectedfun, (message :: term(), {host :: term(), port :: term()} -> (:report | :skip))}

  # Functions

  def check_preferred_algorithms(algs) when is_list(algs) do
    check_input_ok(algs)
    {true, normalize_mod_algs(algs, true)}
  end

  def check_preferred_algorithms(_), do: error_in_check(:modify_algorithms, 'Bad option value. List expected.')

  @spec default((role() | :common)) :: option_declarations()
  def default(:server), do: ...

  def default(:client), do: ...

  def default(:common), do: ...

  @spec delete_key(option_class(), option_key(), private_options(), atom(), non_neg_integer()) :: private_options()
  def delete_key(:user_options, key, opts, _CallerMod, _CallerLine) when is_map(opts) do
    cond do
      is_list(key) ->
        :lists.foldl(&:maps.fun_unknown_name/2, opts, key)
      true ->
        :maps.remove(key, opts)
    end
  end

  def delete_key(class, key, opts, _CallerMod, _CallerLine) when is_map(opts) and class == :socket_options or class == :internal_options do
    %{opts | class => (cond do
      is_list(key) ->
        :lists.foldl(&:maps.fun_unknown_name/2, :maps.get(class, opts), key)
      true ->
        :maps.remove(key, :maps.get(class, opts))
    end)}
  end

  @spec get_value(option_class(), option_key(), private_options(), atom(), non_neg_integer()) :: (any() | no_return())
  def get_value(class, key, opts, _CallerMod, _CallerLine) when is_map(opts) do
    case class do
      :internal_options ->
        :maps.get(key, :maps.get(:internal_options, opts))
      :socket_options ->
        :proplists.get_value(key, :maps.get(:socket_options, opts))
      :user_options ->
        :maps.get(key, opts)
    end
  end

  def get_value(class, key, opts, _CallerMod, _CallerLine), do: error({:bad_options, class, key, opts, _CallerMod, _CallerLine})

  @spec get_value(option_class(), option_key(), private_options(), (() -> any()), atom(), non_neg_integer()) :: (any() | no_return())
  def get_value(:socket_options, key, opts, defFun, _CallerMod, _CallerLine) when is_map(opts), do: :proplists.get_value(key, :maps.get(:socket_options, opts), defFun)

  def get_value(class, key, opts, defFun, callerMod, callerLine) when is_map(opts) do
    try do
      get_value(class, key, opts, callerMod, callerLine)
    catch
      {:error, {:badkey, ^key}, _} ->
        defFun.()
    else
      :undefined ->
        defFun.()
      value ->
        value
    end
  end

  def get_value(class, key, opts, _DefFun, _CallerMod, _CallerLine), do: error({:bad_options, class, key, opts, _CallerMod, _CallerLine})

  @spec handle_options(role(), (client_options() | daemon_options())) :: (private_options() | error())
  def handle_options(role, propList0), do: handle_options(role, propList0, %{:socket_options => [], :internal_options => %{}, :key_cb_options => []})

  def initial_default_algorithms(defList, modList) do
    {true, l0} = check_modify_algorithms(modList)
    rm_non_supported(false, eval_ops(defList, l0))
  end

  @spec keep_set_options((:client | :server), %{}) :: %{}
  def keep_set_options(type, opts) do
    defs = default(type)
    :maps.filter(fn key, value ->
        try do
          %{:default => defVal} = :maps.get(key, defs)
        ^defVal !== ^value
        catch
          {_, _, _} ->
            false
        end
    end, opts)
  end

  @spec keep_user_options((:client | :server), %{}) :: %{}
  def keep_user_options(type, opts) do
    defs = default(type)
    :maps.filter(fn key, _Value ->
        try do
          %{:class => class} = :maps.get(key, defs)
        ^class == :user_option
        catch
          {_, _, _} ->
            false
        end
    end, opts)
  end

  def module_info() do
    # body not decompiled
  end

  def module_info(p0) do
    # body not decompiled
  end

  def no_sensitive(:rm, %{:id_string => _, :tstflg => _}), do: :"*** removed ***"

  def no_sensitive(:filter, opts = %{:id_string => _, :tstflg => _}) do
    sensitive = [:password, :user_passwords, :dsa_pass_phrase, :rsa_pass_phrase, :ecdsa_pass_phrase, :ed25519_pass_phrase, :ed448_pass_phrase]
    :maps.fold(fn k, _V, acc ->
        case :lists.member(k, sensitive) do
          true ->
            %{acc | k => :"***"}
          false ->
            acc
        end
    end, opts, opts)
  end

  def no_sensitive(type, l) when is_list(l) do
    for e <- l do
      no_sensitive(type, e)
    end
  end

  def no_sensitive(type, t) when is_tuple(t), do: list_to_tuple(no_sensitive(type, tuple_to_list(t)))

  def no_sensitive(_, x), do: x

  @spec put_value(option_class(), option_in(), private_options(), atom(), non_neg_integer()) :: private_options()
  def put_value(:user_options, keyVal, opts, _CallerMod, _CallerLine) when is_map(opts), do: put_user_value(keyVal, opts)

  def put_value(:internal_options, keyVal, opts, _CallerMod, _CallerLine) when is_map(opts) do
    internalOpts = :maps.get(:internal_options, opts)
    %{opts | :internal_options => put_internal_value(keyVal, internalOpts)}
  end

  def put_value(:socket_options, keyVal, opts, _CallerMod, _CallerLine) when is_map(opts) do
    socketOpts = :maps.get(:socket_options, opts)
    %{opts | :socket_options => put_socket_value(keyVal, socketOpts)}
  end

  # Private Functions

  defp unquote(:"-check_dh_gex_groups/1-after$^0/0-0-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-check_dh_gex_groups/1-fun-1-")(p0, p1, p2) do
    # body not decompiled
  end

  defp unquote(:"-check_dh_gex_groups/1-fun-2-")(p0, p1) do
    # body not decompiled
  end

  defp unquote(:"-check_fun/2-fun-0-")(p0, p1) do
    # body not decompiled
  end

  defp unquote(:"-check_input_ok/1-lc$^0/1-0-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-check_modify_algorithms/1-lc$^0/1-0-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-check_modify_algorithms/1-lc$^1/1-1-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-check_pref_public_key_algs/1-fun-0-")(p0, p1, p2) do
    # body not decompiled
  end

  defp unquote(:"-check_pref_public_key_algs/1-fun-1-")(p0, p1, p2) do
    # body not decompiled
  end

  defp unquote(:"-collect_per_size/1-fun-0-")(p0, p1) do
    # body not decompiled
  end

  defp unquote(:"-default/1-fun-0-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-default/1-fun-1-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-default/1-fun-10-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-default/1-fun-11-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-default/1-fun-12-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-default/1-fun-13-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-default/1-fun-14-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-default/1-fun-15-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-default/1-fun-16-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-default/1-fun-18-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-default/1-fun-19-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-default/1-fun-2-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-default/1-fun-20-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-default/1-fun-21-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-default/1-fun-22-")(p0, p1, p2) do
    # body not decompiled
  end

  defp unquote(:"-default/1-fun-23-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-default/1-fun-24-")(p0, p1, p2) do
    # body not decompiled
  end

  defp unquote(:"-default/1-fun-25-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-default/1-fun-26-")(p0, p1, p2) do
    # body not decompiled
  end

  defp unquote(:"-default/1-fun-27-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-default/1-fun-28-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-default/1-fun-29-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-default/1-fun-3-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-default/1-fun-30-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-default/1-fun-31-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-default/1-fun-32-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-default/1-fun-33-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-default/1-fun-35-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-default/1-fun-36-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-default/1-fun-37-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-default/1-fun-38-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-default/1-fun-39-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-default/1-fun-4-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-default/1-fun-40-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-default/1-fun-41-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-default/1-fun-42-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-default/1-fun-43-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-default/1-fun-44-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-default/1-fun-45-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-default/1-fun-46-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-default/1-fun-47-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-default/1-fun-48-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-default/1-fun-49-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-default/1-fun-5-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-default/1-fun-50-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-default/1-fun-51-")(p0, p1) do
    # body not decompiled
  end

  defp unquote(:"-default/1-fun-52-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-default/1-fun-53-")(p0, p1, p2, p3) do
    # body not decompiled
  end

  defp unquote(:"-default/1-fun-54-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-default/1-fun-55-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-default/1-fun-57-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-default/1-fun-58-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-default/1-fun-59-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-default/1-fun-6-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-default/1-fun-60-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-default/1-fun-61-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-default/1-fun-62-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-default/1-fun-7-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-default/1-fun-8-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-default/1-fun-9-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-default/1-lc$^56/1-0-")(p0, p1) do
    # body not decompiled
  end

  defp unquote(:"-fun.check_pos_integer/1-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-fun.check_timeout/1-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-fun.eval_op/2-")(p0, p1) do
    # body not decompiled
  end

  defp unquote(:"-fun.put_internal_value/2-")(p0, p1) do
    # body not decompiled
  end

  defp unquote(:"-fun.put_user_value/2-")(p0, p1) do
    # body not decompiled
  end

  defp unquote(:"-handle_options/3-fun-0-")(p0, p1) do
    # body not decompiled
  end

  defp unquote(:"-handle_options/3-fun-2-")(p0, p1, p2, p3, p4) do
    # body not decompiled
  end

  defp unquote(:"-handle_options/3-fun-3-")(p0, p1, p2) do
    # body not decompiled
  end

  defp unquote(:"-handle_options/3-lc$^1/1-0-")(p0, p1) do
    # body not decompiled
  end

  defp unquote(:"-keep_set_options/2-fun-0-")(p0, p1, p2) do
    # body not decompiled
  end

  defp unquote(:"-keep_user_options/2-fun-0-")(p0, p1, p2) do
    # body not decompiled
  end

  defp unquote(:"-nml/2-lc$^0/1-0-")(p0, p1) do
    # body not decompiled
  end

  defp unquote(:"-no_sensitive/2-fun-0-")(p0, p1, p2) do
    # body not decompiled
  end

  defp unquote(:"-no_sensitive/2-lc$^1/1-0-")(p0, p1) do
    # body not decompiled
  end

  defp unquote(:"-rm_non_supported/2-lc$^0/1-0-")(p0, p1) do
    # body not decompiled
  end

  defp unquote(:"-rmns/3-lc$^0/1-0-")(p0, p1, p2) do
    # body not decompiled
  end

  defp unquote(:"-valid_hash/2-fun-0-")(p0, p1) do
    # body not decompiled
  end

  def check_dh_gex_groups({:file, file}) when is_list(file) do
    case :file.consult(file) do
      {:ok, groupDefs} ->
        check_dh_gex_groups(groupDefs)
      {:error, error} ->
        error_in_check({:file, file}, error)
    end
  end

  def check_dh_gex_groups({:ssh_moduli_file, file}) when is_list(file) do
    case :file.open(file, [:read]) do
      {:ok, d} ->
        try do
          read_moduli_file(d, 1, [])
        catch
          {_, _, _} ->
            error_in_check({:ssh_moduli_file, file}, 'Bad format in file ' ++ file)
        else
          {:ok, moduli} ->
            check_dh_gex_groups(moduli)
          {:error, error} ->
            error_in_check({:ssh_moduli_file, file}, error)
        end
      {:error, error} ->
        error_in_check({:ssh_moduli_file, file}, error)
    end
  end

  def check_dh_gex_groups(l0) when is_list(l0) and is_tuple(hd(l0)) do
    {true, collect_per_size(:lists.foldl(fn {n, g, p}, acc when is_integer(n) and n > 0 and is_integer(g) and g > 0 and is_integer(p) and p > 0 ->
        [{n, {g, p}} | acc]
      {n, {g, p}}, acc when is_integer(n) and n > 0 and is_integer(g) and g > 0 and is_integer(p) and p > 0 ->
        [{n, {g, p}} | acc]
      {n, gPs}, acc when is_list(gPs) ->
        :lists.foldr(fn {gi, pi}, acci when is_integer(gi) and gi > 0 and is_integer(pi) and pi > 0 ->
            [{n, {gi, pi}} | acci]
        end, acc, gPs)
    end, [], l0))}
  end

  def check_dh_gex_groups(_), do: false

  def check_dir(dir) do
    case :file.read_file_info(dir) do
      {:ok, file_info(type: :directory, access: access)} ->
        case access do
          :read ->
            true
          :read_write ->
            true
          _ ->
            error_in_check(dir, :eacces)
        end
      {:ok, file_info()} ->
        error_in_check(dir, :enotdir)
      {:error, error} ->
        error_in_check(dir, error)
    end
  end

  def check_fun(key, defs) do
    case :ssh_connection_handler.prohibited_sock_option(key) do
      false ->
        %{:chk => fun} = :maps.get(key, defs)
        ^fun
      true ->
        fn _, _ ->
            :forbidden
        end
    end
  end

  def check_function1(f), do: is_function(f, 1)

  def check_function2(f), do: is_function(f, 2)

  def check_function3(f), do: is_function(f, 3)

  def check_function4(f), do: is_function(f, 4)

  def check_input_ok(algs) do
    for kVs <- algs, notis_tuple(kVs) or size(kVs) !== 2 do
      error_in_check(kVs, 'Bad preferred_algorithms')
    end
  end

  def check_modify_algorithms(m) when is_list(m) do
    for op_KVs <- m, notis_tuple(op_KVs) or size(op_KVs) !== 2 or not:lists.member(element(1, op_KVs), [:append, :prepend, :rm]) do
      error_in_check(op_KVs, 'Bad modify_algorithms')
    end
    {true, for {op, kVs} <- m do
      {op, normalize_mod_algs(kVs, false)}
    end}
  end

  def check_modify_algorithms(_), do: error_in_check(:modify_algorithms, 'Bad option value. List expected.')

  def check_non_neg_integer(i), do: is_integer(i) and i >= 0

  def check_pos_integer(i), do: is_integer(i) and i > 0

  def check_pref_public_key_algs(v) do
    pKs = :ssh_transport.supported_algorithms(:public_key)
    cHK = fn a, ack ->
        case :lists.member(a, pKs) do
          true ->
            case :lists.member(a, ack) do
              false ->
                [a | ack]
              true ->
                ack
            end
          false ->
            error_in_check(a, 'Not supported public key')
        end
    end
    case :lists.foldr(fn :ssh_dsa, ack ->
        cHK.(:"ssh-dss", ack)
      :ssh_rsa, ack ->
        cHK.(:"ssh-rsa", ack)
      x, ack ->
        cHK.(x, ack)
    end, [], v) do
      v ->
        true
      [] ->
        false
      v1 ->
        {true, v1}
    end
  end

  def check_silently_accept_hosts(b) when is_boolean(b), do: true

  def check_silently_accept_hosts(f) when is_function(f, 2), do: true

  def check_silently_accept_hosts({false, s}) when is_atom(s), do: valid_hash(s)

  def check_silently_accept_hosts({s, f}) when is_function(f, 2), do: valid_hash(s)

  def check_silently_accept_hosts(_), do: false

  def check_string(s), do: is_list(s)

  def check_timeout(:infinity), do: true

  def check_timeout(i), do: check_pos_integer(i)

  def cnf_key(:server), do: :server_options

  def cnf_key(:client), do: :client_options

  def collect_per_size(l) do
    :lists.foldr(fn {sz, gP}, [{^sz, gPs} | acc] ->
        [{sz, [gP | gPs]} | acc]
      {sz, gP}, acc ->
        [{sz, [gP]} | acc]
    end, [], :lists.sort(l))
  end

  def config_val(:modify_algorithms = key, roleCnfs, opts) do
    v = (case :application.get_env(:ssh, key) do
      {:ok, v0} ->
        v0
      _ ->
        []
    end) ++ :proplists.get_value(key, roleCnfs, []) ++ :proplists.get_value(key, opts, [])
    case v do
      [] ->
        :undefined
      _ ->
        {:append, v}
    end
  end

  def config_val(key, roleCnfs, opts) do
    case :lists.keysearch(key, 1, opts) do
      {:value, {_, v}} ->
        {:ok, v}
      false ->
        case :lists.keysearch(key, 1, roleCnfs) do
          {:value, {_, v}} ->
            {:ok, v}
          false ->
            :application.get_env(:ssh, key)
        end
    end
  end

  def def_alg(k, false) do
    case :ssh_transport.algo_two_spec_class(k) do
      false ->
        []
      true ->
        [{:client2server, []}, {:server2client, []}]
    end
  end

  def def_alg(k, true), do: :ssh_transport.default_algorithms(k)

  def error_if_empty([{k, []} | _]), do: error({:eoptions, k, 'Empty resulting algorithm list'})

  def error_if_empty([{k, [{:client2server, []}, {:server2client, []}]}]), do: error({:eoptions, k, 'Empty resulting algorithm list'})

  def error_if_empty([{k, [{:client2server, []} | _]} | _]), do: error({:eoptions, {k, :client2server}, 'Empty resulting algorithm list'})

  def error_if_empty([{k, [_, {:server2client, []} | _]} | _]), do: error({:eoptions, {k, :server2client}, 'Empty resulting algorithm list'})

  def error_if_empty([_ | t]), do: error_if_empty(t)

  def error_if_empty([]), do: :ok

  def error_in_check(badValue, extra), do: error({:check, {badValue, extra}})

  def eval_op({op, algKVs}, prefAlgs), do: eval_op(op, algKVs, prefAlgs, [])

  def eval_op(op, [{c, l1} | t1], [{^c, l2} | t2], acc), do: eval_op(op, t1, t2, [{c, eval_op(op, l1, l2, [])} | acc])

  def eval_op(_, [], [], acc), do: :lists.reverse(acc)

  def eval_op(:rm, opt, pref, []) when is_list(opt) and is_list(pref), do: pref -- opt

  def eval_op(:append, opt, pref, []) when is_list(opt) and is_list(pref), do: pref -- opt ++ opt

  def eval_op(:prepend, opt, pref, []) when is_list(opt) and is_list(pref), do: opt ++ pref -- opt

  def eval_ops(prefAlgs, modAlgs), do: :lists.foldl(&eval_op/2, prefAlgs, modAlgs)

  def final_preferred_algorithms(options0) do
    result = case :ssh_options.get_value(:user_options, :modify_algorithms, options0, :ssh_options, 1181) do
      :undefined ->
        rm_non_supported(true, :ssh_options.get_value(:user_options, :preferred_algorithms, options0, :ssh_options, 1184))
      modAlgs ->
        rm_non_supported(false, eval_ops(:ssh_options.get_value(:user_options, :preferred_algorithms, options0, :ssh_options, 1187), modAlgs))
    end
    error_if_empty(result)
    options1 = :ssh_options.put_value(:user_options, {:preferred_algorithms, result}, options0, :ssh_options, 1191)
    case :ssh_options.get_value(:user_options, :pref_public_key_algs, options1, :ssh_options, 1192) do
      :undefined ->
        :ssh_options.put_value(:user_options, {:pref_public_key_algs, :proplists.get_value(:public_key, result)}, options1, :ssh_options, 1194)
      _ ->
        options1
    end
  end

  def handle_options(role, optsList0, opts0) when is_map(opts0) and is_list(optsList0), do: ...

  def nml(k, l) do
    for v <- l, notis_atom(v) do
      error_in_check(k, 'Bad value for this key')
    end
    case l -- :lists.usort(l) do
      [] ->
        :ok
      dups ->
        error_in_check({k, dups}, 'Duplicates')
    end
    l
  end

  def nml1(k, {t, v}) when t == :client2server or t == :server2client, do: {t, nml({k, t}, v)}

  def normalize_mod_alg_list(k, vs, useDefaultAlgs), do: normalize_mod_alg_list(k, :ssh_transport.algo_two_spec_class(k), vs, def_alg(k, useDefaultAlgs))

  def normalize_mod_alg_list(_K, _, [], default), do: default

  def normalize_mod_alg_list(k, true, [{:client2server, l1}], [_, {:server2client, l2}]), do: [nml1(k, {:client2server, l1}), {:server2client, l2}]

  def normalize_mod_alg_list(k, true, [{:server2client, l2}], [{:client2server, l1}, _]), do: [{:client2server, l1}, nml1(k, {:server2client, l2})]

  def normalize_mod_alg_list(k, true, [{:server2client, l2}, {:client2server, l1}], _), do: [nml1(k, {:client2server, l1}), nml1(k, {:server2client, l2})]

  def normalize_mod_alg_list(k, true, [{:client2server, l1}, {:server2client, l2}], _), do: [nml1(k, {:client2server, l1}), nml1(k, {:server2client, l2})]

  def normalize_mod_alg_list(k, true, l0, _) do
    l = nml(k, l0)
    [{:client2server, l}, {:server2client, l}]
  end

  def normalize_mod_alg_list(k, false, l, _), do: nml(k, l)

  def normalize_mod_algs(kVs, useDefaultAlgs), do: normalize_mod_algs(:ssh_transport.algo_classes(), kVs, [], useDefaultAlgs)

  def normalize_mod_algs([k | ks], kVs0, acc, useDefaultAlgs) do
    {vs1, kVs} = case :lists.keytake(k, 1, kVs0) do
      {:value, {k, vs0}, kVs1} ->
        {vs0, kVs1}
      false ->
        {[], kVs0}
    end
    vs = normalize_mod_alg_list(k, vs1, useDefaultAlgs)
    normalize_mod_algs(ks, kVs, [{k, vs} | acc], useDefaultAlgs)
  end

  def normalize_mod_algs([], [], acc, _), do: :lists.reverse(acc)

  def normalize_mod_algs([], [{k, _} | _], _, _) do
    case :ssh_transport.algo_class(k) do
      true ->
        error_in_check(k, 'Duplicate key')
      false ->
        error_in_check(k, 'Unknown key')
    end
  end

  def normalize_mod_algs([], [x | _], _, _), do: error_in_check(x, 'Bad list element')

  def put_internal_value(l, intOpts) when is_list(l), do: :lists.foldl(&put_internal_value/2, intOpts, l)

  def put_internal_value({key, value}, intOpts), do: %{intOpts | key => value}

  def put_socket_value(l, sockOpts) when is_list(l), do: l ++ sockOpts

  def put_socket_value({key, value}, sockOpts), do: [{key, value} | sockOpts]

  def put_socket_value(a, sockOpts) when is_atom(a), do: [a | sockOpts]

  def put_user_value(l, opts) when is_list(l), do: :lists.foldl(&put_user_value/2, opts, l)

  def put_user_value({key, value}, opts), do: %{opts | key => value}

  def read_moduli_file(d, i, acc) do
    case :io.get_line(d, unknown_string) do
      {:error, error} ->
        {:error, error}
      :eof ->
        {:ok, acc}
      '#' ++ _ ->
        read_moduli_file(d, i + 1, acc)
      <<"#", _ :: binary>> ->
        read_moduli_file(d, i + 1, acc)
      data ->
        line = cond do
          is_binary(data) ->
            binary_to_list(data)
          is_list(data) ->
            data
        end
        try do
          [_Time, _Class, _Tests, _Tries, size, g, p] = :string.tokens(line, ' \r\n')
        m = {list_to_integer(size), {list_to_integer(g), list_to_integer(p, 16)}}
        read_moduli_file(d, i + 1, [m | acc])
        catch
          {_, _, _} ->
            read_moduli_file(d, i + 1, acc)
        end
    end
  end

  def rm_non_supported(unsupIsErrorFlg, kVs) do
    for {k, vs} <- kVs do
      {k, rmns(k, vs, unsupIsErrorFlg)}
    end
  end

  def rm_unsup(a, b, flg, errInf) do
    case a -- b do
      unsup = [_ | _] when flg == true ->
        error({:eoptions, {:preferred_algorithms, {errInf, unsup}}, 'Unsupported value(s) found'})
      unsup ->
        a -- unsup
    end
  end

  def rmns(k, vs, unsupIsErrorFlg) do
    case :ssh_transport.algo_two_spec_class(k) do
      false ->
        rm_unsup(vs, :ssh_transport.supported_algorithms(k), unsupIsErrorFlg, k)
      true ->
        for {{c, vsx}, {c, sup}} <- :lists.zip(vs, :ssh_transport.supported_algorithms(k)) do
          {c, rm_unsup(vsx, sup, unsupIsErrorFlg, {k, c})}
        end
    end
  end

  def save({:allow_user_interaction, v}, opts, vals), do: save({:user_interaction, v}, opts, vals)

  def save(inet, defs, optMap) when inet == :inet or inet == :inet6, do: save({:inet, inet}, defs, optMap)

  def save({inet, true}, defs, optMap) when inet == :inet or inet == :inet6, do: save({:inet, inet}, defs, optMap)

  def save({inet, false}, _Defs, optMap) when inet == :inet or inet == :inet6, do: optMap

  def save({:special_trpt_args, t}, _Defs, optMap) when is_map(optMap), do: %{optMap | :socket_options => [t | :maps.get(:socket_options, optMap)]}

  def save({key, value}, defs, optMap) when is_map(optMap) do
    try do
      check_fun(key, defs)(value)
    catch
      {:error, {:badkey, :inet}, _} ->
        %{optMap | :socket_options => [value | :maps.get(:socket_options, optMap)]}
      {:error, {:badkey, ^key}, _} ->
        %{optMap | :socket_options => [{key, value} | :maps.get(:socket_options, optMap)]}
      {:error, {:check, {badValue, extra}}, _} ->
        error({:eoptions, {key, badValue}, extra})
    else
      true ->
        %{optMap | key => value}
      {true, modifiedValue} ->
        %{optMap | key => modifiedValue}
      false ->
        error({:eoptions, {key, value}, 'Bad value'})
      :forbidden ->
        error({:eoptions, {key, value}, :io_lib.format('The option \'~s\' is used internally. The user is not allowed to specify this option.', [key])})
    end
  end

  def save(opt, _Defs, optMap) when is_map(optMap), do: %{optMap | :socket_options => [opt | :maps.get(:socket_options, optMap)]}

  def valid_hash(s), do: valid_hash(s, :proplists.get_value(:hashs, :crypto.supports()))

  def valid_hash(s, ss) when is_atom(s), do: :lists.member(s, [:md5, :sha, :sha224, :sha256, :sha384, :sha512]) and :lists.member(s, ss)

  def valid_hash(l, ss) when is_list(l) do
    :lists.all(fn s ->
        valid_hash(s, ss)
    end, l)
  end

  def valid_hash(x, _), do: error_in_check(x, 'Expect atom or list in fingerprint spec')
end
