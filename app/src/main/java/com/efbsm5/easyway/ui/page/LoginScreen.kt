package com.efbsm5.easyway.ui.page

import android.content.Context
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    val context = LocalContext.current
    var phoneNumber by remember { mutableStateOf("") }
    var isAgreeChecked by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "欢迎登录 行无碍",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        TextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            placeholder = { Text("+86 18888888888") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Phone Icon") },
            trailingIcon = {
                if (phoneNumber.isNotEmpty()) {
                    IconButton(onClick = { phoneNumber = "" }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear Icon")
                    }
                }
            })

        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = isAgreeChecked,
                onCheckedChange = { isAgreeChecked = it },
                colors = CheckboxDefaults.colors(checkmarkColor = Color.Blue)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "同意《协议条款》")
        }

        Button(
            onClick = {
                if (isAgreeChecked && phoneNumber.isNotEmpty()) {
                } else {
                    Toast.makeText(context, "请同意协议条款并输入手机号", Toast.LENGTH_SHORT).show()
                }
            }, modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("获取验证码")
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "其他方式")
            TextButton(onClick = { /* Handle other login methods */ }) {
                Text(text = "密码登录")
            }
        }
    }
}

class LoginViewModel : ViewModel() {
    fun sendVerificationCode(phoneNumber: String, context: Context) {
        // Send verification code logic here
        Toast.makeText(context, "验证码已发送到 $phoneNumber", Toast.LENGTH_SHORT).show()
    }
}

fun ComponentActivity.setupLogin() {
    setContent {
        LoginScreen(onLoginSuccess = {
            // Handle login success
            Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show()
        })
    }
}
