package com.efbsm5.easyway.viewmodel.communityViewModel

import android.net.Uri
import com.efbsm5.easyway.base.BaseViewModel
import com.efbsm5.easyway.contract.community.NewPostContract
import com.efbsm5.easyway.getInitPost

class NewPostViewModel :
    BaseViewModel<NewPostContract.Event, NewPostContract.State, NewPostContract.Effect>() {

    override fun createInitialState(): NewPostContract.State {
        return NewPostContract.State(
            post = getInitPost(), dialogData = null, error = null
        )
    }

    override fun handleEvents(event: NewPostContract.Event) {
        when (event) {
            NewPostContract.Event.Loading -> {

            }

            is NewPostContract.Event.ChangeDialogData -> {
                setState { copy(dialogData = event.data) }
            }

            is NewPostContract.Event.EditContent -> {
                setState { copy(post = post.copy(content = event.string)) }
            }

            is NewPostContract.Event.EditTitle -> {
                setState { copy(post = post.copy(title = event.string)) }
            }
        }
    }

    fun getPicture(uri: Uri?) {
        if (uri != null) {
            setState { copy(post = post.copy(photo = post.photo.plus(uri.toString()))) }
        } else {
            setState { copy(error = "error") }
        }
    }

    fun push() {
        asyncLaunch { }
    }

    private fun updatePhotos(newPhotos: List<Uri>) {

    }

    fun changeContent(string: String) {
        setEvent(NewPostContract.Event.EditContent(string))
    }

    fun changeTitle(string: String) {
        setEvent(NewPostContract.Event.EditTitle(string))
    }

    fun setLocation(location: String) {
        setState { copy(post = post.copy(position = location)) }
    }

    fun selectIndex(index: Int) {
        setState { copy(post = post.copy(type = index)) }
    }

    fun back() {
        setEffect { NewPostContract.Effect.Back }
    }

    fun getLocation() {
        setEffect { NewPostContract.Effect.GetLocation }
    }

    fun getPhoto() {
        setEffect { NewPostContract.Effect.GetPhoto }
    }
}