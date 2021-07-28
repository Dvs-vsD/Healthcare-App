package com.app.consultationpoint

import android.app.Application
import android.content.SharedPreferences
import com.app.consultationpoint.utils.Const
import io.realm.Realm
import io.realm.RealmConfiguration
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class ConsultationApp : Application() {

    companion object {
        lateinit var shPref: SharedPreferences
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@ConsultationApp)
            modules(listOf(viewModelModule, repositoryModule, firebaseModule))
        }

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        Realm.init(this)

        val config = RealmConfiguration.Builder()
            .allowQueriesOnUiThread(true)
            .allowWritesOnUiThread(true)
            .deleteRealmIfMigrationNeeded()
            .build()

        Realm.setDefaultConfiguration(config)

        shPref = this.getSharedPreferences(
            Const.SHARED_PREF_APP_NAME, MODE_PRIVATE
        )
    }
}