package com.app.consultationpoint.utils

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import com.app.consultationpoint.ConsultationApp
import java.text.SimpleDateFormat
import java.util.*

object Utils {

    fun String.toDate(
        dateFormat: String = "yyyy-MM-dd HH:mm:ss",
        timeZone: TimeZone = TimeZone.getTimeZone(
            "UTC"
        ),
    ): Date? {
        val parser = SimpleDateFormat(dateFormat, Locale.getDefault())
        parser.timeZone = timeZone
        return parser.parse(this)
    }

    @SuppressLint("SimpleDateFormat")
    fun String.toLocalDate(dateFormat: String): Date? {
        val parser = SimpleDateFormat(dateFormat)
        return parser.parse(this)
    }

    fun Date.formatTo(dateFormat: String, timeZone: TimeZone = TimeZone.getDefault()): String {
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        formatter.timeZone = timeZone
        return formatter.format(this)
    }

    fun tomorrowDate(date: Date): Date {
        val cal = Calendar.getInstance()
        cal.time = date
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.add(Calendar.DATE, 1)
        return cal.time
    }

    fun todayDate(date: Date): Date {
        val cal = Calendar.getInstance()
        cal.time = date
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        return cal.time
    }

    private var dialog: ProgressDialog? = null

    fun showProgressDialog(context: Context, msg: String) {
        if (dialog == null) {
            dialog = ProgressDialog(context)
            dialog?.setTitle("Please Wait...")
            dialog?.setMessage(msg)
            dialog?.setCancelable(false)
            dialog?.show()
        }
    }

    fun dismissProgressDialog() {
        if (dialog != null) {
            dialog?.dismiss()
            dialog = null
        }
    }

    fun getFirstName(): String {
        return ConsultationApp.shPref.getString(Const.FIRST_NAME,"")?:""
    }
    fun getLastName(): String {
        return ConsultationApp.shPref.getString(Const.LAST_NAME,"")?:""
    }
    fun getUserEmail(): String {
        return ConsultationApp.shPref.getString(Const.USER_EMAIL,"")?:""
    }
    fun getUserId(): String {
        return ConsultationApp.shPref.getString(Const.USER_ID,"")?:""
    }
    fun getUserProfile(): String {
        return ConsultationApp.shPref.getString(Const.USER_PROFILE,"")?:""
    }
    fun getUserPhnNo(): String {
        return ConsultationApp.shPref.getString(Const.PHN_NO,"")?:""
    }
    fun getUserAdr(): String {
        return ConsultationApp.shPref.getString(Const.ADDRESS,"")?:""
    }

    fun getImageBitMap(context: Context, image: String): Bitmap {
        val imgUri: Uri = Uri.parse(image)
        return MediaStore.Images.Media.getBitmap(context.contentResolver,imgUri)
    }

    fun getUserType(): Int {
        return ConsultationApp.shPref.getInt(Const.USER_TYPE, 0)
    }

}