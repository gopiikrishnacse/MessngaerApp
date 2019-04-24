package com.example.messngaerapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.ldoublem.loadingviewlib.view.LVCircularRing
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.register_main.*

class LoginActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        lv_circularring.setViewColor(Color.WHITE);
        lv_circularring.setTextColor(Color.TRANSPARENT);
        lv_circularring.setPorBarStartColor(Color.parseColor("#ee660b"));
        lv_circularring.setPorBarEndColor(Color.parseColor("#ee660b"));

        btn_login.setOnClickListener {
            loginProcess()
        }

        txt_Register.setOnClickListener {
            val intent = Intent(this,RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun loginProcess(){
        lv_circularring.visibility = View.VISIBLE;


        lv_circularring.startAnim()
        var email = ed_lgn_user_name.text.toString();
        var password = ed_lgn_password.text.toString();

        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(this,"Please Enter Email and Password", Toast.LENGTH_LONG).show()
            lv_circularring.visibility = View.INVISIBLE;
            lv_circularring.stopAnim()
            lv_circularring.visibility = View.INVISIBLE;
            return
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
            .addOnCompleteListener(){
                if(!it.isSuccessful) return@addOnCompleteListener

                Toast.makeText(this,"Login Successfull",Toast.LENGTH_LONG).show()
                Log.d("Main" ,"Login Successfull: user id :${it.result.user.uid}")
                val intent = Intent(this,DashBoardActivity::class.java)
                lv_circularring.stopAnim()
                lv_circularring.visibility = View.INVISIBLE;
                startActivity(intent)
            }
            .addOnFailureListener(){
                Toast.makeText(this,"Login Un-Successfull",Toast.LENGTH_LONG).show()
                Log.d("Main" ,"Login Un-Successfull:message :${it.message}")
                lv_circularring.visibility = View.INVISIBLE;
                lv_circularring.stopAnim()
            }
    }
        }
