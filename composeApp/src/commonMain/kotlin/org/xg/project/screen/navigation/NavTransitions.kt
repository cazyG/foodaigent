package org.xg.project.screen.navigation

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith

fun appHorizontalTransition(): ContentTransform =
    slideInHorizontally { it } + fadeIn() togetherWith slideOutHorizontally { -it } + fadeOut()

fun appHorizontalPopTransition(): ContentTransform =
    slideInHorizontally { -it } + fadeIn() togetherWith slideOutHorizontally { it } + fadeOut()
