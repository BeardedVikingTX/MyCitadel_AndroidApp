package lol.mycitadel.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import lol.mycitadel.app.ui.theme.Cyan
import lol.mycitadel.app.ui.theme.Slab

@Composable
fun CitadelPanel(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(14.dp),
                ambientColor = Cyan.copy(alpha = 0.2f),
                spotColor = Cyan.copy(alpha = 0.3f),
            )
            .clip(RoundedCornerShape(14.dp))
            .background(Slab)
            .border(
                width = 1.dp,
                color = Cyan.copy(alpha = 0.25f),
                shape = RoundedCornerShape(14.dp),
            )
            .padding(20.dp),
        content = content,
    )
}