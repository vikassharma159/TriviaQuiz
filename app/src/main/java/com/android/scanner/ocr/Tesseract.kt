package com.android.scanner.ocr

import android.content.Context
import android.graphics.Bitmap
import com.android.scanner.util.SingletonHolder
import com.googlecode.tesseract.android.TessBaseAPI
import java.io.File


/**
 * @author Harsh Mehlawat on 17/05/21.
 */
class Tesseract private constructor(context : Context){

    private var mTess : TessBaseAPI = TessBaseAPI()

    init {
        val path = context.filesDir.toString() + "/tesseract/"
        val language = "eng"
        val dir = File(path + "tessdata/")
        if (!dir.exists()) dir.mkdirs()
        mTess.init(path, language)
    }

    companion object : SingletonHolder<Tesseract, Context>(::Tesseract)

    fun getOCRResult(bitmap: Bitmap?): String? {
        if (bitmap == null) {
            return null
        }
        mTess.setImage(bitmap)
        return mTess.utF8Text
    }

    fun onDestroy() {
        mTess.end()
    }

}