package com.efbsm5.easyway.viewmodel.communityViewModel

//
//@HiltViewModel
//class PostsSharedViewModel @Inject constructor(
//    private val repo: PostRepository,
//    savedStateHandle: SavedStateHandle
//) : ViewModel() {
//
//    // 列表数据（示例用简单 List）
//    var posts by mutableStateOf<List<PostAndUser>>(emptyList())
//        private set
//
//    // 选中项
//    var selected by mutableStateOf<PostAndUser?>(null)
//        private set
//
//    var loading by mutableStateOf(false)
//        private set
//
//    var error by mutableStateOf<String?>(null)
//        private set
//
//    init {
//        load()
//    }
//
//    fun load() {
//        loading = true
//        error = null
//        viewModelScope.launch {
//            runCatching { repo.fetchPostList() }
//                .onSuccess {
//                    posts = it
//                    loading = false
//                }
//                .onFailure {
//                    error = it.message
//                    loading = false
//                }
//        }
//    }
//
//    fun select(post: PostAndUser) {
//        selected = post
//    }
//
//    fun selectById(id: Int) {
//        val hit = posts.firstOrNull { it.post.id == id }
//        if (hit != null) {
//            selected = hit
//        } else {
//            // 需要单独加载细节（按需实现）
//            viewModelScope.launch {
//                val detail = repo.fetchPostDetail(id)
//                selected = detail
//            }
//        }
//    }
//
//    fun clearSelection() {
//        selected = null
//    }
//}
