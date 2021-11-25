package com.example.asynclayoutinflater

import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*

class CustomAdapter(
    private val items: MutableList<String>,
    private val onClick: (String) -> Unit,
    var onUndoDeleteStarted: (()-> Unit) -> Unit,
    var delayUpdate: Long = 0L,
    var delayBetweenItems: Long = 0L,
    var createAnimation: () -> Animation? = { null }
    ): RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    fun resetNumberOfBindings() {
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
    class ViewHolder(private val view: View): RecyclerView.ViewHolder(view) {

        fun bind(str: String, onClick: () -> Unit, onDeleteClick: (Int) -> Unit, asyncInflateDelay: Long, inflateAnimation: Animation?) {
            (view as ItemView).bind(str, onClick, { onDeleteClick(adapterPosition) }, asyncInflateDelay, inflateAnimation)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemView(parent.context, {waitForItem()}, {itemReady()})
        return ViewHolder(view)
    }

    var numberOfBindingsYet = 0
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ride = items[position]
        holder.bind(ride, { onClick(ride) }, { position -> deleteItem(position) }, delayBetweenItems * numberOfBindingsYet, createAnimation())
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

    private fun resetInfo() {
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

    fun updateData(rides: List<String>, onUpdateFinished: () -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            delay(delayUpdate)
            resetInfo()
            this@CustomAdapter.items.clear()
            this@CustomAdapter.items.addAll(rides)
            notifyDataSetChanged()
            allItemsReadyJob.join()
            onUpdateFinished()
        }
    }

    private fun undoDelete(ride: String, position: Int) {
        items.add(position, ride)
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