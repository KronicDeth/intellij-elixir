package org.elixir_lang.debugger.stack_frame.value;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.icons.AllIcons;

import javax.swing.*;

class PrimitiveBase<T extends OtpErlangObject> extends Base<T> {
  PrimitiveBase(T value) {
    super(value);
  }

  @Override
  protected Icon getIcon() {
    return AllIcons.Debugger.Db_primitive;
  }
}
