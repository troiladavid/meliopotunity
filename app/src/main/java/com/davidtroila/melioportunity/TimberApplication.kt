package com.davidtroila.melioportunity

import android.app.Application
import timber.log.Timber

/**
 * Created by David Troila
 */
class PusherApplication : Application() {

    /**
     * Initialize Timber
     */
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}