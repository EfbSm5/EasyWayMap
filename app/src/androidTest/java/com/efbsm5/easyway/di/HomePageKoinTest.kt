package com.efbsm5.easyway.di

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.efbsm5.easyway.data.database.AppDataBase
import com.efbsm5.easyway.data.database.dao.PointsDao
import com.efbsm5.easyway.data.database.dao.PostDao
import com.efbsm5.easyway.data.database.dao.UserDao
import com.efbsm5.easyway.repo.HomePageRepository
import com.efbsm5.easyway.repo.RoomHomePageRepository
import com.efbsm5.easyway.viewmodel.HomePageViewModel
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.GlobalContext

@RunWith(AndroidJUnit4::class)
class HomePageKoinTest {

    @Test
    fun applicationGraph_resolvesHomePageDependencies() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val koin = GlobalContext.get()
        val database = koin.get<AppDataBase>()

        assertSame(AppDataBase.getDatabase(context), database)
        assertSame(database.userDao(), koin.get<UserDao>())
        assertSame(database.pointsDao(), koin.get<PointsDao>())
        assertSame(database.postDao(), koin.get<PostDao>())
        assertTrue(koin.get<HomePageRepository>() is RoomHomePageRepository)
        assertNotNull(koin.get<HomePageViewModel>())
    }
}
