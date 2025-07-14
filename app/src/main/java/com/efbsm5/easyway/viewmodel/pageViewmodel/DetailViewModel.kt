package com.efbsm5.easyway.viewmodel.pageViewmodel

import com.efbsm5.easyway.base.BaseViewModel
import com.efbsm5.easyway.contract.DetailContract
import com.efbsm5.easyway.data.UserManager
import com.efbsm5.easyway.data.models.PostComment
import com.efbsm5.easyway.data.models.assistModel.PostCommentAndUser
import com.efbsm5.easyway.getCurrentFormattedTime
import com.efbsm5.easyway.getInitPost
import com.efbsm5.easyway.getInitUser
import com.efbsm5.easyway.model.ImmutableListWrapper
import com.efbsm5.easyway.repo.DataRepository
import kotlinx.coroutines.Dispatchers

class DetailViewModel :
    BaseViewModel<DetailContract.Event, DetailContract.State, DetailContract.Effect>() {

    init {
        setEvent(DetailContract.Event.Loading)

    }

    override fun createInitialState(): DetailContract.State {
        return DetailContract.State(
            post = getInitPost(),
            user = getInitUser(),
            comments = ImmutableListWrapper(emptyList<PostCommentAndUser>()),
            commentString = "",
            error = null,
            showTextField = false
        )
    }

    override fun handleEvents(event: DetailContract.Event) {
        when (event) {
            DetailContract.Event.Loading -> {
                asyncLaunch(Dispatchers.IO) {
                    val user = DataRepository.getUserById(UserManager.userId)
                    val post = getInitPost()
                    val newComment = DataRepository.getPostAndComments(currentState.user.id)
                    setState {
                        copy(
                            user = user,
                            post = post,
                            comments = ImmutableListWrapper<PostCommentAndUser>(newComment)
                        )
                    }
                }
            }

            is DetailContract.Event.EditComment -> {
                setState { copy(commentString = event.string) }
            }

            DetailContract.Event.Upload -> {
                asyncLaunch {

                }
            }
        }
    }

    fun likePost(boolean: Boolean) {
        asyncLaunch(Dispatchers.IO) {
            if (boolean) {
                setState { copy(post.copy(like = post.like + 1)) }
                DataRepository.addLikeForPost(currentState.post.id)
            } else {
                setState { copy(post.copy(like = post.like + 1)) }
                DataRepository.decreaseLikeForPost(currentState.post.id)
            }
        }
    }

    fun likeComment(boolean: Boolean, commentIndex: Int) {
        asyncLaunch(Dispatchers.IO) {
            if (boolean) {
                val newList = ImmutableListWrapper(
                    currentState.comments.items.modifyCommentAt(
                        commentIndex,
                        modify = { comment ->
                            comment.copy(like = comment.like + 1)
                        },
                    )
                )
                setState { copy(comments = newList) }
                DataRepository.addLikeForPostComment(currentState.comments.items[commentIndex].postComment.index)
            } else {
                val newList = ImmutableListWrapper(
                    currentState.comments.items.modifyCommentAt(
                        commentIndex,
                        modify = { comment ->
                            comment.copy(like = comment.like - 1)
                        },
                    )
                )
                setState { copy(comments = newList) }
                DataRepository.decreaseLikeForPostComment(currentState.comments.items[commentIndex].postComment.index)
            }
        }
    }

    fun dislikeComment(boolean: Boolean, commentIndex: Int) {
        asyncLaunch(Dispatchers.IO) {
            if (boolean) {
                val newList = ImmutableListWrapper(
                    currentState.comments.items.modifyCommentAt(
                        commentIndex,
                        modify = { comment ->
                            comment.copy(like = comment.dislike + 1)
                        },
                    )
                )
                setState { copy(comments = newList) }
                DataRepository.addDisLikeForPostComment(currentState.comments.items[commentIndex].postComment.index)
            } else {
                val newList = ImmutableListWrapper(
                    currentState.comments.items.modifyCommentAt(
                        commentIndex,
                        modify = { comment ->
                            comment.copy(like = comment.dislike - 1)
                        },
                    )
                )
                setState { copy(comments = newList) }
                DataRepository.decreaseDisLikeForPostComment(currentState.comments.items[commentIndex].postComment.index)
            }
        }
    }

    fun comment(string: String) {
        if (currentState.commentString != null) {
            asyncLaunch(Dispatchers.IO) {
                val postComment = PostComment(
                    postId = currentState.post.id,
                    userId = UserManager.userId,
                    content = currentState.commentString ?: "",
                    like = 0,
                    dislike = 0,
                    date = getCurrentFormattedTime()
                )
                val user = DataRepository.getUserById(UserManager.userId)
                DataRepository.uploadPostComment(postComment)
                setState {
                    copy(
                        comments = ImmutableListWrapper(
                            currentState.comments.items + PostCommentAndUser(
                                postComment = postComment, user = user
                            )
                        ), commentString = ""
                    )
                }
            }
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

    fun changeShowTextField(boolean: Boolean) {
        setState { copy(showTextField = boolean) }
    }

}