package com.app.consultationpoint

import android.app.Application
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.app.consultationpoint.utils.Const
import com.app.consultationpoint.utils.Utils
import dagger.hilt.android.HiltAndroidApp
import io.realm.Realm
import io.realm.RealmConfiguration
import timber.log.Timber
import java.util.*

@HiltAndroidApp
class ConsultationApp : Application() {

    companion object {
        lateinit var shPref: SharedPreferences
        lateinit var shPrefGlobal: SharedPreferences
        lateinit var config: RealmConfiguration

        fun createRealmDB() {
            val realmKey = Utils.getSecureRealmKey()

            config = RealmConfiguration.Builder()
                .name(Utils.getUserId() + "db.realm")
                .allowQueriesOnUiThread(true)
                .allowWritesOnUiThread(true)
                .deleteRealmIfMigrationNeeded()
                .encryptionKey(realmKey)
                .build()

            Realm.setDefaultConfiguration(config)

            Arrays.fill(realmKey, 0.toByte())

            Timber.d("Db created Open Instance at %s", System.currentTimeMillis().toString())
        }
    }

    override fun onCreate() {
        super.onCreate()
        /*startKoin {
            androidContext(this@ConsultationApp)
            modules(listOf(viewModelModule, repositoryModule, firebaseModule))
        }*/

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
        val masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)

        shPref = EncryptedSharedPreferences.create(
            Const.SHARED_PREF_APP_NAME,
            masterKeyAlias,
            this,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        shPrefGlobal = EncryptedSharedPreferences.create(
            Const.SHARED_PREF_NAME_BIOMETRIC,
            masterKeyAlias,
            this,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        Realm.init(this)

        Timber.d("user id %s", Utils.getUserId())

        if (Utils.getUserId().isNotEmpty()) {
            createRealmDB()
        }
    }
}