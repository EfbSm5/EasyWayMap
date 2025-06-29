package com.efbsm5.easyway.viewmodel.pageViewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.efbsm5.easyway.data.models.assistModel.DynamicPostAndUser
import com.efbsm5.easyway.data.repository.DataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ShowPageViewModel(val repository: DataRepository) : ViewModel() {
    private var allPosts = MutableStateFlow<List<DynamicPostAndUser>>(emptyList())
    private var _showPosts = MutableStateFlow<List<DynamicPostAndUser>>(emptyList())
    val posts: StateFlow<List<DynamicPostAndUser>> = _showPosts

    init {
        fetchPosts()
    }

    private fun fetchPosts() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllDynamicPosts().collect { dynamicPosts ->
                val list = emptyList<DynamicPostAndUser>().toMutableList()
                dynamicPosts.forEach { post ->
                    repository.getCommentCount(post.commentId).collect {
                        list.add(
                            DynamicPostAndUser(
                                dynamicPost = post,
                                user = repository.getUserById(post.userId),
                                commentCount = it,
                            )
                        )
                        allPosts.value = list.toList()
                    }
                }
            }
        }
    }

    fun changeTab(int: Int) {
        if (int != 0) {
            _showPosts.value = allPosts.value.filter {
                it.dynamicPost.type == int
            }
        } else {
            _showPosts.value = allPosts.value
        }
    }

    fun search(string: String) {
        _showPosts.value = allPosts.value.filter {
            it.dynamicPost.title.contains(string)
        }
    }
}

