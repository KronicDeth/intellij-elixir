# Source code recreated from a .beam file by IntelliJ Elixir
defmodule :docgen_xmerl_xml_cb do

  # Functions

  def unquote(:"#element#")(tag, data, attrs, _Parents, _E) do
    {newTag, newAttrs} = convert_tag(tag, attrs)
    :xmerl_lib.markup(newTag, newAttrs, data)
  end

  def unquote(:"#root#")(data, attrs, [], _E) do
    encoding = for xmlAttribute(name: :encoding, value: e) <- attrs do
      e
    end
    |> case do
      [e] ->
        e
      _ ->
        atom_to_list(:epp.default_encoding())
    end
    ['<', dTD, '>'] = hd(hd(data))
    ['<?xml version="1.0" encoding="', encoding, '" ?>\n', '<!DOCTYPE ' ++ dTD ++ ' SYSTEM "' ++ dTD ++ '.dtd">\n', data]
  end

  def unquote(:"#text#")(text), do: :xmerl_lib.export_text(text)

  def unquote(:"#xml-inheritance#")(), do: [:xmerl_xml]

  def module_info() do
    # body not decompiled
  end

  def module_info(p0) do
    # body not decompiled
  end

  # Private Functions

  defp unquote(:"-#root#/4-lc$^0/1-0-")(p0) do
    # body not decompiled
  end

  def convert_tag(:a, [attr]) do
    case xmlAttribute(attr, :name) do
      :href ->
        val = xmlAttribute(attr, :value)
        case is_url(val) do
          true ->
            {:url, [attr]}
          false ->
            {:seealso, [xmlAttribute(attr, name: :marker)]}
        end
      :name ->
        {:marker, [xmlAttribute(attr, name: :id)]}
    end
  end

  def convert_tag(:b, attrs), do: {:em, attrs}

  def convert_tag(:blockquote, attrs), do: {:quote, attrs}

  def convert_tag(:code, attrs), do: {:c, attrs}

  def convert_tag(:dd, attrs), do: {:item, attrs}

  def convert_tag(:dl, attrs), do: {:taglist, attrs}

  def convert_tag(:dt, attrs), do: {:tag, attrs}

  def convert_tag(:li, attrs), do: {:item, attrs}

  def convert_tag(:ol, attrs), do: {:list, attrs}

  def convert_tag(:strong, attrs), do: {:em, attrs}

  def convert_tag(:td, attrs), do: {:cell, attrs}

  def convert_tag(:tr, attrs), do: {:row, attrs}

  def convert_tag(:tt, attrs), do: {:c, attrs}

  def convert_tag(:ul, attrs), do: {:list, attrs}

  def convert_tag(:underline, attrs), do: {:em, attrs}

  def convert_tag(tag, attrs), do: {tag, attrs}

  def is_url('http:' ++ _), do: true

  def is_url('../' ++ _), do: true

  def is_url(fileRef) do
    case :filename.extension(fileRef) do
      unknown_string ->
        false
      _Ext ->
        true
    end
  end
end
