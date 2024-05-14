package com.example.myapplication

//import com.google.auth.oauth2.GoogleCredentials

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import com.example.fitnhealthy.R

import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth


class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    public override fun onStart() {
        //
        super.onStart()

// Issue operations with healthConnectClient


    }
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)




        auth=FirebaseAuth.getInstance()



        val currentUser = FirebaseAuth.getInstance().currentUser

        //currentUser!!.reload()

        if (currentUser != null && currentUser.isEmailVerified) {
            //Get username from database
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.putExtra("selected_theme","light")
            startActivity(intent)
            finish()




        }
        else{

            setContentView(R.layout.activity_login)




            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                requestNotificationPermission()
            }
            auth = FirebaseAuth.getInstance()
            val signUpNow = findViewById<TextView>(R.id.signUpNow)
            val editTextEmail = findViewById<TextInputEditText>(R.id.email)
            val editTextPassword = findViewById<TextInputEditText>(R.id.passwordText)
            val buttonSignIn = findViewById<Button>(R.id.btn_login)
            val progressBar=findViewById<ProgressBar>(R.id.progressBar)
            val resetPwd= findViewById<TextView>(R.id.forgotPwd)
            signUpNow.setOnClickListener {
                goToSignUp()
            }

            buttonSignIn.setOnClickListener {
                progressBar.visibility = View.VISIBLE
                val email = editTextEmail.text.toString()
                val password = editTextPassword.text.toString()


                if (TextUtils.isEmpty(email)) {

                    Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show()
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show()

                }

                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener() { task ->
                        val user=auth.currentUser

                        if (task.isSuccessful && user!!.isEmailVerified) {

                                val intent = Intent(applicationContext, MainActivity::class.java)
                                intent.putExtra("selected_theme","dark")
                                startActivity(intent)
                                finish()







                        }
                        else {
                            // If sign in fails, display a message to the user.
                            if(!(task.isSuccessful)){
                                Toast.makeText(
                                    this,
                                    "Incorrect email or password",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            else if (user != null) {
                                if (!user.isEmailVerified) {
                                    Toast.makeText(
                                        this,
                                        "Verify your email address",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else{
                                    Toast.makeText(
                                        this,
                                        "User Authentication failed",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }




            }
        }
    }

    private fun goToLogin(){
        val intent = Intent(applicationContext, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
    private fun goToSignUp(){
        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
        finish()
    }



    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {

        }
    }




    @RequiresApi(Build.VERSION_CODES.O)
    private fun requestNotificationPermission() {

        val notificationManagerCompat = NotificationManagerCompat.from(this)
        val areNotificationsEnabled = notificationManagerCompat.areNotificationsEnabled()

        if (!areNotificationsEnabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
            //startActivityForResult(intent, NOTIFICATION_PERMISSION_REQUEST_CODE)
        } else {
            // Notifications are already enabled
        }
    }



}