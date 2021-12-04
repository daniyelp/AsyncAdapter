AsyncAdapter (WIP :construction:)
============
***AsyncAdapter*** is a ***RecyclerView.Adapter*** that inflates a ***RecyclerView***'s items asynchronously. If a ***RecyclerView*** has to display items with a pretty complex layout, inflating such several items at once could result in skipped frames. This repository showcases a solution for avoiding that. (Will soon be turned into a library)

![2021-12-03 13-29-11 (online-video-cutter com) (6)](https://user-images.githubusercontent.com/84658876/144628848-78bd68ea-ffca-4f63-88b1-cfe51ccd26dc.gif)

The ***RecyclerView*** uses an ***AsyncAdapter***, which, when it creates a ***ViewHolder***, it inflates an ***AsyncItem***, that extends a ***Framelayout***. (Inflating several empty ***Framelayouts*** at once is cheap). When the ***AsyncAdapter*** wants to bind the data to the ***ViewHolder***, it will call the ***bind*** method of the ***AsyncItem***. In this ***bind*** method, the inlfation of the real item layout takes place, using a ***AsyncLayoutInflater***. When the real layout is inflated, the binding then takes place and the layout is added to its respective ***Framelayout***. But launching several asynchronous inflations at once is still somehow not performant enough and the UI jank could be still there. Not only that but the items will not appear all at the same time in the ***RecyclerView*** and that is not eye pleasing. To fix this, the ***AsyncAdapter*** sends along with the binding data a delay value which indicates how many milliseconds the ***AsyncItem*** needs to wait before it starts the asyhchronous inflation. This delay value is based on the position of the item in the ***AsyncAdapter*** and a constant indicating the delay between inflations.


Usage
============
```kotlin
private val asyncAdapter: AsyncAdapter<String > = AsyncAdapter(
    items = (1..10).map { it.toString() }.toMutableList(),
    onItemClick = { item -> },
    onUndoDeleteStarted = { undoAction -> showSnackbar(
        snackState.value,
        "Item deleted",
        "Undo",
        { undoAction() },
        { }
    )},
    betweenItemsInflateDelay = 100L,
    buildInflateAnimation = { AlphaAnimation(0f, 1f).apply { duration = 300; }},
    buildAsyncItem = { context, onSyncInflationFinished, onDisplayFinished ->
        ItemView(context, onSyncInflationFinished, onDisplayFinished)
    }
)

reyclerView.adapter = asyncAdapter
 ```
Here is how the ***ItemView*** from above may look like. It extends the ***AsyncItem*** class that the ***AsyncAdapter*** requires.

 ```kotlin
class ItemView(
    context: Context,
    onSyncInflationFinished: () -> Unit,
    onDisplayFinished: () -> Unit
) : AsyncAdapter.AsyncItem<String>(context, R.layout.real_layout, onSyncInflationFinished, onDisplayFinished) {

    private lateinit var exampleTextView: TextView

    override fun findViews(view: View) {
        exampleTextView = view.findViewById(R.id.example_text)
    }

    override fun bindToViews(item: String, onClick: () -> Unit, onDeleteClick: () -> Unit) {
        exampleTextView.text = item
    }
}
```
License
============
Copyright (c) 2021 daniyelp

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

