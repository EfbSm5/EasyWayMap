package com.efbsm5.easyway.viewmodel.communityViewModel

import com.efbsm5.easyway.base.BaseViewModel
import com.efbsm5.easyway.contract.community.DetailContract
import com.efbsm5.easyway.data.UserManager
import com.efbsm5.easyway.data.models.PostComment
import com.efbsm5.easyway.data.models.User
import com.efbsm5.easyway.data.models.assistModel.PostAndUser
import com.efbsm5.easyway.data.models.assistModel.PostCommentAndUser
import com.efbsm5.easyway.getCurrentFormattedTime
import com.efbsm5.easyway.repo.DataRepository
import kotlinx.coroutines.Dispatchers

class DetailViewModel :
    BaseViewModel<DetailContract.Event, DetailContract.State, DetailContract.Effect>() {

    fun setPostAndUser(postAndUser: PostAndUser) {
        setEvent(DetailContract.Event.Load(postAndUser))
    }

    fun onEvent(event: DetailContract.Event) {
        setEvent(event)
    }

    override fun createInitialState(): DetailContract.State {
        return DetailContract.State()
    }

    override fun handleEvents(event: DetailContract.Event) {
        when (event) {
            is DetailContract.Event.Load -> load()
            is DetailContract.Event.ChangeInput -> setState { copy(input = event.value) }
            DetailContract.Event.SendComment -> send()
            is DetailContract.Event.ToggleLikePost -> onLikeClick()
            is DetailContract.Event.ShowInput -> setState { copy(showTextField = event.boolean) }
            is DetailContract.Event.ToggleDisLikeComment -> dislikeComment(event.index)
            is DetailContract.Event.ToggleLikeComment -> likeComment(event.index)
        }

    }

    private fun load() = asyncLaunch(Dispatchers.IO) {
        setState { copy(loading = true) }
        currentState.post?.let {
            DataRepository.getPostComments(it.id).onSuccess { comments ->
                setState { copy(loading = false, comments = comments) }
            }.onFailure {
                setState { copy(loading = false, error = "加载失败") }
                setEffect { DetailContract.Effect.Toast("加载失败") }
            }
        }
    }

    private fun send() = asyncLaunch(Dispatchers.IO) {
        val text = currentState.input.trim()
        if (text.isBlank()) return@asyncLaunch
        setState { copy(sending = true) }
        val oldComments = currentState.comments
        val tempComment = PostComment(
            postId = currentState.post?.id ?: 0,
            userId = UserManager.userId,
            content = text,
            date = getCurrentFormattedTime()
        )
        val tempCommentAndUser = PostCommentAndUser(
            postComment = tempComment, user = User(
                UserManager.userId, UserManager.name, UserManager.avatar
            )
        )
        setState { copy(comments = oldComments + tempCommentAndUser, input = "") }
        runCatching { DataRepository.uploadPostComment(tempComment) }.onFailure {
            setState { copy(comments = oldComments) }
            setEffect { (DetailContract.Effect.Toast("发送失败")) }
        }
        setState { copy(sending = false) }
    }


    fun onLikeClick() {
        val snapshot = currentState.post
        val targetLiked = !snapshot!!.likedByMe
        val delta = if (targetLiked) 1 else -1
        setEffect { DetailContract.Effect.Liked(targetLiked) }
        // 1. 乐观
        setState {
            copy(
                post = snapshot.copy(
                    likedByMe = targetLiked, like = (snapshot.like + delta).coerceAtLeast(0)
                )
            )
        }
        // 2. 异步请求
        asyncLaunch(Dispatchers.IO) {
            val res = runCatching {
                if (targetLiked) DataRepository.addLikeForPost(snapshot.id)
                else DataRepository.decreaseLikeForPost(snapshot.id)
            }
            res.onFailure {
                // 3. 回滚
                setState { copy(post = snapshot) }
                setEffect { DetailContract.Effect.Toast("稍后重试") }
            }.onSuccess { serverPostOrCount ->
                // 4. 可选：校正
                // 如果服务器返回最终 likeCount 和 liked，优先使用服务端
                setState {
                    copy(
                        post = post!!.copy(
                            like = snapshot.like + 1,
                            likedByMe = !snapshot.likedByMe
                        )
                    )
                }
            }
        }
    }


    fun likeComment(commentIndex: Int) {
        val snapshot = currentState
        if (commentIndex !in snapshot.comments.indices) return

        val oldItem = snapshot.comments[commentIndex]
        val oldComment = oldItem.postComment
        val commentId = oldComment.index

        // 如果该评论的点赞操作正在进行，直接忽略（阻止并发）
//        if (snapshot.likeOps[commentId] is LikeOpState.Working) return

        val targetLiked = !oldComment.likedByMe
        val delta = if (targetLiked) 1 else -1
        val newLikeCount = (oldComment.like + delta).coerceAtLeast(0)

        // 1. 乐观更新列表
        val newList = snapshot.comments.toMutableList()
        newList[commentIndex] = oldItem.copy(
            postComment = oldComment.copy(
                likedByMe = targetLiked, like = newLikeCount
            )
        )

        // 2. 写入状态（标记该评论操作中）
        setState {
            copy(
                comments = newList,
//                likeOps = it.likeOps + (commentId to LikeOpState.Working)
            )
        }

        // 3. 发起异步请求
        asyncLaunch {
            runCatching {
                if (targetLiked) DataRepository.addLikeForPostComment(commentId)
                else DataRepository.decreaseLikeForPostComment(commentId)
            }

//            result.onSuccess { server ->
//                // 4. 用服务器值校正（如果服务器返回）
//                _state.update { cur ->
//                    val idx = cur.comments.indexOfFirst { it.postComment.id == commentId }
//                    if (idx == -1) return@update cur // 已被移除
//                    val currentItem = cur.comments[idx]
//                    val corrected = currentItem.copy(
//                        postComment = currentItem.postComment.copy(
//                            like = server.getOrNull()?.like ?: currentItem.postComment.like,
//                            liked = server.getOrNull()?.liked ?: currentItem.postComment.liked
//                        )
//                    )
//                    cur.copy(
//                        comments = cur.comments.toMutableList().apply { this[idx] = corrected },
//                        likeOps = cur.likeOps - commentId // 回归 Idle
//                    )
//                }
//            }.onFailure { e ->
//                // 5. 回滚（恢复 oldComment）
//                _state.update { cur ->
//                    val idx = cur.comments.indexOfFirst { it.postComment.id == commentId }
//                    if (idx == -1) return@update cur
//                    val rollbackList = cur.comments.toMutableList()
//                    rollbackList[idx] = oldItem // 直接还原旧 item
//                    cur.copy(
//                        comments = rollbackList,
////                        likeOps = cur.likeOps + (commentId to LikeOpState.Error(
////                            e.message ?: "失败"
////                        )
////                    )
//                    )
//                }

        }
    }


    fun dislikeComment(commentIndex: Int) {
        val snapshot = currentState
        if (commentIndex !in snapshot.comments.indices) return

        val oldItem = snapshot.comments[commentIndex]
        val oldComment = oldItem.postComment
        val commentId = oldComment.index

        // 如果该评论的点赞操作正在进行，直接忽略（阻止并发）
//        if (snapshot.likeOps[commentId] is LikeOpState.Working) return

        val targetLiked = !oldComment.likedByMe
        val delta = if (targetLiked) 1 else -1
        val newLikeCount = (oldComment.like + delta).coerceAtLeast(0)

        // 1. 乐观更新列表
        val newList = snapshot.comments.toMutableList()
        newList[commentIndex] = oldItem.copy(
            postComment = oldComment.copy(
                likedByMe = targetLiked, like = newLikeCount
            )
        )

        // 2. 写入状态（标记该评论操作中）
        setState {
            copy(
                comments = newList,
//                likeOps = it.likeOps + (commentId to LikeOpState.Working)
            )
        }

        // 3. 发起异步请求
        asyncLaunch {
            runCatching {
                if (targetLiked) DataRepository.addDisLikeForPostComment(commentId)
                else DataRepository.decreaseDisLikeForPostComment(commentId)
            }

//            result.onSuccess { server ->
//                // 4. 用服务器值校正（如果服务器返回）
//                _state.update { cur ->
//                    val idx = cur.comments.indexOfFirst { it.postComment.id == commentId }
//                    if (idx == -1) return@update cur // 已被移除
//                    val currentItem = cur.comments[idx]
//                    val corrected = currentItem.copy(
//                        postComment = currentItem.postComment.copy(
//                            like = server.getOrNull()?.like ?: currentItem.postComment.like,
//                            liked = server.getOrNull()?.liked ?: currentItem.postComment.liked
//                        )
//                    )
//                    cur.copy(
//                        comments = cur.comments.toMutableList().apply { this[idx] = corrected },
//                        likeOps = cur.likeOps - commentId // 回归 Idle
//                    )
//                }
//            }.onFailure { e ->
//                // 5. 回滚（恢复 oldComment）
//                _state.update { cur ->
//                    val idx = cur.comments.indexOfFirst { it.postComment.id == commentId }
//                    if (idx == -1) return@update cur
//                    val rollbackList = cur.comments.toMutableList()
//                    rollbackList[idx] = oldItem // 直接还原旧 item
//                    cur.copy(
//                        comments = rollbackList,
////                        likeOps = cur.likeOps + (commentId to LikeOpState.Error(
////                            e.message ?: "失败"
////                        )
////                    )
//                    )
//                }
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