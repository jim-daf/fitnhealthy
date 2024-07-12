package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Parcel
import android.util.Log

import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class DataLayerListenerService : WearableListenerService() {

    private val messageClient by lazy { Wearable.getMessageClient(this) }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)







    @SuppressLint("VisibleForTests")
    override fun onDataChanged(dataEvents: DataEventBuffer) {
        super.onDataChanged(dataEvents)


        dataEvents.forEach { dataEvent ->
            val uri = dataEvent.dataItem.uri
            when (uri.path) {
                COUNT_PATH -> {
                    scope.launch {
                        try {
                            val nodeId = uri.host!!
                            val payload = uri.toString().toByteArray()
                            messageClient.sendMessage(
                                nodeId,
                                DATA_ITEM_RECEIVED_PATH,
                                payload
                            )
                                .await()
                            Log.d(TAG, "Message sent successfully")
                        } catch (cancellationException: CancellationException) {
                            throw cancellationException
                        } catch (exception: Exception) {
                            Log.d(TAG, "Message failed")
                        }
                    }
                }
            }
        }
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)

        when (messageEvent.path) {

            START_ACTIVITY_PATH -> {
                isNotPaused=true
                Log.d("Start","Start")
                startActivity(
                    Intent(this, MainActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                )
            }
            STOP_ACTIVITY_PATH ->{
                Log.d("STOP","STOP")

                /*val closeAppIntent = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
                applicationContext.sendBroadcast(closeAppIntent)
                System.exit(0)
                mainActivity!!.finish()

                 */
                val intent = Intent("finish_activity")
                sendBroadcast(intent)
            }
            PAUSE_ACTIVITY_PATH ->{
                isNotPaused=false
            }
            CONTINUE_ACTIVITY_PATH ->{
                isNotPaused=true
            }

        }



    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    companion object {

        private const val TAG = "DataLayerService"
        private const val STOP_ACTIVITY_PATH = "/stop-activity"
        private const val START_ACTIVITY_PATH = "/start-activity"
        private const val PAUSE_ACTIVITY_PATH = "/pause-activity"
        private const val CONTINUE_ACTIVITY_PATH = "/continue-activity"
        private const val DATA_ITEM_RECEIVED_PATH = "/data-item-received"
        const val COUNT_PATH = "/count"
        var isNotPaused: Boolean = true

    }

}