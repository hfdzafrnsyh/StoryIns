package com.example.storyins.utils

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object Helpers {


    val timesStamp:String = SimpleDateFormat("dd-MM-yyyy", Locale.US)
        .format(System.currentTimeMillis())

    fun formatDate(value: String?): String? {
        return convertISOTimeToDate(value.toString())
    }

   @SuppressLint("SimpleDateFormat")
   private fun convertISOTimeToDate(isoTime: String): String? {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val convertedDate: Date?
        var formattedDate: String? = null
        try {
            convertedDate = sdf.parse(isoTime)
            formattedDate = SimpleDateFormat("HH:mm, MMM dd").format(convertedDate!!)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return formattedDate
    }


    fun uriToFIle(selectImg : Uri, context : Context): File {
        val cr : ContentResolver = context.contentResolver
        val myFile = createCustomTempFile(context)

        val inputStream  = cr.openInputStream(selectImg) as InputStream
        val outputStream : OutputStream = FileOutputStream(myFile)
        val buf = ByteArray(1024)
        var len : Int

        while(inputStream.read(buf).also { len = it } > 0){
            outputStream.write(buf,0,len)
        }

        outputStream.close()
        inputStream.close()

        return myFile

    }


    fun createCustomTempFile(context: Context): File {
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(timesStamp, ".jpg", storageDir)
    }


}