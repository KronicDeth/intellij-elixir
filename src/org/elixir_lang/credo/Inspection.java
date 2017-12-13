package org.elixir_lang.credo;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ex.ExternalAnnotatorBatchInspection;

public class Inspection extends LocalInspectionTool implements ExternalAnnotatorBatchInspection {
    public static final String SHORT_NAME = "Credo";
}
