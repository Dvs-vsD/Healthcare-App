package com.app.consultationpoint.utils

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower
import com.app.consultationpoint.ConsultationApp
import com.app.consultationpoint.GlideApp
import com.app.consultationpoint.R
import com.app.consultationpoint.general.LoginActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*


object Utils {

    fun ImageView.loadImage(url: String) {
        Glide.with(this).load(url).placeholder(R.drawable.progress_animation)
            .error(R.drawable.error).into(this)
    }

    fun ImageView.loadImageFromCloud(url: String) {
        val ref: StorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(url)
        GlideApp.with(this).load(ref).placeholder(R.drawable.default_user)
            .error(R.drawable.error).into(this)
    }

    fun ImageView.loadImageFromCloudWithProgress(url: String, progressBar: ProgressBar) {
        progressBar.show()

        val ref: StorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(url)
        GlideApp.with(this).load(ref).placeholder(R.drawable.default_user)
            .error(R.drawable.error)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    progressBar.hide()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    progressBar.hide()
                    return false
                }
            }).into(this)
    }

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

    var dialog: ACProgressFlower? = null

    fun showProgressDialog(context: Context) {
        if (dialog == null) {
            dialog = ACProgressFlower.Builder(context)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(ContextCompat.getColor(context, R.color.blue))
                .text("Loading...")
                .textColor(ContextCompat.getColor(context, R.color.blue))
                .build()

        }
        dialog?.setCancelable(false)
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

    fun getUserGender(): Int {
        return ConsultationApp.shPref.getInt(Const.GENDER, -1)
    }

    fun getDOB(): String {
        return ConsultationApp.shPref.getString(Const.DOB, "") ?: ""
    }

    fun getSpecialistID(): Int {
        return ConsultationApp.shPref.getInt(Const.SPECIALIST_ID, 0)
    }

    fun getExperienceYears(): String {
        return ConsultationApp.shPref.getString(Const.EXPERIENCE_YEAR, "") ?: ""
    }

    fun getAboutInfo(): String {
        return ConsultationApp.shPref.getString(Const.ABOUT, "") ?: ""
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


    fun setErrorEdt(context: Context, textInputLayout: TextInputLayout) {
        textInputLayout.defaultHintTextColor =
            ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red))
    }

    fun setErrorFreeEdt(context: Context, textInputLayout: TextInputLayout) {
        textInputLayout.defaultHintTextColor =
            ColorStateList.valueOf(ContextCompat.getColor(context, R.color.blue))
    }

    fun setError(context: Context, textView: TextView) {
        textView.setTextColor(ContextCompat.getColor(context, R.color.red))
    }

    fun setErrorFree(context: Context, textView: TextView) {
        textView.setTextColor(ContextCompat.getColor(context, R.color.black))
    }

    fun Context.showToast(msg: String, length: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, msg, length).show()
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // For 29 api or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                    ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        }
        // For below 29 api
        else {
            if (connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!.isConnectedOrConnecting) {
                return true
            }
        }
        return false
    }

    fun View.show() {
        this.visibility = View.VISIBLE
    }

    fun View.hide() {
        this.visibility = View.GONE
    }

    fun getImageUri(context: Context, bitmap: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path: String = MediaStore.Images.Media.insertImage(
            context.contentResolver,
            bitmap,
            "IMG_" + System.currentTimeMillis(),
            null
        )
        return Uri.parse(path)
    }

    fun logoutConformationDialog(context: Context) {
        AlertDialog.Builder(context)
            .setTitle("Are you sure you want to logout?")
            .setPositiveButton("Yes") { dialog, _ ->

                FirebaseAuth.getInstance().signOut()
                ConsultationApp.shPref.edit().clear().apply()

                val intent = Intent(context, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent)

                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}