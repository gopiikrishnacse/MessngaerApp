package com.example.messngaerapp

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

 var Current_User_Name = ""
var Current_User_Uid = ""
var Current_User_Photo_Url = ""
public fun fetchCurrentUser(){
    val uid = FirebaseAuth.getInstance().uid
    val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
    var name:User?  = null
    ref.addListenerForSingleValueEvent(object: ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {
        }

        override fun onDataChange(p0: DataSnapshot) {
            LatestMessagesActivity.currentUser = p0.getValue(User::class.java)
            name = p0.getValue(User::class.java)
            Current_User_Uid =  name?.uid!!
            Current_User_Name = name?.username!!
             Current_User_Photo_Url = name?.profileImageUrl!!

        }

    })

}