package io.github.mmauro94.common.client.entities

data class GetCommentsForm(
    val userPreferences: GetCommentsUserPreferenceInfo,
    val context: GetCommentsFormContextualInfo? = null,
    val page: GetCommentsFormPageInfo? = null,
    val maxDepth: Int? = null,
) {

    init {
        require(maxDepth == null || maxDepth > 0) { "Invalid max depth: $maxDepth" }
    }

    fun withIncreasedPage(): GetCommentsForm {
        check(page != null)
        return copy(page = page.increased())
    }
}

sealed interface GetCommentsFormContextualInfo {
    data class CommunityId(
        val communityId: Long,
    ) : GetCommentsFormContextualInfo

    data class CommunityName(
        val communityName: String,
    ) : GetCommentsFormContextualInfo

    data class PostId(
        val postId: Long,
    ) : GetCommentsFormContextualInfo

    data class ParentId(
        val commentId: Long,
    ) : GetCommentsFormContextualInfo
}

data class GetCommentsFormPageInfo(
    val page: Int,
    val limit: Int,
) {
    init {
        require(page > 0) { "Invalid page: $page" }
        require(limit > 0) { "Invalid limit: $limit" }
    }

    fun increased(): GetCommentsFormPageInfo {
        return copy(page = page + 1)
    }
}

data class GetCommentsUserPreferenceInfo(
    val savedOnly: Boolean? = null,
    val sort: CommentSortType? = null,
    val type: ListingType? = null,
)
