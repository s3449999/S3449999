package uk.ac.tees.mad.bookish.ui.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import uk.ac.tees.mad.bookish.domain.UserPreferences

class ProfileViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ViewModel() {
    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val profileState = _profileState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            auth.currentUser?.let { user ->
                try {

                    val firestoreData = firestore.collection("users")
                        .document(user.uid)
                        .get()
                        .await()

                    val updatedPrefs = UserPreferences(
                        userId = user.uid,
                        name = firestoreData.getString("name") ?: user.displayName ?: "",
                        email = user.email ?: "",
                        photoUrl = firestoreData.getString("photoUrl") ?: user.photoUrl?.toString()
                    )

                    _profileState.value = ProfileState.Success(updatedPrefs)
                } catch (e: Exception) {
                    if (_profileState.value !is ProfileState.Success) {
                        _profileState.value =
                            ProfileState.Error(e.message ?: "Failed to load profile")
                    }
                }
            }
        }
    }

    fun updateProfile(name: String, photoUri: Uri?) {
        viewModelScope.launch {
            _profileState.value = ProfileState.Loading
            try {
                auth.currentUser?.let { user ->
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .apply {
                            photoUri?.let { setPhotoUri(it) }
                        }
                        .build()
                    user.updateProfile(profileUpdates).await()

                    // Update Firestore
                    val userData = hashMapOf(
                        "name" to name,
                        "photoUrl" to photoUri?.toString()
                    )
                    firestore.collection("users")
                        .document(user.uid)
                        .update(userData as Map<String, Any>)
                        .await()

                    // Update local preferences
                    val updatedPrefs = UserPreferences(
                        userId = user.uid,
                        name = name,
                        email = user.email ?: "",
                        photoUrl = photoUri?.toString()
                    )
                    _profileState.value = ProfileState.Success(updatedPrefs)
                }
            } catch (e: Exception) {
                _profileState.value = ProfileState.Error(e.message ?: "Failed to update profile")
            }
        }
    }

    fun signOut() {
        auth.signOut()
    }
}

sealed class ProfileState {
    object Loading : ProfileState()
    data class Success(val userPreferences: UserPreferences) : ProfileState()
    data class Error(val message: String) : ProfileState()
}