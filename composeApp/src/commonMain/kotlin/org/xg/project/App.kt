package org.xg.project

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import io.github.vinceglb.filekit.coil.addPlatformFileSupport
import org.koin.compose.KoinApplication
import org.koin.dsl.koinConfiguration
import org.xg.project.di.appModule
import org.xg.project.screen.navigation.AppRootNavHost
import org.xg.project.ui.appTypography

@Composable
fun App() {
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .components {
                add(KtorNetworkFetcherFactory())
                addPlatformFileSupport()
            }
            .build()
    }
    KoinApplication(
        configuration = koinConfiguration { modules(appModule) },
    ) {
        MaterialTheme(typography = appTypography()) {
            AppRootNavHost()
        }
    }
}
