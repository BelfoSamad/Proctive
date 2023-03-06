package net.roeia.proctive.usecases

import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.userProfileChangeRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import net.roeia.proctive.data.Response
import net.roeia.proctive.data.Status
import net.roeia.proctive.utils.*
import javax.inject.Inject

class AuthUseCase @Inject constructor(private val auth: FirebaseAuth) {

    fun register(
        username: String,
        email: String,
        password: String
    ): Flow<Response<String>> = flow {
        val user = auth.createUserWithEmailAndPassword(email, password)
            .await()
            .user!!
        //Update Profile
        user.updateProfile(userProfileChangeRequest {
            this.displayName = username
        }).await()
        //Send Email Verification
        user.sendEmailVerification().await()
        //Return Data
        emit(
            try {
                Response(Status.SUCCESS, user.uid, null)
            } catch (e: FirebaseAuthWeakPasswordException) {
                Response(Status.ERROR, null, AUTH_WEAK_PASSWORD_ERROR)
            }
        )
    }

    fun login(email: String, password: String): Flow<Response<String>> = flow {
        emit(
            try {
                Response(
                    Status.SUCCESS,
                    auth.signInWithEmailAndPassword(email, password).await().user?.uid,
                    null
                )
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                Response(Status.ERROR, null, AUTH_WRONG_CREDENTIALS_ERROR)
            } catch (e: FirebaseAuthUserCollisionException) {
                Response(Status.ERROR, null, AUTH_USER_COLLISION_ERROR)
            }
        )
    }

    fun getUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun logout() {
        auth.signOut()
    }
}