package com.exfarnanda1945.artflite.ar

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.sceneview.Scene
import io.github.sceneview.math.Position
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberCameraManipulator
import io.github.sceneview.rememberCameraNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberEnvironmentLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNode
import io.github.sceneview.rememberOnGestureListener

@Composable
fun SceneView(modifier: Modifier = Modifier) {
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val environmentLoader = rememberEnvironmentLoader(engine)

    val centerNode = rememberNode(engine)

    val cameraNode = rememberCameraNode(engine) {
        position = Position(x = 0f, y = -0.5f, z = 2.0f)
        lookAt(centerNode)

        centerNode.addChildNode(this)
    }

    Scene(
        modifier = Modifier.fillMaxSize(),
        engine = engine,
        modelLoader = modelLoader,
        cameraNode = cameraNode,
        cameraManipulator = rememberCameraManipulator(
            orbitHomePosition = cameraNode.worldPosition,
            targetPosition = centerNode.worldPosition
        ),
        childNodes = listOf(
            centerNode,
            ModelNode(
                modelInstance = modelLoader.createModelInstance(
                    assetFileLocation = "models/t-shirt_and_pant.glb"
                ),
                scaleToUnits = 0.25f
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
        )
    )
}