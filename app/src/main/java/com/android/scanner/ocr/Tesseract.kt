package com.android.scanner.ocr

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import com.android.scanner.util.SingletonHolder
import com.googlecode.tesseract.android.TessBaseAPI
import java.io.*


/**
 * @author Harsh Mehlawat on 17/05/21.
 */
class Tesseract private constructor(context : Context){

    private lateinit var mTess : TessBaseAPI

    init {
        val path = context.filesDir.toString()

        val folder = File("$path/tessdata")
        if (!folder.exists()) {
            folder.mkdirs()
        }

        val saving = File(folder, "eng.traineddata")
        try {
            saving.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        var stream: InputStream? = null
        try {
            stream = context.assets.open("eng.traineddata", AssetManager.ACCESS_STREAMING)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        if (stream != null) {
            copyInputStreamToFile(stream, saving)
        }

        val language = "eng"
        mTess = TessBaseAPI()
        mTess.init(path, language)

    }

    companion object : SingletonHolder<Tesseract, Context>(::Tesseract)

    private fun copyInputStreamToFile(`in`: InputStream, file: File) {
        try {
            val out: OutputStream = FileOutputStream(file)
            val buf = ByteArray(1024)
            var len: Int
            while (`in`.read(buf).also { len = it } > 0) {
                out.write(buf, 0, len)
            }
            out.close()
            `in`.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getOCRResult(bitmap: Bitmap?): String? {
        if (bitmap == null) {
            return null
        }
        mTess.setImage(bitmap)
        val text = mTess.utF8Text
        mTess.end()
        return text
    }

}