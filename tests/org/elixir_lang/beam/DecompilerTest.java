package org.elixir_lang.beam;

import com.ericsson.otp.erlang.OtpErlangDecodeException;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.impl.VfsRootAccess;
import com.intellij.testFramework.LightCodeInsightTestCase;

import java.io.*;

public class DecompilerTest extends LightCodeInsightTestCase {
    /*
     * Tests
     */

    public void testIssue575() throws IOException, OtpErlangDecodeException {
        String ebinDirectory = ebinDirectory();

        VfsRootAccess.allowRootAccess(ebinDirectory);

        VirtualFile virtualFile = LocalFileSystem
                .getInstance()
                .findFileByIoFile(
                        new File(ebinDirectory + "Elixir.Bitwise.beam")
                );

        assertNotNull(virtualFile);

        Decompiler decompiler = new Decompiler();
        CharSequence decompiled = decompiler.decompile(virtualFile);

        assertEquals(
                "# Source code recreated from a .beam file by IntelliJ Elixir\n" +
                "defmodule Bitwise do\n" +
                "\n" +
                "  # Macros\n" +
                "\n" +
                "  defmacro left &&& right do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  defmacro left <<< right do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  defmacro left >>> right do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  defmacro left ^^^ right do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  defmacro __using__(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  defmacro band(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  defmacro bnot(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  defmacro bor(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  defmacro bsl(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  defmacro bsr(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  defmacro bxor(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  defmacro left ||| right do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  defmacro ~~~(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  # Functions\n" +
                "\n" +
                "  def __info__(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def module_info() do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def module_info(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "end\n",
                decompiled.toString()
        );
    }

    public void testIssue672() throws IOException, OtpErlangDecodeException {
        String testDataPath = getTestDataPath();

        VfsRootAccess.allowRootAccess(testDataPath);

        VirtualFile virtualFile = LocalFileSystem
                .getInstance()
                .findFileByIoFile(
                        new File(testDataPath + "/rebar3_hex_config.beam")
                );

        assertNotNull(virtualFile);

        Decompiler decompiler = new Decompiler();
        CharSequence decompiled = decompiler.decompile(virtualFile);

        assertEquals(
                "# Source code recreated from a .beam file by IntelliJ Elixir\n" +
                "defmodule :rebar3_hex_config do\n" +
                "\n" +
                "  # Macros\n" +
                "\n" +
                "  def api_url() do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def auth() do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def cdn_url() do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:do)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def format_error(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def http_proxy() do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def https_proxy() do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def init(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def module_info() do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def module_info(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def path() do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def read() do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def update(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def username() do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def write(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "end\n",
                decompiled.toString()
        );
    }

