package com.efbsm5.easyway.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.efbsm5.easyway.data.models.assistModel.PostAndUser
import com.efbsm5.easyway.repo.CommunityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class CommunitySharedViewModel(
    private val repo: CommunityRepository, private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _posts = MutableStateFlow<List<PostAndUser>>(emptyList())
    val posts: StateFlow<List<PostAndUser>> = _posts.asStateFlow()
    private val _currentPost = MutableStateFlow<PostAndUser?>(null)
    val currentPost: StateFlow<PostAndUser?> = _currentPost.asStateFlow()
    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        viewModelScope.launch {
            repo.observePosts()
                .catch { throwable ->
                    _loading.value = false
                    _error.value = throwable.message ?: "社区数据加载失败"
                }
                .collect { latestPosts ->
                    _posts.value = latestPosts
                    _loading.value = false
                    _error.value = null

                    val selectedId = savedStateHandle.get<Int>(SELECTED_POST_ID)
                    if (selectedId != null) {
                        _currentPost.value = latestPosts.firstOrNull { it.post.id == selectedId }
                    }
                }
        }
    }

    fun select(post: PostAndUser) {
        savedStateHandle[SELECTED_POST_ID] = post.post.id
        _currentPost.value = post
    }

    /** 详情页在进程恢复或直接导航时按稳定 ID 从 Room 恢复。 */
    fun select(postId: Int) {
        savedStateHandle[SELECTED_POST_ID] = postId
        _posts.value.firstOrNull { it.post.id == postId }?.let {
            _currentPost.value = it
            return
        }
        viewModelScope.launch {
            repo.getPost(postId)
                .onSuccess { _currentPost.value = it }
                .onFailure { _error.value = it.message ?: "帖子加载失败" }
        }
    }

    companion object {
        private const val SELECTED_POST_ID = "selected_post_id"
    }
}
