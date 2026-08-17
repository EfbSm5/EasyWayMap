package com.efbsm5.easyway.viewmodel.communityViewModel

import com.efbsm5.easyway.data.models.assistModel.PostAndUser

/**
 * 社区筛选是纯函数：Room 持有原始列表，页面只根据查询词和分类派生展示结果。
 */
internal fun filterCommunityPosts(
    posts: List<PostAndUser>,
    query: String,
    selectedTab: Int,
): List<PostAndUser> {
    val keyword = query.trim()
    return posts.filter { item ->
        val matchesTab = selectedTab == 0 || item.post.type == selectedTab
        val matchesKeyword = keyword.isEmpty() ||
            item.post.title.contains(keyword, ignoreCase = true) ||
            item.post.content.contains(keyword, ignoreCase = true) ||
            item.post.position.contains(keyword, ignoreCase = true) ||
            item.user.name.contains(keyword, ignoreCase = true)
        matchesTab && matchesKeyword
    }
}
