package com.app.consultationpoint.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower
import com.app.consultationpoint.ConsultationApp
import com.app.consultationpoint.R
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

    private var dialog: ACProgressFlower? = null

    fun showProgressDialog(context: Context) {
        if (dialog == null) {
            dialog = ACProgressFlower.Builder(context)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(ContextCompat.getColor(context, R.color.blue))
                .text("Loading...")
                .textColor(ContextCompat.getColor(context, R.color.blue))
                .build()
        }
        dialog?.show()
    }

    fun dismissProgressDialog() {
        if (dialog != null) {
            dialog?.dismiss()
            dialog = null
        }
    }

    fun getFirstName(): String {
        return ConsultationApp.shPref.getString(Const.FIRST_NAME, "") ?: ""
    }

    fun getUserName(): String {
        return ConsultationApp.shPref.getString(Const.USER_NAME, "") ?: ""
    }

    fun getLastName(): String {
        return ConsultationApp.shPref.getString(Const.LAST_NAME, "") ?: ""
    }

    fun getUserEmail(): String {
        return ConsultationApp.shPref.getString(Const.USER_EMAIL, "") ?: ""
    }

    fun getUserId(): String {
        return ConsultationApp.shPref.getString(Const.USER_ID, "") ?: ""
    }

    fun getUserProfile(): String {
        return ConsultationApp.shPref.getString(Const.USER_PROFILE, "") ?: ""
    }

    fun getUserPhnNo(): String {
        return ConsultationApp.shPref.getString(Const.PHN_NO, "") ?: ""
    }

    fun getUserGender(): String {
        return ConsultationApp.shPref.getString(Const.GENDER, "") ?: ""
    }

    fun getDOB(): String {
        return ConsultationApp.shPref.getString(Const.DOB, "") ?: ""
    }

    fun getUserAdr(): String {
        return ConsultationApp.shPref.getString(Const.ADDRESS, "") ?: ""
    }

    fun getCity(): String {
        return ConsultationApp.shPref.getString(Const.CITY, "") ?: ""
    }

    fun getState(): String {
        return ConsultationApp.shPref.getString(Const.STATE, "") ?: ""
    }

    fun getCountry(): String {
        return ConsultationApp.shPref.getString(Const.COUNTRY, "") ?: ""
    }

    fun getPinCode(): Int {
        return ConsultationApp.shPref.getInt(Const.PIN_CODE, 0)
    }

    fun getImageBitMap(context: Context, image: String): Bitmap {
        val imgUri: Uri = Uri.parse(image)
        return MediaStore.Images.Media.getBitmap(context.contentResolver, imgUri)
    }

    fun getUserType(): Int {
        return ConsultationApp.shPref.getInt(Const.USER_TYPE, 0)
    }

}