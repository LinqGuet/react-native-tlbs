package com.guet.tlbs
import androidx.core.os.bundleOf

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import com.tencent.map.geolocation.*
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import expo.modules.kotlin.Promise
import expo.modules.kotlin.exception.Exceptions
import java.util.concurrent.atomic.AtomicReference

class TLBSLocationModule : Module () {

    // 上下文和定位管理器
    private val context: Context
        get() = requireNotNull(appContext.reactContext) { "React Context is null" }
    private var locationManager: TencentLocationManager? = null
    private var isLocationStarted = false
    private var scene: Int = 0
    private val lastLocationRef = AtomicReference<TencentLocation?>(null)


    // 初始化定位管理器
    private fun initializeLocationManager() {
        if (locationManager == null) {
            locationManager = TencentLocationManager.getInstance(context)
        }
    }

    // 检查定位权限
    private fun checkLocationPermission(): Boolean {
        TencentLocationManager.setUserAgreePrivacy(true);


        val fineLocationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            PackageManager.PERMISSION_GRANTED
        }

        val coarseLocationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        } else {
            PackageManager.PERMISSION_GRANTED
        }

        return fineLocationPermission == PackageManager.PERMISSION_GRANTED ||
                coarseLocationPermission == PackageManager.PERMISSION_GRANTED
    }

    // 转换位置为Map格式
    private fun locationToMap(location: TencentLocation): Map<String, Any?> {
        return mapOf(
            "latitude" to location.latitude,
            "longitude" to location.longitude,
            "altitude" to location.altitude,
            "accuracy" to location.accuracy,
            "speed" to location.speed,
            "bearing" to location.bearing,
            "time" to location.time,
            "address" to location.address,
            "province" to location.province,
            "city" to location.city,
            "district" to location.district,
            "town" to location.town,
            "village" to location.village,
            "street" to location.street,
            "streetNo" to location.streetNo
        )
    }

    // 定位监听器
    private val locationListener = object : TencentLocationListener {
        override fun onLocationChanged(
            location: TencentLocation?,
            errorCode: Int,
            errorMsg: String?
        ) {
            when (errorCode) {
                TencentLocation.ERROR_OK -> {
                    // 定位成功
                    lastLocationRef.set(location)
                    Log.d("TLBSLocation", "Location success: $location")
                    // 触发位置更新事件 - 使用正确的事件名称
                    location?.let {
                        this@TLBSLocationModule.sendEvent(
                            "onLocationUpdate",
                            bundleOf("location" to locationToMap(it))
                        )
                    }
                }

                else -> {
                    // 定位失败
                    Log.e("TLBSLocation", "Location error: $errorCode - $errorMsg")
                    this@TLBSLocationModule.sendEvent(
                        "onLocationError", bundleOf(
                            "error" to bundleOf(
                                "errorCode" to errorCode,
                                "errorMessage" to errorMsg
                            )
                        )
                    )

                }
            }
        }

        override fun onStatusUpdate(name: String?, status: Int, desc: String?) {
            // 实现状态更新回调，可以根据需要处理
            Log.d("TLBSLocation", "Location status update: $name - $status - $desc")
            this@TLBSLocationModule.sendEvent(
                "onStatusUpdate", bundleOf(
                    "scene" to scene
                )
            )
        }
    }


    override fun definition() = ModuleDefinition {
        Name("TLBSLocation")

        Events("onLocationError", "onLocationUpdate", "onStatusUpdate")

        AsyncFunction("stopLocationWithScene") { scene: Int,promise: Promise ->

            this@TLBSLocationModule.scene = scene

            try {
                locationManager?.stopLocationWithScene(scene, locationListener)
                // locationManager?.removeUpdates(locationListener)
                isLocationStarted = false
                promise.resolve()
            } catch (e: Exception) {
                promise.reject("LocationError", "Error stopping location: ${e.message}", e)
            }
        }


        // 开始连续定位
        AsyncFunction("startLocationUpdatesAsync") { scene: Int, promise: Promise ->
            if (!checkLocationPermission()) {
                promise.reject("PermissionError", "Location permission is required", null)
                return@AsyncFunction
            }

            this@TLBSLocationModule.scene = scene





            try {
                initializeLocationManager()

                // 打印场景值
                Log.d("TLBSLocation", "Starting location with scene: $scene")
                
                if (!isLocationStarted) {
                    val errorCode =
                        locationManager?.requestLocationWithScene(scene, locationListener)


                    if (errorCode == TencentLocation.ERROR_OK) {
                        isLocationStarted = true
                        promise.resolve()
                    } else {
                        val errorMsg = "Failed to start location updates: $errorCode"
                        promise.reject("LocationError", errorMsg, null)
                        // 发送错误事件
                        sendEvent(
                            "onLocationError",
                            bundleOf(
                                "error" to bundleOf(
                                    "errorCode" to errorCode,
                                    "errorMessage" to errorMsg
                                )
                            )
                        )
                    }
                } else {
                    promise.resolve() // 已经在定位中
                }
            } catch (e: Exception) {
                promise.reject("LocationError", "Error starting location: ${e.message}", e)
            }
        }
    }

    // 模块卸载时清理资源 - 使用正确的 Expo 模块生命周期方法
    fun onDestroy() {
        // 移除了 override 关键字，因为 Module 类可能没有 onDestroy 方法
        if (isLocationStarted && locationManager != null) {
            locationManager?.stopLocationWithScene(scene, locationListener)
            // locationManager?.removeUpdates(locationListener)
            locationManager = null
            isLocationStarted = false
        }
    }
}