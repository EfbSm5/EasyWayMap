package com.efbsm5.easyway.viewmodel.pageViewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.efbsm5.easyway.LocationSaver
import com.efbsm5.easyway.data.models.Post
import com.efbsm5.easyway.repo.DataRepository
import com.efbsm5.easyway.getInitPost
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NewPostPageViewModel(
    private val repository: DataRepository,
    val locationSaver: LocationSaver
) :
    ViewModel() {
    private var _newPost = MutableStateFlow(getInitPost())
    private val _chosenPhotos = MutableStateFlow(emptyList<Uri>())
    val newPost: StateFlow<Post> = _newPost
    val chosenPhotos: StateFlow<List<Uri>> = _chosenPhotos

    fun editPost(dynamicPost: Post) {
        _newPost.value = dynamicPost
    }

    fun getPicture(uri: Uri) {
        _chosenPhotos.value = _chosenPhotos.value.plus(uri)
    }

    fun push() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.uploadPost(_newPost.value, _chosenPhotos.value)
        }
    }

    private fun updatePhotos(newPhotos: List<Uri>) {
        _chosenPhotos.value = newPhotos
    }

    fun getLocation(): String {
        return locationSaver.locationDetail
    }
}