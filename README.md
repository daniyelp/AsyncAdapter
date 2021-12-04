Asynchronous RecyclerView
============
If a ***RecyclerView*** has to display items having a pretty complex layout, inflating such several items at once could result in skipped frames. This repository showcases a solution for avoiding this.

The ***CustomAdapter***, when it creates a ***ViewHolder***, it inflates a ***CustomItem***, that extends a ***Framelayout***. (Inflating several empty ***Framelayouts*** at once is very cheap). When the ***CustomAdapter*** needs to bind the data to the ***ViewHolder***, it calls the ***bind*** method of the ***CustomItem***. In this ***bind*** method, the inlfation of the real layout takes place, using a ***AsyncLayoutInflater***. When the real layout is inflated, the binding takes place and the layout is added to its respective ***Framelayout***. But launching several asynchronous inflations at once is still somehow not performant enough and the UI jank could be still there. Not only that but the items will not appear all at the same time in the ***RecyclerView*** and that is not eye pleasing. To fix this, the ***CustomAdapter*** sends along with the binding data a delay value which indicates how many milliseconds the ***CustomItem*** needs to wait before it starts the asyhchronous inflation. This delay value is based on the position of the item in the ***CustomAdapter*** and a constant indicating the delay between inflations. 

![2021-12-03 13-29-11 (online-video-cutter com) (6)](https://user-images.githubusercontent.com/84658876/144628848-78bd68ea-ffca-4f63-88b1-cfe51ccd26dc.gif)
## License
Copyright 2021 daniyelp

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

