package com.example.fitnhealthy

import android.util.Log
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyPhoneListenerService : WearableListenerService() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)


    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onDestroy() {
        //arraylistOfHeartRates.clear()
        scope.cancel()
        super.onDestroy()
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)

        coroutineScope.launch {
            when (messageEvent.path) {
                HEART_RATE_VALUES_PATH -> {
                    val currentUser = getCurrentUser()
                    Log.d(TAG, String(messageEvent.data))
                    if (String(messageEvent.data).toFloat().toInt() == 0){
                        arraylistOfHeartRates.clear()
                    }
                    arraylistOfHeartRates.add(String(messageEvent.data).toFloat().toInt())

                    //Firebase.database.getReference("/Users").child(currentUser!!.uid).child("HeartRateValues").child("Session").setValue(arraylistOfHeartRates)
                }

            }
        }
    }
    private suspend fun getCurrentUser(): FirebaseUser? = withContext(Dispatchers.IO) {
        return@withContext Firebase.auth.currentUser
    }
    companion object{
        private const val TAG = "PhoneListenerService"
        private const val HEART_RATE_VALUES_PATH = "/heart_rate_values"

        var arraylistOfHeartRates=ArrayList<Int>()
    }

}