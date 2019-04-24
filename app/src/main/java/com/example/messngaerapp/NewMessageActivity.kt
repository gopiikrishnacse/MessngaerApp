package com.example.messngaerapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewDebug
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class NewMessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)
        supportActionBar?.title = "Select User"
        var adapter = GroupAdapter<ViewHolder>()
        recylerview_newmessage.setAdapter(adapter)

        fetchUsers()
    }
    companion object {
        val UserKey = "UserKey"
    }
    private fun fetchUsers(){
    val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()

                p0.children.forEach(){
                    Log.d("New Message","message ${it.toString()}")
                    val user = it.getValue(User::class.java)
                    if(user != null && LatestMessagesActivity.currentUser?.uid != user.uid ) {
                        adapter.add(UserItem(user))
                    }
                }
                adapter.setOnItemClickListener { item, view ->
                    val userItem = item as UserItem
                    val intent = Intent(view.context,ChatLogActivity::class.java)
                    intent.putExtra(UserKey,userItem.user)
                    startActivity(intent)
                    finish()
                }
                recylerview_newmessage.adapter = adapter
            }
        })
    }
    class UserItem(val user : User?) : Item<ViewHolder>(){
        override fun getLayout(): Int {
            return R.layout.user_row_new_message
        }

        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.txt_username.text = user?.username
            Picasso.get().load(user?.profileImageUrl).into(viewHolder.itemView.img_user)
        }
    }


}




