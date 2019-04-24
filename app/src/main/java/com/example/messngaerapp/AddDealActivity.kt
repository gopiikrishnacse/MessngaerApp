package com.example.messngaerapp

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_submit_deal.*
import java.util.*
import kotlin.math.roundToInt

class AddDealActivity : AppCompatActivity() {
    var isPhotoSelected = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submit_deal)
        supportActionBar?.title = "Submit Deal"
        isPhotoSelected = false

        lv_circularring_add_deal.setViewColor(Color.WHITE);
        lv_circularring_add_deal.setTextColor(Color.TRANSPARENT);
        lv_circularring_add_deal.setPorBarStartColor(Color.parseColor("#ee660b"));
        lv_circularring_add_deal.setPorBarEndColor(Color.parseColor("#ee660b"));

        img_deal_add_deal_img.setOnClickListener {
            selectPhoto()
        }
        btnCancel.setOnClickListener {
            finish()
        }
        btnSubmitDeal.setOnClickListener {
            lv_circularring_add_deal.visibility = View.VISIBLE
            lv_circularring_add_deal.startAnim()
            if(isPhotoSelected){
                uploadDealImage()
            }else{
                submitDeal("https://firebasestorage.googleapis.com/v0/b/kotlinmessenger-c41a3.appspot.com/o/images%2Fdeal_default_img.png?alt=media&token=9ef769c5-6d42-4366-817e-76af0c47a76a")
            }
        }
        ed_deal_retailprice_add_deal.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(ed_deal_price_add_deal.text.isEmpty() || ed_deal_retailprice_add_deal.text.isEmpty()) return
                var dealPrice = ed_deal_price_add_deal.text.toString().toFloat()
                var retailPrice = ed_deal_retailprice_add_deal.text.toString().toFloat()
                if(retailPrice < dealPrice){
                    return
                }
                var value =retailPrice - dealPrice
                var discount = (value/retailPrice)*100
                ed_deal_discount_add_deal.setText(""+discount.roundToInt())
            }
        })
    }



    private fun submitDeal(url:String){


        var dealLink = ed_deal_link_add_deal.text.toString()
        var dealTitle = ed_deal_title_add_deal.text.toString()
        var dealDesc = ed_deal_desc_add_deal.text.toString()
        var dealPrice = ed_deal_price_add_deal.text.toString()
        var retailPrice = ed_deal_retailprice_add_deal.text.toString()
        var discount = ed_deal_discount_add_deal.text.toString()
        var expiryDate = ed_deal_expirydate_add_deal.text.toString()
        var filename = UUID.randomUUID().toString()
        val ref= FirebaseDatabase.getInstance().getReference("/deals/$filename")
        var deal = DealsActivity.Deal(dealTitle,dealDesc,dealLink,dealPrice,retailPrice,discount,url,"")
        var chat = arrayListOf<DealsActivity.ChatItem>()
        val dealData = DealsActivity.DealData(Current_User_Photo_Url, Current_User_Name, deal, chat)
        ref.setValue(dealData).addOnSuccessListener {
            Toast.makeText(this,"Deal Submited SuccesFully",Toast.LENGTH_LONG).show()
            lv_circularring_add_deal.stopAnim()
            lv_circularring_add_deal.visibility = View.INVISIBLE
            finish()
        }.addOnFailureListener{
            lv_circularring_add_deal.stopAnim()
            lv_circularring_add_deal.visibility = View.INVISIBLE
            Toast.makeText(this,"Deal Submisson Failure",Toast.LENGTH_LONG).show()

        }
    }
    private fun uploadDealImage(){
        if(selectedPhotoUri ==null) return
        var url = ""
        var filename = UUID.randomUUID().toString()
        var ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectedPhotoUri!!).addOnSuccessListener {
            Log.d("Main" ,"upload Successfull: image path :${it.metadata?.path}")
            ref.downloadUrl.addOnSuccessListener {
                Log.d("Main" ,"upload image url:  :$it")
                url = it.toString()
                submitDeal(url)
            }
        }

    }
    private fun selectPhoto(){
        var intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent,0)
    }

    var selectedPhotoUri: Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode,resultCode, data)
        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data!= null ){
            selectedPhotoUri = data.data;
            isPhotoSelected = true
            var bitmap = MediaStore.Images.Media.getBitmap(contentResolver,selectedPhotoUri)
            img_deal_add_deal_img.setImageBitmap(bitmap)
            //  var bitmapdrawable = BitmapDrawable(bitmap)
            //   btn_profile_img.setBackgroundDrawable(bitmapdrawable);
        }

    }
}
