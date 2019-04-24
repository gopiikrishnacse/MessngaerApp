package com.example.messngaerapp

import android.content.Intent
import android.graphics.Rect
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_deals.*
import kotlinx.android.synthetic.main.deal_row_item.view.*
import java.util.*

class DealsActivity : AppCompatActivity() {
    var adapter = GroupAdapter<ViewHolder>()
    companion object {
        var RS =""
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deals)
        supportActionBar?.title = "Deals"
        RS = getString(R.string.Rs)
        recyclerview_deals.adapter = adapter
        recyclerview_deals.addItemDecoration(
            MarginItemDecoration(
                resources.getDimension(R.dimen.dim_deals_row_padding).toInt()
            )
        )
        fetchCurrentUser()
        getDeals()
//        adapter.add(DealItem(DealItem))
//        adapter.add(DealItem())
//        adapter.add(DealItem())
//        adapter.add(DealItem())


    }

    private fun getDeals(){
        val ref= FirebaseDatabase.getInstance().getReference("/deals")

        ref.addChildEventListener(object : ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val deal = p0.getValue(DealData::class.java)
                adapter.add(DealItem(deal!!))
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }
        })
    }

    class DealItem(val dealData:DealData) : Item<ViewHolder>() {
        override fun getLayout(): Int {
            return R.layout.deal_row_item
        }

        override fun bind(viewHolder: ViewHolder, position: Int) {
            Picasso.get().load(dealData.imgUser).into(viewHolder.itemView.img_profile_deal)
            viewHolder.itemView.txt_username_deal_row.text = dealData.userName
            viewHolder.itemView.txt_deal_des_row.text = dealData.deal.title
            Picasso.get().load(dealData.deal.dealImage).into(viewHolder.itemView.img_deal_pic_row)
            viewHolder.itemView.txt_deal_merchant.text = dealData.deal.merchant
            var rate = dealData.deal.dealPrice

            if(!rate.isEmpty() && rate != ""){

                rate =  RS +" "+ rate
            }else {
                rate = ""
            }
            var discountVal = dealData.deal.discount
            if(!discountVal.isEmpty() && discountVal != ""){

                discountVal =  discountVal +"%"+ " OFF"
            }else {
                discountVal = ""
            }
            viewHolder.itemView.txt_rate_discount_deal_row.text = rate +  "  " +  discountVal
            viewHolder.itemView.txt_chat_count_row.text = if(dealData.chat.lastIndex == -1) "0" else (dealData.chat.lastIndex+1).toString()
        }

    }


    class DealData(val imgUser: String,val userName:String,val deal:Deal,val chat:ArrayList<ChatItem>){
        constructor() : this(imgUser = "",userName = "",deal = Deal(title = "", desc = "",dealURL = "", dealPrice = "", retailPrice = "", discount = "",merchant = "",dealImage = ""),chat =
        arrayListOf(ChatItem(name = "",imgCommentUser = "",comment = "")))
    }
    class ChatItem(val name: String,val imgCommentUser: String,val comment:String){
        constructor() : this(name ="",imgCommentUser = "",comment = "")
    }
    class Deal(val title:String,val desc:String,val dealURL:String,val dealPrice:String,val retailPrice:String,val discount:String,val dealImage:String,val merchant:String){
        constructor() : this(title = "", desc = "",dealURL = "", dealPrice = "", retailPrice = "", discount = "",merchant = "",dealImage = "")
    }

    class MarginItemDecoration(private val spaceHeight: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect, view: View,
            parent: RecyclerView, state: RecyclerView.State
        ) {
            with(outRect) {
                if (parent.getChildAdapterPosition(view) == 0) {
                    top = spaceHeight
                }
                left = spaceHeight
                right = spaceHeight
                bottom = spaceHeight
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.menu_deal_page -> {
                val intent = Intent(this,AddDealActivity::class.java)
                startActivity(intent)
//                var filename = UUID.randomUUID().toString()
//                val ref= FirebaseDatabase.getInstance().getReference("/deals/$filename")
//                var deal = Deal()
//
//                var chat = arrayListOf<ChatItem>()
//                val dealData = DealData("","",deal,chat)
//                ref.setValue(dealData)
            }


        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.deal_menu_item,menu)
        return super.onCreateOptionsMenu(menu)
    }
}
