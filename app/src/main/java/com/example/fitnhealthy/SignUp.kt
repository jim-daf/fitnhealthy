package com.example.fitnhealthy

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fitnhealthy.R.id.signInNow
import com.example.fitnhealthy.SingleWorkout.Audio
import com.example.fitnhealthy.models.Session
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.regex.Pattern


class SignUp : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var audioList: ArrayList<String>
    private var averageHeartRate: Float = 0f
    private lateinit var date: String
    private var calories: Int = 0
    private lateinit var workoutTime: String


    public override fun onStart() {
        super.onStart()
        // Check if user is signed up
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(applicationContext, Login::class.java)
            startActivity(intent)
            finish()
        }
    }


    @SuppressLint("WrongViewCast", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        val editTextName=findViewById<TextInputEditText>(R.id.username)
        val editTextEmail = findViewById<TextInputEditText>(R.id.email)
        val editTextPassword = findViewById<TextInputEditText>(R.id.passwordText)
        val editTextConfirmPassword = findViewById<TextInputEditText>(R.id.confirmPasswordText)
        val buttonSignUp = findViewById<Button>(R.id.btn_signUp)

        val signInNow = findViewById<TextView>(signInNow)

        fun goToLogin(){
            val intent = Intent(applicationContext, Login::class.java)
            startActivity(intent)
            finish()
        }
        fun goToSignUp(){
            val intent = Intent(applicationContext, SignUp::class.java)
            startActivity(intent)
            finish()
        }
        signInNow.setOnClickListener {
            goToLogin()
        }
        buttonSignUp.setOnClickListener {

            // User credentials
            val username = editTextName.text.toString()
            val email= editTextEmail.text.toString()
            val password=editTextPassword.text.toString()
            val confirmPassword =editTextConfirmPassword.text.toString()

            // Database field initializations
            val experience="Beginner"
            val file = null
            val ui_theme_choice="light"
            val age = 0L
            val height = 0F
            val weight = 0F
            val gender = ""
            val target = ""
            audioList = ArrayList<String>()
            audioList.add("default")
            averageHeartRate=0f
            calories=0
            workoutTime=""
            date=""








            val progressBar=findViewById<ProgressBar>(R.id.progressBar)

            val fetchDataCallback = object : FetchDataCallback {
                override fun onDataFetched(usernameAlreadyExists: Boolean) {

                    if(usernameAlreadyExists){
                        Toast.makeText(this@SignUp,"username already exists",Toast.LENGTH_SHORT).show()
                    }
                    else if(TextUtils.isEmpty(username)){
                        Toast.makeText(this@SignUp,"Enter your name",Toast.LENGTH_SHORT).show()
                    }
                    else if(TextUtils.isEmpty(email)){
                        Toast.makeText(this@SignUp,"Enter your email",Toast.LENGTH_SHORT).show()
                    }
                    else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                        Toast.makeText(this@SignUp,"Incorrect email",Toast.LENGTH_SHORT).show()
                    }
                    else if(TextUtils.isEmpty(password)){
                        Toast.makeText(this@SignUp,"Enter a password",Toast.LENGTH_SHORT).show()

                    }
                    else if(TextUtils.isEmpty(confirmPassword)){
                        Toast.makeText(this@SignUp,"Re-write your password",Toast.LENGTH_SHORT).show()

                    }
                    else if (password!=confirmPassword){
                        Toast.makeText(this@SignUp,"Passwords must match",Toast.LENGTH_SHORT).show()

                    }
                    else if(password.length<6){
                        Toast.makeText(this@SignUp,"Password must contain at least 6 characters",Toast.LENGTH_SHORT).show()
                    }
                    else if(password.length>50 || confirmPassword.length>50 || email.length > 50 || username.length>30){
                        Toast.makeText(this@SignUp,"The credentials that you are giving are too long",Toast.LENGTH_SHORT).show()
                    }
                    else if(!Pattern.compile("[0-9]").matcher(password).find()){
                        Toast.makeText(this@SignUp,"Password must contain at least one digit",Toast.LENGTH_SHORT).show()
                    }
                    else if(!Pattern.compile("[A-Z]").matcher(password).find()){
                        Toast.makeText(this@SignUp,"Password must contain at least one uppercase letter",Toast.LENGTH_SHORT).show()
                    }
                    else if(!Pattern.compile("[a-z]").matcher(password).find()){
                        Toast.makeText(this@SignUp,"Password must contain at least one lowercase letter",Toast.LENGTH_SHORT).show()
                    }
                    else{
                        progressBar.visibility=View.VISIBLE
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                progressBar.visibility=View.GONE
                                if (task.isSuccessful) {
                                    val user = auth.currentUser
                                    val newUser = User(username, email, experience,file,ui_theme_choice,age,height,weight,target,gender,
                                        audioList
                                    )
                                    val userSession = Session(averageHeartRate,calories,workoutTime,date)

                                    database.child("/Users").child(user!!.uid).setValue(newUser).addOnSuccessListener {
                                        Toast.makeText(
                                            this@SignUp,
                                            "User saved to realtime database",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }.addOnFailureListener{
                                        Toast.makeText(
                                            this@SignUp,
                                            "Failed to save user to realtime database",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    user.sendEmailVerification().addOnSuccessListener {
                                        Toast.makeText(
                                            this@SignUp,
                                            "Verification Email has been sent to "+user.email,
                                            Toast.LENGTH_SHORT
                                        ).show()


                                        goToLogin()
                                    }.addOnFailureListener{
                                        Toast.makeText(
                                            this@SignUp,
                                            "Verification Email not sent",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    database.child("/Users").child(user.uid).child("Sessions").setValue(userSession)


                                } else {

                                    if (task.exception is FirebaseAuthUserCollisionException){
                                        Toast.makeText(
                                            this@SignUp,
                                            "User already exists",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    else{
                                        Toast.makeText(
                                            this@SignUp,
                                            "Authentication failed.",
                                            Toast.LENGTH_SHORT,
                                        ).show()

                                    }




                                }
                            }
                    }
                }
            }

            fetchData(username, fetchDataCallback)




        }

    }

    interface FetchDataCallback {
        fun onDataFetched(usernameAlreadyExists: Boolean)
    }

    fun fetchData(username: String, callback: FetchDataCallback) {
        FirebaseDatabase.getInstance().getReference("/Users").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var usernameAlreadyExists=false
                for (child in snapshot.children) {
                    val user = child.getValue(User::class.java)!!

                    if (user.username.equals(username)) {
                        usernameAlreadyExists = true
                        break
                    }

                }
                callback.onDataFetched(usernameAlreadyExists)
                // Invoke the callback method with the fetched data

            }

            override fun onCancelled(error: DatabaseError) {
                callback.onDataFetched(false)
            }
        })
    }
    override fun onBackPressed() {

        val intent = Intent(this@SignUp, Login::class.java)
        startActivity(intent)
        // Finish the current activity
        finish()
        super.onBackPressed()
    }


}