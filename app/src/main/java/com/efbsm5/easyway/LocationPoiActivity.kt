package com.efbsm5.easyway

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.efbsm5.easyway.ui.page.map.DragDropSelectPointScreen

class LocationPoiActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
            ) {
                DragDropSelectPointScreen(
                    onSelected = {
                        val resultIntent = Intent().apply {
                            putExtra("result_key", it)
                        }
                        setResult(RESULT_OK, resultIntent)
                        finish()
                    })
            }
        }
    }
}