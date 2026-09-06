package com.rmakiyama.wishline

import androidx.compose.runtime.Composable
import com.rmakiyama.wishline.designsystem.theme.WlTheme
import com.rmakiyama.wishline.navigation.WishlineNavGraph

@Composable
fun App() {
    WlTheme {
        WishlineNavGraph()
    }
}
