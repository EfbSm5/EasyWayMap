# EasyWayMap

一个基于 Jetpack Compose 的地图与社区应用。支持地图点位浏览与管理、位置与路线能力、社区帖子与评论、离线数据存储与后台同步等。

## 功能概览

- 地图
  - 显示点位、当前位置与距离计算
  - 拖拽选择点位、多点覆盖物渲染
  - 外部地图（高德/百度/腾讯）URI 跳转与路线规划入口
- 社区
  - 帖子、评论、用户的本地存储与关联查询（Room + @Relation 聚合）
  - 列表与详情数据访问，点赞、评论等交互
- 数据与同步
  - 本地数据库（Room，含类型转换器）
  - 网络访问封装（Retrofit + Gson）
  - 后台任务与数据同步（WorkManager）
- UI 与导航
  - Jetpack Compose（Material 3、Accompanist 权限/FlowLayout、Coil 图片加载）
  - Navigation Compose 多页面导航
  - Material3 高级 BottomSheet（io.morfly.compose:advanced-bottomsheet-material3）
- 其他
  - 依赖注入（Koin）
  - 内存泄漏检测（LeakCanary，Debug）

## 技术栈

- 语言与工具：Kotlin、Gradle/AGP、JDK
- Compose：Material3、Navigation、Foundation/Runtime/UI-Graphics、Coil
- 数据层：Room（Runtime/Ktx + KSP 编译器）
- 网络层：Retrofit + Gson Converter + Gson
- 异步/任务：WorkManager
- 依赖注入：Koin（android + androidx compose）
- 辅助库：Accompanist（permissions、flowlayout）、LeakCanary、Material Components

依赖与版本请参考：
- app/build.gradle.kts
- gradle/libs.versions.toml

## 架构与目录

- 单模块 app 工程
- 基于 ViewModel + Repository + DAO/Entity 的分层，UI 状态通过 ViewModel 暴露；数据在 Repository 聚合成 UI 所需的组合模型
- 状态管理采用事件（Event）- 状态（State）- 副作用（Effect）的模式，使用 StateFlow/SharedFlow/Channel 驱动界面与一次性操作（如 Toast、导航）

目录约定（部分）：
```
app/src/main/java/com/efbsm5/easyway/
├─ data/
│  ├─ database/        # AppDataBase、Converter、Dao
│  ├─ models/          # Entity 与组合模型（assistModel）
│  └─ network/         # ServiceCreator、HttpInterface、Worker
├─ repo/               # 社区、地图、多点覆盖物、路线规划等仓库
├─ ui/                 # 导航、组件、主题、页面（Compose）
├─ viewmodel/          # 页面/业务 ViewModel（事件-状态-副作用）
└─ base/               # BaseViewModel 等基础设施
```

数据模型（示例）：
- Entity：User、Post、PostComment、EasyPoint、PointComment
- 组合模型（assistModel）：PostAndUser、PostWithComments、PostCommentAndUser、PointWithComments、PointCommentAndUser、EasyPointSimplify


## 关键模块说明（摘选）

- 地图相关
  - 多点覆盖物、点位选择、定位权限与 GPS 开关引导
  - 定位回调与 UI 状态同步
- 社区相关
  - 帖子与评论的本地缓存、网络读写、乐观更新
  - 列表与详情的状态管理与副作用（如 Toast）

## 开发约定

- View 层通过观察 ViewModel 暴露的 State 渲染 UI，避免直接持久化业务状态到 Composable
- 一次性操作（如弹窗、导航）走副作用通道，避免因重组重复触发
- 数据拉取、变更与合并在 Repository 层完成，尽量保持 ViewModel 纯粹

## 致谢

- Jetpack Compose / Material3 / Navigation
- Room / WorkManager
- Retrofit / Gson
- Koin / Coil / Accompanist / LeakCanary
- Advanced BottomSheet for Material3（io.morfly.compose:advanced-bottomsheet-material3）
- 地图 URI 跳转（高德/百度/腾讯）
