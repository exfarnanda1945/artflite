package com.exfarnanda1945.artflite.od

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy

class ImageAnalyzer(
    private val detector: ObjectDetection,
    private val onResult: (List<Classification>) -> Unit
) : ImageAnalysis.Analyzer {
    private var skipFrame = 0

    override fun analyze(image: ImageProxy) {
        if (skipFrame % 30 == 0) {
            val rotationDegree = image.imageInfo.rotationDegrees
            val bitmap = image.toBitmap()

            val result = detector.detect(bitmap, rotationDegree)
            onResult(result)

        }
        skipFrame++

        image.close()
    }
}
