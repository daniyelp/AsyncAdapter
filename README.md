AsyncAdapter (WIP :construction:)
============
***AsyncAdapter*** is a ***RecyclerView.Adapter*** that inflates a ***RecyclerView***'s items asynchronously. If a ***RecyclerView*** has to display items with a pretty complex layout, inflating such several items at once could result in skipped frames. This repository showcases a solution for avoiding that. (Will soon be turned into a library)

![2021-12-03 13-29-11 (online-video-cutter com) (6)](https://user-images.githubusercontent.com/84658876/144628848-78bd68ea-ffca-4f63-88b1-cfe51ccd26dc.gif)

The ***RecyclerView*** uses a ***CustomAdapter***, which, when it creates a ***ViewHolder***, it inflates a ***CustomItem***, that extends a ***Framelayout***. (Inflating several empty ***Framelayouts*** at once is very cheap). When the ***CustomAdapter*** wants to bind the data to the ***ViewHolder***, it will call the ***bind*** method of the ***CustomItem***. In this ***bind*** method, the inlfation of the real item layout takes place, using a ***AsyncLayoutInflater***. When the real layout is inflated, the binding then takes place and the layout is added to its respective ***Framelayout***. But launching several asynchronous inflations at once is still somehow not performant enough and the UI jank could be still there. Not only that but the items will not appear all at the same time in the ***RecyclerView*** and that is not eye pleasing. To fix this, the ***CustomAdapter*** sends along with the binding data a delay value which indicates how many milliseconds the ***CustomItem*** needs to wait before it starts the asyhchronous inflation. This delay value is based on the position of the item in the ***CustomAdapter*** and a constant indicating the delay between inflations.


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
) : AsyncAdapter.AsyncItem<String>(context, onSyncInflationFinished, onDisplayFinished) {
    
    private lateinit var exampleTextView: TextView

    override fun inflateView(): View {
        return inflate(context, R.layout.example_view, this)
    }

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
MIT License

Copyright (c) 2021 daniyelp

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
