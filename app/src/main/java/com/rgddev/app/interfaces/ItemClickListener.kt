package com.rgddev.app.interfaces

import android.view.View

interface ItemClickListener {
    abstract fun recyclerViewListClicked(v: View, position: Int, flag: Int)


}