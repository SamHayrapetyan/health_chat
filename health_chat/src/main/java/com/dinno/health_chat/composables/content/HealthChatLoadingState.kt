package com.dinno.health_chat.composables.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.dinno.health_chat.components.Skeleton

@Composable
internal fun HealthChatLoadingState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(state = rememberScrollState(), enabled = false)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Bottom)
    ) {
        Skeleton(
            modifier = Modifier
                .align(Alignment.End)
                .clip(RoundedCornerShape(16.dp))
                .size(260.dp)
        )
        Row(
            modifier = Modifier
                .align(Alignment.Start),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Skeleton(
                modifier = Modifier
                    .clip(shape = CircleShape)
                    .size(32.dp)
            )
            Skeleton(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .size(width = 260.dp, height = 120.dp)
            )
        }
        Skeleton(
            modifier = Modifier
                .align(Alignment.End)
                .clip(RoundedCornerShape(16.dp))
                .size(width = 260.dp, height = 120.dp)
        )
    }
}