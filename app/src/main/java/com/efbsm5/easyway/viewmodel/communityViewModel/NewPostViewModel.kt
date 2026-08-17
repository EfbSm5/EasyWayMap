package com.efbsm5.easyway.viewmodel.communityViewModel

import android.net.Uri
import com.efbsm5.easyway.base.BaseViewModel
import com.efbsm5.easyway.contract.community.NewPostContract
import com.efbsm5.easyway.repo.DataRepository
import kotlinx.coroutines.Dispatchers

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
                setState {
                    copy(
                        onSelectedCategory = event.int,
                        post = post.copy(type = event.int),
                    )
                }
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
        if (currentState.publishing) return
        val draft = currentState.post
        if (draft.title.isBlank() || draft.content.isBlank()) {
            setEffect { NewPostContract.Effect.Toast("请填写标题和正文") }
            return
        }
        setState { copy(publishing = true, error = null) }
        asyncLaunch(Dispatchers.IO) {
            DataRepository.uploadPost(draft)
                .onSuccess { saved ->
                    setState { copy(post = saved.post, publishing = false) }
                    setEffect { NewPostContract.Effect.Upload }
                }
                .onFailure { throwable ->
                    val message = throwable.message ?: "发布失败，请稍后重试"
                    setState { copy(publishing = false, error = message) }
                    setEffect { NewPostContract.Effect.Toast(message) }
                }
        }
    }


}
