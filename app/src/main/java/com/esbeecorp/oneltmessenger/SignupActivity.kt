package com.esbeecorp.oneltmessenger

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_signup.*
import java.util.*

class SignupActivity : AppCompatActivity() {

    val firestore = FirebaseFirestore.getInstance()

    val SELECT_PHOTO_ID = 1

    var selectedPhotoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        btn_addphoto.setOnClickListener { selectImage() }

        btn_signup.setOnClickListener { proccessRegistration() }

        tv_already_have_account.setOnClickListener { finish() }

    }


    // Validates input fields
    private fun proccessRegistration(){

        UtilityClass.startLoadingOnButton(btn_signup, pbc_loading)

        val email = et_email.text.toString()
        val password = et_password.text.toString()
        val username = et_username.text.toString()

        // check if input is not empty
        if (email.isBlank() || password.isBlank() || username.isBlank() || selectedPhotoUri == null){
            Toast.makeText(this, "Fill empty fields or select an image!! ", Toast.LENGTH_SHORT).show()
            UtilityClass.stopLoadingOnButton(btn_signup, pbc_loading)
            return
        } else {

            // Check if username is already taken
            firestore.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnSuccessListener {

                    // Check if username is already taken
                    if (!it.isEmpty){

                        Log.d("Register", "username already taken")
                        Toast.makeText(this, "Username: ${it.documents.get(0)["username"]} is already taken :(.", Toast.LENGTH_SHORT).show()
                        UtilityClass.stopLoadingOnButton(btn_signup, pbc_loading)

                    } else { singupNewUser(email, password) }
                }
        }

    }


    // Signups user on the FirebaseAuth
    private fun singupNewUser(email: String, password: String){
        // try to register the user on firebase
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    Log.d("Register", "new user uid: ${it.result!!.user.uid}")

                    // upload the image and save user in the database
                    uploadImage()
                }
            }
            .addOnFailureListener {
                Log.d("Register", "SignUp failed: ${it.message}")
                Toast.makeText(this, "SignUp failed: ${it.message}", Toast.LENGTH_LONG).show()
                UtilityClass.stopLoadingOnButton(btn_signup, pbc_loading)
            }
    }


    // Shows file selection activity
    private fun selectImage(){
        Log.d("Register", "Add photo button clicked")

        // open file selection activity and listen for its result
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, SELECT_PHOTO_ID)
    }


    // Listens to result from file selection activity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // check if image was selected
        if (requestCode == SELECT_PHOTO_ID && resultCode == Activity.RESULT_OK){
            if (data != null){

                selectedPhotoUri = data.data

                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

                // show image on circle image view and make button transparent
                iv_circle_image.setImageBitmap(bitmap)
                btn_addphoto.alpha = 0f
            }
        }
    }


    // Uploads an image to FirebaseStorage
    private fun uploadImage(){
        val filename = UUID.randomUUID().toString()

        // realtime database
        val reference = FirebaseStorage.getInstance().getReference("/images/$filename")

        reference.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("Register", "Image uploaded succesfully. ${it.metadata?.path}")

                // insert new user into database after successful signup and image upload
                reference.downloadUrl.addOnSuccessListener { url -> saveNewUser(et_username.text.toString(), url.toString()) }
            }
            .addOnFailureListener{
                Toast.makeText(this, "Something went wrong :(. ${it.message}", Toast.LENGTH_LONG).show()
                UtilityClass.stopLoadingOnButton(btn_signup, pbc_loading)
            }

    }


    // Inserts new user into Firestore database. also has a Realtime Database example, but Firestore is better...
    private fun saveNewUser(username: String, profileImageUrl: String){

        val uid = FirebaseAuth.getInstance().uid
        if (uid != null){

            val user = User(uid, username, profileImageUrl)

            // Realtime Database insert
            /*val reference = FirebaseDatabase.getInstance().getReference("/users/$uid")

            reference.setValue(user)
                .addOnSuccessListener {
                    Log.d("Register", "New user saved in the database")
                    Toast.makeText(this, "Nice. You can Now Login!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Something went wrong :(. ${it.message}", Toast.LENGTH_LONG).show()
                }*/


            // FireStore insert with the same ID as login/register uid
            // to insert with random uid, just use .add(user) instead of .document().set()
            firestore.collection("users")
                .document(user.uid)
                .set(user)
                .addOnSuccessListener {
                    Log.d("Register", "New user saved in the Firestore.")
                    //Toast.makeText(this, "Nice. You can Now Login!", Toast.LENGTH_SHORT).show()

                    // signup was successful, show chatlist class and close everything else so back button closes
                    // the app insted of taking you back to login screen
                    val intent = Intent(this, ChatListActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Something went wrong :(. ${it.message}", Toast.LENGTH_LONG).show()
                    UtilityClass.stopLoadingOnButton(btn_signup, pbc_loading)
                }

        }
    }

    // Hide keyboard if clicked outside of it.....
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        UtilityClass.closeKeyboardOnOutsideTouch(ev, currentFocus, this)
        return super.dispatchTouchEvent(ev)
    }

}
