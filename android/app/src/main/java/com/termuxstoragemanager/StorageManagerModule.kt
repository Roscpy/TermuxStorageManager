package com.termuxstoragemanager

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import com.facebook.react.bridge.*import java.io.File

class StorageManagerModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    private var treeUri: Uri? = null
    private var pendingPromise: Promise? = null
    private val REQUEST_TREE = 1001

    override fun getName(): String = "StorageManager"

    @ReactMethod
    fun requestStorageAccess(promise: Promise) {
        val activity = reactApplicationContext.currentActivity ?: run {
            promise.reject("NO_ACTIVITY", "Activity null")
            return
        }
        pendingPromise = promise
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        activity.startActivityForResult(intent, REQUEST_TREE)
    }

    @ReactMethod
    fun listFiles(uri: String?, promise: Promise) {
        val context = reactApplicationContext
        val targetUri = if (uri.isNullOrEmpty()) treeUri else Uri.parse(uri)
        
        if (targetUri == null) {
            promise.reject("NO_URI", "Aucun dossier sélectionné")
            return
        }

        try {
            val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(
                targetUri, 
                DocumentsContract.getDocumentId(targetUri)
            )
            
            val projection = arrayOf(
                DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                DocumentsContract.Document.COLUMN_MIME_TYPE,
                DocumentsContract.Document.COLUMN_SIZE,
                DocumentsContract.Document.COLUMN_LAST_MODIFIED
            )

            val resolver = context.contentResolver
            val cursor = resolver.query(childrenUri, projection, null, null, null)
            val files = Arguments.createArray()

            cursor?.use {
                while (it.moveToNext()) {                    val name = it.getString(0) ?: continue
                    val mime = it.getString(1) ?: "application/octet-stream"
                    val size = it.getLong(2)
                    val modified = it.getLong(3)
                    val isDir = mime == "vnd.android.document/directory"

                    val item = Arguments.createMap()
                    item.putString("name", name)
                    item.putString("mime", mime)
                    item.putDouble("size", size.toDouble())
                    item.putDouble("modified", modified.toDouble())
                    item.putBoolean("isDirectory", isDir)
                    
                    val docId = DocumentsContract.getDocumentId(targetUri)
                    val childUri = DocumentsContract.buildDocumentUriUsingTree(targetUri, "$docId/$name")
                    item.putString("uri", childUri.toString())
                    files.pushMap(item)
                }
            }
            promise.resolve(files)
        } catch (e: Exception) {
            promise.reject("LIST_ERROR", e.message)
        }
    }

    @ReactMethod
    fun openFile(uri: String, promise: Promise) {
        try {
            val context = reactApplicationContext
            val fileUri = Uri.parse(uri)
            val mimeType = context.contentResolver.getType(fileUri) ?: "*/*"
            
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(fileUri, mimeType)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(Intent.createChooser(intent, "Ouvrir avec"))
                promise.resolve(true)
            } else {
                promise.reject("NO_APP", "Aucune application trouvée")
            }
        } catch (e: Exception) {
            promise.reject("OPEN_ERROR", e.message)
        }
    }

    @ReactMethod
    fun copyFile(fromUri: String, toDirUri: String, promise: Promise) {        promise.resolve(true)
    }

    @ReactMethod
    fun deleteFile(uri: String, promise: Promise) {
        promise.resolve(true)
    }

    fun handleActivityResult(requestCode: Int, resultCode: Int, uri: Uri?): Boolean {
        if (requestCode == REQUEST_TREE && resultCode == Activity.RESULT_OK) {
            uri?.let { selectedUri ->
                treeUri = selectedUri
                reactApplicationContext.contentResolver.takePersistableUriPermission(
                    selectedUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
                pendingPromise?.resolve(selectedUri.toString())
            }
        } else {
            pendingPromise?.reject("CANCELLED", "Utilisateur a annulé")
        }
        pendingPromise = null
        return true
    }
}
