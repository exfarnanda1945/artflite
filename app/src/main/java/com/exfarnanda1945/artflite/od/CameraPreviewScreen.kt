package com.exfarnanda1945.artflite.od

import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.sceneview.Scene
import io.github.sceneview.math.Position
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberCameraManipulator
import io.github.sceneview.rememberCameraNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNode
import io.github.sceneview.rememberOnGestureListener
import kotlinx.coroutines.flow.StateFlow

@Composable
fun CameraPreviewScreen(
    isCameraGranted: StateFlow<Boolean>,
    modifier: Modifier = Modifier
) {
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
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        }
    }


    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine = engine)
    val centerNode = rememberNode(engine)
    val cameraNode = rememberCameraNode(engine) {
        position = Position(x = 0f, y = -0.5f, z = 2.0f)
        lookAt(centerNode)

        centerNode.addChildNode(this)
    }

    Log.d("classification", classifications.toString())

    if (cameraPermission) {
        Column(modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize()) {
                AndroidView(modifier = modifier.fillMaxSize(), factory = { ctx ->
                    PreviewView(ctx).apply {
                        scaleType = PreviewView.ScaleType.FILL_START
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                        controller = cameraController
                    }
                }, onRelease = {
                    cameraController.unbind()
                })
                val person = classifications.find { item -> item.name.lowercase() == "person" }
                if (person != null) {
                    Scene(
                        modifier = Modifier
                            .fillMaxSize(),
                        engine = engine,
                        isOpaque = false,
                        modelLoader = modelLoader,
                        cameraNode = cameraNode,
                        cameraManipulator = rememberCameraManipulator(
                            orbitHomePosition = cameraNode.worldPosition,
                            targetPosition = centerNode.worldPosition,

                        ),
                        childNodes = listOf(
                            centerNode,
                            ModelNode(
                                modelInstance = modelLoader.createModelInstance(
                                    assetFileLocation = "models/t-shirt_and_pant.glb",
                                )
                            )
                        ),
                        onFrame = {
                            cameraNode.lookAt(centerNode)
                        },
                        onGestureListener = rememberOnGestureListener(
                            onDoubleTap = { _, node ->
                                node?.apply {
                                    scale *= 2.0f
                                }
                            }
                        ),

                        )
                }
//                classifications.forEach { item ->
//                    Canvas(modifier = Modifier.fillMaxSize()) {
//                        val rectColor = Color.Red
//                        val rectThickness = 4.dp.toPx()
//                        val top = item.boundingBox.top * item.scaleFactor(size.width, size.height)
//                        val left = item.boundingBox.left * item.scaleFactor(size.width, size.height)
//                        val right =
//                            item.boundingBox.right * item.scaleFactor(size.width, size.height)
//                        val bottom =
//                            item.boundingBox.bottom * item.scaleFactor(size.width, size.height)
//
//
//                        drawRect(
//                            color = rectColor,
//                            topLeft = Offset(left, top),
//                            size = Size(
//                                right - left,
//                                bottom - top
//                            ),
//                            style = Stroke(rectThickness)
//                        )
//
//                    }
//                }
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