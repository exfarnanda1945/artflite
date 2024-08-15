package com.exfarnanda1945.artflite.od

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy

class ImageAnalyzer(
    private val detector: ObjectDetection,
    private val onResult: (List<Classification>) -> Unit
) : ImageAnalysis.Analyzer {
    override fun analyze(image: ImageProxy) {
        val rotationDegree = image.imageInfo.rotationDegrees
        val bitmap = image.toBitmap()

        val result = detector.detect(bitmap, rotationDegree)
        onResult(result)

        image.close()
    }
}
