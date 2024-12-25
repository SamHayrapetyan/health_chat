package com.dinno.health_chat.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Size

@Composable
fun ImageWithLoading(
    url: Any?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    fullSize: Boolean = false
) = SubcomposeAsyncImage(
    model = defaultImageModel(data = url, diskCacheEnabled = true, fullSize = fullSize),
    contentDescription = null,
    contentScale = contentScale,
    filterQuality = FilterQuality.High,
    error = {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF9CA3AF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier
                    .sizeIn(maxWidth = 64.dp, maxHeight = 64.dp)
                    .fillMaxSize(),
                imageVector = Icons.Rounded.Error,
                tint = Color(0x1764748B),
                contentDescription = null
            )
        }
    },
    loading = {
        val backgroundColor = Color(0xFF9CA3AF)
        val transition = rememberInfiniteTransition(label = "Coil pulsing alpha brush")
        val alphaAnimation = transition.animateFloat(
            initialValue = 0.25f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(animation = tween(800), repeatMode = RepeatMode.Reverse),
            label = "Coil pulsing alpha animation"
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = alphaAnimation.value }
                .background(backgroundColor)
        )
    },
    modifier = modifier
)

@Composable
fun defaultImageModel(data: Any?, diskCacheEnabled: Boolean = false, fullSize: Boolean = false): ImageRequest {
    val request = ImageRequest.Builder(LocalContext.current)
        .data(data)
        .crossfade(true)
        .diskCachePolicy(if (diskCacheEnabled) CachePolicy.ENABLED else CachePolicy.DISABLED)
        .memoryCachePolicy(CachePolicy.ENABLED)
    if (fullSize) request.size(Size.ORIGINAL)
    return request.build()
}