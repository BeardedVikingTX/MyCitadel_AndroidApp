package lol.mycitadel.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lol.mycitadel.app.ui.theme.Cyan
import lol.mycitadel.app.ui.theme.CyanBright
import lol.mycitadel.app.ui.theme.Slab
import lol.mycitadel.app.ui.theme.TextFaint
import lol.mycitadel.app.ui.theme.Void

@Composable
fun CitadelTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    singleLine: Boolean = true,
    minLines: Int = 1,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = Cyan,
                letterSpacing = 1.sp,
            )
        },
        placeholder = if (placeholder.isNotEmpty()) {
            {
                Text(
                    text = placeholder,
                    color = TextFaint,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        } else null,
        singleLine = singleLine,
        minLines = minLines,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction,
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Cyan,
            unfocusedBorderColor = Cyan.copy(alpha = 0.3f),
            focusedLabelColor = CyanBright,
            unfocusedLabelColor = Cyan.copy(alpha = 0.7f),
            cursorColor = CyanBright,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
        ),
        shape = RoundedCornerShape(10.dp),
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CitadelChipRow(
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        options.forEach { option ->
            val isSelected = option == selected
            Text(
                text = option.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                letterSpacing = 1.sp,
                color = if (isSelected) Void else Cyan,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .background(
                        color = if (isSelected) Cyan else Color.Transparent,
                        shape = RoundedCornerShape(999.dp),
                    )
                    .border(
                        width = 1.dp,
                        color = if (isSelected) Cyan else Cyan.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(999.dp),
                    )
                    .clickable { onSelect(option) }
                    .padding(horizontal = 16.dp, vertical = 10.dp),
            )
        }
    }
}