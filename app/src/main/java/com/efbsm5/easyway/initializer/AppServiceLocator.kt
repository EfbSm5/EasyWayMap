package com.efbsm5.easyway.initializer

import com.efbsm5.easyway.repo.CommunityRepository

object AppServiceLocator {
    // 直接指向单例，避免未初始化异常
    val communityRepository: CommunityRepository = CommunityRepository
}
