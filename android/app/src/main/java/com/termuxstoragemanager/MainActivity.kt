package com.termuxstoragemanager

import android.content.Intent
import com.termuxstoragemanager.StorageManagerModule
import com.facebook.react.ReactActivity
import com.facebook.react.ReactActivityDelegate
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint.fabricEnabled
import com.facebook.react.defaults.DefaultReactActivityDelegate

class MainActivity : ReactActivity() {

    override fun getMainComponentName(): String = "TermuxStorageManager"

    override fun createReactActivityDelegate(): ReactActivityDelegate =
        DefaultReactActivityDelegate(this, mainComponentName, fabricEnabled)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val reactContext = (application as com.facebook.react.ReactApplication)
            .reactNativeHost
            .reactInstanceManager
            .currentReactContext
        reactContext
            ?.getNativeModule(StorageManagerModule::class.java)
            ?.handleActivityResult(requestCode, resultCode, data?.data)
    }
}
