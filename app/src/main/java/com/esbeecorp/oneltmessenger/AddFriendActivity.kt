package com.esbeecorp.oneltmessenger

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.ScriptGroup
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.Toast
import com.esbeecorp.oneltmessenger.RecyclerViewAdapters.InvitationListAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_add_friend.*

class AddFriendActivity : AppCompatActivity() {

    private val TAG = "AddFriendActivity"

    private val db = FirebaseFirestore.getInstance()

    val invitations = mutableListOf<Invitation>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friend)

        fetchInvitations()

        // Connect recyclerview with custom adapter
        rv_invitation_list.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        rv_invitation_list.adapter = InvitationListAdapter(invitations, 7)

        btn_add_friend.setOnClickListener { sendFriendInvite() }
    }

    private fun fetchInvitations(){

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        Log.d(TAG, "Logged in user id: $uid")

        db.collection("invitations")
            .whereArrayContains("users", uid.toString())
            .orderBy("dateTime", Query.Direction.DESCENDING)
            .addSnapshotListener(this) { querySnapshot, e ->
                if (e != null) {
                    Log.d(TAG, "Listen failed. $e")
                    return@addSnapshotListener
                }

                if (querySnapshot!!.isEmpty) {
                    Log.d(TAG, "NO INVITATIONS")
                    invitations.clear()
                    rv_invitation_list.adapter?.notifyDataSetChanged()
                    // TODO show some text, maybe hide recycler...
                    return@addSnapshotListener
                }

                invitations.clear()
                for (inv in querySnapshot!!.documents) {
                    val invitation = inv.toObject(Invitation::class.java)
                    invitation!!.id = inv.id
                    invitations.add(invitation)
                }
                rv_invitation_list.adapter?.notifyDataSetChanged()
                Log.d(TAG, "invites changed: $invitations")
            }
    }

    private fun sendFriendInvite(){

        // Hide keyboard on button click. Utility class method doesnt work, because button is inside EditText view,
        // which is focused right now
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

        val username = et_username.text.toString()
        et_username.setText("")

        if (username.isBlank()){
            Toast.makeText(this, "No can do :P...", Toast.LENGTH_SHORT).show()
            return
        }

        // TODO check if
        //  1) user exists
        //  2) isnt a friend already
        //  create invitation if both true

        Log.d(TAG, "Let's try to create friend invitation for username $username")
    }


    // Hide keyboard if clicked outside of it.....
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        UtilityClass.closeKeyboardOnOutsideTouch(ev, currentFocus, this)
        return super.dispatchTouchEvent(ev)
    }
}
