defmodule Issue1796 do
  @type package_identifier() :: 0x0001..0xFFFF | nil
  @type octal_permission() :: 0o000..0o777
end
