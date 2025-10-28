package com.efbsm5.easyway.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.efbsm5.easyway.SDKUtils
import com.efbsm5.easyway.data.database.AppDataBase
import com.efbsm5.easyway.data.models.assistModel.PostAndUser
import com.efbsm5.easyway.measureRoomUpdateLatencyRepeatTx
import com.efbsm5.easyway.repo.CommunityRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "SharedViewModel"

class CommunitySharedViewModel(
    handle: SavedStateHandle
) : ViewModel() {

    private val repo = CommunityRepository
    private val _posts = MutableStateFlow<List<PostAndUser>>(emptyList())
    val posts: StateFlow<List<PostAndUser>> = _posts
    private val _currentPost = MutableStateFlow<PostAndUser?>(null)
    val currentPost: StateFlow<PostAndUser?> = _currentPost
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadInitialIfEmpty() {
        if (_posts.value.isNotEmpty()) return
        viewModelScope.launch(Dispatchers.IO) {
            _loading.value = true
            repo.getAllPosts().onSuccess { _posts.value = it }
                .onFailure { _error.value = it.message }
            _loading.value = false
        }
    }

    init {
        debug()
    }

    fun debug() {
        viewModelScope.launch(Dispatchers.IO) {
            measureRoomUpdateLatencyRepeatTx(AppDataBase.getDatabase(SDKUtils.getContext()))

        }
    }


    fun select(post: PostAndUser) {
        _currentPost.value = post
    }

    fun updateLike(postId: Int, liked: Boolean, newCount: Int) {
        _posts.update { list ->
            list.map {
                if (it.post.id == postId) it.copy(
                    post = it.post.copy(likedByMe = liked, like = newCount)
                ) else it
            }
        }
        _currentPost.update { cur ->
            if (cur?.post?.id == postId) cur.copy(
                post = cur.post.copy(likedByMe = liked, like = newCount)
            ) else cur
        }
    }

    fun insertNewPost(newPost: PostAndUser) {
        _posts.update { listOf(newPost) + it }
    }

    fun updateSingle(updated: PostAndUser) {
        _posts.update { list -> list.map { if (it.post.id == updated.post.id) updated else it } }
        if (_currentPost.value?.post?.id == updated.post.id) {
            _currentPost.value = updated
        }
    }

}
