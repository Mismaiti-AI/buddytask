package com.mytask.core.data.auth

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CompletableDeferred
import platform.AuthenticationServices.ASAuthorization
import platform.AuthenticationServices.ASAuthorizationAppleIDCredential
import platform.AuthenticationServices.ASAuthorizationAppleIDProvider
import platform.AuthenticationServices.ASAuthorizationController
import platform.AuthenticationServices.ASAuthorizationControllerDelegateProtocol
import platform.AuthenticationServices.ASAuthorizationControllerPresentationContextProvidingProtocol
import platform.AuthenticationServices.ASAuthorizationScopeEmail
import platform.AuthenticationServices.ASAuthorizationScopeFullName
import platform.Foundation.NSError
import platform.Foundation.create
import platform.UIKit.UIApplication
import platform.UIKit.UIWindow
import platform.UIKit.UIWindowScene
import platform.darwin.NSObject

/**
 * Apple Sign-In implementation using AuthenticationServices framework.
 *
 * ## Setup Required
 * 1. Open `iosApp.xcodeproj` in Xcode
 * 2. Go to target -> Signing & Capabilities -> Add "Sign in with Apple"
 * 3. Ensure your Apple Developer account has this capability enabled
 */
actual suspend fun signInWithSocialProvider(): SocialAuthResult {
    val deferred = CompletableDeferred<SocialAuthResult>()

    val appleIDProvider = ASAuthorizationAppleIDProvider()
    val request = appleIDProvider.createRequest().apply {
        requestedScopes = listOf(ASAuthorizationScopeFullName, ASAuthorizationScopeEmail)
    }

    val delegate = AppleSignInDelegate(deferred)
    val controller = ASAuthorizationController(authorizationRequests = listOf(request))
    controller.delegate = delegate
    controller.presentationContextProvider = delegate
    controller.performRequests()

    return deferred.await()
}

@OptIn(ExperimentalForeignApi::class)
private class AppleSignInDelegate(
    private val deferred: CompletableDeferred<SocialAuthResult>
) : NSObject(),
    ASAuthorizationControllerDelegateProtocol,
    ASAuthorizationControllerPresentationContextProvidingProtocol {

    @OptIn(BetaInteropApi::class)
    override fun authorizationController(
        controller: ASAuthorizationController,
        didCompleteWithAuthorization: ASAuthorization
    ) {
        val credential = didCompleteWithAuthorization.credential as? ASAuthorizationAppleIDCredential
        if (credential == null) {
            deferred.completeExceptionally(Exception("Apple Sign-In failed: invalid credential type"))
            return
        }

        val userId = credential.user
        val fullName = credential.fullName
        val name = buildString {
            fullName?.givenName?.let { append(it) }
            fullName?.familyName?.let {
                if (isNotEmpty()) append(" ")
                append(it)
            }
        }
        val email = credential.email ?: ""

        val identityToken = credential.identityToken?.let {
            platform.Foundation.NSString.create(
                data = it,
                encoding = platform.Foundation.NSUTF8StringEncoding
            )?.toString()
        }

        deferred.complete(
            SocialAuthResult(
                id = userId,
                name = name,
                email = email,
                provider = AuthProvider.APPLE,
                idToken = identityToken
            )
        )
    }

    override fun authorizationController(
        controller: ASAuthorizationController,
        didCompleteWithError: NSError
    ) {
        deferred.completeExceptionally(
            Exception("Apple Sign-In failed: ${didCompleteWithError.localizedDescription}")
        )
    }

    @Suppress("CONFLICTING_OVERLOADS")
    override fun presentationAnchorForAuthorizationController(
        controller: ASAuthorizationController
    ): UIWindow {
        val scene = UIApplication.sharedApplication.connectedScenes.firstOrNull() as? UIWindowScene
        return scene?.windows?.firstOrNull { (it as? UIWindow)?.isKeyWindow() == true } as? UIWindow
            ?: UIWindow()
    }
}
