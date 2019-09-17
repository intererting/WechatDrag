package com.example.wechatdrag

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.scrollview_test.*

class ScrollViewTest : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.scrollview_test)
        println("  testA.checkHasImage()   ${testA.checkHasImage()}")
    }
}