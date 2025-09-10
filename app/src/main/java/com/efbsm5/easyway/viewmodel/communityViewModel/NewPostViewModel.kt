package com.efbsm5.easyway.viewmodel.communityViewModel

import android.net.Uri
import com.efbsm5.easyway.base.BaseViewModel
import com.efbsm5.easyway.contract.community.NewPostContract
import com.efbsm5.easyway.repo.DataRepository

class NewPostViewModel :
    BaseViewModel<NewPostContract.Event, NewPostContract.State, NewPostContract.Effect>() {

    override fun createInitialState(): NewPostContract.State {
        return NewPostContract.State()
    }

    override fun handleEvents(event: NewPostContract.Event) {
        when (event) {
            is NewPostContract.Event.EditContent -> {
                setState { copy(post = post.copy(content = event.string)) }
            }

            is NewPostContract.Event.EditTitle -> {
                setState { copy(post = post.copy(title = event.string)) }
            }

            is NewPostContract.Event.PickPhotoDialogResult -> {
                setState { copy(previewPhoto = event.uri) }
            }

            NewPostContract.Event.Publish -> {
                publish()
            }

            is NewPostContract.Event.SelectedCategory -> {
                setState { copy(onSelectedCategory = event.int) }
            }

            is NewPostContract.Event.TitleChanged -> {
                setState { copy(post = post.copy(title = event.string)) }
            }
        }
    }

    fun onEffect(effect: NewPostContract.Effect) {
        setEffect { effect }
    }

    fun getPicture(uri: Uri?) {
        if (uri != null) {
            setState { copy(post = post.copy(photo = post.photo.plus(uri.toString()))) }
        } else {
            setState { copy(error = "error") }
        }
    }

    fun setLocation(location: String) {
        setState { copy(post = post.copy(position = location)) }
    }

    private fun publish() {
        asyncLaunch {
            DataRepository.uploadPost(currentState.post)
            setEffect { NewPostContract.Effect.Upload }
        }
    }


}