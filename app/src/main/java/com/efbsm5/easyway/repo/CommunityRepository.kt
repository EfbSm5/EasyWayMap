package com.efbsm5.easyway.repo

import com.efbsm5.easyway.data.models.assistModel.PointCommentAndUser
import com.efbsm5.easyway.data.repository.DataRepository

object CommunityRepository {

    private suspend fun fetchPosts() {
        DataRepository.getAllDynamicPosts().collect { dynamicPosts ->
            val list = emptyList<PointCommentAndUser>().toMutableList()
            dynamicPosts.forEach { post ->
                DataRepository.getCommentCount(post.commentId).collect {
                    list.add(
                        PointCommentAndUser(
                            dynamicPost = post,
                            user = DataRepository.getUserById(post.userId),
                            commentCount = it,
                        )
                    )
                    allPosts.value = list.toList()
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