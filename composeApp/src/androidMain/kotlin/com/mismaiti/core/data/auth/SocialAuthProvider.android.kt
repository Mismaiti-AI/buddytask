package com.mismaiti.core.data.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import org.koin.core.context.GlobalContext

/**
 * Google Sign-In implementation using Android Credential Manager API.
 *
 * ## Setup Required
 * 1. Go to [Google Cloud Console](https://console.cloud.google.com)
 * 2. Create OAuth 2.0 credentials -> **Web application** type
 * 3. Copy the Web Client ID and paste it into [WEB_CLIENT_ID] below
 * 4. Also create an **Android** OAuth client (SHA-1 + package name) — no code change needed
 */

/**
 * Replace with your Google Cloud Web Client ID.
 * Get it from: Google Cloud Console -> APIs & Services -> Credentials -> OAuth 2.0 Client IDs -> Web application
 */
const val WEB_CLIENT_ID = "YOUR_WEB_CLIENT_ID_HERE"

actual suspend fun signInWithSocialProvider(): SocialAuthResult {
    val context: Context = GlobalContext.get().get()

    val credentialManager = CredentialManager.create(context)

    val googleIdOption = GetSignInWithGoogleOption.Builder(WEB_CLIENT_ID)
        .build()

    val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    val result = credentialManager.getCredential(
        request = request,
        context = context
    )

    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(result.credential.data)

    return SocialAuthResult(
        id = googleIdTokenCredential.id,
        name = googleIdTokenCredential.displayName ?: "",
        email = googleIdTokenCredential.id,
        provider = AuthProvider.GOOGLE,
        idToken = googleIdTokenCredential.idToken
    )
}
