package com.example.fitnhealthy

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class ProfileSetup : AppCompatActivity() {

    private var toggle: ActionBarDrawerToggle? = null
    private var user_theme: String?=null
    private var drawer: DrawerLayout? = null
    private var toolbar: Toolbar? = null
    private var auth: FirebaseAuth? = null
    private var currentUser: FirebaseUser? = null
    private var newImageUploaded: Boolean? = null
    private var imageRemoved: Boolean? = null
    private var navigatedFrom: String?=null

    


    @SuppressLint("MissingInflatedId", "UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nav_drawer)

        //Initialize layout visibilities
        val homeLayout = findViewById<View>(R.id.homeScreenLayout) as ScrollView
        val workoutsLayout = findViewById<View>(R.id.workoutsLayoutLight) as ScrollView
        val workoutOptionsLayout = findViewById<View>(R.id.selectionCategoriesLayout) as ScrollView
        val profileSetupLayout = findViewById<View>(R.id.profileSetupLayout) as ScrollView
        val settingsLayout = findViewById<View>(R.id.settingsLayout) as ScrollView

        homeLayout.visibility = View.GONE
        workoutOptionsLayout.visibility = View.GONE
        workoutsLayout.visibility = View.GONE
        profileSetupLayout.visibility = View.VISIBLE
        settingsLayout.visibility = View.GONE


        imageRemoved=false
        newImageUploaded=false
        auth = FirebaseAuth.getInstance()

        // Nav Drawer
        drawer = findViewById<DrawerLayout>(R.id.drawerLayout)
        toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar!!.setNavigationIcon(R.drawable.ic_hamburger_menu)
        toolbar!!.title = "Profile Setup"

        toggle = ActionBarDrawerToggle(
            this,
            drawer,
            toolbar,
            R.string.nav_open,
            R.string.nav_close
        )


        drawer!!.addDrawerListener(toggle!!)
        toggle!!.syncState()

        val navigationView = findViewById<NavigationView>(R.id.nav_view)

        navigationView.setNavigationItemSelectedListener(NavigationView.OnNavigationItemSelectedListener { item: MenuItem? ->
            item?.let {
                this.onNavigationItemSelected(
                    it
                )
            } == true
        })

        val saveDataBtn = findViewById<Button>(R.id.saveProfData)
        val editTextName=findViewById<TextInputEditText>(R.id.newUsername)
        val editTextPwd   = findViewById<TextInputEditText>(R.id.newPwd)
        val editTextImageUpload = findViewById<TextInputEditText>(R.id.uploadImage)
        val editTextImageRemove = findViewById<TextInputEditText>(R.id.removeImage)



        val newImage=findViewById<CardView>(R.id.circularImageIdProf)
        val imageProf=findViewById<ImageView>(R.id.image_prof)
        val emailTextView=findViewById<TextView>(R.id.profileEmail)
        emailTextView.setText(FirebaseAuth.getInstance().currentUser!!.email)
        val usernameTextView=findViewById<TextView>(R.id.profileUsername)
        val updateDataTextView=findViewById<TextView>(R.id.changeData)
        val reference=FirebaseDatabase.getInstance().getReference("/Users")
        val currentUser=FirebaseAuth.getInstance().currentUser


        //UI Theme
        val intent = intent
        navigatedFrom=intent.getStringExtra("navigatedFrom")
        user_theme = intent.getStringExtra("selected_theme")
        if (user_theme == "light") {
            toolbar!!.setBackgroundColor(Color.parseColor("#FFFFFF"))
            toolbar!!.setTitleTextColor(Color.parseColor("#000000"))

            profileSetupLayout.setBackgroundColor(Color.parseColor("#DAE4D6D6"))

        } else if (user_theme == "dark") {
            toolbar!!.setBackgroundColor(Color.parseColor("#E62E2D2D"))
            toolbar!!.setTitleTextColor(Color.parseColor("#FFFFFF"))

            usernameTextView.setTextColor(Color.parseColor("#ffffff"))
            emailTextView.setTextColor(Color.parseColor("#ffffff"))
            updateDataTextView.setTextColor(Color.parseColor("#ffffff"))

            editTextName.setBackgroundResource(R.drawable.input_background_dark)
            editTextPwd.setBackgroundResource(R.drawable.input_background_dark)
            editTextImageUpload.setBackgroundResource(R.drawable.input_background_dark)
            editTextImageRemove.setBackgroundResource(R.drawable.input_background_dark)
            saveDataBtn.setBackgroundResource(R.drawable.input_background_dark)

            editTextName.setTextColor(Color.parseColor("#ffffff"))
            editTextPwd.setTextColor(Color.parseColor("#ffffff"))
            editTextImageUpload.setTextColor(Color.parseColor("#ffffff"))
            editTextImageRemove.setTextColor(Color.parseColor("#ffffff"))
            saveDataBtn.setTextColor(Color.parseColor("#ffffff"))

            editTextName.setHintTextColor(Color.parseColor("#DCFFFFFF"))
            editTextPwd.setHintTextColor(Color.parseColor("#DCFFFFFF"))
            editTextImageUpload.setHintTextColor(Color.parseColor("#DCFFFFFF"))
            editTextImageRemove.setHintTextColor(Color.parseColor("#DCFFFFFF"))

            editTextName.setCompoundDrawablesRelativeWithIntrinsicBounds(resources.getDrawable(R.drawable.ic_person,theme),null,null,null)
            editTextPwd.setCompoundDrawablesRelativeWithIntrinsicBounds(resources.getDrawable(R.drawable.ic_pwd_white,theme),null,null,null)
            editTextImageUpload.setCompoundDrawablesRelativeWithIntrinsicBounds(resources.getDrawable(R.drawable.add_img_white,theme),null,null,null)
            editTextImageRemove.setCompoundDrawablesRelativeWithIntrinsicBounds(resources.getDrawable(R.drawable.delete_img_white,theme),null,null,null)

            /*
            for (i in 0 until gridLayout.getChildCount()) {
                if (gridLayout.getChildAt(i) is CardView) {
                    val cardView = gridLayout.getChildAt(i)
                    cardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
                    for (j in 0 until cardView.childCount) {
                        if (cardView.getChildAt(j) is LinearLayout) {
                            val layout = cardView.getChildAt(j) as LinearLayout
                            for (k in 0 until layout.childCount) {
                                if (layout.getChildAt(k) is TextView) {
                                    val txtView = layout.getChildAt(k) as TextView
                                    txtView.setTextColor(Color.parseColor("#B5000000"))
                                }
                            }
                        }
                    }
                }
            }

             */

            profileSetupLayout.setBackgroundColor(Color.parseColor("#AB000000"))
        } else {

            val reference =
                FirebaseDatabase.getInstance().getReference("/Users").child(currentUser!!.getUid())
                    .child("ui_theme_choice")
            reference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value == "light") {
                        toolbar!!.setBackgroundColor(Color.parseColor("#E62E2D2D"))
                        toolbar!!.setTitleTextColor(Color.parseColor("#FFFFFF"))

                        profileSetupLayout.setBackgroundColor(Color.parseColor("#DAE4D6D6"))

                    } else if (snapshot.value == "dark") {
                        toolbar!!.setBackgroundColor(Color.parseColor("#E62E2D2D"))
                        toolbar!!.setTitleTextColor(Color.parseColor("#FFFFFF"))

                        usernameTextView.setTextColor(Color.parseColor("#ffffff"))
                        emailTextView.setTextColor(Color.parseColor("#ffffff"))
                        updateDataTextView.setTextColor(Color.parseColor("#ffffff"))

                        editTextName.setBackgroundResource(R.drawable.input_background_dark)
                        editTextPwd.setBackgroundResource(R.drawable.input_background_dark)
                        editTextImageUpload.setBackgroundResource(R.drawable.input_background_dark)
                        editTextImageRemove.setBackgroundResource(R.drawable.input_background_dark)
                        saveDataBtn.setBackgroundResource(R.drawable.input_background_dark)

                        editTextName.setTextColor(Color.parseColor("#ffffff"))
                        editTextPwd.setTextColor(Color.parseColor("#ffffff"))
                        editTextImageUpload.setTextColor(Color.parseColor("#ffffff"))
                        editTextImageRemove.setTextColor(Color.parseColor("#ffffff"))
                        saveDataBtn.setTextColor(Color.parseColor("#ffffff"))

                        editTextName.setHintTextColor(Color.parseColor("#DCFFFFFF"))
                        editTextPwd.setHintTextColor(Color.parseColor("#DCFFFFFF"))
                        editTextImageUpload.setHintTextColor(Color.parseColor("#DCFFFFFF"))
                        editTextImageRemove.setHintTextColor(Color.parseColor("#DCFFFFFF"))

                        editTextName.setCompoundDrawablesRelativeWithIntrinsicBounds(resources.getDrawable(R.drawable.ic_person,theme),null,null,null)
                        editTextPwd.setCompoundDrawablesRelativeWithIntrinsicBounds(resources.getDrawable(R.drawable.ic_pwd_white,theme),null,null,null)
                        editTextImageUpload.setCompoundDrawablesRelativeWithIntrinsicBounds(resources.getDrawable(R.drawable.add_img_white,theme),null,null,null)
                        editTextImageRemove.setCompoundDrawablesRelativeWithIntrinsicBounds(resources.getDrawable(R.drawable.delete_img_white,theme),null,null,null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }



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

        /*
        val spinner = findViewById<Spinner>(R.id.dropdown_experiences)

        // Set selected item by position
        spinner.setSelection(0) // Selects the third item (position 2) as the default

        // Set selected item by value
        val items = resources.getStringArray(R.array.experience_values)
        val selectedItem = "Beginner" // Set the desired item value
        val position = items.indexOf(selectedItem)
        spinner.prompt="Select training difficulty level"

*/

        val imageUri = FirebaseDatabase.getInstance().getReference("/Users").child(
            currentUser!!.uid
        )

        imageUri.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (child in snapshot.children) {
                    if (child.key == "profileImageUrl") {
                        if (child.value != "" && child.value != "null" && child.value != null) {
                            Glide.with(this@ProfileSetup)
                                .load(child.value)
                                .into(imageProf)
                            val navHeaderImage= findViewById<ImageView>(R.id.nav_header_image)
                            Glide.with(this@ProfileSetup)
                                .load(child.value)
                                .into(navHeaderImage)

                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled event if needed
            }
        })


        newImage.setOnClickListener {
            ImagePicker.with(this)
                .crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(4096)			//Final image size will be less than 4 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start()
        }
        editTextImageUpload.setOnClickListener {
            ImagePicker.with(this)
                .crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(4096)			//Final image size will be less than 4 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start()
        }
        editTextImageRemove.setOnClickListener {
            imageRemoved=true
            FirebaseStorage.getInstance().getReference("/profileImages").child(currentUser!!.uid).delete().addOnSuccessListener {
                Toast.makeText(this@ProfileSetup, "Image succesfully removed", Toast.LENGTH_SHORT)
                    .show()
            }.addOnFailureListener {
                Toast.makeText(this@ProfileSetup, "Something went wrong", Toast.LENGTH_SHORT)
                    .show()
            }
            FirebaseDatabase.getInstance().getReference("/Users").child(currentUser.uid.toString()).child("profileImageUrl").setValue("")
            val navHeader= findViewById<ImageView>(R.id.nav_header_image)
            imageProf.setImageDrawable(resources.getDrawable(R.drawable.person_profile))
            navHeader.setImageDrawable(resources.getDrawable(R.drawable.person_profile))

        }
        saveDataBtn.setOnClickListener {


            val username = editTextName.text.toString()
            val password = editTextPwd.text.toString()

            //val age = editTextAge.text.toString()
            //val height = editTextHeight.text.toString()
            //val weight = editTextWeight.text.toString()



            val fetchDataCallback = object : ProfileSetup.FetchDataCallback {
                override fun onDataFetched(usernameAlreadyExists: Boolean) {

                    if (usernameAlreadyExists) {
                        Toast.makeText(this@ProfileSetup, "Username already exists", Toast.LENGTH_SHORT)
                            .show()
                    } else if (username.length > 30) {
                        Toast.makeText(
                            this@ProfileSetup, "Username has too many characters",
                            Toast.LENGTH_SHORT
                        ).show()
                    }



                    else {
                        val currentUser=FirebaseAuth.getInstance().currentUser
                        if (currentUser != null) {
                            val storageReference=FirebaseStorage.getInstance().getReference("/profileImages").child(currentUser.uid)
                            if (newImageUploaded!!){
                                storageReference.downloadUrl.addOnSuccessListener { uri ->
                                    val downloadUrl = uri.toString()
                                    FirebaseDatabase.getInstance().getReference("/Users").child(currentUser.uid).child("profileImageUrl").setValue(downloadUrl)

                                }.addOnFailureListener { exception ->
                                    // Handle any errors that occurred while retrieving the download URL
                                }
                            }
                            if (!TextUtils.isEmpty(username)){

                                FirebaseDatabase.getInstance().getReference("/Users").child(currentUser.uid).child("username").setValue(username).addOnSuccessListener {
                                    usernameTextView.setText(username)
                                }

                            }
                            if(!TextUtils.isEmpty(password)){
                                currentUser.updatePassword(password).addOnSuccessListener {

                                }.addOnFailureListener{exception ->
                                    // Handle any errors
                                }
                            }

                            //FirebaseDatabase.getInstance().getReference("/Users").child(currentUser.uid).child("experience").setValue(spinner.selectedItem.toString())
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
        val navHeader= findViewById<ImageView>(R.id.nav_header_image)

        navHeader.setImageURI(data?.data)

        //FirebaseDatabase.getInstance().getReference("/Users").child(currentUser!!.uid).child("profileImageUrl").setValue(FirebaseStorage.getInstance().getReference("/profileImages/"+currentUser.uid).toString())
        val progressDialog = ProgressDialog.show(this, "Image upload", "Uploading...", true)
        val storageReference=FirebaseStorage.getInstance().getReference("/profileImages").child(currentUser!!.uid)


        try {
            storageReference.putFile(data?.data!!).addOnProgressListener {
                progressDialog.show()
            }.addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this@ProfileSetup, "Image Uploaded", Toast.LENGTH_SHORT)
                    .show()
                newImageUploaded=true
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
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.overflow_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (toggle!!.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }

    private fun onNavigationItemSelected(item: MenuItem): Boolean? {
        if (item.itemId == R.id.nav_home) {
            Handler().postDelayed({
                val intent = Intent(this@ProfileSetup, Home::class.java)
                intent.putExtra("selected_theme", user_theme.toString())
                startActivity(intent)
                finish()
            }, 410) // Delay in milliseconds
        } else if (item.itemId == R.id.nav_profile_setup) {
            Handler().postDelayed({
                val intent = Intent(
                    this@ProfileSetup,
                    ProfileSetup::class.java
                )
                intent.putExtra("selected_theme", user_theme.toString())
                startActivity(intent)
                finish()
            }, 410)
        } else if (item.itemId == R.id.nav_workout) {
            Handler().postDelayed({
                val intent = Intent(
                    this@ProfileSetup,
                    WorkoutOptions::class.java
                )
                intent.putExtra("selected_theme", user_theme.toString())
                startActivity(intent)
                finish()
            }, 410)
        } else if (item.itemId == R.id.nav_stats) {
            Handler().postDelayed({
                val intent = Intent(this@ProfileSetup, Home::class.java)
                intent.putExtra("selected_theme", user_theme.toString())
                startActivity(intent)
                finish()
            }, 410)
        } else if (item.itemId == R.id.nav_quiz) {
            Handler().postDelayed({
                val intent = Intent(this@ProfileSetup, Home::class.java)
                intent.putExtra("selected_theme", user_theme.toString())
                startActivity(intent)
                finish()
            }, 410)
        } else if (item.itemId == R.id.nav_logout) {
            Handler().postDelayed({
                auth!!.signOut()
                val intent = Intent(this@ProfileSetup, Login::class.java)
                startActivity(intent)
                finish()
            }, 410)
        } else if (item.itemId == R.id.nav_settings) {
            Handler().postDelayed({
                val intent = Intent(this@ProfileSetup, Settings::class.java)
                startActivity(intent)
                finish()
            }, 410)
        } else if (item.itemId == R.id.nav_diet) {
            Handler().postDelayed({
                val intent = Intent(this@ProfileSetup, Home::class.java)
                intent.putExtra("selected_theme", user_theme.toString())
                startActivity(intent)
                finish()
            }, 410)
        }
        drawer!!.closeDrawer(GravityCompat.START)
        return true
    }
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle!!.syncState()
    }

    override fun onConfigurationChanged(newConfiguration: Configuration) {
        super.onConfigurationChanged(newConfiguration)
        toggle!!.syncState()
    }

    override fun onBackPressed() {

        // Create an intent to start a new activity
        if (navigatedFrom.equals("homeScreen")){
            val intent = Intent(this@ProfileSetup, Home::class.java)
            intent.putExtra("selected_theme", user_theme.toString())
            startActivity(intent)
            // Finish the current activity
            finish()
        }else{
            val intent = Intent(this@ProfileSetup, Settings::class.java)
            intent.putExtra("selected_theme", user_theme.toString())
            startActivity(intent)
            // Finish the current activity
            finish()
        }


        super.onBackPressed()
    }


}