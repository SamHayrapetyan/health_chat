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
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dinno.health_chat.R
import com.dinno.health_chat.api.model.ChatUserModel
import com.dinno.health_chat.components.ImageWithLoading
import com.dinno.health_chat.modifier.noRippleClickable

@Composable
internal fun TopBarVisibleState(
    otherUser: ChatUserModel,
    chatExpirationDate: String?,
    onBackClick: () -> Unit,
    onUserClick: () -> Unit
) {
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
                tint = Color(0xFF0E2D6B),
                contentDescription = null
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        ImageWithLoading(
            modifier = Modifier
                .noRippleClickable(onClick = onUserClick)
                .clip(shape = CircleShape)
                .size(32.dp),
            url = otherUser.imageUrl
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier
                .noRippleClickable(onClick = onUserClick)
                .weight(weight = 1f, fill = true), verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = otherUser.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            otherUser.description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Center) {
            val untilString = stringResource(R.string.hc_until)
            val expiredString = stringResource(R.string.hc_finished)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = if (chatExpirationDate == null) expiredString else untilString,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF0E2D6B)
                )
                Icon(
                    modifier = Modifier
                        .size(16.dp)
                        .alignByBaseline(),
                    imageVector = Icons.Rounded.AccessTime,
                    tint = Color(0xFF0E2D6B),
                    contentDescription = null
                )
            }
            chatExpirationDate?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF0E2D6B),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}