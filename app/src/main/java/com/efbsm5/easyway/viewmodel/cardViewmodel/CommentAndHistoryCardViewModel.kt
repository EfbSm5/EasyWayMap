package com.efbsm5.easyway.viewmodel.cardViewmodel

import com.efbsm5.easyway.base.BaseViewModel
import com.efbsm5.easyway.contract.card.CommentAndHistoryCardContract
import com.efbsm5.easyway.data.UserManager
import com.efbsm5.easyway.data.models.EasyPoint
import com.efbsm5.easyway.data.models.PointComment
import com.efbsm5.easyway.getCurrentFormattedTime
import com.efbsm5.easyway.getInitPoint
import com.efbsm5.easyway.model.ImmutableListWrapper
import com.efbsm5.easyway.repo.DataRepository
import kotlinx.coroutines.Dispatchers


class CommentAndHistoryCardViewModel() :
    BaseViewModel<CommentAndHistoryCardContract.Event, CommentAndHistoryCardContract.State, CommentAndHistoryCardContract.Effect>() {

    fun setPoint(easyPoint: EasyPoint) {
        setState { copy(point = easyPoint) }
        setState { copy(state = CommentCardScreen.Comment) }
    }

    fun changeCommentContent(string: String) {
        setState { copy(commentContent = string) }
    }

    fun update() {
        setEffect {
            CommentAndHistoryCardContract.Effect.Update
        }
    }

    fun select(index: Int) {
        setState {
            copy(
                state = if (index == 0) CommentCardScreen.Comment
                else CommentCardScreen.History
            )
        }
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


            is CommentAndHistoryCardContract.Event.ChangeComment -> TODO()
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
//                setState { copy() }
            }
        }
    }


    fun likePost(boolean: Boolean) {
        asyncLaunch(Dispatchers.IO) {
            if (boolean) {
                setState { copy(point = point.copy(likes = point.likes + 1)) }
                DataRepository.addLikeForPoint(currentState.point.pointId)
            } else {
                setState { copy(point = point.copy(likes = point.likes - 1)) }
                DataRepository.decreaseLikeForPoint(currentState.point.pointId)
            }
        }
    }

    fun dislikePost(boolean: Boolean) {
        asyncLaunch(Dispatchers.IO) {
            if (boolean) {
                setState { copy(point = point.copy(dislikes = point.dislikes + 1)) }
                DataRepository.addDisLikeForPoint(currentState.point.pointId)
            } else {
                setState { copy(point = point.copy(dislikes = point.dislikes - 1)) }
                DataRepository.decreaseDisLikeForPoint(currentState.point.pointId)
            }
        }
    }

    fun likeComment(commentIndex: Int, boolean: Boolean) {
        asyncLaunch(Dispatchers.IO) {
            if (boolean) {
//               currentState.pointComments.items.find { commentAndUser ->
//                    commentAndUser.pointComment.index == commentIndex
//                }!!
                DataRepository.addDisLikeForPointComment(commentIndex)
            } else {
//                _pointComments.value.find { commentAndUser ->
//                    commentAndUser.first.index == commentIndex
//                }!!.first.like - 1
                DataRepository.decreaseLikeForPointComment(commentIndex)
            }
        }
    }

    fun dislikeComment(commentIndex: Int, boolean: Boolean) {
        asyncLaunch(Dispatchers.IO) {
            if (boolean) {
//                _pointComments.value.find { commentAndUser ->
//                    commentAndUser.first.index == commentIndex
//                }!!.first.dislike + 1
                DataRepository.addDisLikeForPointComment(commentIndex)
            } else {
//                _pointComments.value.find { commentAndUser ->
//                    commentAndUser.first.index == commentIndex
//                }!!.first.dislike - 1
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