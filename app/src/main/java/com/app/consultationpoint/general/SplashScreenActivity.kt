package com.app.consultationpoint.general

import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.app.consultationpoint.ConsultationApp
import com.app.consultationpoint.R
import com.app.consultationpoint.patient.bottomNavigation.BottomNavigationActivity
import com.app.consultationpoint.utils.Const

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        installSplashScreen()
        setContentView(R.layout.activity_splash_screen)

        if (ConsultationApp.shPref.getBoolean(Const.PREF_IS_LOGIN, false)) {
            authenticateUser()
        } else {
            val intent: Intent
            if (ConsultationApp.shPrefGlobal.getBoolean(Const.IS_ON_BOARDING_COMPLETE, false)) {
                intent = Intent(this, LoginActivity::class.java)
            } else {
                intent = Intent(this, OnBoardingActivity::class.java)
                ConsultationApp.shPrefGlobal.edit().putBoolean(Const.IS_ON_BOARDING_COMPLETE, true)
                    .apply()
            }
            startActivity(intent)
            finish()
        }
    }

    private fun authenticateUser() {
        val keyguardManager: KeyguardManager =
            getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        if (keyguardManager.isKeyguardSecure) {
            val intent: Intent = keyguardManager.createConfirmDeviceCredentialIntent(
                "Verify Your Identity",
                null
            )
            startActivityForResult(intent, 100)
        } else
            goToBottomNavigationActivity()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                goToBottomNavigationActivity()
            } else
                finish()
        }
    }

    private fun goToBottomNavigationActivity() {
        val intent = Intent(this, BottomNavigationActivity::class.java)
        intent.putExtra("fromSplash", true)
        startActivity(intent)
        finish()
    }
}