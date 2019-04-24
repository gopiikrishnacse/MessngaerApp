package com.example.messngaerapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_log_lef_row.view.*
import kotlinx.android.synthetic.main.chat_log_right_row.view.*
import java.sql.Timestamp

class ChatLogActivity : AppCompatActivity() {
    var user: User? = null
    var adapter = GroupAdapter<ViewHolder>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)
        user = intent.getParcelableExtra<User>(NewMessageActivity.UserKey)
        supportActionBar?.title = user?.username
//        adapter?.add(ChatItemFrom())
//        adapter.add(ChatItemTo())
//        adapter.add(ChatItemFrom())
//        adapter.add(ChatItemTo())
//        adapter.add(ChatItemFrom())
//        adapter.add(ChatItemTo())
//        adapter.add(ChatItemFrom())
//        adapter.add(ChatItemTo())
        recyclerview_chatlog.adapter = adapter

        btn_sendmsg.setOnClickListener {
            sendMessgae()
        }

        listenToMessgaes()

    }

    private fun listenToMessgaes() {
        val fromId = FirebaseAuth.getInstance().uid
        val toId = user?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")
        //    val refFrom = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMsg = p0.getValue(ChatMessage::class.java)
                if (chatMsg?.fromId == FirebaseAuth.getInstance().uid) {
                    adapter?.add(ChatItemTo(chatMsg?.msg!!, LatestMessagesActivity.currentUser?.profileImageUrl!!))
                } else {
                    adapter?.add(ChatItemFrom(chatMsg?.msg!!, user?.profileImageUrl!!))
                }
                recyclerview_chatlog.scrollToPosition(adapter.itemCount -1)
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }
        })

    }

    private fun sendMessgae() {
        val msg = ed_msg_chatlog.text.toString()

        val fromId = FirebaseAuth.getInstance().uid

        val refTo = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/${user?.uid}").push()
        val refFrom = FirebaseDatabase.getInstance().getReference("/user-messages/${user?.uid}/$fromId").push()

        val chatItem = ChatMessage(refTo.key.toString(), fromId!!, user?.uid!!, msg, System.currentTimeMillis() / 1000)
        refTo.setValue(chatItem).addOnSuccessListener {
            Log.d("Chat Log", "sent message succesfuly ${refTo.key}");
            ed_msg_chatlog.text.clear()
            recyclerview_chatlog.scrollToPosition(adapter?.itemCount - 1)
        }

        refFrom.setValue(chatItem)

        var latestMsgRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/${user?.uid}")
        latestMsgRef.setValue(chatItem)
        var latestMsgToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/${user?.uid}/$fromId")
        latestMsgToRef.setValue(chatItem)
    }


    class ChatItemFrom(val msg: String, val profImg: String) : Item<ViewHolder>() {
        override fun getLayout(): Int {
            return R.layout.chat_log_lef_row
        }

        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.txt_log_left.text = msg
            Picasso.get().load(profImg).into(viewHolder.itemView.img_left_log)
        }
    }

    class ChatItemTo(val msg: String, val profImg: String) : Item<ViewHolder>() {
        override fun getLayout(): Int {
            return R.layout.chat_log_right_row
        }

        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.txt_log_right.text = msg
            Picasso.get().load(profImg).into(viewHolder.itemView.img_right_chatlog)

        }
    }

    class ChatMessage(val id: String, val fromId: String, val toId: String, val msg: String, val timestamp: Long) {
        constructor() : this(id = "", fromId = "", toId = "", msg = "", timestamp = 0)
    }
}