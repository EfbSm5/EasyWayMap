package com.efbsm5.easyway.viewmodel.componentsViewmodel

import com.efbsm5.easyway.base.BaseViewModel
import com.efbsm5.easyway.contract.CommentAndHistoryCardContract
import com.efbsm5.easyway.data.UserManager
import com.efbsm5.easyway.data.models.EasyPoint
import com.efbsm5.easyway.data.models.PointComment
import com.efbsm5.easyway.data.models.User
import com.efbsm5.easyway.getCurrentFormattedTime
import com.efbsm5.easyway.getInitPoint
import com.efbsm5.easyway.model.ImmutableListWrapper
import com.efbsm5.easyway.repo.DataRepository
import kotlinx.coroutines.Dispatchers


class CommentAndHistoryCardViewModel() :
    BaseViewModel<CommentAndHistoryCardContract.Event, CommentAndHistoryCardContract.State, CommentAndHistoryCardContract.Effect>() {

    fun changeState(commentCardScreen: CommentCardScreen) {
        setState { copy(state = commentCardScreen) }
    }

    fun setPoint(easyPoint: EasyPoint) {
        setState { copy(point = easyPoint) }
        setState { copy(state = CommentCardScreen.Comment) }
    }

    fun changeCommentContent(string: String) {
        setState { copy(commentContent = string) }
    }

    override fun createInitialState(): CommentAndHistoryCardContract.State {
        return CommentAndHistoryCardContract.State(
            state = CommentCardScreen.Loading,
            point = getInitPoint(),
            pointComments = ImmutableListWrapper(emptyList()),
            commentContent = ""
        )
    }

    override fun handleEvents(event: CommentAndHistoryCardContract.Event) {
        when (event) {
            CommentAndHistoryCardContract.Event.Comment -> {
                publish()
            }

        }
    }

    fun publish() {
        asyncLaunch(Dispatchers.IO) {
            PointComment(
                pointId = currentState.point.pointId,
                userId = UserManager.userId,
                content = currentState.commentContent,
                date = getCurrentFormattedTime()
            ).let {
                DataRepository.uploadPointComment(
                    comment = it
                )
                setState { copy() }
            }


            _pointComments.value = _pointComments.value.plus(
                Pair<PointComment, User>(
                    first = pointComment, second = repository.getUserById(userManager.userId)
                )
            )
        }
    }


    fun likePost(boolean: Boolean) {
        asyncLaunch(Dispatchers.IO) {
            if (boolean) {
                _point.value = _point.value.copy(likes = _point.value.likes + 1)
                DataRepository.addLikeForPoint(point.value.pointId)
            } else {
                _point.value = _point.value.copy(likes = _point.value.likes - 1)
                DataRepository.decreaseLikeForPoint(point.value.pointId)
            }
        }
    }

    fun dislikePost(boolean: Boolean) {
        asyncLaunch(Dispatchers.IO) {
            if (boolean) {
                _point.value = _point.value.copy(dislikes = _point.value.dislikes + 1)
                DataRepository.addDisLikeForPoint(point.value.pointId)
            } else {
                _point.value = _point.value.copy(dislikes = _point.value.dislikes - 1)
                DataRepository.decreaseDisLikeForPoint(point.value.pointId)
            }
        }
    }

    fun likeComment(commentIndex: Int, boolean: Boolean) {
        asyncLaunch(Dispatchers.IO) {
            if (boolean) {
                _pointComments.value.find { commentAndUser ->
                    commentAndUser.first.index == commentIndex
                }!!.first.like + 1
                DataRepository.addDisLikeForPointComment(commentIndex)
            } else {
                _pointComments.value.find { commentAndUser ->
                    commentAndUser.first.index == commentIndex
                }!!.first.like - 1
                DataRepository.decreaseLikeForPointComment(commentIndex)
            }
        }
    }

    fun dislikeComment(commentIndex: Int, boolean: Boolean) {
        asyncLaunch(Dispatchers.IO) {
            if (boolean) {
                _pointComments.value.find { commentAndUser ->
                    commentAndUser.first.index == commentIndex
                }!!.first.dislike + 1
                DataRepository.addDisLikeForPointComment(commentIndex)
            } else {
                _pointComments.value.find { commentAndUser ->
                    commentAndUser.first.index == commentIndex
                }!!.first.dislike - 1
                DataRepository.decreaseDisLikeForPointComment(commentIndex)
            }
        }
    }


}

sealed interface CommentCardScreen {
    data object Loading : CommentCardScreen
    data object Comment : CommentCardScreen
    data object History : CommentCardScreen
}