package com.efbsm5.easyway.viewmodel

import android.net.Uri
import com.efbsm5.easyway.base.BaseViewModel
import com.efbsm5.easyway.contract.community.NewContract
import com.efbsm5.easyway.data.UserManager
import com.efbsm5.easyway.data.models.PostComment
import com.efbsm5.easyway.data.models.User
import com.efbsm5.easyway.data.models.assistModel.PostAndUser
import com.efbsm5.easyway.data.models.assistModel.PostCommentAndUser
import com.efbsm5.easyway.getCurrentFormattedTime
import com.efbsm5.easyway.repo.DataRepository
import kotlinx.coroutines.Dispatchers

class NewViewModel : BaseViewModel<NewContract.Event, NewContract.State, NewContract.Effect>() {
    override fun createInitialState(): NewContract.State = NewContract.State()
    override fun handleEvents(event: NewContract.Event) {
        when (event) {
            is NewContract.Event.EditContent -> {
                setState { copy(newPost = newPost.copy(content = event.string)) }
            }

            is NewContract.Event.EditTitle -> {
                setState { copy(newPost = newPost.copy(title = event.string)) }
            }

            is NewContract.Event.PickPhotoDialogResult -> {
                setState { copy(previewPhoto = event.uri) }
            }

            NewContract.Event.Publish -> {
                publish()
            }

            is NewContract.Event.SelectedCategory -> {
                setState { copy(onSelectedCategory = event.int) }
            }

            is NewContract.Event.TitleChanged -> {
                setState { copy(newPost = newPost.copy(title = event.string)) }
            }

            is NewContract.Event.Load -> load()
            is NewContract.Event.ChangeInput -> setState { copy(input = event.value) }
            NewContract.Event.SendComment -> send()
            is NewContract.Event.ToggleLikePost -> onLikeClick()
            is NewContract.Event.ShowInput -> setState { copy(showTextField = event.boolean) }
            is NewContract.Event.ToggleDisLikeComment -> dislikeComment(event.index)
            is NewContract.Event.ToggleLikeComment -> likeComment(event.index)
            NewContract.Event.ToggleLikePost -> TODO()
            is NewContract.Event.ClickPost -> {
                setEffect { NewContract.Effect.SelectedPost(event.postAndUser) }
            }

            is NewContract.Event.EditText -> {
                setState { copy(searchText = event.string) }
            }

            NewContract.Event.Submit -> submit()
            is NewContract.Event.TabSelect -> {
                setState { copy(selectedTab = event.int) }
            }
        }
    }

    fun onEffect(effect: NewContract.Effect) {
        setEffect { effect }
    }

    fun getPicture(uri: Uri?) {
        if (uri != null) {
            setState { copy(newPost = newPost.copy(photo = newPost.photo.plus(uri.toString()))) }
        } else {
            setState { copy(error = "error") }
        }
    }

    fun setLocation(location: String) {
        setState { copy(newPost = newPost.copy(position = location)) }
    }

    private fun publish() {
        asyncLaunch(Dispatchers.IO) {
            DataRepository.uploadPost(currentState.newPost)
            setEffect { NewContract.Effect.Upload }
        }
    }

    fun setPostAndUser(postAndUser: PostAndUser) {
        setEvent(NewContract.Event.Load(postAndUser))
    }

    fun updateSingle(updated: PostAndUser, insertIfMissing: Boolean = false) {
        setState {
            copy(filterPosts = change(updated = updated, insertIfMissing = insertIfMissing))
        }
    }

    fun selectPost(posts: List<PostAndUser>) {
        setState { copy(filterPosts = posts) }
    }

    private fun submit() {
        asyncLaunch(Dispatchers.IO) {
            val r = DataRepository.searchForPoint(currentState.searchText)
            r.onSuccess {
                setState { copy(filterPosts = it) }
            }.onFailure {
                setState { copy(error = "no data") }
            }
        }
    }


    private fun change(updated: PostAndUser, insertIfMissing: Boolean = false): List<PostAndUser> {
        val old = currentState.filterPosts
        val idx = old.indexOfFirst { it.post.id == updated.post.id }
        if (idx == -1) {
            if (!insertIfMissing) return old
            return listOf(updated) + old
        }
        // 不做无变化替换
        val current = old[idx]
        if (current == updated) return old
        val list = old.toMutableList()
        list[idx] = updated
        return list
    }

    fun back() {
        setEffect { NewContract.Effect.Back }
    }

    private fun load() = asyncLaunch(Dispatchers.IO) {
        setState { copy(loading = true) }
        currentState.post?.let {
            DataRepository.getPostComments(it.id).onSuccess { comments ->
                setState { copy(loading = false, comments = comments) }
            }.onFailure {
                setState { copy(loading = false, error = "加载失败") }
                setEffect { NewContract.Effect.Toast("加载失败") }
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
            setEffect { (NewContract.Effect.Toast("发送失败")) }
        }
        setState { copy(sending = false) }
    }


    fun onLikeClick() {
        val snapshot = currentState.post
        val targetLiked = !snapshot!!.likedByMe
        val delta = if (targetLiked) 1 else -1
        setEffect { NewContract.Effect.Liked(targetLiked) }
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
                setEffect { NewContract.Effect.Toast("稍后重试") }
            }.onSuccess { serverPostOrCount ->
                // 4. 可选：校正
                // 如果服务器返回最终 likeCount 和 liked，优先使用服务端
                setState {
                    copy(
                        post = post!!.copy(
                            like = snapshot.like + 1, likedByMe = !snapshot.likedByMe
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