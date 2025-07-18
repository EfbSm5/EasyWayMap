// MIT License
// 
// Copyright (c) 2022 被风吹过的夏天
// 
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
// 
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
// 
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.

package com.efbsm5.easyway.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.efbsm5.easyway.R

/**
 * RoadTrafficSwitch
 * @author 被风吹过的夏天
 * @email developer_melody@163.com
 * @github: https://github.com/TheMelody/OmniMap
 * created 2022/10/19 09:50
 */
@Composable
fun RoadTrafficSwitch(modifier: Modifier, isEnable: Boolean) {
    Card(modifier = modifier) {
        if (isEnable) {
            Image(
                modifier = Modifier.requiredSizeIn(minWidth = 46.dp, minHeight = 46.dp),
                painter = painterResource(id = R.drawable.ic_road_condition_on),
                contentDescription = null
            )
        } else {
            Image(
                modifier = Modifier.requiredSizeIn(minWidth = 46.dp, minHeight = 46.dp),
                painter = painterResource(id = R.drawable.ic_road_condition_off),
                contentDescription = null
            )
        }
    }
}