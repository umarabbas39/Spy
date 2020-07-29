package com.shehzad.screenshottesting

import android.graphics.Bitmap

class ListenerSS {
    companion object {
        private var informer: ((Bitmap?) -> Unit)? = null

        fun setObserver(handler:(Bitmap?) -> Unit){
            informer = handler
        }

        fun inform(bitmap: Bitmap?){
            informer?.invoke(bitmap)
        }
    }
}