package com.example.fitnhealthy

//import com.google.auth.oauth2.GoogleCredentials

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fitnhealthy.SingleWorkout.Audio
import com.example.fitnhealthy.models.Session
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import kotlin.collections.ArrayList


class Login : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var weight=0F
    private var height=0F
    private var age=0L
    private var gender=""
    private var ui_theme=""
    private var experience=""
    private var target=""
    private var username=""
    private var audioList= ArrayList<String>()
    private var datesList = ArrayList<String>()
    private var caloriesList = ArrayList<Int>()
    private var avgHeartRatesList = ArrayList<Float>()
    private var workoutTimesList = ArrayList<String>()
    private var avgHeartRatesArray = floatArrayOf()




    /*public override fun onStart() {
        //
        super.onStart()




    }

     */
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash_screen)
        auth=FirebaseAuth.getInstance()




        val currentUser = FirebaseAuth.getInstance().currentUser


        //currentUser!!.reload()

        if (currentUser != null && currentUser.isEmailVerified) {
            //Get username from database
            val reference =
                FirebaseDatabase.getInstance().getReference("/Users").child(currentUser.uid)

            reference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (child in snapshot.children) {
                        val user = snapshot.getValue(User::class.java)!!

                        ui_theme= user.ui_theme_choice.toString()
                        gender=user.gender.toString()
                        username=user.username.toString()
                        experience=user.experience.toString()
                        target=user.Target.toString()
                        weight=user.weight!!.toFloat()
                        height=user.height!!.toFloat()
                        age=user.age!!.toLong()
                        audioList=user.audioList!!

                        if (child.key.equals("Sessions")){
                            for (subChild in child.children){
                                val session = subChild.getValue(Session::class.java)
                                avgHeartRatesList.add(session!!.AverageHeartRate!!)
                                caloriesList.add(session.Calories!!)
                                workoutTimesList.add(session.WorkoutTime!!)
                                datesList.add(session.Date!!)
                            }
                        }
                        avgHeartRatesArray =  avgHeartRatesList.toFloatArray()

                        // Sign in success


                        val intent = Intent(applicationContext, Home::class.java)
                        intent.putExtra("selected_theme",ui_theme)
                        intent.putExtra("age",age)
                        intent.putExtra("weight",weight)
                        intent.putExtra("height",height)
                        intent.putExtra("username",username)
                        intent.putExtra("gender",gender)
                        intent.putExtra("experience",experience)
                        intent.putExtra("target",target)
                        intent.putExtra("savedAudioList",audioList)
                        intent.putExtra("datesList",datesList)
                        intent.putExtra("avgHeartRatesList",avgHeartRatesArray)
                        intent.putExtra("caloriesList",caloriesList)
                        intent.putExtra("workoutTimesList",workoutTimesList)

                        startActivity(intent)
                        finish()

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@Login, "Error", Toast.LENGTH_SHORT).show()

                }
            })



        }
        else{
            setContentView(R.layout.activity_login)
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
            resetPwd.setOnClickListener {
                val intent = Intent(applicationContext,ResetPassword::class.java)
                startActivity(intent)
                finish()
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
                            val reference =
                                FirebaseDatabase.getInstance().getReference("/Users")

                            reference.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    for (child in snapshot.children) {
                                        val user = child.getValue(User::class.java)!!
                                        ui_theme= user.ui_theme_choice.toString()
                                        gender=user.gender.toString()
                                        username=user.username.toString()
                                        experience=user.experience.toString()
                                        target=user.Target.toString()
                                        weight=user.weight!!.toFloat()
                                        height=user.height!!.toFloat()
                                        age=user.age!!.toLong()
                                        audioList=user.audioList!!

                                        if (child.key.equals("Sessions")){
                                            for (subChild in child.children){
                                                val session = subChild.getValue(Session::class.java)
                                                avgHeartRatesList.add(session!!.AverageHeartRate!!)
                                                caloriesList.add(session.Calories!!)
                                                workoutTimesList.add(session.WorkoutTime!!)
                                                datesList.add(session.Date!!)
                                            }
                                        }
                                        avgHeartRatesArray =  avgHeartRatesList.toFloatArray()


                                        // Sign in success
                                        progressBar.visibility=View.GONE

                                        val intent = Intent(applicationContext, Home::class.java)
                                        intent.putExtra("selected_theme",ui_theme)
                                        intent.putExtra("age",age)
                                        intent.putExtra("weight",weight)
                                        intent.putExtra("height",height)
                                        intent.putExtra("username",username)
                                        intent.putExtra("gender",gender)
                                        intent.putExtra("experience",experience)
                                        intent.putExtra("target",target)
                                        intent.putExtra("savedAudioList",audioList)
                                        intent.putExtra("datesList",datesList)
                                        intent.putExtra("avgHeartRatesList",avgHeartRatesArray)
                                        intent.putExtra("caloriesList",caloriesList)
                                        intent.putExtra("workoutTimesList",workoutTimesList)

                                        startActivity(intent)
                                        finish()

                                    }

                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Toast.makeText(this@Login, "Error", Toast.LENGTH_SHORT).show()

                                }
                            })




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
        val intent = Intent(applicationContext, Login::class.java)
        startActivity(intent)
        finish()
    }
    private fun goToSignUp(){
        val intent = Intent(applicationContext, SignUp::class.java)
        startActivity(intent)
        finish()
    }
    private fun goToMain(){

        val intent = Intent(applicationContext, Home::class.java)
        startActivity(intent)
        finish()
    }












}