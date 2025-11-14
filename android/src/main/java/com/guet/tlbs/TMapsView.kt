package com.guet.tlbs

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import com.tencent.map.geolocation.*
import com.tencent.tencentmap.mapsdk.maps.*
import com.tencent.tencentmap.mapsdk.maps.model.*
import expo.modules.kotlin.AppContext
import expo.modules.kotlin.viewevent.EventDispatcher
import expo.modules.kotlin.views.ExpoView

class TMapsView(context: Context, appContext: AppContext) : ExpoView(context, appContext) {
    private val onLoad by EventDispatcher<Unit>()
    private val onMapClick by EventDispatcher<MapLatLng>()
    private val onMapLongClick by EventDispatcher<MapLatLng>()
    private val onCompassClick by EventDispatcher<Unit>()
    private val onCameraChange by EventDispatcher<TMapsCameraPosition>()
    private val onMarkerClick by EventDispatcher<MarkerRecord>()

    private val markerList = mutableListOf<Marker>()
    // 存储markerRecord.id与地图SDK生成的marker.id的映射关系
    private val markerIdMap = HashMap<String, String>()

    internal var locationSource: TMapsLocationSource = TMapsLocationSource(context)

    private var mapOptions: TencentMapOptions =
            TencentMapOptions().apply {
                mapViewType = MapViewType.TextureView
                isOfflineMapEnable = false
            }
    internal var tencentMap: TencentMap? = null
    private lateinit var mapView: TextureMapView

    private fun findMarker(markerRecord: MarkerRecord): Marker? {
        // 根据markerIdMap中的映射关系查找marker
        val sdkMarkerId = markerIdMap[markerRecord.id]
        return if (sdkMarkerId != null) {
            markerList.find { it.id == sdkMarkerId }
        } else {
            null
        }
    }

    // 通过地图SDK生成的marker.id找到对应的markerRecord.id
    internal fun findMarkerRecordIdBySdkId(sdkMarkerId: String): String? {
        return markerIdMap.entries.find { it.value == sdkMarkerId }?.key
    }

    internal fun addMarkers(markers: List<MarkerRecord>) {
        // 创建一个集合来存储所有新传入的marker的id
        val newMarkerIds = HashSet<String>()

        // 处理添加和更新markers
        markers.forEach { markerRecord ->
            val marker = findMarker(markerRecord)
            if (marker == null) {
                val newMarker =
                        tencentMap?.addMarker(
                                markerRecord.toMarkerOptions(tencentMap ?: return@forEach)
                        )
                if (newMarker != null) {
                    markerList.add(newMarker)
                    newMarkerIds.add(newMarker.id)
                    // 存储markerRecord.id与地图SDK生成的marker.id的映射关系
                    markerIdMap[markerRecord.id] = newMarker.id
                    // 打印添加的markerid
                    Log.d(
                            "TMapsView",
                            "addMarker sdkId: ${newMarker.id}, recordId: ${markerRecord.id}"
                    )
                }
            } else {
                newMarkerIds.add(marker?.id ?: return@forEach)
                val options = markerRecord.toMarkerOptions(tencentMap ?: return@forEach)

                // 统一检查必要参数，避免多处重复返回
                val validOptions = options ?: return@forEach
                val validMarkerRecord = markerRecord ?: return@forEach

                // 设置位置、图标和基础属性
                marker.setPosition(validOptions.position)
                marker.setIcon(validOptions.icon)
                marker.isVisible = validOptions.isVisible
                marker.isDraggable = validOptions.isDraggable
                marker.setZIndex(validOptions.zIndex)
                marker.setRotation(validOptions.rotation)
                marker.setTitle(validOptions.title)

                // 设置锚点相关属性（避免重复调用）
                // 使用传统if判断替代lambda表达式，以兼容JVM target 1.8
                if (validMarkerRecord.anchor != null) {
                    marker.setAnchor(validMarkerRecord.anchor.x, validMarkerRecord.anchor.y)
                }

                if (validMarkerRecord.infoWindowAnchor != null) {
                    marker.setInfoWindowAnchor(
                            validMarkerRecord.infoWindowAnchor.x,
                            validMarkerRecord.infoWindowAnchor.y
                    )
                }

                if (validMarkerRecord.infoWindowOffset != null) {
                    marker.setInfoWindowOffset(
                            validMarkerRecord.infoWindowOffset.x,
                            validMarkerRecord.infoWindowOffset.y
                    )
                }

                // 控制信息窗口显示/隐藏
                if (validMarkerRecord.viewInfoWindow == true) {
                    marker.showInfoWindow()
                } else {
                    marker.hideInfoWindow()
                }
            }
        }

        // 找出需要移除的markers（在markerList中但不在newMarkerIds中的）
        val markersToRemove = ArrayList<Marker>()
        for (marker in markerList) {
            if (!newMarkerIds.contains(marker.id)) {
                markersToRemove.add(marker)
            }
        }

        // 移除标记
        for (marker in markersToRemove) {
            // 打印移除的markerid
            Log.d("TMapsView", "removeMarker sdkId: ${marker.id}")
            // 从地图中移除
            marker.remove()
            // 从列表中移除
            markerList.remove(marker)
            // 从映射中移除
            markerIdMap.entries.removeIf { it.value == marker.id }
        }
    }

