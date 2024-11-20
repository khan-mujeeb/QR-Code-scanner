package com.khandev.qrcodescanner.utlis

import com.khandev.qrcodescanner.data.Permission

interface PermissionRequester {
    val permissionList: List<Permission>
}
