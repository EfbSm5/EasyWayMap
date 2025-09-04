package com.efbsm5.easyway.viewmodel.componentsViewmodel

import android.net.Uri
import com.efbsm5.easyway.base.BaseViewModel
import com.efbsm5.easyway.contract.NewPointCardContract
import com.efbsm5.easyway.data.LocationSaver
import com.efbsm5.easyway.getInitPoint
import com.efbsm5.easyway.repo.DataRepository
import kotlinx.coroutines.Dispatchers

class NewPointCardViewModel :
    BaseViewModel<NewPointCardContract.Event, NewPointCardContract.State, NewPointCardContract.Effect>() {


    fun changeInfoValue(string: String) {
        setState { copy(tempPoint = tempPoint.copy(info = string)) }
    }

    fun changeNameValue(string: String) {
        setState { copy(tempPoint = tempPoint.copy(name = string)) }
    }

    fun changeLocation(location: String) {
        setState { copy(tempPoint = tempPoint.copy(location = location)) }
    }

    fun changeType(type: String) {
        setState { copy(tempPoint = tempPoint.copy(type = type)) }
    }

    fun getPhoto(uri: Uri?) {
        uri?.let {
            setEffect { NewPointCardContract.Effect.UploadPhoto(it) }
        }
    }

    fun upLoadPhoto(string: String) {
        setState { copy(tempPoint = tempPoint.copy(photo = string)) }
    }

    fun callback(boolean: Boolean) {
        if (boolean) {
            LocationSaver.location.apply {
                setState { copy(tempPoint = tempPoint.copy(lat = latitude, lng = longitude)) }
            }
            asyncLaunch(Dispatchers.IO) {
                DataRepository.uploadPoint(currentState.tempPoint)
            }
            setEffect { NewPointCardContract.Effect.Back }
        } else {
            setEffect { NewPointCardContract.Effect.Back }
        }
    }

    override fun createInitialState(): NewPointCardContract.State {
        return NewPointCardContract.State(
            tempPoint = getInitPoint()
        )
    }

    override fun handleEvents(event: NewPointCardContract.Event) {
        when (event) {
            is NewPointCardContract.Event.ChangeInfo -> {
                changeInfoValue(event.string)
            }

            is NewPointCardContract.Event.ChangeLocation -> {
                changeLocation(event.string)
            }
        }
    }
}