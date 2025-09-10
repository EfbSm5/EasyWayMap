package com.efbsm5.easyway.initializer

import com.efbsm5.easyway.repo.CommunityRepository

object AppServiceLocator {
    // 在 Application.onCreate 中初始化
    lateinit var communityRepository: CommunityRepository
}
