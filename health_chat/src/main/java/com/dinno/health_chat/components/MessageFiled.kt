package com.dinno.health_chat.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
internal fun MessageField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    placeholder: String? = null
) {
    OutlinedTextField(
        modifier = modifier,
        value = query,
        onValueChange = onQueryChange,
        enabled = enabled,
        singleLine = false,
        maxLines = 3,
        placeholder = placeholder?.let { { Text(text = it) } },
        shape = MaterialTheme.shapes.extraLarge,
        colors = getColors()
    )
}

@Composable
private fun getColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color(0xFF374151),
    unfocusedTextColor = Color(0xFF374151),
    disabledTextColor = Color(0xFF3C4657),
    focusedBorderColor = Color.Transparent,
    unfocusedBorderColor = Color.Transparent,
    disabledBorderColor = Color.Transparent,
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    disabledContainerColor = Color.Transparent,
    cursorColor = Color(0xFF374151),
    focusedLeadingIconColor = Color(0xFF374151),
    unfocusedLeadingIconColor = Color(0xFF374151),
    disabledLeadingIconColor = Color(0xFF3C4657),
    focusedTrailingIconColor = Color(0xFF374151),
    unfocusedTrailingIconColor = Color(0xFF374151),
    disabledTrailingIconColor = Color(0xFF3C4657),
    focusedPlaceholderColor = Color(0xFF374151),
    unfocusedPlaceholderColor = Color(0xFF374151),
    disabledPlaceholderColor = Color(0xFF3C4657)
)
