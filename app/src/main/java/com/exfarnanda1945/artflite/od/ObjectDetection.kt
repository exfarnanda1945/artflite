package com.exfarnanda1945.artflite.od

import android.graphics.Bitmap

interface ObjectDetection {
    fun detect(image: Bitmap, rotation: Int): List<Classification>
}