package com.example.wechatdrag

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_IDLE
import com.github.chrisbanes.photoview.PhotoView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.activity_preview.*
import kotlinx.android.synthetic.main.item_preview.view.*


class PreviewActivity : AppCompatActivity() {

    private var currentScrollState: Int = SCROLL_STATE_IDLE

    private val dragCloseHelper by lazy {
        DragCloseHelper(this)
    }

    private val mAdapter by lazy {
        PagerAdapter()
    }

    private val index by lazy {
        intent.getIntExtra("index", 0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)
        supportPostponeEnterTransition()
        dragCloseHelper.setDragCloseViews(
            containerView,
            previewViewPager
        )
        dragCloseHelper.setDragCloseListener(object : DragCloseHelper.DragCloseListener {
            override fun isDragEnable(): Boolean {
                val currentScaleView: PhotoView? =
                    (previewViewPager[0] as? RecyclerView)?.get(0)?.findViewById(R.id.previewImage) as? PhotoView
                currentScaleView?.apply {
                    return !isFinishing && currentScrollState == SCROLL_STATE_IDLE && (scale == 1F || displayRect.top >= 0)
                }
                return false
            }

            override fun dragStart() {
            }

            override fun dragging(percent: Float) {
            }

            override fun dragCancel() {
            }

            override fun dragClose(isShareElementMode: Boolean) {
                onBackPressed()
            }
        })

        previewViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == index) {
                    supportStartPostponedEnterTransition()
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
                currentScrollState = state
                super.onPageScrollStateChanged(state)
            }
        })
        previewViewPager.adapter = mAdapter
        previewViewPager.setCurrentItem(index, false)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        return if (dragCloseHelper.handleEvent(event)) {
            true
        } else {
            super.dispatchTouchEvent(event)
        }
    }

    override fun finishAfterTransition() {
        setResult(Activity.RESULT_OK, Intent().apply {
            putExtra("currentIndex", previewViewPager.currentItem)
        })
        super.finishAfterTransition()
    }
}

class PagerAdapter : RecyclerView.Adapter<PagerAdapter.PagerHolder>() {

    private val imags = intArrayOf(R.drawable.test_a, R.drawable.test_b)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerHolder {
        return PagerHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_preview,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return imags.size
    }

    override fun onBindViewHolder(holder: PagerHolder, position: Int) {
        holder.itemView.previewImage.setImageResource(imags[position])
    }

    class PagerHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
        LayoutContainer
}