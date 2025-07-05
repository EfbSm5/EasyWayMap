package com.efbsm5.easyway.ui.page.communityPage

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.efbsm5.easyway.data.models.assistModel.PostAndUser
import com.efbsm5.easyway.viewmodel.CommunityViewModel

@Composable
fun CommunityPage(back: () -> Unit, onChangeState: (PostAndUser) -> Unit) {
    val viewModel: CommunityViewModel = viewModel()
    val currentState by viewModel.uiState.collectAsState()

    when {
        currentState.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        currentState.error != null -> {
            Box(
                modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                Text(currentState.error!!)
            }
        }

        else -> {
            ShowPage(
                back = back,
                posts = currentState.postItems,
                onSelect = viewModel::select,
                onClick = onChangeState,
                search = viewModel::search
            )
        }
    }
}


