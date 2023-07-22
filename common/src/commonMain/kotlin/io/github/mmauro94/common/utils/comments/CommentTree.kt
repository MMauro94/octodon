package io.github.mmauro94.common.utils.comments

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.mmauro94.common.client.api.DEFAULT_COMMENTS_LIMIT
import io.github.mmauro94.common.client.entities.CommentView
import io.github.mmauro94.common.client.entities.GetCommentsForm
import io.github.mmauro94.common.client.entities.GetCommentsFormContextualInfo
import io.github.mmauro94.common.client.entities.GetCommentsFormPageInfo
import io.github.mmauro94.common.client.entities.GetCommentsUserPreferenceInfo

sealed class CommentTree {

    var collapsed by mutableStateOf(false)

    abstract val commentView: CommentView
    abstract val children: CommentList<Inner>

    data class Root(
        override val commentView: CommentView,
        override val children: CommentList<Inner> = InnerCommentList.emptyLoading(),
    ) : CommentTree()

    data class Inner(
        override val commentView: CommentView,
        override val children: CommentList<Inner> = InnerCommentList.emptyLoading(),
    ) : CommentTree()
}

private data class Node(
    val commentView: CommentView,
    var parent: Node? = null,
    val children: MutableMap<Long, Node> = mutableMapOf(),
) {
    fun size(): Long = 1 + children.values.sumOf { it.size() }

    fun childrenSize() = size() - 1

    fun toRootCommentTree(userPreferences: GetCommentsUserPreferenceInfo): CommentTree.Root {
        check(parent == null)
        return CommentTree.Root(commentView).also {
            setChildren(it, userPreferences)
        }
    }

    private fun setChildren(tree: CommentTree, userPreferences: GetCommentsUserPreferenceInfo) {
        tree.children.update(
            comments = this.children.mapValues { (_, value) ->
                value.toInnerCommentTree(userPreferences)
            },
            state = if (this.commentView.counts.childCount <= this.childrenSize()) {
                CommentListState.Finished
            } else {
                CommentListState.MoreComments(
                    GetCommentsForm(
                        context = GetCommentsFormContextualInfo.ParentId(commentView.comment.id),
                        userPreferences = userPreferences,
                        page = GetCommentsFormPageInfo(1, DEFAULT_COMMENTS_LIMIT),
                    ),
                )
            },
        )
    }

    fun toInnerCommentTree(userPreferences: GetCommentsUserPreferenceInfo): CommentTree.Inner {
        return CommentTree.Inner(commentView).also {
            setChildren(it, userPreferences)
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
    fun CommentView.createAndAddNode(): Node {
        // Either the parent node has already been created (is in nodes) or has to be created now
        val parent = nodes[comment.parentId] ?: comments.remove(this.comment.parentId)?.createAndAddNode()
        return Node(commentView = this, parent = parent).also {
            nodes[comment.id] = it
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
        commentView.createAndAddNode()
    }

    return nodes
}

fun List<CommentView>.toInnerComments(parentId: Long, userPreferences: GetCommentsUserPreferenceInfo): Map<Long, CommentTree.Inner> {
    return toNodeMap()
        .mapValues { it.value.toInnerCommentTree(userPreferences) }
        .filterValues { it.commentView.comment.parentId == parentId }
}

fun List<CommentView>.toRoots(userPreferences: GetCommentsUserPreferenceInfo): Map<Long, CommentTree.Root> {
    return toNodeMap()
        .filterValues {
            val comment = it.commentView.comment
            // Note: the parent == null && parentId != null case might manifest when the API returns some inner comment without returning
            // all of its parents. In this case we have no choice other than to ignore that comment.
            if (it.parent == null && comment.parentId != null) {
                System.err.println("Ignoring comment ${comment.id}: parent with id ${comment.parentId} not found")
            }
            it.parent == null && comment.parentId == null
        }
        .mapValues { it.value.toRootCommentTree(userPreferences) }
}
