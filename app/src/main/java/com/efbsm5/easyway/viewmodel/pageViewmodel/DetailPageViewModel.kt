package com.efbsm5.easyway.viewmodel.pageViewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.efbsm5.easyway.data.UserManager
import com.efbsm5.easyway.data.models.PointComment
import com.efbsm5.easyway.data.models.Post
import com.efbsm5.easyway.data.models.User
import com.efbsm5.easyway.repo.DataRepository
import com.efbsm5.easyway.getCurrentFormattedTime
import com.efbsm5.easyway.getInitUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetailPageViewModel(
    val repository: DataRepository, val userManager: UserManager, dynamicPost: Post
) : ViewModel() {
    private val _dynamicPost = MutableStateFlow<Post>(dynamicPost)
    private var _postUser = MutableStateFlow(getInitUser())
    private val _Point_commentAndUsers =
        MutableStateFlow(emptyList<Pair<PointComment, User>>().toMutableList())
    val postUser: StateFlow<User> = _postUser
    val pointCommentAndUser: StateFlow<MutableList<Pair<PointComment, User>>> = _Point_commentAndUsers
    val post: StateFlow<Post?> = _dynamicPost


    init {
        getData()
    }

    private fun getData() {
        viewModelScope.launch(Dispatchers.IO) {
            _postUser.value = repository.getUserById(_dynamicPost.value.userId)
            repository.getAllCommentsById(commentId = _dynamicPost.value.commentId)
                .collect { comments ->
                    _Point_commentAndUsers.value.clear()
                    comments.forEach {
                        _Point_commentAndUsers.value.add(
                            Pair<PointComment, User>(
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
                _Point_commentAndUsers.value.find { commentAndUser ->
                    commentAndUser.first.index == commentIndex
                }!!.first.like + 1
                repository.addLikeForComment(commentIndex)
            } else {
                _Point_commentAndUsers.value.find { commentAndUser ->
                    commentAndUser.first.index == commentIndex
                }!!.first.like - 1
                repository.decreaseLikeForComment(commentIndex)
            }
        }
    }

    fun dislikeComment(boolean: Boolean, commentIndex: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            if (boolean) {
                _Point_commentAndUsers.value.find { commentAndUser ->
                    commentAndUser.first.index == commentIndex
                }!!.first.dislike + 1
                repository.addDisLikeForComment(commentIndex)
            } else {
                _Point_commentAndUsers.value.find { commentAndUser ->
                    commentAndUser.first.index == commentIndex
                }!!.first.dislike - 1
                repository.decreaseDisLikeForComment(commentIndex)
            }
        }
    }

    fun comment(string: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val pointComment = PointComment(
                index = repository.getCommentCount() + 1,
                commentId = _dynamicPost.value.commentId,
                userId = userManager.userId,
                content = string,
                like = 0,
                dislike = 0,
                date = getCurrentFormattedTime()
            )
            repository.uploadComment(pointComment)
            _Point_commentAndUsers.value.add(
                Pair<PointComment, User>(
                    pointComment, repository.getUserById(pointComment.userId)
                )
            )
        }
    }
}