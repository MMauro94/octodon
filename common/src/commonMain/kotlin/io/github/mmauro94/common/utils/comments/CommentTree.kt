package io.github.mmauro94.common.utils.comments

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.mmauro94.common.client.entities.CommentView
import io.github.mmauro94.common.client.entities.PostView
import io.github.mmauro94.common.utils.Result

sealed class CommentTree {

    var collapsed by mutableStateOf(false)

    abstract val commentView: CommentView
    abstract val parent: CommentTree?
    abstract val children: MutableState<CommentList<Inner>>

    data class Root(
        override val commentView: CommentView,
        override val children: MutableState<CommentList<Inner>> = mutableStateOf(CommentList(emptyMap())),
    ) : CommentTree() {
        override val parent = null
    }

    data class Inner(
        override val parent: CommentTree,
        override val commentView: CommentView,
        override val children: MutableState<CommentList<Inner>> = mutableStateOf(CommentList(emptyMap())),
    ) : CommentTree()
}

private data class Node(
    val commentView: CommentView,
    var parent: Node? = null,
    val children: MutableMap<Long, Node> = mutableMapOf(),
) {
    fun toRootCommentTree(): CommentTree.Root {
        check(parent == null)
        return CommentTree.Root(commentView).also {
            setChildren(it)
        }
    }

    private fun setChildren(tree: CommentTree) {
        tree.children.value = CommentList(
            comments = this.children.mapValues { (_, value) ->
                value.toInnerCommentTree(tree)
            },
            state = Result.Success(
                // TODO if childCount is inclusive of all levels, this is wrong
                when (this.commentView.counts.childCount) {
                    this.children.size.toLong() -> CommentListState.FINISHED
                    else -> CommentListState.MORE_COMMENTS
                },
            ),
        )
    }

    private fun toInnerCommentTree(parent: CommentTree): CommentTree.Inner {
        require(this.parent?.commentView === parent.commentView)
        return CommentTree.Inner(parent, commentView).also {
            setChildren(it)
        }
    }
}

private fun List<CommentView>.toNodeMap(): MutableMap<Long, Node> {
    // Create a mutable map of CommentId -> CommentView
    val comments = associateByTo(mutableMapOf()) { it.comment.id }

    val nodes = mutableMapOf<Long, Node>()

    // Map of CommentId -> set of children ids
    val childrenIdsMap = mutableMapOf<Long, MutableSet<Long>>()

    // Function that given a CommentView creates the tree node with its parent, if found. The parent is removed from map.
    // It will also populate childrenIdsMap
    fun CommentView.toNode(): Node {
        // Either the parent node has already been created (is in nodes) or has to be created now
        val parent = nodes[comment.parentId] ?: comments.remove(this.comment.parentId)?.toNode()
        return Node(commentView = this, parent = parent).also {
            if (parent != null) {
                parent.children[it.commentView.comment.id] = it
            }
        }
    }

    // While we have comments in our map, we remove it and convert it to a Node
    // This while loop also populates the childrenIdsMap
    while (comments.isNotEmpty()) {
        val commentView = comments.remove(comments.keys.first())!!

        if (commentView.comment.parentId != null) {
            childrenIdsMap
                .getOrPut(commentView.comment.parentId) { mutableSetOf() }
                .add(commentView.comment.id)
        }
        nodes[commentView.comment.id] = commentView.toNode()
    }

    return nodes
}

fun List<CommentView>.toPostComments(postView: PostView): PostComments {
    val roots = toNodeMap()
        .filterValues {
            // Note: the parent == null && parentId != null case might manifest when the API returns some inner comment without returning
            // all of its parents. In this case we have no choice other than to ignore that comment.
            it.parent == null && it.commentView.comment.parentId == null
        }
        .mapValues { it.value.toRootCommentTree() }

    return PostComments(
        postView = postView,
        commentList = CommentList(roots),
    )
}
