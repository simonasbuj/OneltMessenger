package com.esbeecorp.oneltmessenger

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.activity_chat_list.*

class ChatListActivity : AppCompatActivity() {

    private val TAG = "ChatListActivity"

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list)

        verifyUserIsLoggedIn()

        Log.d(TAG, "onCreate was called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume was called")
    }

    private fun verifyUserIsLoggedIn(){
        // check if user is logged in, if not show login activity
        val uid = FirebaseAuth.getInstance().uid
        Log.d(TAG, "Logged in users uid: $uid")

        if (uid == null){
            Log.d(TAG, "Not logged in")

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)

        } else { fetchChats(uid) }
    }

    private fun fetchChats(uid: String){

        Log.d(TAG, "Lets query all user's chats")

        db.collection("chats")
            .whereArrayContains("users", uid)
            .addSnapshotListener(EventListener<QuerySnapshot> { data, e ->
                if (e != null) {
                    Log.d(TAG, "Listen failed. $e")
                    return@EventListener
                }

                Log.d(TAG, "Still listening and getting data boi")
                if (data!!.isEmpty){
                    tv_welcome.text = getString(R.string.forever_alone)
                } else {
                    val chats = data.toObjects(Chat::class.java)
                    tv_welcome.text = "YOU PARTICIPATE IN ${chats.size} CHATS :)..."
                }

            })
    }


    // Options menu creation and options menu item selection

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId){

            R.id.menu_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                verifyUserIsLoggedIn()
            }

            R.id.menu_add_friend -> {
                val intent = Intent(this, AddFriendActivity::class.java)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.chat_list_nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

}
