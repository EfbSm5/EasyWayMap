package com.efbsm5.easyway.viewmodel.pageViewmodel

import com.efbsm5.easyway.base.BaseViewModel
import com.efbsm5.easyway.contract.DetailContract
import com.efbsm5.easyway.data.UserManager
import com.efbsm5.easyway.data.models.Post
import com.efbsm5.easyway.data.models.PostComment
import com.efbsm5.easyway.data.models.assistModel.PostCommentAndUser
import com.efbsm5.easyway.getCurrentFormattedTime
import com.efbsm5.easyway.getInitPost
import com.efbsm5.easyway.getInitUser
import com.efbsm5.easyway.model.ImmutableListWrapper
import com.efbsm5.easyway.repo.DataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow

class DetailViewModel :
    BaseViewModel<DetailContract.Event, DetailContract.State, DetailContract.Effect>() {
    private val _post = MutableStateFlow<Post>(getInitPost())
    private var _postUser = MutableStateFlow(getInitUser())
    private val _comment = MutableStateFlow(emptyList<PostCommentAndUser>())
    private val _commentString = MutableStateFlow<String?>(null)


    init {
        setEvent(DetailContract.Event.Loading)
    }

    override fun createInitialState(): DetailContract.State {
        return DetailContract.State(
            post = _post.value,
            user = _postUser.value,
            comments = ImmutableListWrapper(_comment.value),
            commentString = _commentString.value,
            error = null
        )
    }

    override fun handleEvents(event: DetailContract.Event) {
        when (event) {
            DetailContract.Event.Loading -> {
                asyncLaunch(Dispatchers.IO) {
                    _comment.value = DataRepository.getPostAndComments(_post.value.id)
                    setState { copy(comments = ImmutableListWrapper<PostCommentAndUser>(_comment.value)) }
                }
                setEvent(DetailContract.Event.Loaded)
            }

            is DetailContract.Event.Editing -> {
                _commentString.value = event.string
                setEvent(DetailContract.Event.Loaded)
            }

            DetailContract.Event.Loaded -> {
                setState {
                    copy(
                        post = _post.value,
                        user = _postUser.value,
                        comments = ImmutableListWrapper(_comment.value),
                        commentString = _commentString.value,
                        error = null
                    )
                }
            }
        }
    }

    fun likePost(boolean: Boolean) {
        asyncLaunch(Dispatchers.IO) {
            if (boolean) {
                _post.value.like + 1
                DataRepository.addLikeForPost(_post.value.id)
            } else {
                _post.value.like - 1
                DataRepository.decreaseLikeForPost(_post.value.id)
            }
        }
    }

    fun likeComment(boolean: Boolean, commentIndex: Int) {
        asyncLaunch(Dispatchers.IO) {
            if (boolean) {
                _comment.value[commentIndex].postComment.like++
                setState { copy(comments = ImmutableListWrapper(_comment.value)) }
                DataRepository.addLikeForPostComment(_comment.value[commentIndex].postComment.index)
            } else {
                _comment.value[commentIndex].postComment.like--
                setState { copy(comments = ImmutableListWrapper(_comment.value)) }
                DataRepository.decreaseLikeForPostComment(_comment.value[commentIndex].postComment.index)
            }
        }
    }

    fun dislikeComment(boolean: Boolean, commentIndex: Int) {
        asyncLaunch(Dispatchers.IO) {
            if (boolean) {
                _comment.value[commentIndex].postComment.dislike++
                setState { copy(comments = ImmutableListWrapper(_comment.value)) }
                DataRepository.addDisLikeForPostComment(_comment.value[commentIndex].postComment.index)
            } else {
                _comment.value[commentIndex].postComment.like--
                setState { copy(comments = ImmutableListWrapper(_comment.value)) }
                DataRepository.decreaseDisLikeForPostComment(_comment.value[commentIndex].postComment.index)
            }
        }
    }

    fun comment(string: String) {
        if (_commentString.value != null) asyncLaunch(Dispatchers.IO) {
            val postComment = PostComment(
                postId = _post.value.id,
                userId = UserManager.userId,
                content = _commentString.value!!,
                like = 0,
                dislike = 0,
                date = getCurrentFormattedTime()
            )
            DataRepository.uploadPostComment(postComment)
            _comment.value.toMutableList().add(
                PostCommentAndUser(
                    postComment = postComment, user = DataRepository.getUserById(UserManager.userId)
                )
            )
            setEvent(DetailContract.Event.Loaded)
        }
    }
}