    init {

        Log.d("TMapsView", "View created ")
        // 确保先同意隐私政策
        TencentMapInitializer.setAgreePrivacy(context, true)

        // 初始化腾讯地图
        TencentMapInitializer.start(context)

        TencentLocationManager.setUserAgreePrivacy(true)

        // 初始化地图选项，添加定位相关配置
        mapOptions =
                TencentMapOptions().apply {
                    mapViewType = MapViewType.TextureView
                    isOfflineMapEnable = false
                }

        mapView =
                TextureMapView(context, mapOptions).also {
                    tencentMap = it.map

                    // // 设置位置源
                    it.map?.setLocationSource(locationSource)
                    tencentMap?.setMyLocationStyle(
                            MyLocationStyle().apply {
                                myLocationType(
                                        MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER
                                )
                            }
                    )

                    // // 确保启用定位和定位按钮
                    // tencentMap?.isMyLocationEnabled = true
                    // tencentMap?.uiSettings?.isMyLocationButtonEnabled = true

                    // 设置地图加载完成监听器
                    it.map?.setOnMapLoadedCallback {
                        Log.d("TMapsView", "Map loaded, triggering onLoad event")
                        onLoad(Unit)
                    }

                    // 设置地图点击监听器
                    tencentMap?.setOnMapClickListener { latLng ->
                        Log.d("TMapsView", "Map clicked at $latLng, triggering onMapClick event")
                        onMapClick(MapLatLng(latLng.latitude, latLng.longitude, latLng.altitude))
                    }

                    // 设置地图长按监听器
                    tencentMap?.setOnMapLongClickListener { latLng ->
                        Log.d(
                                "TMapsView",
                                "Map long clicked at $latLng, triggering onMapLongClick event"
                        )
                        onMapLongClick(
                                MapLatLng(latLng.latitude, latLng.longitude, latLng.altitude)
                        )
                    }
                    // 设置指南针点击监听器
                    tencentMap?.setOnMarkerClickListener { marker ->
                        Log.d(
                                "TMapsView",
                                "Marker clicked at $marker, triggering onMarkerClick event"
                        )
                        // 通过地图SDK生成的marker.id查找对应的markerRecord.id
                        val recordId = findMarkerRecordIdBySdkId(marker.id)
                        onMarkerClick(
                                MarkerRecord(
                                        id = recordId ?: marker.id, // 如果找不到映射，使用SDK生成的ID作为回退
                                        position =
                                                MapLatLng(
                                                        marker.position.latitude,
                                                        marker.position.longitude,
                                                        marker.position.altitude
                                                ),
                                )
                        )
                        true
                    }

                    // 使用匿名内部类替代lambda表达式，以兼容JVM target 1.8
                    tencentMap?.setOnCameraChangeListener(
                            object : TencentMap.OnCameraChangeListener {
                                override fun onCameraChange(cameraPosition: CameraPosition) {
                                    //  Log.d("TMapsView", "Camera changing, position:
                                    // $cameraPosition")
                                }

                                override fun onCameraChangeFinished(
                                        cameraPosition: CameraPosition
                                ) {
                                    Log.d(
                                            "TMapsView",
                                            "Camera change finished, position: $cameraPosition"
                                    )
                                    val cp =
                                            TMapsCameraPosition(
                                                    rotation = cameraPosition.bearing,
                                                    zoom = cameraPosition.zoom,
                                                    center =
                                                            MapLatLng(
                                                                    cameraPosition.target.latitude,
                                                                    cameraPosition.target.longitude,
                                                                    cameraPosition.target.altitude
                                                            ),
                                                    pitch = cameraPosition.tilt,
                                                    timespan = System.currentTimeMillis(),
                                            )

                                    this@TMapsView.onCameraChange(cp)
                                }
                            }
                    )
                }

        addView(
                mapView,
                ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                )
        )
    }
}
