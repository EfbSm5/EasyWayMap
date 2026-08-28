package com.efbsm5.easyway.di

import com.efbsm5.easyway.data.UserManager
import com.efbsm5.easyway.data.database.AppDataBase
import com.efbsm5.easyway.data.database.dao.PointsDao
import com.efbsm5.easyway.data.database.dao.PostDao
import com.efbsm5.easyway.data.database.dao.UserDao
import com.efbsm5.easyway.getInitUser
import com.efbsm5.easyway.repo.HomePageRepository
import com.efbsm5.easyway.repo.RoomHomePageRepository
import com.efbsm5.easyway.viewmodel.HomePageViewModel
import kotlinx.coroutines.flow.Flow
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

private const val HOME_PAGE_USER_IDS = "homePageUserIds"

val homePageModule = module {
    single<AppDataBase> { AppDataBase.getDatabase(androidContext()) }
    single<UserDao> { get<AppDataBase>().userDao() }
    single<PointsDao> { get<AppDataBase>().pointsDao() }
    single<PostDao> { get<AppDataBase>().postDao() }
    single<Flow<Int>>(named(HOME_PAGE_USER_IDS)) { UserManager.userIdFlow }
    single<HomePageRepository> {
        RoomHomePageRepository(
            userDao = get<UserDao>(),
            pointsDao = get<PointsDao>(),
            postDao = get<PostDao>(),
            userIds = get<Flow<Int>>(named(HOME_PAGE_USER_IDS)),
            fallbackUser = getInitUser(),
        )
    }
    viewModel { HomePageViewModel(repository = get<HomePageRepository>()) }
}
