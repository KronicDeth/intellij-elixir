defmodule IntelliJElixir.Debugger.ServerTest do
  use ExUnit.Case, async: false

  setup :reset

  setup do
    {:ok, pid} = IntelliJElixir.Debugger.Server.start()

    on_exit(fn ->
      GenServer.stop(pid)
    end)

    %{pid: pid}
  end

  describe "handle_call(:attach, ..., ...)" do
    setup %{pid: pid} do
      spawn_link(fn ->
        Process.register(self(), IntelliJElixir.Debugged)

        receive do
          {:"$gen_call", from = {^pid, _tag}, :continue} ->
            GenServer.reply(from, :ok)
        end
      end)

      on_exit(fn ->
        :int.auto_attach(false)
      end)

      :ok
    end

    test "registers `IntelliJElixir.Debugger.Server.breakpoint_reached` to auto-attach", %{pid: pid} do
      refute :int.auto_attach()

      GenServer.call(pid, :attach)

      assert :int.auto_attach() == {[:break], {IntelliJElixir.Debugger.Server, :breakpoint_reached, []}}
    end

    test "returns GenServer.call(IntelliJElixir.Debugged, :continue) result", %{pid: pid} do
      assert GenServer.call(pid, :attach) == :ok
    end
  end

  #  describe "handle_call({:interpret, module}, ..., ...)" do
  #    test "with module", %{pid: pid} do
  #      assert GenServer.call(pid, {:interpret, Bitwise}) == :ok
  #    end
  #
  #    test "without module", %{pid: pid} do
  #      assert GenServer.call(pid, {:interpret, NonExistent}) == :error
  #    end
  #  end

  describe "handle_call({:interpret, %{...}, ..., ...)" do
    setup %{pid: pid} do
      {:ok, capture_group_leader} = StringIO.open("", capture_prompt: true)
      Process.group_leader(pid, capture_group_leader)

      :ok
    end

    test "without sdk_paths without reject_elixir_module_name_patterns interprets everything", %{pid: pid} do
      assert :int.interpreted() == []

      assert GenServer.call(pid, {:interpret, %{sdk_paths: [], reject_elixir_module_name_patterns: []}}, 40_000) == :ok

      refute :int.interpreted() == []

      assert :int.interpreted()
             |> Enum.any?(fn interpreted ->
               interpreted
               |> to_string()
               |> String.starts_with?("Elixir.")
               |> Kernel.not()
             end)
    end

    test "without sdk_paths with reject_elixir_module_name_patterns does not interpret matching modules", %{pid: pid} do
      assert :int.interpreted() == []

      assert GenServer.call(
               pid,
               {:interpret, %{sdk_paths: [], reject_elixir_module_name_patterns: ["IntelliJElixir.*"]}},
               40_000
             ) == :ok

      refute IntelliJElixir.Debugger.Interpreted in :int.interpreted()
    end

    test "with sdk_paths with reject_elixir_module_name_patterns does not interpret any Erlang SDK modules", %{pid: pid} do
      assert :int.interpreted() == []

      sdk_absolute_paths =
        :code.get_path()
        |> Stream.map(&to_string/1)
        |> Stream.map(&Path.absname/1)
        |> Enum.reject(fn absolute_code_path ->
          String.contains?(absolute_code_path, "/resources/debugger/_build/shared/lib/intellij_elixir_debugger/")
        end)

      assert GenServer.call(
               pid,
               {:interpret, %{sdk_paths: sdk_absolute_paths, reject_elixir_module_name_patterns: []}}
             ) == :ok

      assert :int.interpreted() == [IntelliJElixir.Debugger.Interpreted]
    end

    test "with sdks_paths with reject_elixir_module_name_patterns does not inpreter matching modules outside sdk_paths",
         %{pid: pid} do
      assert :int.interpreted() == []

      sdk_absolute_paths =
        :code.get_path()
        |> Stream.map(&to_string/1)
        |> Stream.map(&Path.absname/1)
        |> Enum.reject(fn absolute_code_path ->
          String.contains?(absolute_code_path, "/resources/debugger/_build/shared/lib/intellij_elixir_debugger/")
        end)

      assert GenServer.call(
               pid,
               {:interpret, %{sdk_paths: sdk_absolute_paths, reject_elixir_module_name_patterns: ["IntelliJElixir.*"]}}
             ) == :ok

      assert :int.interpreted() == []
    end
  end

  describe "handle_call({:stop_interpreting, module}, ..., ...)" do
    test "with module intepreted", %{pid: pid} do
      :int.ni(:observer)

      assert :int.interpreted() == [:observer]

      assert GenServer.call(pid, {:stop_interpreting, :observer}) == :ok

      assert :int.interpreted() == []
    end

    test "without module interpreted", %{pid: pid} do
      assert :int.interpreted() == []

      assert GenServer.call(pid, {:stop_interpreting, :observer}) == :ok

      assert :int.interpreted() == []
    end
  end

  describe "handle_call({:set_breakpoint, module, line}, ..., ...)" do
    test "with invalid module", %{pid: pid} do
      assert GenServer.call(pid, {:set_breakpoint, Invalid, 1}) == :ok
      assert :int.all_breaks() == [{{Invalid, 1}, [:active, :enable, :null, :null]}]
    end

    test "with valid module with previously interpreted", %{pid: pid} do
      :int.ni(IntelliJElixir.Debugger.Interpreted)

      assert IntelliJElixir.Debugger.Interpreted in :int.interpreted()

      assert GenServer.call(pid, {:set_breakpoint, IntelliJElixir.Debugger.Interpreted, 7}) == :ok

      assert :int.all_breaks() == [{{IntelliJElixir.Debugger.Interpreted, 7}, [:active, :enable, :null, :null]}]
    end

    test "with valid module without previously interpreted", %{pid: pid} do
      refute IntelliJElixir.Debugger.Interpreted in :int.interpreted()

      assert GenServer.call(pid, {:set_breakpoint, IntelliJElixir.Debugger.Interpreted, 7}) == :ok

      assert IntelliJElixir.Debugger.Interpreted in :int.interpreted()
      assert :int.all_breaks() == [{{IntelliJElixir.Debugger.Interpreted, 7}, [:active, :enable, :null, :null]}]
    end
  end

  describe "handle_call({:remove_breakpoint, module, line}, ..., ...)" do
    test "can remove breakpoint set with {:set_breakpoint, module, line}", %{pid: pid} do
      module = IntelliJElixir.Debugger.Interpreted
      line = 7

      GenServer.call(pid, {:set_breakpoint, module, line})

      assert :int.all_breaks() == [{{module, line}, [:active, :enable, :null, :null]}]

      assert GenServer.call(pid, {:remove_breakpoint, module, line}) == :ok

      assert :int.all_breaks() == []
    end

    test "with non-existent breakpoint", %{pid: pid} do
      assert :int.all_breaks() == []

      assert GenServer.call(pid, {:remove_breakpoint, IntelliJElixir.Debugger.Interpreted, 7}) == :ok

      assert :int.all_breaks() == []
    end
  end

  def reset(_) do
    on_exit(fn ->
      :int.interpreted()
      |> Enum.each(&:int.nn/1)
    end)

    on_exit(fn ->
      :int.no_break()
    end)

    :ok
  end
end
