package com.mismaiti.core.data.auth

import co.touchlab.kermit.Logger
import com.mismaiti.core.data.local.AppDatabase
import com.mismaiti.core.data.local.model.UserEntity
import kotlin.coroutines.cancellation.CancellationException

// =============================================================================
// Social Auth — Two Modes
// =============================================================================
//
// MODE 1: LOCAL-ONLY (default, no backend)
// ─────────────────────────────────────────
//   Native sign-in (Google/Apple) → grab user profile (name, email) → save to Room.
//
//   This is NOT real authentication. There is no session, no token verification,
//   no security. It's a convenience for:
//     • Quick user profile setup (skip manual name/email entry)
//     • Personalizing the app locally
//     • Prototyping before a backend exists
//
//   To use: just call AuthRepository.signInWithSocial(). No extra config needed.
//
//
// MODE 2: WITH BACKEND (production)
// ─────────────────────────────────
//   Native sign-in (Google/Apple) → get ID token → send to your backend
//   → backend verifies token → returns authenticated user → save to Room.
//
//   This is real authentication. Your backend verifies the idToken:
//     • Google: verify JWT with Google's tokeninfo or your server library
//     • Apple: verify identity token with Apple's public keys
//
//   To enable:
//     1. Implement SocialAuthBackendHandler (see below)
//     2. Register it in Koin — that's it
//
// =============================================================================

/**
 * Implement this to connect native sign-in to your backend.
 *
 * The native sign-in always happens first (Google/Apple OS prompt).
 * This handler receives the result including [SocialAuthResult.idToken],
 * which your backend uses to verify the sign-in server-side.
 *
 * ## Setup
 * ```kotlin
 * // 1. Implement
 * class MySocialAuthBackend(private val api: MyApi) : SocialAuthBackendHandler {
 *     override suspend fun authenticate(result: SocialAuthResult): BackendAuthResponse {
 *         val response = api.socialLogin(
 *             provider = result.provider.name,
 *             idToken = result.idToken ?: error("Missing ID token"),
 *         )
 *         return BackendAuthResponse(
 *             userId = response.userId,
 *             name = response.name,
 *             email = response.email
 *         )
 *     }
 * }
 *
 * // 2. Register in appModule()
 * fun appModule() = module {
 *     single<SocialAuthBackendHandler> { MySocialAuthBackend(get()) }
 *     single { AuthRepository(get(), getOrNull()) }
 * }
 * ```
 */
interface SocialAuthBackendHandler {
    suspend fun authenticate(result: SocialAuthResult): BackendAuthResponse
}

data class BackendAuthResponse(
    val userId: String,
    val name: String,
    val email: String
)

class AuthRepository(
    private val database: AppDatabase,
    private val backendHandler: SocialAuthBackendHandler? = null
) {

    suspend fun signInWithSocial(): SocialSignInResult {
        // Step 1: Always perform native sign-in (Google/Apple OS prompt)
        val authResult = try {
            signInWithSocialProvider()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Logger.e("AuthRepository") { "Social sign-in failed: ${e.message}" }
            val message = e.message ?: "Sign-in failed"
            return if (isCancellation(message)) {
                SocialSignInResult.Cancelled
            } else {
                SocialSignInResult.Error(message)
            }
        }

        val providerName = authResult.provider.name.lowercase()

        // Step 2: If backend is configured, verify token server-side
        if (backendHandler != null) {
            val backendResponse = try {
                backendHandler.authenticate(authResult)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Logger.e("AuthRepository") { "Backend auth failed: ${e.message}" }
                return SocialSignInResult.Error(e.message ?: "Backend authentication failed")
            }

            return saveUser(
                name = backendResponse.name,
                email = backendResponse.email,
                providerName = providerName,
                providerUserId = backendResponse.userId
            )
        }

        // Step 2 (local-only): No backend — just grab profile info from native sign-in
        // NOTE: This is NOT authenticated. The user's name/email come directly from
        // Google/Apple without server-side verification. Fine for prototyping or
        // local-only apps, but add a SocialAuthBackendHandler for production auth.
        val existing = database.userDao.getByProviderId(providerName, authResult.id)
        if (existing != null) {
            return SocialSignInResult.Success(existing)
        }

        return saveUser(
            name = authResult.name,
            email = authResult.email,
            providerName = providerName,
            providerUserId = authResult.id
        )
    }

    fun getUserByProviderId(provider: String, providerUserId: String): UserEntity? {
        return database.userDao.getByProviderId(provider, providerUserId)
    }

    private fun saveUser(
        name: String,
        email: String,
        providerName: String,
        providerUserId: String
    ): SocialSignInResult {
        val user = UserEntity().apply {
            this.name = name
            this.email = email
            this.authProvider = providerName
            this.providerUserId = providerUserId
        }
        database.userDao.insert(user)

        val saved = database.userDao.getByProviderId(providerName, providerUserId)
        return if (saved != null) {
            SocialSignInResult.Success(saved)
        } else {
            SocialSignInResult.Error("Failed to save user")
        }
    }
}

private fun isCancellation(message: String): Boolean {
    val lower = message.lowercase()
    return "cancel" in lower || "user denied" in lower || "dismissed" in lower
}

sealed class SocialSignInResult {
    data class Success(val user: UserEntity) : SocialSignInResult()
    data object Cancelled : SocialSignInResult()
    data class Error(val message: String) : SocialSignInResult()
}
