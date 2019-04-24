
package com.example.messngaerapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.FontsContract
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import kotlinx.android.synthetic.main.activity_dash_board.*
import android.widget.Toast

import android.support.annotation.NonNull
import android.view.MenuItem
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.header_navigation_menu.*


class DashBoardActivity : AppCompatActivity() {
    var dl : DrawerLayout? = null
    var t: ActionBarDrawerToggle? = null
    var nv : NavigationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash_board)
        dl = activity_dash_board as DrawerLayout
        t = ActionBarDrawerToggle(this,dl,R.string.open,R.string.close)
        dl?.addDrawerListener(t!!)
        t?.syncState()
        supportActionBar?.title = "Dashboard"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setProfileImage()
        nv = navView as NavigationView
        nv?.setNavigationItemSelectedListener(object : NavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                val id = item.getItemId()
                when (id) {
                    R.id.menu_home -> {
                        Toast.makeText(this@DashBoardActivity, "My Account", Toast.LENGTH_SHORT).show()
//                        val intent = Intent(this,DashBoardActivity::class.java)
//                        startActivity(intent)
                        return true
                    }
                    R.id.menu_msgs -> {
                        Toast.makeText(this@DashBoardActivity, "Messages", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@DashBoardActivity,LatestMessagesActivity::class.java)
                        startActivity(intent)
                        return true
                    }
                    R.id.menu_deals -> {
                        Toast.makeText(this@DashBoardActivity, "Deals", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@DashBoardActivity,DealsActivity::class.java)
                        startActivity(intent)
                        return true
                    }
                    else -> return true
                }


            }
        })



    }

    private fun setProfileImage(){

        LatestMessagesActivity.currentUser?.profileImageUrl

        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                var user = p0.getValue(User::class.java)
                Picasso.get().load(user?.profileImageUrl).into(img_profile)
            }

        })

    }
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(t?.onOptionsItemSelected(item)!!)
            return true;
        return super.onOptionsItemSelected(item)
    }
}
