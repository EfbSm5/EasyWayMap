# EasyWay

一个基于 Jetpack Compose 的地图与社区应用。支持地图点位浏览与管理、位置与路线能力、社区帖子与评论、离线数据存储与后台同步等。

## 功能概览

- 地图能力
    - 显示点位、当前位置与距离计算（MapUtils）
    - 拖拽选择点位、多点覆盖物（DragDropSelectPointRepository、MultiPointOverlayRepository）
    - 外部地图跳转（高德/百度/腾讯地图 URI Scheme）与路线规划入口（RoutePlanRepository）
- 社区能力
    - 帖子 Post、评论 Comment、用户 User 的本地存储与关联查询（Room + @Relation 聚合，如
      PostWithComments、PostAndUser、PostCommentAndUser）
    - 社区列表与详情数据访问（CommunityRepository + PostDao/PostCommentDao/UserDao）
- 数据与同步
    - 本地数据库 Room（类型转换器 Converter.kt、UriTypeAdapter.kt）
    - Retrofit + Gson 的网络访问封装（ServiceCreator、HttpInterface、EasyPointNetWork）
    - WorkManager 后台同步（SyncWorker）
- UI 与导航
    - 全面使用 Jetpack Compose（Material 3、Accompanist 权限/FlowLayout、Coil 图片加载）
    - Navigation Compose 构建多页面导航（ui/Nav.kt）
    - Advanced BottomSheet for Material3（io.morfly.compose:advanced-bottomsheet-material3）
- 其他
    - Koin 依赖注入
    - LeakCanary Debug 内存泄漏检测

## 技术栈

- 语言与工具：Kotlin 2.2.x，AGP 8.11.x，JDK 19
- Compose：Compose BOM（2025.08.01）、Material3、Navigation、Foundation/Runtime/UI-Graphics
- 数据层：Room Runtime/KTK + KSP 编译器
- 网络层：Retrofit 3.x + Gson Converter + Gson
- 异步/任务：WorkManager
- 依赖注入：Koin (android + androidX compose)
- 辅助库：Accompanist（permissions、flowlayout）、Coil、LeakCanary、Material Components

依赖声明可见：

- app/build.gradle.kts
- gradle/libs.versions.toml

## 架构与目录

- 单模块 app 工程
- MVVM 分层（ViewModel + Repository + DAO/Entity），数据通过 Repository 聚合 UI 所需模型（assistModel）
- 主要目录：
    - data/
        - database/（AppDataBase、Converter、Dao）
        - models/（Entity 与组合模型 assistModel）
        - network/（ServiceCreator、HttpInterface、Worker）
    - repo/（社区、地图、多点覆盖物、路线规划等仓库）
    - ui/（导航、组件、主题、页面）
    - viewmodel/（Home、Map、Community 等）


## 数据模型

- Entity：User、Post、PostComment、EasyPoint、PointComment
- 组合模型（assistModel）：
    - PostAndUser、PostWithComments、PostCommentAndUser
    - PointWithComments、PointCommentAndUser、EasyPointSimplify

## 第三方与致谢

- Jetpack Compose、Material3、Navigation、Room、WorkManager、Koin、Coil、Accompanist、LeakCanary
- Advanced BottomSheet for Material3（io.morfly.compose:advanced-bottomsheet-material3）
- 地图 URI 跳转（高德/百度/腾讯）


