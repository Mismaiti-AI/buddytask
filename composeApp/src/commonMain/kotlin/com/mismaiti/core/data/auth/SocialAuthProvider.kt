package com.mismaiti.core.data.auth

/**
 * Platform-specific native sign-in.
 *
 * - **Android**: Launches Google Sign-In via Credential Manager
 * - **iOS**: Launches Apple Sign-In via AuthenticationServices
 *
 * This always performs the native OS sign-in flow regardless of whether
 * a backend is configured. See [AuthRepository] for how the result is handled.
 */
expect suspend fun signInWithSocialProvider(): SocialAuthResult
