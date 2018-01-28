package org.elixir_lang.beam.assembly

import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import org.elixir_lang.beam.assembly.psi.Types

private val FILE: IFileElementType = IFileElementType(Language)

class ParserDefinition : com.intellij.lang.ParserDefinition {
    override fun createElement(node: ASTNode?): PsiElement = Types.Factory.createElement(node)
    override fun createFile(viewProvider: FileViewProvider): PsiFile = File(viewProvider)
    override fun createLexer(project: Project?): Lexer = org.elixir_lang.beam.assembly.FlexLexerAdapter()
    override fun createParser(project: Project?): PsiParser = BEAMAssemblyParser()
    override fun getCommentTokens(): TokenSet = TokenSet.EMPTY
    override fun getFileNodeType(): IFileElementType = FILE
    override fun getStringLiteralElements(): TokenSet = TokenSet.EMPTY
    override fun spaceExistanceTypeBetweenTokens(left: ASTNode?, right: ASTNode?): ParserDefinition.SpaceRequirements =
            ParserDefinition.SpaceRequirements.MAY
}
