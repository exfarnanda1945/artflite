package com.exfarnanda1945.artflite

import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.StateFlow

@Composable
fun CameraPreviewScreen(isCameraGranted: StateFlow<Boolean>, modifier: Modifier = Modifier) {
    val cameraPermission by isCameraGranted.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var classifications by remember {
        mutableStateOf(emptyList<Classification>())
    }

    val imageAnalyzer = remember {
        ImageAnalyzer(
            detector = ObjectDetectionImpl(
                context = context
            ),
            onResult = {
                classifications = it
            }
        )
    }

    val cameraController = remember {
        LifecycleCameraController(context).apply {
            bindToLifecycle(lifecycleOwner)
            setEnabledUseCases(CameraController.IMAGE_ANALYSIS)
            setImageAnalysisAnalyzer(
                ContextCompat.getMainExecutor(context), imageAnalyzer
            )
        }
    }

    if (cameraPermission) {
        Column(modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize(0.9f)) {
                AndroidView(modifier = modifier.fillMaxSize(), factory = { ctx ->
                    PreviewView(ctx).apply {
                        scaleType = PreviewView.ScaleType.FILL_START
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                        controller = cameraController
                    }
                }, onRelease = {
                    cameraController.unbind()
                })
                classifications.forEach { item ->
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val rectColor = Color.Red
                        val rectThickness = 4.dp.toPx()
                        val top = item.boundingBox.top * item.scaleFactor(size.width, size.height)
                        val left = item.boundingBox.left * item.scaleFactor(size.width, size.height)
                        val right =
                            item.boundingBox.right * item.scaleFactor(size.width, size.height)
                        val bottom =
                            item.boundingBox.bottom * item.scaleFactor(size.width, size.height)


                        drawRect(
                            color = rectColor,
                            topLeft = Offset(left, top),
                            size = Size(
                                right - left,
                                bottom - top
                            ),
                            style = Stroke(rectThickness)
                        )

                    }
                }
            }
            classifications.forEach { item ->
                Text(text = "name :" + item.name)
                Text(text = "score :" + item.score)
            }
        }
    } else {
        Text(text = "Camera permission denied!")
    }


}