package com.example.messngaerapp

import android.app.Activity
import android.app.DownloadManager
import android.content.Intent
import android.drm.DrmStore
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.register_main.*
import java.net.URI
import java.util.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_main)
        val username = ed_user_name.text.toString();
        Log.d("emai",username);
        btn_register.setOnClickListener {
//            var intent = Intent(this,LoginActivity::class.java)
//            startActivity(intent)
            getRegistered()

        }
        btn_profile_img.setOnClickListener {
          var intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)
        }
        txt_hadAccount.setOnClickListener {
            val intent = Intent(this,LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }


    }
    var selectedPhotoUri: Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode,resultCode, data)
        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data!= null ){
            selectedPhotoUri = data.data;
            var bitmap = MediaStore.Images.Media.getBitmap(contentResolver,selectedPhotoUri)
            img_selected.setImageBitmap(bitmap)
            btn_profile_img.alpha = 0f
          //  var bitmapdrawable = BitmapDrawable(bitmap)
         //   btn_profile_img.setBackgroundDrawable(bitmapdrawable);
        }

    }
    private fun getRegistered(){
        var email = ed_emailid.text.toString();
        var password = ed_password.text.toString();

        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(this,"Please Enter Email and Password",Toast.LENGTH_LONG).show()
            return
        }
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener(){
            if(!it.isSuccessful) return@addOnCompleteListener

            Toast.makeText(this,"Register Successfull",Toast.LENGTH_LONG).show()


           Log.d("Main" ,"Registration Successfull: user id :${it.result.user.uid}")
                uploadImageToFirebaseStorage()
        }
            .addOnFailureListener(){
                Toast.makeText(this,"Register Un-Successfull",Toast.LENGTH_LONG).show()
                Log.d("Main" ,"Registration Un-Successfull:message :${it.message}")
            }
    }

    private fun uploadImageToFirebaseStorage(){
        if(selectedPhotoUri ==null) return
        var filename = UUID.randomUUID().toString()
        var ref =FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectedPhotoUri!!).addOnSuccessListener {
            Log.d("Main" ,"upload Successfull: image path :${it.metadata?.path}")
            ref.downloadUrl.addOnSuccessListener {
                Log.d("Main" ,"upload image url:  :$it")
                saveUserToDB(it.toString())
            }
        }

    }

    private fun saveUserToDB( url:String){
        var uid = FirebaseAuth.getInstance().uid?:""
        var ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        var user = User(uid,ed_user_name.text.toString(),url)
        ref.setValue(user).addOnSuccessListener {
            Log.d("Main" ,"User Deatails got stored Successflly")
            val intent = Intent(this,LatestMessagesActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

}
@Parcelize
class User(val uid:String ,val username: String, val profileImageUrl : String):Parcelable{
    constructor() : this(uid="", username="", profileImageUrl="")
}
