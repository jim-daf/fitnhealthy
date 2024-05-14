
package com.example.myapplication

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.fitnhealthy.databinding.ActivityMainBinding
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.Wearable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch


/**
 * Activity displaying the app UI. Notably, this binds data from [MainViewModel] to views on screen,
 * and performs the permission check when enabling measure data.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity(){

    private lateinit var binding: ActivityMainBinding
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private var activityContext: Context? = null
    private var transcriptionNodeId: String? = null
    private val viewModel: MainViewModel by viewModels()

    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser
    private lateinit var reference: DatabaseReference
    private lateinit var arrayOfHeartRateValues: ArrayList<Float>
    private var x=0
    private var y = 0

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityContext = this
        //auth = FirebaseAuth.getInstance()
        //currentUser = auth.currentUser!!
        //reference = FirebaseDatabase.getInstance().getReference("/Users").child(currentUser.uid)
          //  .child("/heartRateValues")
        binding = ActivityMainBinding.inflate(layoutInflater)
        arrayOfHeartRateValues = ArrayList<Float>()



        setContentView(binding.root)
        binding.stopbtn.setOnClickListener {
            //TODO GRAPHS
        }
        val graphView = binding.graph
        val series: LineGraphSeries<DataPoint> = LineGraphSeries<DataPoint>()
        // Enable drawing data points as markers
        series.isDrawDataPoints = true

        // Set the radius of the data points
        series.dataPointsRadius = 7f
        series.isDrawBackground = false
        series.thickness = 3
// Adjust the update frequency and interpolation
        val gridLabelRenderer = graphView.gridLabelRenderer
        gridLabelRenderer.textSize=14f
        gridLabelRenderer.padding=15

// Customize the text appearance for the axis values

        //graphView.viewport.isXAxisBoundsManual = true
        //graphView.viewport.setMinX(0.00)
        //graphView.viewport.setMaxX(50.00)
        series.setOnDataPointTapListener(null)  // Disable tap listener to prevent interaction with hidden points
        graphView.addSeries(series)
        graphView.title= "Heart Rate Graph"
         permissionLauncher =
             registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
                 when (result) {
                     true -> {
                         Log.i(TAG, "Body sensors permission granted")
                         // Only measure while the activity is at least in STARTED state.
                         // MeasureClient provides frequent updates, which requires increasing the
                         // sampling rate of device sensors, so we must be careful not to remain
                         // registered any longer than necessary.
                         lifecycleScope.launchWhenStarted {
                             viewModel.measureHeartRate()
                         }
                     }
                     false -> Log.i(TAG, "Body sensors permission not granted")
                 }
             }

         // Bind viewmodel state to the UI.
         lifecycleScope.launchWhenStarted{
             viewModel.uiState.collect {
                 updateViewVisiblity(it)
             }
         }


         lifecycleScope.launchWhenStarted {
             viewModel.heartRateAvailable.collect {
                 //binding.statusText.text = getString(R.string.measure_status, it)
             }
         }
         lifecycleScope.launchWhenStarted {
             viewModel.heartRateBpm.collect {
                 binding.lastMeasuredValue.text = String.format("%.1f", it)
                 x=x+1



                 y=String.format("%.0f", it).toInt()
                 series.appendData(DataPoint(x.toDouble(), y.toDouble()), true, 10)
                 graphView.addSeries(series)
                 arrayOfHeartRateValues.add(String.format("%.1f", it).toFloat())
                 lifecycleScope.launch(Dispatchers.IO) {
                     transcriptionNodeId = getNodes().first()?.also { nodeId ->
                         val sendTask: Task<*> =
                             Wearable.getMessageClient(applicationContext).sendMessage(
                                 nodeId,
                                 HEART_RATE_VALUES_PATH,
                                 String.format("%.1f", it).toByteArray() //data to send
                             ).apply {
                                 addOnSuccessListener { Log.d(TAG, "OnSuccess") }
                                 addOnFailureListener { Log.d(TAG, "OnFailure") }
                             }
                     }
                 }

             }
         }


    }



    // Code for heart rate
    override fun onStart() {
        super.onStart()
        permissionLauncher.launch(android.Manifest.permission.BODY_SENSORS)
    }
    private fun updateViewVisiblity(uiState: UiState) {
        (uiState is UiState.Startup).let {
            binding.progress.isVisible = it
        }
        // These views are visible when heart rate capability is not available.
        (uiState is UiState.HeartRateNotAvailable).let {
            //binding.brokenHeart.isVisible = it
            binding.notAvailable.isVisible = it
        }
        // These views are visible when the capability is available.
        (uiState is UiState.HeartRateAvailable).let {
            //binding.statusText.isVisible = it
            //binding.lastMeasuredLabel.isVisible = it
            binding.lastMeasuredValue.isVisible = it
            binding.heart.isVisible = it
        }
    }
    private fun getNodes(): Collection<String> {
        return Tasks.await(Wearable.getNodeClient(applicationContext).connectedNodes).map { it.id }
    }

    companion object{
        private const val TAG = "MainWearActivity"
        private const val STOP_ACTIVITY_PATH = "/stop-activity"
        private const val HEART_RATE_VALUES_PATH = "/heart_rate_values"
    }



}
