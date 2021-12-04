package com.example.asynclayoutinflater

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.FrameLayout
import androidx.asynclayoutinflater.view.AsyncLayoutInflater
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*

class AsyncAdapter<T>(
    private val items: MutableList<T>,
    private val onItemClick: (T) -> Unit,
    var onUndoDeleteStarted: (()-> Unit) -> Unit,
    var betweenItemsInflateDelay: Long = 0L,
    var buildInflateAnimation: () -> Animation? = { null },
    private val buildAsyncItem: (
        context: Context,
        onSyncInflationFinished: () -> Unit,
        onDisplayFinished: () -> Unit
    ) -> AsyncItem<T>
): RecyclerView.Adapter<AsyncAdapter<T>.ViewHolder>() {

    abstract class AsyncItem<T>(
        context: Context,
        val onSyncInflationFinished: () -> Unit,
        val onDisplayFinished: () -> Unit
    ): FrameLayout(context) {
        private var isRealLayoutInflated = false
        init {
            inflateView()
            onSyncInflationFinished()
        }
        abstract fun inflateView(): View
        abstract fun findViews(view: View)
        abstract fun bindToViews(item: T, onClick: () -> Unit, onDeleteClick: () -> Unit)
        fun bind(item: T, onClick: () -> Unit, onDeleteClick: () -> Unit, asyncInflateDelay: Long = 0L, inflateAnimation: Animation? = null) {
            if(isRealLayoutInflated) {
                bindToViews(item, onClick, onDeleteClick)
                onDisplayFinished()
            } else {
                CoroutineScope(Dispatchers.Main).launch {
                    delay(asyncInflateDelay)
                    AsyncLayoutInflater(context).inflate(R.layout.example_view_ready, this@AsyncItem) { view, _, _ ->
                        findViews(view)
                        bindToViews(item, onClick, onDeleteClick)
                        inflateAnimation?.let { view.animation = it }
                        addView(view)
                        isRealLayoutInflated = true
                        onDisplayFinished()
                    }
                }
            }
        }
    }

    private fun resetNumberOfBindings() {
        numberOfBindingsYet = 0
    }

    init {
        registerAdapterDataObserver(object: RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                resetNumberOfBindings()
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                resetNumberOfBindings()
            }

            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount)
                resetNumberOfBindings()
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                super.onItemRangeRemoved(positionStart, itemCount)
                resetNumberOfBindings()
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                super.onItemRangeChanged(positionStart, itemCount)
                resetNumberOfBindings()
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
                super.onItemRangeChanged(positionStart, itemCount, payload)
                resetNumberOfBindings()
            }
        })
    }
    inner class ViewHolder(private val view: View): RecyclerView.ViewHolder(view) {
        fun bind(item: T, onClick: () -> Unit, onDeleteClick: (Int) -> Unit, asyncInflateDelay: Long, inflateAnimation: Animation?) {
            (view as AsyncItem<T>).bind(item, onClick, { onDeleteClick(adapterPosition) }, asyncInflateDelay, inflateAnimation)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = buildAsyncItem(parent.context, ::waitForItem, ::itemReady)
        return ViewHolder(view)
    }

    private var numberOfBindingsYet = 0
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, { onItemClick(item) }, { position -> deleteItem(position) }, betweenItemsInflateDelay * numberOfBindingsYet, buildInflateAnimation())
        numberOfBindingsYet++
    }

    override fun getItemCount(): Int {
        return items.size
    }
    
    private var itemsToWaitFor = 0
    private var itemsReady = 0
    private var onAllItemsReady = {}
    private var allItemsReadyJob = Job()
    private var syncInflationsFinished = false

    private fun resetState() {
        itemsToWaitFor = 0
        itemsReady = 0
        onAllItemsReady = {}
        allItemsReadyJob = Job()
        syncInflationsFinished = false
    }

    fun onSyncInflationsFinished() {
        syncInflationsFinished = true
        if(itemsToWaitFor == itemsReady) {
            allItemsReadyJob.complete()
        }
    }

    private fun waitForItem() {
        itemsToWaitFor++
    }

    private fun itemReady() {
        itemsReady++
        if(syncInflationsFinished) {
            if(itemsToWaitFor == itemsReady) {
                allItemsReadyJob.complete()
            }
        }
    }

    fun updateData(items: List<T>, onUpdateFinished: () -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            resetState()
            this@AsyncAdapter.items.clear()
            this@AsyncAdapter.items.addAll(items)
            notifyDataSetChanged()
            allItemsReadyJob.join()
            onUpdateFinished()
        }
    }

    private fun undoDelete(item: T, position: Int) {
        items.add(position, item)
        notifyItemInserted(position)
    }

    private fun deleteItem(position: Int) {
        val removedRide = items.removeAt(position)
        notifyItemRemoved(position)
        onUndoDeleteStarted {
            undoDelete(removedRide, position)
        }
    }
}