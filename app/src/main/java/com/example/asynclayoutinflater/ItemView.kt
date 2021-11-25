package com.example.asynclayoutinflater

import android.content.Context
import android.view.View
import android.view.animation.Animation
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.asynclayoutinflater.view.AsyncLayoutInflater
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.*
import ru.rambler.libs.swipe_layout.SwipeLayout

class ItemView(
    context: Context,
    private val onSyncInflateFinished: () -> Unit,
    private val onEverythingFinished: () -> Unit,
): FrameLayout(context) {

    private var isInflated = false

    private lateinit var card: MaterialCardView
    private lateinit var swipeLayout: SwipeLayout
    private lateinit var image: ImageView
    private lateinit var textDatePlace: TextView
    private lateinit var moreMenu: ImageView

    init {
        inflate(context, R.layout.ride_view, this)
        onSyncInflateFinished()
    }

    private fun findViews(parentView: View) {
        card = parentView.findViewById(R.id.ride_card)
        swipeLayout = parentView.findViewById(R.id.ride_swipe_layout)
        image = parentView.findViewById(R.id.ride_image)
        textDatePlace = parentView.findViewById(R.id.ride_text_date_place)
        moreMenu = parentView.findViewById(R.id.ride_menu_more)
    }

    fun bind(ride: String, onClick: () -> Unit, onDeleteClick: () -> Unit, asyncInflateDelay: Long = 0L, inflateAnimation: Animation? = null) {
        fun bindToViews() {
            //after we swipe to delete an item
            swipeLayout.reset()
            textDatePlace.text = ride
            card.setOnClickListener { onClick() }
            swipeLayout.setOnSwipeListener(object :SwipeLayout.OnSwipeListener {
                override fun onBeginSwipe(swipeLayout: SwipeLayout?, moveToRight: Boolean) { }
                override fun onSwipeClampReached(swipeLayout: SwipeLayout?, moveToRight: Boolean) {
                    onDeleteClick()
                }
                override fun onLeftStickyEdge(swipeLayout: SwipeLayout?, moveToRight: Boolean) { }
                override fun onRightStickyEdge(swipeLayout: SwipeLayout?, moveToRight: Boolean) { }
            })
        }

        if(isInflated) {
            bindToViews()
            onEverythingFinished()
        } else {
            CoroutineScope(Dispatchers.Main).launch {
                delay(asyncInflateDelay)
                AsyncLayoutInflater(context).inflate(R.layout.ride_view_ready, this@ItemView) { view, _, _ ->
                    findViews(view)
                    bindToViews()
                    inflateAnimation?.let { view.animation = it }
                    addView(view)
                    isInflated = true
                    onEverythingFinished()
                }
            }
        }
    }
}