package com.efbsm5.easyway.viewmodel.pageViewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.efbsm5.easyway.data.UserManager
import com.efbsm5.easyway.data.models.Comment
import com.efbsm5.easyway.data.models.DynamicPost
import com.efbsm5.easyway.data.models.User
import com.efbsm5.easyway.data.repository.DataRepository
import com.efbsm5.easyway.getCurrentFormattedTime
import com.efbsm5.easyway.getInitUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetailPageViewModel(
    val repository: DataRepository, val userManager: UserManager, dynamicPost: DynamicPost
) : ViewModel() {
    private val _dynamicPost = MutableStateFlow<DynamicPost>(dynamicPost)
    private var _postUser = MutableStateFlow(getInitUser())
    private val _commentAndUsers =
        MutableStateFlow(emptyList<Pair<Comment, User>>().toMutableList())
    val postUser: StateFlow<User> = _postUser
    val commentAndUser: StateFlow<MutableList<Pair<Comment, User>>> = _commentAndUsers
    val post: StateFlow<DynamicPost?> = _dynamicPost


    init {
        getData()
    }

    private fun getData() {
        viewModelScope.launch(Dispatchers.IO) {
            _postUser.value = repository.getUserById(_dynamicPost.value.userId)
            repository.getAllCommentsById(commentId = _dynamicPost.value.commentId)
                .collect { comments ->
                    _commentAndUsers.value.clear()
                    comments.forEach {
                        _commentAndUsers.value.add(
                            Pair<Comment, User>(
                                it, repository.getUserById(it.userId)
                            )
                        )
                    }
                }

        }
    }

    fun likePost(boolean: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            if (boolean) {
                _dynamicPost.value.like + 1
                repository.addLikeForPost(_dynamicPost.value.id)
            } else {
                _dynamicPost.value.like - 1
                repository.decreaseLikeForPost(_dynamicPost.value.id)
            }
        }
    }

    fun likeComment(boolean: Boolean, commentIndex: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            if (boolean) {
                _commentAndUsers.value.find { commentAndUser ->
                    commentAndUser.first.index == commentIndex
                }!!.first.like + 1
                repository.addLikeForComment(commentIndex)
            } else {
                _commentAndUsers.value.find { commentAndUser ->
                    commentAndUser.first.index == commentIndex
                }!!.first.like - 1
                repository.decreaseLikeForComment(commentIndex)
            }
        }
    }

    fun dislikeComment(boolean: Boolean, commentIndex: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            if (boolean) {
                _commentAndUsers.value.find { commentAndUser ->
                    commentAndUser.first.index == commentIndex
                }!!.first.dislike + 1
                repository.addDisLikeForComment(commentIndex)
            } else {
                _commentAndUsers.value.find { commentAndUser ->
                    commentAndUser.first.index == commentIndex
                }!!.first.dislike - 1
                repository.decreaseDisLikeForComment(commentIndex)
            }
        }
    }

    fun comment(string: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val comment = Comment(
                index = repository.getCommentCount() + 1,
                commentId = _dynamicPost.value.commentId,
                userId = userManager.userId,
                content = string,
                like = 0,
                dislike = 0,
                date = getCurrentFormattedTime()
            )
            repository.uploadComment(comment)
            _commentAndUsers.value.add(
                Pair<Comment, User>(
                    comment, repository.getUserById(comment.userId)
                )
            )
        }
    }
}