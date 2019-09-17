package com.example.wechatdrag

import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.target.ImageViewTarget
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.android.synthetic.main.activity_img_test.*

class TestImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_img_test)
//        Glide.with(this).asBitmap().load(R.drawable.test_c).into(
//                object : ImageViewTarget<Bitmap>(testImg) {
//                    override fun setResource(resource: Bitmap?) {
//                        resource?.apply {
//                            println(resource.width)
//                            println(resource.height)
//                            testImg.setImageBitmap(resource)
//                        }
//                    }
////                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
////                    println(resource.width)
////                    println(resource.height)
////                    testImg.setImageBitmap(resource)
////                }
//                }
//        )
    }
}