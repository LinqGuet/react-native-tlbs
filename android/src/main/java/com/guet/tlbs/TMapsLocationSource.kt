package com.guet.tlbs

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Looper
import android.util.Log
import com.tencent.map.geolocation.*
import com.tencent.tencentmap.mapsdk.maps.*
import com.tencent.tencentmap.mapsdk.maps.LocationSource.OnLocationChangedListener
import com.tencent.tencentmap.mapsdk.maps.model.*

class TMapsLocationSource(ctx: Context) : LocationSource {
    private val context = ctx
    private var locationManager: TencentLocationManager? = null
    private var isLocationStarted = false
    private var location = Location("LongPressLocationProvider")
    private var locationChangedListener: OnLocationChangedListener? = null
    private var locationRequest: TencentLocationRequest? = null

    // 初始化定位管理器
    private fun initializeLocationManager() {
        if (locationManager == null) {
            locationManager = TencentLocationManager.getInstance(context)
            locationRequest = TencentLocationRequest.create()
            // 设置定位周期（位置监听器回调周期）为3s
            locationRequest?.setInterval(3000)

            Log.d("TMapsLocationSource", "Location request: $locationRequest")
        }
    }

    // 检查定位权限
    private fun checkLocationPermission(): Boolean {
        TencentLocationManager.setUserAgreePrivacy(true)
        val fineLocationPermission =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            } else {
                PackageManager.PERMISSION_GRANTED
            }

        val coarseLocationPermission =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
            } else {
                PackageManager.PERMISSION_GRANTED
            }

        return fineLocationPermission == PackageManager.PERMISSION_GRANTED ||
                coarseLocationPermission == PackageManager.PERMISSION_GRANTED
    }

    private val locationListener =
        object : TencentLocationListener {
            override fun onLocationChanged(
                tencentLocation: TencentLocation?,
                errorCode: Int,
                errorMsg: String?
            ) {
                when (errorCode) {
                    TencentLocation.ERROR_OK -> {
                        // 定位成功
                        Log.d("TLBSLocation", "Location success: $location")
                        // 触发位置更新事件 - 使用正确的事件名称
                        // 设置经纬度
                        location.apply {
                            latitude = tencentLocation?.latitude ?: 0.0
                            longitude = tencentLocation?.longitude ?: 0.0
                            accuracy = tencentLocation?.accuracy ?: 0.0f
                            bearing = tencentLocation?.bearing ?: 0.0f
                        }
                        // 将位置信息返回给地图
                        locationChangedListener?.onLocationChanged(location)
                    }

                    else -> {
                        // 定位失败
                        Log.e("TLBSLocation", "Location error: $errorCode - $errorMsg")
                    }
                }
            }

            override fun onStatusUpdate(name: String?, status: Int, desc: String?) {
                // 实现状态更新回调，可以根据需要处理
                Log.d("TLBSLocation", "Location status update: $name - $status - $desc")
            }
        }

    override fun activate(onLocationChangedListener: OnLocationChangedListener?) {
        // 这里我们将地图返回的位置监听保存为当前 Activity 的成员变量
        locationChangedListener = onLocationChangedListener

        if (!checkLocationPermission()) {
            return@activate
        }
        initializeLocationManager()

        // 开启定位
        val err: Int =
            locationManager?.requestLocationUpdates(
                locationRequest,
                locationListener,
                Looper.myLooper()
            )
                ?: TencentLocation.ERROR_OK
        when (err) {
            //            1 -> Toast.makeText(this, "设备缺少使用腾讯定位服务需要的基本条件", Toast.LENGTH_SHORT)
            //                .show()
            //
            //            2 -> Toast.makeText(this, "manifest 中配置的 key 不正确",
            // Toast.LENGTH_SHORT).show()
            //            3 -> Toast.makeText(this, "自动加载libtencentloc.so失败",
            // Toast.LENGTH_SHORT).show()
            else -> {}
        }
    }

    override fun deactivate() {
        // 当不需要展示定位点时，需要停止定位并释放相关资源
        locationManager?.removeUpdates(locationListener)
        locationManager = null
        locationRequest = null
        locationChangedListener = null
    }
}