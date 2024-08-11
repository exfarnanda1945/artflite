package com.exfarnanda1945.artflite

import android.graphics.RectF

data class Classification(
    val name:String,
    val score:Float,
    val boundingBox:RectF,
    val imageHeight:Int,
    val imageWidth:Int,
    val scaleFactor:(width:Float,height:Float) -> Float
)
