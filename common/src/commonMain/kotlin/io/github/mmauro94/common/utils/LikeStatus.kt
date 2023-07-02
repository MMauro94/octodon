package io.github.mmauro94.common.utils

enum class LikeStatus(val score: Int) {
    UPVOTED(1),
    NO_VOTE(0),
    DOWNVOTED(-1),
    ;

    fun computeNewStatus(action: LikeStatus): LikeStatus {
        return if (action == NO_VOTE || this == action) {
            NO_VOTE
        } else {
            action
        }
    }

    companion object {

        fun fromScore(score: Int?): LikeStatus {
            if (score == null) {
                return NO_VOTE
            }
            return LikeStatus.values().first { it.score == score }
        }
    }
}
