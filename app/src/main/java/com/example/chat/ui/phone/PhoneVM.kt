package com.example.chat.ui.phone

import android.app.Activity
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.chat.ui.auth.TextFieldVM
import com.example.chat.ui.base.BaseViewModel
import com.example.domain.common.Result
import com.example.domain.use_cases.remote.LinkPhoneToAccountUseCase
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class PhoneVM(
    private val linkPhoneToAccountUseCase: LinkPhoneToAccountUseCase
): BaseViewModel<PhoneContract.Event, PhoneContract.State, PhoneContract.Effect>() {

    companion object {
        private const val TIMEOUT = 60L
    }

    private var verificationId: String? = null
    private var token: PhoneAuthProvider.ForceResendingToken? = null

    val phoneNumberVM = TextFieldVM()
    val smsCodeVM = TextFieldVM(maxCount = 6)

    init {
        phoneNumberVM.onValueChanged(Firebase.auth.currentUser?.phoneNumber?.drop(4) ?: "")
    }

    override fun createInitialState(): PhoneContract.State {
        return PhoneContract.State(
            smsIsSending = false,
            applyChangedInProgress = false
        )
    }

    private fun linkWithCredentials(block: suspend () -> Result<Unit>) {
        setState { copy(applyChangedInProgress = true) }
        if(verificationId != null) {
            viewModelScope.launch(Dispatchers.IO) {
                val result = block()
                val effect = when(result) {
                    is Result.Success -> PhoneContract.Effect.PhoneSavedSuccessfully
                    is Result.Failure -> PhoneContract.Effect.PhoneSaveFailure(result.throwable.message)
                }
                setState { copy(applyChangedInProgress = false) }
                setEffect { effect }
            }
        }
    }

    private fun linkWithCredentials() {
        linkWithCredentials {
            linkPhoneToAccountUseCase(phoneNumberVM.value, verificationId!!, smsCodeVM.value)
        }
    }

    private fun linkWithCredentials(credential: PhoneAuthCredential) {
        linkWithCredentials {
            linkPhoneToAccountUseCase(phoneNumberVM.value, credential)
        }
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            Log.d("asd", "onVerificationCompleted:$credential")
            linkWithCredentials(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.w("asd", "onVerificationFailed", e)

            e.printStackTrace()

            setState { copy(smsIsSending = false) }
            setEffect { PhoneContract.Effect.PhoneSaveFailure(e.message) }
        }

        override fun onCodeSent(_verificationId: String, _token: PhoneAuthProvider.ForceResendingToken) {
            Log.d("asd", "verificationId $verificationId")
            verificationId = _verificationId
            token = _token
            setState { copy(smsIsSending = false) }
            setEffect { PhoneContract.Effect.SmsSended }
        }

        override fun onCodeAutoRetrievalTimeOut(p0: String) {
            verificationId = null
            setState { copy(smsIsSending = false) }
        }
    }

    override fun handleEvent(event: PhoneContract.Event) {
        when(event) {
            PhoneContract.Event.OnSmsCodeValidate -> smsCodeValidate()
            is PhoneContract.Event.OnPhoneNumberVerify -> verifyPhoneNumber(event.activity)
        }
    }

    private fun smsCodeValidate() {
        if(verificationId != null) {
            linkWithCredentials()
        }
    }

    private fun verifyPhoneNumber(activity: Activity) {
        setState { copy(smsIsSending = true) }
        val options = PhoneAuthOptions.newBuilder(Firebase.auth)
            .setPhoneNumber("+375" + phoneNumberVM.value)       // Phone number to verify
            .setTimeout(TIMEOUT, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(activity)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }
}