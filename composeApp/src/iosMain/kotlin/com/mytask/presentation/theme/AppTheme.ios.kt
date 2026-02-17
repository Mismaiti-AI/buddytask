package com.mytask.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import platform.UIKit.UIApplication
import platform.UIKit.UIStatusBarStyleDarkContent
import platform.UIKit.UIStatusBarStyleLightContent
import platform.UIKit.UIUserInterfaceStyle
import platform.UIKit.UIWindow
import platform.UIKit.setStatusBarStyle

@Composable
internal actual fun SystemAppearance(isDark: Boolean) {
    LaunchedEffect(isDark) {
        UIApplication.sharedApplication.setStatusBarStyle(
            if (isDark) UIStatusBarStyleDarkContent else UIStatusBarStyleLightContent
        )

        UIApplication.sharedApplication.windows.forEach { window ->
            (window as? UIWindow)?.overrideUserInterfaceStyle = UIUserInterfaceStyle.UIUserInterfaceStyleLight
        }
    }
}