package com.mytask.core.data.auth

/**
 * Result from native sign-in (Google/Apple).
 *
 * This is returned by [signInWithSocialProvider] after the user completes
 * the native OS sign-in prompt.
 *
 * @property id User identifier from the provider (Google email / Apple user ID)
 * @property name Display name from the provider
 * @property email Email address from the provider
 * @property provider Which social provider was used
 * @property idToken The provider's ID token (Google JWT / Apple identity token).
 *   Only needed when you have a backend — your server uses this to verify
 *   the sign-in server-side. Ignored in local-only mode.
 */
data class SocialAuthResult(
    val id: String,
    val name: String,
    val email: String,
    val provider: AuthProvider,
    val idToken: String? = null
)

enum class AuthProvider {
    GOOGLE,
    APPLE,
    EMAIL
}
