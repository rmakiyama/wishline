package com.rmakiyama.skeleton

import androidx.compose.runtime.Composable
import com.rmakiyama.skeleton.designsystem.theme.SkeletonTheme
import com.rmakiyama.skeleton.navigation.SkeletonNavGraph

@Composable
fun App() {
    SkeletonTheme {
        SkeletonNavGraph()
    }
}
