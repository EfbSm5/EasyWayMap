package com.efbsm5.easyway.viewmodel.componentsViewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.efbsm5.easyway.data.UserManager
import com.efbsm5.easyway.data.models.Comment
import com.efbsm5.easyway.data.models.EasyPoint
import com.efbsm5.easyway.data.models.User
import com.efbsm5.easyway.data.repository.DataRepository
import com.efbsm5.easyway.getCurrentFormattedTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class CommentAndHistoryCardViewModel(
    val repository: DataRepository,
    val userManager: UserManager,
    initialPoint: EasyPoint,
) : ViewModel() {
    private var _state = MutableStateFlow<CommentCardScreen>(CommentCardScreen.Comment)
    private val _point = MutableStateFlow(initialPoint)
    private var _pointComments = MutableStateFlow<List<Pair<Comment, User>>>(emptyList())

    val point: StateFlow<EasyPoint> = _point
    val state: StateFlow<CommentCardScreen> = _state
    val pointComments: StateFlow<List<Pair<Comment, User>>> = _pointComments

    fun changeState(commentCardScreen: CommentCardScreen) {
        _state.value = commentCardScreen
    }

    fun publish(string: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val comment = Comment(
                index = repository.getCommentCount(),
                commentId = _point.value.commentId,
                userId = userManager.userId,
                content = string,
                like = 0,
                dislike = 0,
                date = getCurrentFormattedTime()
            )
            repository.uploadComment(
                comment = comment
            )
            _pointComments.value = _pointComments.value.plus(
                Pair<Comment, User>(
                    first = comment, second = repository.getUserById(userManager.userId)
                )
            )
        }
    }


    fun likePost(boolean: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            if (boolean) {
                _point.value = _point.value.copy(likes = _point.value.likes + 1)
                repository.addLikeForPoint(point.value.pointId)
            } else {
                _point.value = _point.value.copy(likes = _point.value.likes - 1)
                repository.decreaseLikeForPoint(point.value.pointId)
            }
        }
    }

    fun dislikePost(boolean: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            if (boolean) {
                _point.value = _point.value.copy(dislikes = _point.value.dislikes + 1)
                repository.addDisLikeForPoint(point.value.pointId)
            } else {
                _point.value = _point.value.copy(dislikes = _point.value.dislikes - 1)
                repository.decreaseDisLikeForPoint(point.value.pointId)
            }
        }
    }

    fun likeComment(commentIndex: Int, boolean: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            if (boolean) {
                _pointComments.value.find { commentAndUser ->
                    commentAndUser.first.index == commentIndex
                }!!.first.like + 1
                repository.addLikeForComment(commentIndex)
            } else {
                _pointComments.value.find { commentAndUser ->
                    commentAndUser.first.index == commentIndex
                }!!.first.like - 1
                repository.decreaseLikeForComment(commentIndex)
            }
        }
    }

    fun dislikeComment(commentIndex: Int, boolean: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            if (boolean) {
                _pointComments.value.find { commentAndUser ->
                    commentAndUser.first.index == commentIndex
                }!!.first.dislike + 1
                repository.addDisLikeForComment(commentIndex)
            } else {
                _pointComments.value.find { commentAndUser ->
                    commentAndUser.first.index == commentIndex
                }!!.first.dislike - 1
                repository.decreaseDisLikeForComment(commentIndex)
            }
        }
    }
}

sealed interface CommentCardScreen {
    data object Comment : CommentCardScreen
    data object History : CommentCardScreen
}