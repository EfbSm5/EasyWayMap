package com.efbsm5.easyway.viewmodel.communityViewModel

import com.efbsm5.easyway.base.BaseViewModel
import com.efbsm5.easyway.contract.community.CommunityContract

class CommunityViewModel :
    BaseViewModel<CommunityContract.Event, CommunityContract.State, CommunityContract.Effect>() {
    override fun createInitialState(): CommunityContract.State = CommunityContract.State()


    override fun handleEvents(event: CommunityContract.Event) {
        when (event) {

            is CommunityContract.Event.ClickPost -> {
                setEffect { CommunityContract.Effect.SelectedPost(event.postAndUser) }
            }

            is CommunityContract.Event.EditText -> {
                setState { copy(searchText = event.string) }
            }

            // 列表过滤由 Room 列表和当前输入实时推导，提交键无需再发起一次数据库查询。
            CommunityContract.Event.Submit -> Unit
            is CommunityContract.Event.TabSelect -> {
                setState { copy(selectedTab = event.int) }
            }
        }
    }

    fun back() {
        setEffect { CommunityContract.Effect.Back }
    }
}
