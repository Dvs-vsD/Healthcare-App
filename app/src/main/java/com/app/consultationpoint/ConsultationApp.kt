package com.app.consultationpoint

import android.app.Application
import android.content.SharedPreferences
import com.app.consultationpoint.utils.Const
import com.app.consultationpoint.utils.Utils
import dagger.hilt.android.HiltAndroidApp
import io.realm.Realm
import io.realm.RealmConfiguration
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

@HiltAndroidApp
class ConsultationApp : Application() {

    companion object {
        lateinit var shPref: SharedPreferences
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

        shPref = this.getSharedPreferences(
            Const.SHARED_PREF_APP_NAME, MODE_PRIVATE
        )

        Realm.init(this)

        Timber.d("user id %s", Utils.getUserId())

        if (Utils.getUserId().isNotEmpty()) {

            val config = RealmConfiguration.Builder()
                .name(Utils.getUserId() + "db.realm")
                .allowQueriesOnUiThread(true)
                .allowWritesOnUiThread(true)
                .deleteRealmIfMigrationNeeded()
                .build()

            Realm.setDefaultConfiguration(config)
        }
    }
}