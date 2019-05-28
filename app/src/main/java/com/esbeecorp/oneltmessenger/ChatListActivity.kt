package com.esbeecorp.oneltmessenger

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_chat_list.*

class ChatListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list)

        verifyUserIsLoggedIn()


    }

    private fun verifyUserIsLoggedIn(){
        // check if user is logged in, if not show login activity
        val uid = FirebaseAuth.getInstance().uid
        Log.d("ChatList", "Logged in users uid: $uid")

        if (uid == null){
            Log.d("ChatList", "Not logged in")

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } else {

            FirebaseFirestore.getInstance().document("users/$uid")
                .get()
                .addOnSuccessListener {

                    if (it.exists()){
                        val user = it.toObject(User::class.java)
                        Log.d("ChatList", "User retrieved from database. ${user}")
                        tv_welcome.text = "WELCOME: ${user?.username}"
                    }

                }
        }
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId){

            R.id.menu_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                verifyUserIsLoggedIn()
            }

            R.id.menu_add_friend -> {

            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.chat_list_nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

}
