package io.github.mmauro94.common.markdown

import org.intellij.markdown.IElementType
import org.intellij.markdown.ast.ASTNode

fun ASTNode.childOfType(type: IElementType, recursive: Boolean = false): ASTNode? {
    return children.firstNotNullOfOrNull {
        when {
            it.type == type -> it
            recursive -> it.childOfType(type, true)
            else -> null
        }
    }
}

fun ASTNode.stringContent(plaintext: String): String {
    return plaintext.substring(startOffset, endOffset)
}
