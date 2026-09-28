package lol.mycitadel.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import lol.mycitadel.app.ui.navigation.CitadelNavHost
import lol.mycitadel.app.ui.theme.MyCitadelTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyCitadelTheme {
                CitadelNavHost(modifier = Modifier.fillMaxSize())
            }
        }
    }
}