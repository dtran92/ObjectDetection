package com.dtran.scanner.ui.screen.scan

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.dtran.scanner.util.rotate
import com.google.mlkit.common.model.CustomRemoteModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.ObjectDetector
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalGetImage::class)
class ImageAnalyzer(
    remoteModel: CustomRemoteModel,
    private val onSuccessCallback: (String, String) -> Unit,
    private val onFailureCallback: () -> Unit
) : ImageAnalysis.Analyzer {
    private val objectDetector: ObjectDetector
    private var item = emptyList<String>()

    init {
        val optionsBuilder = CustomObjectDetectorOptions.Builder(remoteModel)
        val customObjectDetectorOptions =
            optionsBuilder.setDetectorMode(CustomObjectDetectorOptions.STREAM_MODE).enableClassification()
                .setClassificationConfidenceThreshold(0.75F).build()
        objectDetector = ObjectDetection.getClient(customObjectDetectorOptions)
    }

    fun updateItem(newItem: List<String>) {
        item = newItem
    }

    override fun analyze(p0: ImageProxy) {
        val img = p0.image
        img?.let {

            val inputImage = InputImage.fromMediaImage(img, p0.imageInfo.rotationDegrees)
            objectDetector.process(inputImage).addOnSuccessListener {
                it.forEach { elem ->
                    elem.labels.apply {
                        val text = buildString {
                            this@apply.forEach { label ->
                                append(label.text)
                                append(" ")
                            }
                        }.trimEnd()
                        Log.d("ImageAnalyzer", text)
                        var isInList = false
                        if (item.isEmpty()) isInList = true
                        else {
                            for (obj in item) {
                                if (text.contains(obj, ignoreCase = true)) {
                                    isInList = true
                                    break
                                }
                            }
                        }

                        if (text.isNotEmpty() && isInList) {
                            val byteArrayOutputStream = ByteArrayOutputStream()
                            p0.toBitmap().rotate(inputImage.rotationDegrees.toFloat())
                                .compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream)
                            onSuccessCallback.invoke(
                                Base64.encodeToString(
                                    byteArrayOutputStream.toByteArray(), Base64.DEFAULT
                                ), text
                            )
                        } else {
                            onFailureCallback.invoke()
                        }
                    }
                }
            }.addOnFailureListener {
                onFailureCallback.invoke()
                Log.e("ImageAnalyzer", it.message.toString())
            }.addOnCompleteListener {
                p0.close()
            }
        }
    }
}