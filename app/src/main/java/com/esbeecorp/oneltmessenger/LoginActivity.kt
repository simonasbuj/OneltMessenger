package com.esbeecorp.oneltmessenger

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        tv_dont_have_account.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        btn_login.setOnClickListener { proccessLogin() }

        fetchAllUsersTest()
    }

    private fun proccessLogin(){
        val email = et_email.text.toString()
        val password = et_password.text.toString()

        // check if input is not empty
        if (email.isBlank() || password.isBlank()){
            Toast.makeText(this, "No can do :(", Toast.LENGTH_SHORT).show()
            return
        }

        // try to login the user
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    val user = FirebaseAuth.getInstance().currentUser
                    Log.d("Login", "Welcome ${user!!.email}")

                    // login was successful, show chatlist class and close everything else so back button closes
                    // the app insted of taking you back to login screen
                    val intent = Intent(this, ChatListActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Login failed: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }



    // Just a test function to famirialize with querying data from Firestore
    private fun fetchAllUsersTest(){
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("users")
            .get()
            .addOnSuccessListener {

                // check if users table in the database is empty
                if (it.isEmpty) {
                    Log.d("Login", "No users in the database...")
                    return@addOnSuccessListener
                }

                // A) desirialize query into users list 1st try
                /*var users = mutableListOf<User>()

                for (user in it){
                   users.add(user.toObject(User::class.java))
                }

                for (u in users){
                    Log.d("Login", "uid: ${u.uid} username: ${u.username} profileImageUrl: ${u.profileImageUrl}")
                }*/

                // B) Desirialize query object into User model objects list
                val users = it.toObjects(User::class.java)

                for (u in users) {
                    Log.d("Login", "uid: ${u.uid} username: ${u.username} profileImageUrl: ${u.profileImageUrl}")
                }

            }
    }


    // Hide keyboard if clicked outside of it.....
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        UtilityClass.closeKeyboardOnOutsideTouch(ev, currentFocus, this)
        return super.dispatchTouchEvent(ev)
    }
}