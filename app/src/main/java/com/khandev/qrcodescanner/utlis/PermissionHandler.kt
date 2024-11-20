package com.khandev.qrcodescanner.utlis

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.khandev.qrcodescanner.data.Permission

object PermissionHandler {

    // Function to check and request permissions
    fun checkAndRequestPermissions(
        context: Context,
        permissions: List<Permission>
    ) {
        val permissionsToRequest = mutableListOf<String>()

        // Loop through the permissions to check which ones are not granted
        permissions.forEach { permission ->
            if (ContextCompat.checkSelfPermission(context, permission.permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission.permission)
            } else {
                // If the permission is already granted, execute the onGranted action
                permission.onGranted()
            }
        }

        // If any permissions are not granted, request them
        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                context as Activity, // Cast to Activity since requestPermissions requires it
                permissionsToRequest.toTypedArray(),
                permissions.hashCode() // Use a unique request code
            )
        }
    }

    // Handle permission request result
    fun onRequestPermissionsResult(
        context: Context,
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        permissions.forEachIndexed { index, permission ->
            if (grantResults[index] == PackageManager.PERMISSION_GRANTED) {
                // Find the corresponding Permission object for the granted permission
                val grantedPermission = (context as? PermissionRequester)?.permissionList
                    ?.firstOrNull { it.permission == permission }
                grantedPermission?.onGranted?.invoke()
            } else {
                // Handle permission denial
                Toast.makeText(context, "$permission permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
