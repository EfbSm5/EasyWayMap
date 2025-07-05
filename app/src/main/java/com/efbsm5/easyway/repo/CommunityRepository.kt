package com.efbsm5.easyway.repo

import com.efbsm5.easyway.data.models.assistModel.PostAndUser
import com.efbsm5.easyway.model.ImmutableListWrapper

object CommunityRepository {

    suspend fun fetchPosts(): ImmutableListWrapper<PostAndUser> {
        return DataRepository.getPostAndUser()
    }
}