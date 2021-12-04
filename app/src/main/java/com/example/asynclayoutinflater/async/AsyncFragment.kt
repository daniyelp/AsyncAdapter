package com.example.asynclayoutinflater.async

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.asynclayoutinflater.AsyncAdapter
import com.example.asynclayoutinflater.ItemView
import com.example.asynclayoutinflater.components.*
import com.example.asynclayoutinflater.ui.theme.AsyncLayoutInflaterTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception
import kotlin.random.Random

class AsyncFragment: Fragment() {

    @ExperimentalMaterialApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                AsyncLayoutInflaterTheme {
                    Scaffold {
                        Screen()
                    }
                }
            }
        }
    }
}

var recyclerVisible = mutableStateOf(false)
var progressBarVisible = mutableStateOf(false)
var toggleButtonText = mutableStateOf("Show")
val snackState = mutableStateOf(SnackbarHostState())

private val dummyItems = mutableListOf(
    "neque porro quisquam",
    "est qui dolorem",
    "ipsum quia dolor sit",
    "lorem ipsum dolor sit amet",
    "consectetur adipiscing elit",
    "duis eget consequat eros",
    "Lorem ipsum dolor sit amet",
    "consectetur adipiscing elit",
    "pellentesque in volutpat nec",
)

private val actualItems = mutableListOf<String>()

private val optionsAdd = listOf(1, 2, 3, 4, 5)
private val selectedOptionAdd = mutableStateOf(optionsAdd[0])

private val optionsDel = listOf(1, 2, 3, 4, 5)
private val selectedOptionDel = mutableStateOf(optionsDel[0])

private var initialDelayText = mutableStateOf("500")
private var betweenDelayText = mutableStateOf("200")

private val asyncAdapter: AsyncAdapter<String > = AsyncAdapter(
    items = actualItems,
    onItemClick = { },
    onUndoDeleteStarted = { undoAction -> showSnackbar(
        snackState.value,
        "Item deleted",
        "Undo",
        { undoAction() },
        {}
    )},
    betweenItemsInflateDelay = betweenDelayText.value.toLong(),
    buildInflateAnimation = { AlphaAnimation(0f, 1f).apply { duration = 300; }},
    buildAsyncItem = { context, onSyncInflationFinished, onDisplayFinished ->
        ItemView(context, onSyncInflationFinished, onDisplayFinished)
    }
)

private fun add() {
    val numberOfItemsToAdd = selectedOptionAdd.value
    repeat(numberOfItemsToAdd) {
        val positionToAdd = minOf(Random.nextInt(0, 6), maxOf(actualItems.size - 1, 0))
        val item = dummyItems[0]
        actualItems.add(positionToAdd, item)
        asyncAdapter.notifyItemInserted(positionToAdd)
    }
}

private fun remove() {
    val numberOfItemsToDelete = selectedOptionDel.value
    repeat(numberOfItemsToDelete) {
        if(actualItems.isEmpty()) return
        val positionToDelete = minOf(Random.nextInt(0, 6), actualItems.size - 1)
        actualItems.removeAt(positionToDelete)
        asyncAdapter.notifyItemRemoved(positionToDelete)
    }
}

@ExperimentalMaterialApi
@Composable
fun Screen() {
    Column(modifier = Modifier.fillMaxWidth()) {
        ControlPanelCard()
        Box {
            Items()
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                if(progressBarVisible.value) {
                    CircularProgressIndicator()
                }
            }
            CustomSnackbar(
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(8.dp),
                snackState
            )
        }
    }
}

@ExperimentalMaterialApi
@Composable
private fun ControlPanelCard() {
    var moreOptionsExpanded by remember { mutableStateOf(false) }
    val moreArrowRotationState by animateFloatAsState(
        targetValue = if(moreOptionsExpanded) 180f else 0f
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 400,
                    easing = LinearOutSlowInEasing
                )
            )
    ) {
        Column {
            Row {
                Button(
                    modifier = Modifier
                        .padding(8.dp)
                        .width(80.dp),
                    onClick = {
                        recyclerVisible.value = !recyclerVisible.value
                        toggleButtonText.value = if(toggleButtonText.value == "Show") "Hide" else "Show"
                    }
                ) {
                    Text(toggleButtonText.value)
                }
                Button(
                    modifier = Modifier.padding(8.dp),
                    onClick = ::add
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "")
                }
                Button(
                    modifier = Modifier.padding(8.dp),
                    onClick = ::remove
                ) {
                    Icon(imageVector = Icons.Default.Remove, contentDescription = "")
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        modifier = Modifier
                            .padding(8.dp)
                            .rotate(moreArrowRotationState),
                        onClick = { moreOptionsExpanded = !moreOptionsExpanded }
                    ) {
                        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "")
                    }
                }
            }
            if (moreOptionsExpanded) {
                Column {
                    LabelWithDropDownMenu(
                        modifier = Modifier.padding(8.dp),
                        label = "Additions",
                        options = optionsAdd,
                        selectedOption = selectedOptionAdd.value,
                        onSelectedOptionChange = {
                            selectedOptionAdd.value = it
                        }
                    )
                    LabelWithDropDownMenu(
                        modifier = Modifier.padding(8.dp),
                        label = "Deletions",
                        options = optionsDel,
                        selectedOption = selectedOptionDel.value,
                        onSelectedOptionChange = {
                            selectedOptionDel.value = it
                        }
                    )
                    LabelWithText(
                        modifier = Modifier.padding(8.dp),
                        label = "Initial delay",
                        text = initialDelayText.value,
                        onTextChange = {
                            initialDelayText.value = it
                        }
                    )
                    LabelWithText(
                        modifier = Modifier.padding(8.dp),
                        label = "Between delay",
                        text = betweenDelayText.value,
                        onTextChange = {
                            betweenDelayText.value = it
                            asyncAdapter.betweenItemsInflateDelay = try {
                                it.toLong()
                            } catch (e: Exception) {
                                0L
                            }
                        }
                    )
                }
            }
        }
    }
}
@Composable
fun Items() {
    if(recyclerVisible.value) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                RecyclerView(context).apply {
                    layoutManager = object: LinearLayoutManager(context, VERTICAL, false) {
                        override fun onLayoutCompleted(state: RecyclerView.State?) {
                            super.onLayoutCompleted(state)
                            val firstVisibleItemPosition = findFirstVisibleItemPosition()
                            val lastVisibleItemPosition = findLastVisibleItemPosition()
                            if(firstVisibleItemPosition == -1 || lastVisibleItemPosition == -1)
                                return
                            asyncAdapter.onSyncInflationsFinished()
                        }
                    }
                }
            },
            update = {
                it.apply {
                    itemAnimator = DefaultItemAnimator()
                    adapter = asyncAdapter
                    //setHasFixedSize(true)
                    progressBarVisible.value = true
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(try { initialDelayText.value.toLong() } catch (e: Exception) {0L})
                        asyncAdapter.updateData(dummyItems) {
                            progressBarVisible.value = false
                        }
                    }
                }
            }
        )
    }
}