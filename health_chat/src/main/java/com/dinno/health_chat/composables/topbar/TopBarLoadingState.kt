package com.dinno.health_chat.composables.topbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBackIos
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dinno.health_chat.components.Skeleton

@Composable
internal fun TopBarLoadingState(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .background(color = Color(0xFFF8FAFC))
            .fillMaxWidth()
            .height(80.dp)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            modifier = Modifier.size(24.dp),
            onClick = onBackClick,
            enabled = true
        ) {
            Icon(
                modifier = Modifier.size(16.dp),
                imageVector = Icons.AutoMirrored.Rounded.ArrowBackIos,
                tint = Color(0xFF374151),
                contentDescription = null
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Skeleton(
            modifier = Modifier
                .clip(shape = CircleShape)
                .size(32.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(verticalArrangement = Arrangement.Center) {
            Skeleton(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .size(width = 120.dp, height = 16.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Skeleton(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .size(width = 80.dp, height = 12.dp)
            )
        }
        Spacer(modifier = Modifier.weight(weight = 1f, fill = true))
        Skeleton(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .size(width = 80.dp, height = 16.dp)
        )
    }
}