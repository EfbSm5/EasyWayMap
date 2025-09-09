package com.efbsm5.easyway.contract.card

import android.net.Uri
import androidx.compose.runtime.Immutable
import com.efbsm5.easyway.data.models.EasyPoint
import com.efbsm5.easyway.state.IUiEffect
import com.efbsm5.easyway.state.IUiEvent
import com.efbsm5.easyway.state.IUiState

class NewPointCardContract {
    sealed class Event : IUiEvent {
        data class ChangeLocation(val string: String) : Event()
        data class ChangeInfo(val string: String) : Event()
    }

    @Immutable
    data class State(
        val tempPoint: EasyPoint

    ) : IUiState

    sealed class Effect : IUiEffect {
        object Upload : Effect()
        class UploadPhoto(val uri: Uri) : Effect()
        object Back : Effect()
    }
}