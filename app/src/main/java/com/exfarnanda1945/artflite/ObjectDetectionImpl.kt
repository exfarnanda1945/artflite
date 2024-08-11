package com.exfarnanda1945.artflite

import android.content.Context
import android.graphics.Bitmap
import android.view.Surface
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.Rot90Op
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.core.vision.ImageProcessingOptions
import org.tensorflow.lite.task.vision.detector.ObjectDetector
import kotlin.math.max

class ObjectDetectionImpl(
    private val context: Context,
    private val threshold: Float = 0.5f,
    private val maxResult: Int = 1
) : ObjectDetection {
    private var detector: ObjectDetector? = null

    private fun setupDetector() {
        val baseOption = BaseOptions.builder().setNumThreads(2).build()

        val detectorBuilder =
            ObjectDetector.ObjectDetectorOptions.builder().setBaseOptions(baseOption)
                .setScoreThreshold(threshold)
                .setMaxResults(maxResult)
                .build()

        try {
            detector = ObjectDetector.createFromFileAndOptions(
                context,
                "efficientdet_model.tflite",
                detectorBuilder
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun detect(image: Bitmap, rotation: Int): List<Classification> {
        if (detector == null) setupDetector()

        val imageProcessor = ImageProcessor.Builder().add(Rot90Op(-rotation / 90)).build()
        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(image))

        val orientation = when (rotation) {
            Surface.ROTATION_90 -> ImageProcessingOptions.Orientation.TOP_LEFT
            Surface.ROTATION_180 -> ImageProcessingOptions.Orientation.RIGHT_BOTTOM
            Surface.ROTATION_270 -> ImageProcessingOptions.Orientation.BOTTOM_RIGHT
            else -> ImageProcessingOptions.Orientation.RIGHT_TOP

        }

        val imageProcessingOptions =
            ImageProcessingOptions.builder().setOrientation(orientation).build()
        val result = detector?.detect(tensorImage)


        return result?.flatMap { item ->
            item.categories.map { category ->
                Classification(
                    name = category.label,
                    score = category.score,
                    boundingBox = item.boundingBox,
                    imageHeight = tensorImage.height,
                    imageWidth = tensorImage.width,
                    scaleFactor = { width, height ->
                        max(width * 1f / tensorImage.width, height * 1f / tensorImage.height)
                    }
                )
            }
        }?.distinctBy { it.name } ?: emptyList()
    }
}