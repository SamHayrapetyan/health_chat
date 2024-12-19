package com.dinno.health_chat.modifier

import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalFocusManager

internal fun Modifier.unFocusOnTap() = composed {
    val focusManager = LocalFocusManager.current
    noRippleClickable(onClick = { focusManager.clearFocus() })
}