package org.elixir_lang.psi.impl

import com.intellij.openapi.editor.Document
import com.intellij.psi.PsiElement


fun PsiElement.document(): Document? = containingFile.viewProvider.document
