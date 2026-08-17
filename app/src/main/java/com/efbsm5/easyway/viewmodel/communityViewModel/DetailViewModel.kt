package com.efbsm5.easyway.viewmodel.communityViewModel

import com.efbsm5.easyway.base.BaseViewModel
import com.efbsm5.easyway.contract.community.DetailContract
import com.efbsm5.easyway.data.UserManager
import com.efbsm5.easyway.data.models.PostComment
import com.efbsm5.easyway.data.models.assistModel.PostAndUser
import com.efbsm5.easyway.data.models.assistModel.PostCommentAndUser
import com.efbsm5.easyway.getCurrentFormattedTime
import com.efbsm5.easyway.repo.DataRepository

class DetailViewModel :
    BaseViewModel<DetailContract.Event, DetailContract.State, DetailContract.Effect>() {

    private var postLikePending = false
    private var commentSendPending = false
    private val pendingCommentReactions = mutableSetOf<Int>()
    private var nextTemporaryCommentId = -1

    fun setPostAndUser(postAndUser: PostAndUser) {
        handleEvents(DetailContract.Event.Load(postAndUser))
    }

    fun onEvent(event: DetailContract.Event) {
        // UI 事件按调用顺序处理，避免发送与关闭输入框等连续事件发生乱序。
        handleEvents(event)
    }

    override fun createInitialState(): DetailContract.State {
        return DetailContract.State()
    }

    override fun handleEvents(event: DetailContract.Event) {
        when (event) {
            is DetailContract.Event.Load -> load(event.postAndUser)
            is DetailContract.Event.ChangeInput -> setState { copy(input = event.value) }
            DetailContract.Event.SendComment -> send()
            is DetailContract.Event.ToggleLikePost -> onLikeClick()
            is DetailContract.Event.ShowInput -> setState { copy(showTextField = event.boolean) }
            is DetailContract.Event.ToggleDisLikeComment -> dislikeComment(event.index)
            is DetailContract.Event.ToggleLikeComment -> likeComment(event.index)
        }

    }

    private fun load(postAndUser: PostAndUser) = asyncLaunch {
        setState {
            copy(
                loading = true,
                user = postAndUser.user,
                post = postAndUser.post,
                error = null,
            )
        }
        DataRepository.getPostComments(postAndUser.post.id).onSuccess { comments ->
            setState { copy(loading = false, comments = comments) }
        }.onFailure {
            setState { copy(loading = false, error = "加载失败") }
            setEffect { DetailContract.Effect.Toast("加载失败") }
        }
    }

    private fun send() {
        val text = currentState.input.trim()
        if (text.isBlank() || commentSendPending) return
        val postId = currentState.post?.id ?: return
        commentSendPending = true
        val temporaryId = nextTemporaryCommentId--
        val tempComment = PostComment(
            index = temporaryId,
            postId = postId,
            userId = UserManager.userId,
            content = text,
            date = getCurrentFormattedTime()
        )
        val tempCommentAndUser = PostCommentAndUser(
            postComment = tempComment,
            user = UserManager.getUser(),
        )
        setState {
            copy(
                comments = comments + tempCommentAndUser,
                input = "",
                sending = true,
            )
        }
        asyncLaunch {
            DataRepository.uploadPostComment(tempComment)
                .onSuccess { saved ->
                    setState {
                        copy(
                            comments = comments.map { item ->
                                if (item.postComment.index == temporaryId) saved else item
                            },
                            sending = false,
                        )
                    }
                }
                .onFailure {
                    setState {
                        copy(
                            comments = comments.filterNot { item ->
                                item.postComment.index == temporaryId
                            },
                            input = text,
                            sending = false,
                        )
                    }
                    setEffect { DetailContract.Effect.Toast("发送失败") }
                }
            commentSendPending = false
        }
    }


    fun onLikeClick() {
        if (postLikePending) return
        val snapshot = currentState.post ?: return
        val updated = togglePostLike(snapshot)
        postLikePending = true
        setState { copy(post = updated) }

        asyncLaunch {
            DataRepository.setPostLike(
                postId = updated.id,
                likedByMe = updated.likedByMe,
                likeCount = updated.like,
            ).onFailure {
                setState { copy(post = snapshot) }
                setEffect { DetailContract.Effect.Toast("稍后重试") }
            }
            postLikePending = false
        }
    }


    fun likeComment(commentIndex: Int) {
        toggleComment(commentIndex, CommentReaction.Like)
    }


    fun dislikeComment(commentIndex: Int) {
        toggleComment(commentIndex, CommentReaction.Dislike)
    }

    private fun toggleComment(commentIndex: Int, reaction: CommentReaction) {
        val snapshot = currentState
        if (commentIndex !in snapshot.comments.indices) return

        val oldItem = snapshot.comments[commentIndex]
        val oldComment = oldItem.postComment
        val commentId = oldComment.index
        if (!pendingCommentReactions.add(commentId)) return

        val updatedComment = toggleCommentReaction(oldComment, reaction)
        val newList = snapshot.comments.toMutableList()
        newList[commentIndex] = oldItem.copy(
            postComment = updatedComment,
        )
        setState { copy(comments = newList) }

        asyncLaunch {
            DataRepository.setPostCommentReaction(updatedComment).onFailure {
                setState {
                    copy(
                        comments = comments.map { item ->
                            if (item.postComment.index == commentId) oldItem else item
                        },
                    )
                }
                setEffect { DetailContract.Effect.Toast("稍后重试") }
            }
            pendingCommentReactions.remove(commentId)
        }
    }

    fun back() {
        setEffect { DetailContract.Effect.Back }
    }

    fun List<PostCommentAndUser>.modifyCommentAt(
        index: Int, modify: (PostComment) -> PostComment
    ): List<PostCommentAndUser> {
        return this.mapIndexed { i, commentAndUser ->
            if (i == index) {
                commentAndUser.copy(postComment = modify(commentAndUser.postComment))
            } else {
                commentAndUser
            }
        }
    }
}
