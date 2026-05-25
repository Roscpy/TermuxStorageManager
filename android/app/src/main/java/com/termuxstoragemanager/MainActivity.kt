package com.termuxstoragemanager

import android.app.Activity
import android.content.Intent
import com.facebook.react.ReactActivity
import com.facebook.react.ReactActivityDelegate
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint
import com.facebook.react.defaults.DefaultReactActivityDelegate

class MainActivity : ReactActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (BuildConfig.IS_NEW_ARCHITECTURE_ENABLED) {
            DefaultNewArchitectureEntryPoint.load()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val reactContext = (application as com.facebook.react.ReactApplication)
            .reactNativeHost
            .reactInstanceManager
            .currentReactContext
        reactContext
            ?.getNativeModule(StorageManagerModule::class.java)
            ?.handleActivityResult(requestCode, resultCode, data)
    }

    override fun createReactActivityDelegate(): ReactActivityDelegate {
        return DefaultReactActivityDelegate(this, mainComponentName)
    }

    companion object {
        const val mainComponentName = "TermuxStorageManager"
    }
}
