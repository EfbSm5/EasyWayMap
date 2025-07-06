package com.efbsm5.easyway.repo

import com.efbsm5.easyway.data.models.assistModel.PostAndUser

object CommunityRepository {

    suspend fun fetchPosts(): List<PostAndUser> {
        return DataRepository.getPostAndUser()
    }
}