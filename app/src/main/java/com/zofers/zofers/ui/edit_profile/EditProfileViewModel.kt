package com.zofers.zofers.ui.edit_profile

import androidx.lifecycle.MutableLiveData
import com.zofers.zofers.AppViewModel
import com.zofers.zofers.staff.States

class EditProfileViewModel : AppViewModel() {

    val emptyFieldError = MutableLiveData<Boolean>()
    val invalidEmailError = MutableLiveData<Boolean>()

    fun init() {

    }

    fun save (userName: String, email: String, description: String) {
        if (userName.isEmpty() || email.isEmpty()) {
            emptyFieldError.value = true
            return
        }
        if (!email.contains('@')) {
            invalidEmailError.value = true
            return
        }

        state.value = States.LOADING
        val editFieldsMap = mutableMapOf<String, String>()

        if (email != auth.currentUser?.email) {
            firebaseService.updateEmail(email) {}
            //todo await
        }
        if (userName != currentUser?.name) {
            editFieldsMap["name"] = userName
            firebaseService.updateUserName(userName) {}
            //todo await
        }
        if (description != currentUser?.description) {
            editFieldsMap["description"] = description
        }

        if (editFieldsMap.isNotEmpty()) {
            firebaseService.updateDocument("profile", currentUser!!.id, editFieldsMap) {}
        }

    }


}
