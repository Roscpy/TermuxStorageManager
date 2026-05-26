package com.termuxstoragemanager

import android.content.Intent
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
        
        val reactApplication = application as? com.facebook.react.ReactApplication
        
        val reactContext = reactApplication
            ?.reactNativeHost
            ?.reactInstanceManager
            ?.currentReactContext

        reactContext
            ?.getNativeModule("StorageManagerModule")
            ?.let { module ->
                try {
                    val method = module.javaClass.getMethod("handleActivityResult", Int::class.java, Int::class.java, Intent::class.java)
                    method.invoke(module, requestCode, resultCode, data)
                } catch (e: Exception) {
                    // Évite le crash si la méthode ou les types ne correspondent pas
                }
            }
    }
}
