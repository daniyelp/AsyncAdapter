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
import com.example.asynclayoutinflater.CustomAdapter
import com.example.asynclayoutinflater.components.*
import com.example.asynclayoutinflater.ui.theme.AsyncLayoutInflaterTheme
import java.lang.NumberFormatException
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
    "sed facilisis sollicitudin dolor sit",
    "Lorem ipsum dolor sit amet",
    "consectetur adipiscing elit",
    "pellentesque in volutpat nec",
)

private val actualItems = mutableListOf<String>()

private val customAdapter: CustomAdapter = CustomAdapter(
    items = actualItems,
    onClick = { },
    onUndoDeleteStarted = { undoAction -> showSnackbar(
        snackState.value,
        "Ride deleted",
        "Undo",
        { undoAction() },
        {}
    )},
    delayUpdate = 500L,
    delayBetweenItems = 300L,
    createAnimation = { AlphaAnimation(0f, 1f).apply { duration = 300; }}
)

private val optionsAdd = listOf(1, 2, 3, 4, 5)
private val selectedOptionAdd = mutableStateOf(optionsAdd[0])

private val optionsDel = listOf(1, 2, 3, 4, 5)
private val selectedOptionDel = mutableStateOf(optionsDel[0])

private var initialDelayText = mutableStateOf("500")
private var betweenDelayText = mutableStateOf("200")

private fun onOptionsChanged() {
    try {
        customAdapter.delayUpdate = initialDelayText.value.toLong()
        customAdapter.delayBetweenItems = betweenDelayText.value.toLong()
    } catch (ex: NumberFormatException) {
        customAdapter.delayUpdate = 0L
        customAdapter.delayBetweenItems = 0L
    }
}

private fun add() {
    val numberOfItemsToAdd = selectedOptionAdd.value
    repeat(numberOfItemsToAdd) {
        val positionToAdd = minOf(Random.nextInt(0, 6), maxOf(actualItems.size - 1, 0))
        val ride = dummyItems[0]
        actualItems.add(positionToAdd, ride)
        customAdapter.notifyItemInserted(positionToAdd)
    }
}

private fun remove() {
    val numberOfItemsToDelete = selectedOptionDel.value
    repeat(numberOfItemsToDelete) {
        if(actualItems.isEmpty()) return
        val positionToDelete = minOf(Random.nextInt(0, 6), actualItems.size - 1)
        actualItems.removeAt(positionToDelete)
        customAdapter.notifyItemRemoved(positionToDelete)
    }
}

@ExperimentalMaterialApi
@Composable
fun Screen() {
    var moreOptionsExpanded by remember { mutableStateOf(false) }
    val moreArrowRotationState by animateFloatAsState(
        targetValue = if(moreOptionsExpanded) 180f else 0f
    )
    Column(modifier = Modifier.fillMaxWidth()) {
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
                    CustomButton(
                        modifier = Modifier
                            .padding(8.dp)
                            .width(80.dp),
                        onClick = {
                            recyclerVisible.value = !recyclerVisible.value
                            toggleButtonText.value = if(toggleButtonText.value == "Show") "Hide" else "Show"
                        },
                        text = toggleButtonText
                    )
                    Button(
                        modifier = Modifier.padding(8.dp),
                        onClick = { add() }
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "")
                    }
                    Button(
                        modifier = Modifier.padding(8.dp),
                        onClick = { remove() }
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
                            selectedOption = selectedOptionAdd,
                        )
                        LabelWithDropDownMenu(
                            modifier = Modifier.padding(8.dp),
                            label = "Deletions",
                            options = optionsDel,
                            selectedOption = selectedOptionDel,
                        )
                        LabelWithText(
                            modifier = Modifier.padding(8.dp),
                            label = "Initial delay",
                            text = initialDelayText,
                            afterValueChange = { onOptionsChanged()}
                        )
                        LabelWithText(
                            modifier = Modifier.padding(8.dp),
                            label = "Between delay",
                            text = betweenDelayText,
                            afterValueChange = { onOptionsChanged()}
                        )
                    }
                }
            }
        }
        Box {
            Items(recyclerVisible)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                ProgressBar(progressBarVisible)
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

@Composable
fun Items(visible: MutableState<Boolean>) {
    if(visible.value) {
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
                            customAdapter.onSyncInflationsFinished()
                        }
                    }
                }
            },
            update = {
                it.apply {
                    itemAnimator = DefaultItemAnimator()
                    adapter = customAdapter
                    setHasFixedSize(true)
                    progressBarVisible.value = true
                    customAdapter.updateData(dummyItems) {
                        progressBarVisible.value = false
                    }
                }
            }
        )
    }
}