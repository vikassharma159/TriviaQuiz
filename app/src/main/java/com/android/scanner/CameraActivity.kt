package com.android.scanner

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.android.synthetic.main.activity_camera.*
import java.io.File
import java.util.concurrent.Executor

class CameraActivity : AppCompatActivity(), Executor, ImageCapture.OnImageSavedCallback {

    private lateinit var cameraProviderFuture : ListenableFuture<ProcessCameraProvider>
    private lateinit var imageCapture : ImageCapture

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(this))
    }

    fun bindPreview(cameraProvider : ProcessCameraProvider) {
        var preview : Preview = Preview.Builder()
            .build()

        val cameraSelector : CameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        preview.setSurfaceProvider(previewView.getSurfaceProvider())
        imageCapture = ImageCapture.Builder()
            .setTargetRotation(previewView.display.rotation)
            .build()

        var camera = cameraProvider.bindToLifecycle(this as LifecycleOwner, cameraSelector, imageCapture, preview)
    }

    fun onCapture(view: View) {
        val file = File(filesDir, "CaptureImage.jpg")
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(file).build()
        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(this), this)
    }

    override fun execute(command: Runnable) {
        command.run()
    }

    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
        Toast.makeText(this@CameraActivity, "Image Saved Successfully", Toast.LENGTH_SHORT).show()
    }

    override fun onError(exception: ImageCaptureException) {
        Toast.makeText(this@CameraActivity, "Error in Saving", Toast.LENGTH_SHORT).show()
    }

}