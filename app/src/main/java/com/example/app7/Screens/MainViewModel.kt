package com.example.app7.Screens

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app7.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel : ViewModel () {
    private val isLoading = MutableLiveData(false)
    fun isLoading() : LiveData<Boolean> = isLoading
    fun LoginWithGoogle( activity : Activity){
        isLoading.postValue(true)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val client = GoogleSignIn.getClient(activity, gso)
        val signInIntent : Intent = client.getSignInIntent()
        activity.startActivityForResult(signInIntent, 1)
//        viewModelScope.launch{
//            delay(3000)
            isLoading.postValue(false)
        }
    fun finishLogin(task : Task<GoogleSignInAccount>){
        try{
            val account:GoogleSignInAccount? = task.getResult(ApiException::class.java)
            account?.idToken?.let{
                token ->
                val auth = FirebaseAuth.getInstance()
                val credential = GoogleAuthProvider.getCredential(token, null)
                auth.signInWithCredential(credential).addOnCompleteListener{
                    task ->
                    if (task.isSuccessful){
                        var user = auth.currentUser
                        Log.d("Ok", "Ingreso ${user?.displayName}")
                    }else{
                        Log.d("Error", "No se puede conectar")
                    }
                    isLoading.postValue(false)
                }
            }
        }
        catch(e : ApiException){
            Log.d(TAG, "signInResult:failed code=" + e.statusCode)
        }
        isLoading.postValue(false)
    }
}