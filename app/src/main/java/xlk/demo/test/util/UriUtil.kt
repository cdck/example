package xlk.demo.test.util

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log

/**
 * @author by xlk
 * @date 2020/6/4 15:46
 * @desc 根据Uri获取真实文件路径
 */
object UriUtil {
    private const val TAG = "UriUtil-->"
    fun getFilePath(context: Context, uri: Uri?): String? {
        if (uri == null) return null
        var path: String? = null
        // 以 file:// 开头的
        if (ContentResolver.SCHEME_FILE == uri.scheme) {
            path = uri.path
            return path
        }
        // 以 content:// 开头的，比如 content://media/extenral/images/media/17766
        if (ContentResolver.SCHEME_CONTENT == uri.scheme) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                val cursor = context.contentResolver.query(
                    uri,
                    arrayOf(MediaStore.Images.Media.DATA),
                    null,
                    null,
                    null
                )
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        val columnIndex =
                            cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                        if (columnIndex > -1) {
                            path = cursor.getString(columnIndex)
                        }
                    }
                    cursor.close()
                }
                return path
            } else {
                // 4.4及之后的 是以 content:// 开头的，比如 content://com.android.providers.media.documents/document/image%3A235700
                if (DocumentsContract.isDocumentUri(context, uri)) {
                    Log.e(
                        TAG,
                        "getFilePath uri.getAuthority() -->" + uri.authority
                    )
                    if (isExternalStorageDocument(uri)) {
                        // ExternalStorageProvider
                        Log.e(TAG, "getFilePath ExternalStorageProvider -->")
                        val docId = DocumentsContract.getDocumentId(uri)
                        val split = docId.split(":").toTypedArray()
                        val type = split[0]
                        if ("primary".equals(type, ignoreCase = true)) {
                            path = Environment.getExternalStorageDirectory()
                                .toString() + "/" + split[1]
                            return path
                        }
                    } else if (isDownloadsDocument(uri)) {
                        // DownloadsProvider
                        Log.e(TAG, "getFilePath DownloadsProvider -->")
                        val id = DocumentsContract.getDocumentId(uri)
                        val contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"),
                            java.lang.Long.valueOf(id)
                        )
                        path = getDataColumn(context, contentUri, null, null)
                        return path
                    } else if (isMediaDocument(uri)) {
                        // MediaProvider
                        Log.e(TAG, "getFilePath MediaProvider -->")
                        val docId = DocumentsContract.getDocumentId(uri)
                        val split = docId.split(":").toTypedArray()
                        val type = split[0]
                        var contentUri: Uri? = null
                        if ("image" == type) {
                            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        } else if ("video" == type) {
                            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                        } else if ("audio" == type) {
                            contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                        }
                        val selection = "_id=?"
                        val selectionArgs =
                            arrayOf(split[1])
                        return getDataColumn(context, contentUri, selection, selectionArgs)
                    }
                } else if ("content".equals(uri.scheme, ignoreCase = true)) {
                    Log.e(TAG, "getFilePath MediaStore (and general) -->")
                    return getDataColumn(context, uri, null, null)
                } else if ("file".equals(uri.scheme, ignoreCase = true)) {
                    Log.e(TAG, "getFilePath file -->")
                    return uri.path
                }
            }
        }
        return null
    }

    private fun getDataColumn(
        context: Context,
        uri: Uri?,
        selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = MediaStore.Images.ImageColumns.DATA
        val projection = arrayOf(column)
        try {
            cursor =
                context.contentResolver.query(uri, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val column_index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }
}