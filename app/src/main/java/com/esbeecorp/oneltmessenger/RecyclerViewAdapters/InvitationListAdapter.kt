package com.esbeecorp.oneltmessenger.RecyclerViewAdapters

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.esbeecorp.oneltmessenger.Invitation
import com.esbeecorp.oneltmessenger.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.invitation_list_item.view.*
import java.time.Duration
import java.util.*
import java.util.concurrent.TimeUnit

class InvitationListAdapter(val invitations: MutableList<Invitation>, val uid: Int): RecyclerView.Adapter<InvitationListAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.invitation_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = invitations.size

    override fun onBindViewHolder(holder: ViewHolder, index: Int) {

        val username = invitations.get(index).receiver.username

        val age = invitations[index].displayAge()
        holder.title.text = username
        holder.timeold.text = "sent you a friend request $age"
        Picasso.get().load(invitations[index].receiver.profileImageUrl).into(holder.profileImg)
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val title: TextView = view.tv_title
        val timeold: TextView = view.tv_timeold
        val profileImg = view.iv_profileImg
    }

}