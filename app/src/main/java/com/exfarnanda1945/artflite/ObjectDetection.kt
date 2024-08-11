package com.exfarnanda1945.artflite

import android.graphics.Bitmap

interface ObjectDetection {
    fun detect(image: Bitmap, rotation: Int): List<Classification>
}