    public void testIssue683() throws IOException, OtpErlangDecodeException {
        String testDataPath = getTestDataPath();

        VfsRootAccess.allowRootAccess(testDataPath);

        VirtualFile virtualFile = LocalFileSystem
                .getInstance()
                .findFileByIoFile(
                        new File(testDataPath + "/orber_ifr.beam")
                );

        assertNotNull(virtualFile);

        Decompiler decompiler = new Decompiler();
        CharSequence decompiled = decompiler.decompile(virtualFile);

        assertEquals(
                "# Source code recreated from a .beam file by IntelliJ Elixir\n" +
                "defmodule :orber_ifr do\n" +
                "\n" +
                "  # Macros\n" +
                "\n" +
                "  def unquote(:AliasDef__get_absolute_name)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:AliasDef__get_containing_repository)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:AliasDef__get_def_kind)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:AliasDef__get_defined_in)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:AliasDef__get_id)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:AliasDef__get_name)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:AliasDef__get_original_type_def)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:AliasDef__get_type)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:AliasDef__get_version)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:AliasDef__set_id)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:AliasDef__set_name)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:AliasDef__set_original_type_def)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:AliasDef__set_version)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:AliasDef_describe)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:AliasDef_destroy)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:AliasDef_move)(p0, p1, p2, p3) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ArrayDef__get_def_kind)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ArrayDef__get_element_type)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ArrayDef__get_element_type_def)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ArrayDef__get_length)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ArrayDef__get_type)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ArrayDef__set_element_type_def)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ArrayDef__set_length)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ArrayDef_destroy)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:AttributeDef__get_absolute_name)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:AttributeDef__get_containing_repository)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:AttributeDef__get_def_kind)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:AttributeDef__get_defined_in)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:AttributeDef__get_id)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:AttributeDef__get_mode)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:AttributeDef__get_name)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:AttributeDef__get_type)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:AttributeDef__get_type_def)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:AttributeDef__get_version)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:AttributeDef__set_id)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:AttributeDef__set_mode)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:AttributeDef__set_name)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:AttributeDef__set_type_def)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:AttributeDef__set_version)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:AttributeDef_describe)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:AttributeDef_destroy)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:AttributeDef_move)(p0, p1, p2, p3) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ConstantDef__get_absolute_name)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ConstantDef__get_containing_repository)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ConstantDef__get_def_kind)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ConstantDef__get_defined_in)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ConstantDef__get_id)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ConstantDef__get_name)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ConstantDef__get_type)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ConstantDef__get_type_def)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ConstantDef__get_value)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ConstantDef__get_version)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ConstantDef__set_id)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ConstantDef__set_name)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ConstantDef__set_type_def)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ConstantDef__set_value)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ConstantDef__set_version)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ConstantDef_describe)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ConstantDef_destroy)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ConstantDef_move)(p0, p1, p2, p3) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:Contained__get_absolute_name)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:Contained__get_containing_repository)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:Contained__get_def_kind)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:Contained__get_defined_in)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:Contained__get_id)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:Contained__get_name)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:Contained__get_version)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:Contained__set_id)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:Contained__set_name)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:Contained__set_version)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:Contained_describe)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:Contained_move)(p0, p1, p2, p3) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:Container__get_def_kind)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:Container_contents)(p0, p1, p2) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:Container_create_alias)(p0, p1, p2, p3, p4) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:Container_create_constant)(p0, p1, p2, p3, p4, p5) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:Container_create_enum)(p0, p1, p2, p3, p4) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:Container_create_exception)(p0, p1, p2, p3, p4) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:Container_create_interface)(p0, p1, p2, p3, p4) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:Container_create_module)(p0, p1, p2, p3) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:Container_create_struct)(p0, p1, p2, p3, p4) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:Container_create_union)(p0, p1, p2, p3, p4, p5) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:Container_describe_contents)(p0, p1, p2, p3) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:Container_destroy)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:Container_lookup)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:Container_lookup_name)(p0, p1, p2, p3, p4) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:EnumDef__get_absolute_name)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:EnumDef__get_containing_repository)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:EnumDef__get_def_kind)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:EnumDef__get_defined_in)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:EnumDef__get_id)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:EnumDef__get_members)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:EnumDef__get_name)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:EnumDef__get_type)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:EnumDef__get_version)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:EnumDef__set_id)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:EnumDef__set_members)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:EnumDef__set_name)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:EnumDef__set_version)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:EnumDef_describe)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:EnumDef_destroy)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:EnumDef_move)(p0, p1, p2, p3) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ExceptionDef__get_absolute_name)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ExceptionDef__get_containing_repository)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ExceptionDef__get_def_kind)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ExceptionDef__get_defined_in)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ExceptionDef__get_id)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ExceptionDef__get_members)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ExceptionDef__get_name)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ExceptionDef__get_type)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ExceptionDef__get_version)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ExceptionDef__set_id)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ExceptionDef__set_members)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ExceptionDef__set_name)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ExceptionDef__set_version)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ExceptionDef_describe)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ExceptionDef_destroy)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ExceptionDef_move)(p0, p1, p2, p3) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:FixedDef__get_def_kind)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:FixedDef__get_digits)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:FixedDef__get_scale)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:FixedDef__get_type)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:FixedDef__set_digits)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:FixedDef__set_scale)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:FixedDef_destroy)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:IDLType__get_def_kind)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:IDLType__get_type)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:IDLType_destroy)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:IRObject__get_def_kind)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:InterfaceDef__get_absolute_name)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:InterfaceDef__get_base_interfaces)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:InterfaceDef__get_containing_repository)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:InterfaceDef__get_def_kind)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:InterfaceDef__get_defined_in)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:InterfaceDef__get_id)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:InterfaceDef__get_name)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:InterfaceDef__get_type)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:InterfaceDef__get_version)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:InterfaceDef__set_base_interfaces)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:InterfaceDef__set_id)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:InterfaceDef__set_name)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:InterfaceDef__set_version)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:InterfaceDef_contents)(p0, p1, p2) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:InterfaceDef_create_alias)(p0, p1, p2, p3, p4) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:InterfaceDef_create_attribute)(p0, p1, p2, p3, p4, p5) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:InterfaceDef_create_constant)(p0, p1, p2, p3, p4, p5) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:InterfaceDef_create_enum)(p0, p1, p2, p3, p4) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:InterfaceDef_create_exception)(p0, p1, p2, p3, p4) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:InterfaceDef_create_interface)(p0, p1, p2, p3, p4) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:InterfaceDef_create_module)(p0, p1, p2, p3) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:InterfaceDef_create_operation)(p0, p1, p2, p3, p4, p5, p6, p7, p8) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:InterfaceDef_create_struct)(p0, p1, p2, p3, p4) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:InterfaceDef_create_union)(p0, p1, p2, p3, p4, p5) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:InterfaceDef_describe)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:InterfaceDef_describe_contents)(p0, p1, p2, p3) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:InterfaceDef_describe_interface)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:InterfaceDef_destroy)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:InterfaceDef_is_a)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:InterfaceDef_lookup)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:InterfaceDef_lookup_name)(p0, p1, p2, p3, p4) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:InterfaceDef_move)(p0, p1, p2, p3) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ModuleDef__get_absolute_name)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ModuleDef__get_containing_repository)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ModuleDef__get_def_kind)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ModuleDef__get_defined_in)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ModuleDef__get_id)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ModuleDef__get_name)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ModuleDef__get_version)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ModuleDef__set_id)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ModuleDef__set_name)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ModuleDef__set_version)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ModuleDef_contents)(p0, p1, p2) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ModuleDef_create_alias)(p0, p1, p2, p3, p4) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ModuleDef_create_constant)(p0, p1, p2, p3, p4, p5) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ModuleDef_create_enum)(p0, p1, p2, p3, p4) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ModuleDef_create_exception)(p0, p1, p2, p3, p4) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ModuleDef_create_interface)(p0, p1, p2, p3, p4) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ModuleDef_create_module)(p0, p1, p2, p3) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ModuleDef_create_struct)(p0, p1, p2, p3, p4) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ModuleDef_create_union)(p0, p1, p2, p3, p4, p5) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ModuleDef_describe)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ModuleDef_describe_contents)(p0, p1, p2, p3) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ModuleDef_destroy)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ModuleDef_lookup)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ModuleDef_lookup_name)(p0, p1, p2, p3, p4) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ModuleDef_move)(p0, p1, p2, p3) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ORB_create_alias_tc)(p0, p1, p2) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ORB_create_array_tc)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ORB_create_enum_tc)(p0, p1, p2) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ORB_create_exception_tc)(p0, p1, p2) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ORB_create_interface_tc)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ORB_create_recursive_sequence_tc)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ORB_create_sequence_tc)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ORB_create_string_tc)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ORB_create_struct_tc)(p0, p1, p2) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ORB_create_union_tc)(p0, p1, p2, p3) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:ORB_create_wstring_tc)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:OperationDef__get_absolute_name)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:OperationDef__get_containing_repository)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:OperationDef__get_contexts)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:OperationDef__get_def_kind)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:OperationDef__get_defined_in)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:OperationDef__get_exceptions)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:OperationDef__get_id)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:OperationDef__get_mode)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:OperationDef__get_name)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:OperationDef__get_params)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:OperationDef__get_result)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:OperationDef__get_result_def)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:OperationDef__get_version)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:OperationDef__set_contexts)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:OperationDef__set_exceptions)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:OperationDef__set_id)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:OperationDef__set_mode)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:OperationDef__set_name)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:OperationDef__set_params)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:OperationDef__set_result_def)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:OperationDef__set_version)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:OperationDef_describe)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:OperationDef_destroy)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:OperationDef_move)(p0, p1, p2, p3) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:PrimitiveDef__get_def_kind)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:PrimitiveDef__get_kind)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:PrimitiveDef__get_type)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:PrimitiveDef_destroy)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:Repository__get_def_kind)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:Repository_contents)(p0, p1, p2) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:Repository_create_alias)(p0, p1, p2, p3, p4) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:Repository_create_array)(p0, p1, p2) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:Repository_create_constant)(p0, p1, p2, p3, p4, p5) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:Repository_create_enum)(p0, p1, p2, p3, p4) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:Repository_create_exception)(p0, p1, p2, p3, p4) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:Repository_create_idltype)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:Repository_create_interface)(p0, p1, p2, p3, p4) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:Repository_create_module)(p0, p1, p2, p3) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:Repository_create_sequence)(p0, p1, p2) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:Repository_create_string)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:Repository_create_struct)(p0, p1, p2, p3, p4) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:Repository_create_union)(p0, p1, p2, p3, p4, p5) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:Repository_create_wstring)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:Repository_describe_contents)(p0, p1, p2, p3) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:Repository_destroy)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:Repository_get_primitive)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:Repository_lookup)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:Repository_lookup_id)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:Repository_lookup_name)(p0, p1, p2, p3, p4) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:SequenceDef__get_bound)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:SequenceDef__get_def_kind)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:SequenceDef__get_element_type)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:SequenceDef__get_element_type_def)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:SequenceDef__get_type)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:SequenceDef__set_bound)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:SequenceDef__set_element_type_def)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:SequenceDef_destroy)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:StringDef__get_bound)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:StringDef__get_def_kind)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:StringDef__get_type)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:StringDef__set_bound)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:StringDef_destroy)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:StructDef__get_absolute_name)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:StructDef__get_containing_repository)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:StructDef__get_def_kind)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:StructDef__get_defined_in)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:StructDef__get_id)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:StructDef__get_members)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:StructDef__get_name)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:StructDef__get_type)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:StructDef__get_version)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:StructDef__set_id)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:StructDef__set_members)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:StructDef__set_name)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:StructDef__set_version)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:StructDef_describe)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:StructDef_destroy)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:StructDef_move)(p0, p1, p2, p3) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:TypedefDef__get_absolute_name)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:TypedefDef__get_containing_repository)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:TypedefDef__get_def_kind)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:TypedefDef__get_defined_in)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:TypedefDef__get_id)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:TypedefDef__get_name)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:TypedefDef__get_type)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:TypedefDef__get_version)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:TypedefDef__set_id)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:TypedefDef__set_name)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:TypedefDef__set_version)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:TypedefDef_describe)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:TypedefDef_destroy)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:TypedefDef_move)(p0, p1, p2, p3) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:UnionDef__get_absolute_name)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:UnionDef__get_containing_repository)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:UnionDef__get_def_kind)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:UnionDef__get_defined_in)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:UnionDef__get_discriminator_type)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:UnionDef__get_discriminator_type_def)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:UnionDef__get_id)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:UnionDef__get_members)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:UnionDef__get_name)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:UnionDef__get_type)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:UnionDef__get_version)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:UnionDef__set_discriminator_type_def)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:UnionDef__set_id)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:UnionDef__set_members)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:UnionDef__set_name)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:UnionDef__set_version)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:UnionDef_describe)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:UnionDef_destroy)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:UnionDef_move)(p0, p1, p2, p3) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:WstringDef__get_bound)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:WstringDef__get_def_kind)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:WstringDef__get_type)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:WstringDef__set_bound)(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def unquote(:WstringDef_destroy)(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def add_alias(p0, p1, p2) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def add_alias(p0, p1, p2, p3) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def add_constant(p0, p1, p2) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def add_constant(p0, p1, p2, p3) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def add_enum(p0, p1, p2) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def add_enum(p0, p1, p2, p3) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def add_exception(p0, p1, p2) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def add_exception(p0, p1, p2, p3) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def add_interface(p0, p1, p2) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def add_interface(p0, p1, p2, p3) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def add_items(p0, p1, p2) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def add_module(p0, p1, p2) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def add_module(p0, p1, p2, p3) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def add_struct(p0, p1, p2) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def add_struct(p0, p1, p2, p3) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def add_union(p0, p1, p2) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def add_union(p0, p1, p2, p3) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def contents(p0, p1, p2) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def create_alias(p0, p1, p2, p3, p4) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def create_array(p0, p1, p2) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def create_attribute(p0, p1, p2, p3, p4, p5) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def create_constant(p0, p1, p2, p3, p4, p5) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def create_enum(p0, p1, p2, p3, p4) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def create_exception(p0, p1, p2, p3, p4) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def create_fixed(p0, p1, p2) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def create_idltype(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def create_interface(p0, p1, p2, p3, p4) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def create_module(p0, p1, p2, p3) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def create_operation(p0, p1, p2, p3, p4, p5, p6, p7, p8) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def create_sequence(p0, p1, p2) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def create_string(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def create_struct(p0, p1, p2, p3, p4) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def create_union(p0, p1, p2, p3, p4, p5) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def create_wstring(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def describe(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def describe_contents(p0, p1, p2, p3) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def describe_interface(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def destroy(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def find_repository() do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def get_absolute_name(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def get_base_interfaces(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def get_bound(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def get_containing_repository(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def get_contexts(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def get_def_kind(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def get_defined_in(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def get_discriminator_type(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def get_discriminator_type_def(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def get_element_type(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def get_element_type_def(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def get_exceptions(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def get_id(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def get_kind(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def get_length(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def get_members(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def get_mode(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def get_module(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def get_name(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def get_original_type_def(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def get_params(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def get_primitive(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def get_result(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def get_result_def(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def get_tc(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def get_type(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def get_type_def(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def get_value(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def get_version(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def init(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def initialize(p0, p1, p2) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def is_a(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def lookup(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def lookup_id(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def lookup_name(p0, p1, p2, p3, p4) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def module_info() do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def module_info(p0) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def move(p0, p1, p2, p3) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def remove(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def set_base_interfaces(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def set_bound(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def set_contexts(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def set_discriminator_type_def(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def set_element_type_def(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def set_exceptions(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def set_id(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def set_length(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def set_members(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def set_mode(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def set_name(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def set_original_type_def(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def set_params(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def set_result_def(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def set_type_def(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def set_value(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "\n" +
                "  def set_version(p0, p1) do\n" +
                "    # body not decompiled\n" +
                "  end\n" +
                "end\n",
                decompiled.toString()
        );
    }

    /*
     * Instance Methods
     */

    /*
     * Protected Instance Methods
     */

    @Override
    protected String getTestDataPath() {
        return "testData/org/elixir_lang/beam/decompiler";
    }


    private String ebinDirectory() {
        String ebinDirectory = System.getenv("ELIXIR_EBIN_DIRECTORY");

        assertNotNull("ELIXIR_EBIN_DIRECTORY is not set", ebinDirectory);

        return ebinDirectory;
    }
}
