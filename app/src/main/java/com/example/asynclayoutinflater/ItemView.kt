package com.example.asynclayoutinflater

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.card.MaterialCardView
import ru.rambler.libs.swipe_layout.SwipeLayout

class ItemView(
    context: Context,
    onSyncInflationFinished: () -> Unit,
    onDisplayFinished: () -> Unit
) : AsyncAdapter.AsyncItem<String>(context, onSyncInflationFinished, onDisplayFinished) {

    private lateinit var card: MaterialCardView
    private lateinit var swipeLayout: SwipeLayout
    private lateinit var image: ImageView
    private lateinit var firstText: TextView
    private lateinit var moreMenu: ImageView

    override fun inflateView(): View {
        return inflate(context, R.layout.example_view, this)
    }

    override fun findViews(view: View) {
        card = view.findViewById(R.id.example_card)
        swipeLayout = view.findViewById(R.id.example_swipe_layout)
        image = view.findViewById(R.id.example_image)
        firstText = view.findViewById(R.id.example_text_1)
        moreMenu = view.findViewById(R.id.example_menu_more)
    }

    override fun bindToViews(item: String, onClick: () -> Unit, onDeleteClick: () -> Unit) {
        swipeLayout.reset()
        firstText.text = item
        card.setOnClickListener { onClick() }
        swipeLayout.setOnSwipeListener(object : SwipeLayout.OnSwipeListener {
            override fun onBeginSwipe(swipeLayout: SwipeLayout?, moveToRight: Boolean) {}
            override fun onSwipeClampReached(swipeLayout: SwipeLayout?, moveToRight: Boolean) {
                onDeleteClick()
            }

            override fun onLeftStickyEdge(swipeLayout: SwipeLayout?, moveToRight: Boolean) {}
            override fun onRightStickyEdge(swipeLayout: SwipeLayout?, moveToRight: Boolean) {}
        })
    }
}