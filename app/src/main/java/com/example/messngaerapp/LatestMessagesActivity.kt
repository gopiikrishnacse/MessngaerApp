package com.example.messngaerapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_latest_messages.*
import kotlinx.android.synthetic.main.latest_msg_row.view.*

class LatestMessagesActivity : AppCompatActivity() {
    companion object {
        var currentUser: User? = null
    }
    var adapter =GroupAdapter<ViewHolder>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = "Messages"
        setContentView(R.layout.activity_latest_messages)
        recyclerview_latestmsg.adapter = adapter
        recyclerview_latestmsg.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL))
        fetchCurrentUser()
        adapter.setOnItemClickListener { item, view ->
            val userItem = item as LatestMessageRow
            val intent = Intent(this,ChatLogActivity::class.java)
            intent.putExtra(NewMessageActivity.UserKey,userItem.chatPartnerUser)
            startActivity(intent)
        }

        listenLatestMessages()
    }

    val latestMessages = HashMap<String,ChatLogActivity.ChatMessage>()
    private fun refershRecyclerView(){
        adapter.clear()
        latestMessages.values.forEach{
            adapter.add(LatestMessageRow(it))
        }
    }
    private fun listenLatestMessages(){
        val fromId = FirebaseAuth.getInstance().uid
        var ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")
        ref.addChildEventListener(object: ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatItem = p0.getValue(ChatLogActivity.ChatMessage::class.java)
                latestMessages.put(p0.key!!,chatItem!!)
                refershRecyclerView()
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val chatItem = p0.getValue(ChatLogActivity.ChatMessage::class.java)
                latestMessages.put(p0.key!!,chatItem!!)
                refershRecyclerView()
             //   adapter.add(LatestMessageRow(chatItem!!))
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }
        })


        }

    class LatestMessageRow(val chatMessage : ChatLogActivity.ChatMessage) : Item<ViewHolder>(){
        var chatPartnerUser : User ? = null
        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.txt_latestmsg_msg.text = chatMessage.msg

            val  chatPartnerId: String
            if(chatMessage.fromId == FirebaseAuth.getInstance().uid){
                chatPartnerId = chatMessage.toId
            }else{
                chatPartnerId = chatMessage.fromId
            }
            var ref =FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
            ref.addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    chatPartnerUser = p0.getValue(User::class.java)
                    viewHolder.itemView.txt_latestmg_username.text = chatPartnerUser?.username

                    Picasso.get().load(chatPartnerUser?.profileImageUrl).into(viewHolder.itemView.img_latest_char_row)
                }
            })
            //viewHolder.itemView.txt_latestmg_username.text = chatMessage.toId
        }

        override fun getLayout(): Int {
            return R.layout.latest_msg_row
        }
    }
     public fun fetchCurrentUser(){
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue(User::class.java)
            }

        })
    }
    fun verifyUserLogin(){
        val uid = FirebaseAuth.getInstance().uid
        if(uid == null){
             val intent = Intent(this,RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.menu_new_msg -> {
                val intent = Intent(this,NewMessageActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this,RegisterActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.new_item,menu)
        return super.onCreateOptionsMenu(menu)
    }
}
