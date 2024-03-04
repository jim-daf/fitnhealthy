package com.example.fitnhealthy

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.storage.FirebaseStorage
import java.math.BigDecimal
import java.math.RoundingMode

class ProfileSetup : AppCompatActivity() {


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup_profile)
        val saveDataBtn = findViewById<Button>(R.id.saveProfData)
        val editTextName=findViewById<TextInputEditText>(R.id.newUsername)
        val editTextAge = findViewById<TextInputEditText>(R.id.newAge)
        val editTextHeight = findViewById<TextInputEditText>(R.id.newHeight)
        val editTextWeight = findViewById<TextInputEditText>(R.id.newWeight)
        val newImage=findViewById<CardView>(R.id.circularImageIdProf)
        val emailTextView=findViewById<TextView>(R.id.profileEmail)
        emailTextView.setText(FirebaseAuth.getInstance().currentUser!!.email)
        val usernameTextView=findViewById<TextView>(R.id.profileUsername)
        val reference=FirebaseDatabase.getInstance().getReference("/Users")
        val currentUser=FirebaseAuth.getInstance().currentUser
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (child in snapshot.children) {
                    val user = child.getValue(User::class.java)!!
                    if (child.key.equals(currentUser!!.uid)){
                        usernameTextView.setText(user.username)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled event if needed
            }
        })

        val spinner = findViewById<Spinner>(R.id.dropdown_experiences)

        // Set selected item by position
        spinner.setSelection(0) // Selects the third item (position 2) as the default

        // Set selected item by value
        val items = resources.getStringArray(R.array.experience_values)
        val selectedItem = "Beginner" // Set the desired item value
        val position = items.indexOf(selectedItem)
        spinner.prompt="Select training difficulty level"



        newImage.setOnClickListener {
            ImagePicker.with(this)
                .crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(4096)			//Final image size will be less than 4 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start()
        }
        saveDataBtn.setOnClickListener {


            val username = editTextName.text.toString()
            val age = editTextAge.text.toString()
            val height = editTextHeight.text.toString()
            val weight = editTextWeight.text.toString()



            val fetchDataCallback = object : ProfileSetup.FetchDataCallback {
                override fun onDataFetched(usernameAlreadyExists: Boolean) {

                    if (usernameAlreadyExists) {
                        Toast.makeText(this@ProfileSetup, "Username already exists", Toast.LENGTH_SHORT)
                            .show()
                    } else if (TextUtils.isEmpty(username) && TextUtils.isEmpty(age.toString()) && TextUtils.isEmpty(height.toString()) && TextUtils.isEmpty(weight.toString())) {
                        Toast.makeText(this@ProfileSetup, "All fields are empty", Toast.LENGTH_SHORT).show()
                    } else if (username.length > 30) {
                        Toast.makeText(
                            this@ProfileSetup, "Username has too many characters",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (!age.toString().isEmpty() && (Integer.valueOf(age)<=0 || Integer.valueOf(age)>100)) {
                        Toast.makeText(
                            this@ProfileSetup,
                            "Invalid input for field age",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (!height.toString().isEmpty() && (height.toFloat()<0.50 || height.toFloat()>2.40)){
                        Toast.makeText(
                            this@ProfileSetup,
                            "Invalid input for field height",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (!weight.toString().isEmpty() && (weight.toFloat()<10 || weight.toFloat()>350)){
                        Toast.makeText(
                            this@ProfileSetup,
                            "Invalid input for field weight",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else {
                        val currentUser=FirebaseAuth.getInstance().currentUser
                        if (currentUser != null) {
                            if (!TextUtils.isEmpty(username)){
                                FirebaseDatabase.getInstance().getReference("/Users").child(currentUser.uid).child("username").setValue(username).addOnSuccessListener {

                                    usernameTextView.setText(username)
                                }

                            }
                            if(!TextUtils.isEmpty(age.toString())){
                                FirebaseDatabase.getInstance().getReference("/Users").child(currentUser.uid).child("age").setValue(Integer.valueOf(age)).addOnSuccessListener {


                                }
                            }
                            if(!TextUtils.isEmpty(height.toString())){
                                val heightFormatted = BigDecimal(height).setScale(2, RoundingMode.HALF_UP)

                                FirebaseDatabase.getInstance().getReference("/Users").child(currentUser.uid).child("height").setValue(heightFormatted.toDouble())
                            }
                            if(!TextUtils.isEmpty(weight.toString())){
                                val weightFormatted = BigDecimal(weight).setScale(2, RoundingMode.HALF_UP)

                                FirebaseDatabase.getInstance().getReference("/Users").child(currentUser.uid).child("weight").setValue(weightFormatted.toDouble())
                            }
                            FirebaseDatabase.getInstance().getReference("/Users").child(currentUser.uid).child("experience").setValue(spinner.selectedItem.toString())
                            Toast.makeText(this@ProfileSetup, "New data saved", Toast.LENGTH_SHORT).show()

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


        FirebaseDatabase.getInstance().getReference("/Users").addListenerForSingleValueEvent(object :
            ValueEventListener {
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
                // Handle the error if needed
                //callback.onDataFetched(false)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val imageProf=findViewById<ImageView>(R.id.image_prof)
        val currentUser = FirebaseAuth.getInstance().currentUser
        imageProf.setImageURI(data?.data)
        val uriString = data?.data.toString()
        FirebaseDatabase.getInstance().getReference("/Users").child(currentUser!!.uid).child("profileImageUri").setValue(FirebaseStorage.getInstance().getReference("/profileImages/"+currentUser.uid).toString())
        val progressDialog = ProgressDialog.show(this, "Image upload", "Uploading...", true)
        val storageReference=FirebaseStorage.getInstance().getReference("/profileImages").child(currentUser.uid)
        storageReference.downloadUrl.addOnSuccessListener { uri ->
            val downloadUrl = uri.toString()
            FirebaseDatabase.getInstance().getReference("/Users").child(currentUser!!.uid).child("profileImageUri").setValue(downloadUrl)
            // Use the download URL as needed (e.g., save it to the database)
        }.addOnFailureListener { exception ->
            // Handle any errors that occurred while retrieving the download URL
        }

        try {
            storageReference.putFile(data?.data!!).addOnProgressListener {
                progressDialog.show()
            }.addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this@ProfileSetup, "Image Uploaded", Toast.LENGTH_SHORT)
                    .show()
            }.addOnFailureListener {
                Toast.makeText(this@ProfileSetup, "Image Not Uploaded", Toast.LENGTH_SHORT)
                    .show()
                progressDialog.dismiss()
            }
        } catch (error: RuntimeException){
            progressDialog.dismiss()
        } catch (error:NullPointerException){
            progressDialog.dismiss()
        }



    }
    override fun onBackPressed() {
        // Create an intent to start a new activity
        val getRole = intent
        val userRole = getRole.getStringExtra("role")
        val intent = Intent(this@ProfileSetup, Home::class.java)
        startActivity(intent)
        // Finish the current activity
        finish()

        super.onBackPressed()
    }
}