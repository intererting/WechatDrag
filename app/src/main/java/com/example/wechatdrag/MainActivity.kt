package com.example.wechatdrag

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.app.SharedElementCallback
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide


class MainActivity : AppCompatActivity() {

    private var previewBackIndex: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startA.id = "0_0".hashCode()
        startB.id = "0_1".hashCode()
        startA.setOnClickListener {
            val activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this@MainActivity, startA, "preview"
            )
            ActivityCompat.startActivity(
                this@MainActivity,
                Intent(this@MainActivity, PreviewActivity::class.java).apply {
                    putExtra("index", 0)
                },
                activityOptionsCompat.toBundle()
            )
//            startActivity(Intent(this@MainActivity, PreviewActivity::class.java).apply {
//                putExtra("index", 0)
//            })
        }

        startB.setOnClickListener {
            val activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this@MainActivity, startB, "preview"
            )
            ActivityCompat.startActivity(
                this@MainActivity,
                Intent(this@MainActivity, PreviewActivity::class.java).apply {
                    putExtra("index", 1)
                },
                activityOptionsCompat.toBundle()
            )

//            startActivity(Intent(this@MainActivity, PreviewActivity::class.java).apply {
//                putExtra("index", 1)
//            })
        }

        setExitSharedElementCallback(object : SharedElementCallback() {
            override fun onMapSharedElements(names: MutableList<String>?, sharedElements: MutableMap<String, View>) {
                if (previewBackIndex == -1) {
                    super.onMapSharedElements(names, sharedElements)
                } else {
                    val id = buildString {
                        append("0")
                        append("_")
                        append(previewBackIndex)
                    }

                    sharedElements["preview"] = findViewById(id.hashCode())
                    previewBackIndex = -1
                }
            }
        })
    }

    override fun onActivityReenter(resultCode: Int, data: Intent?) {
        super.onActivityReenter(resultCode, data)
        data?.apply {
            previewBackIndex = getIntExtra("currentIndex", -1)
        }
    }
}