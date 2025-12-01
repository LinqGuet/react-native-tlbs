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
    private val polylineList = mutableListOf<Polyline>()
    private val arcList = mutableListOf<Arc>()
    private val polygonList = mutableListOf<Polygon>()
    private val circleList = mutableListOf<Circle>()



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
    private fun findPolyline(polylineRecord: PolylineRecord): Polyline? {
        // 根据polylineIdMap中的映射关系查找polyline
        val sdkPolylineId = markerIdMap[polylineRecord.id]
        return if (sdkPolylineId != null) {
            polylineList.find { it.id == sdkPolylineId }
        } else {
            null
        }
    }
    private fun findCircle(circleRecord: CircleRecord): Circle? {
        // 根据circleIdMap中的映射关系查找circle
        val sdkCircleId = markerIdMap[circleRecord.id]
        return if (sdkCircleId != null) {
            circleList.find { it.id == sdkCircleId }
        } else {
            null
        }
    }

    private fun findArc(arcRecord: ArcRecord): Arc? {
        // 根据arcIdMap中的映射关系查找arc
        val sdkArcId = markerIdMap[arcRecord.id]
        return if (sdkArcId != null) {
            arcList.find { it.id == sdkArcId }
        } else {
            null
        }
    }

    private fun findPolygon(polygonRecord: PolygonRecord): Polygon? {
        // 根据polygonIdMap中的映射关系查找polygon
        val sdkPolygonId = markerIdMap[polygonRecord.id]
        return if (sdkPolygonId != null) {
            polygonList.find { it.id == sdkPolygonId }
        } else {
            null
        }
    }



    // 通过地图SDK生成的marker.id找到对应的markerRecord.id
    internal fun findMarkerRecordIdBySdkId(sdkMarkerId: String): String? {
        return markerIdMap.entries.find { it.value == sdkMarkerId }?.key
    }

    // 通用属性更新辅助函数
    private fun <T> updateIfChanged(currentValue: T, newValue: T, updateAction: (T) -> Unit) {
        if (currentValue != newValue) {
            updateAction(newValue)
        }
    }

    // 处理可选属性的更新函数
    private fun <T> updateOptionalProperty(optionalValue: T?, updateAction: (T) -> Unit) {
        optionalValue?.let { updateAction(it) }
    }

    // 处理可选属性并比较当前值的更新函数
    private fun <T> updateOptionalPropertyWithComparison(
            optionalValue: T?,
            getCurrentValue: () -> T,
            updateAction: (T) -> Unit
    ) {
        optionalValue?.let { if (getCurrentValue() != it) updateAction(it) }
    }

    // 处理带默认值的可选属性更新
    private fun <T> updatePropertyWithDefault(
            optionalValue: T?,
            defaultValue: T,
            getCurrentValue: () -> T,
            updateAction: (T) -> Unit
    ) {
        updateIfChanged(getCurrentValue(), optionalValue ?: defaultValue, updateAction)
    }

    // 处理需要条件判断的复杂更新
    private fun updateIfCondition(condition: Boolean, updateAction: () -> Unit) {
        if (condition) {
            updateAction()
        }
    }

    internal fun fitToMarkers() {
        Log.d("TMapsView", "fitToMarkers ${markerList.size}")
        if (markerList.isNotEmpty()) {
            var overlays = mutableListOf<IOverlay>()
            for (marker in markerList) {
                overlays.add(marker)
            }
            tencentMap?.moveCamera(CameraUpdateFactory.newCameraPosition(
                tencentMap?.calculateZoomToSpanLevel(
                overlays, null,
                0, 0, 0, 0)));

        }
    }

    internal fun addCircles(circleRecords: List<CircleRecord>) {
        // val polyline = tencentMap?.addPolyline(polylineRecord.toPolylineOptions(tencentMap ?:
        // return))
        // 创建一个集合来存储所有新传入的polyline的id

        val newCircleIds = HashSet<String>()
        circleRecords?.forEach { circleRecord ->
            val circle = findCircle(circleRecord)

            if (circle == null) {

                val newCircle =
                        tencentMap?.addCircle(
                                circleRecord.toCircleOptions(tencentMap ?: return@forEach)
                        )
                //                newCircle?.color = 0xff6600
                Log.d("TMapsView", "addCircles $circleRecord")

                // Log.d("TMapsView", "addCircles $circleRecords")
                if (newCircle != null) {
                    circleList.add(newCircle)
                    newCircleIds.add(newCircle.id)
                    // 存储circleRecord.id与地图SDK生成的circle.id的映射关系
                    markerIdMap[circleRecord.id] = newCircle.id
                    val pid = newCircle.id
                }
            } else {

                newCircleIds.add(circle?.id ?: return@forEach)

                updateOptionalProperty(circleRecord.radius) {
                    if (circle.radius != it) { 
                        circle.radius = it
                    }
                }
                // 更新位置
                circle.setOptions(circleRecord.toCircleOptions(tencentMap ?: return@forEach))
            }
        }

        // 找出需要移除的circles（在circleList中但不在newCircleIds中的）
        val circlesToRemove = ArrayList<Circle>()
        for (circle in circleList) {
            if (!newCircleIds.contains(circle.id)) {
                circlesToRemove.add(circle)
            }
        }

        // 移除标记
        for (circle in circlesToRemove) {
            // 打印移除的circleid
            Log.d("TMapsView", "removeCircle sdkId: ${circle.id}")
            // 从地图中移除
            circle.remove()
            // 从列表中移除
            circleList.remove(circle)
            // 从映射中移除
            markerIdMap.entries.removeIf { it.value == circle.id }
        }
    }

    internal fun addPolygons(polygonRecords: List<PolygonRecord>) {
        // val polyline = tencentMap?.addPolyline(polylineRecord.toPolylineOptions(tencentMap ?:
        // return))
        // 创建一个集合来存储所有新传入的polyline的id

        val newPolygonIds = HashSet<String>()
        polygonRecords?.forEach { polygonRecord ->
            val polygon = findPolygon(polygonRecord)

            if (polygon == null) {

                val newPolygon =
                        tencentMap?.addPolygon(
                                polygonRecord.toPolygonOptions(tencentMap ?: return@forEach)
                        )
                //                newPolyline?.color = 0xff6600
                Log.d("TMapsView", "addPolygons $polygonRecord")

                // Log.d("TMapsView", "addPolylines $polylineRecords")
                if (newPolygon != null) {
                    polygonList.add(newPolygon)
                    newPolygonIds.add(newPolygon.id)
                    // 存储polylineRecord.id与地图SDK生成的polyline.id的映射关系
                    markerIdMap[polygonRecord.id] = newPolygon.id
                    val pid = newPolygon.id
                }
            } else {

                newPolygonIds.add(polygon?.id ?: return@forEach)

                updateOptionalProperty(polygonRecord.points) {
                    if (polygon.points != it) { 
                        polygon.points = it.map { it.toLatLng() }
                    }
                }

                val opts = polygonRecord.toPolygonOptions(tencentMap ?: return@forEach)
                Log.d("TMapsView", "addPolygons $polygonRecord $opts")
                // 更新位置
                polygon.setOptions(opts)
            }
        }

        // 找出需要移除的polygons（在polygonList中但不在newPolygonIds中的）
        val polygonsToRemove = ArrayList<Polygon>()
        for (polygon in polygonList) {
            if (!newPolygonIds.contains(polygon.id)) {
                polygonsToRemove.add(polygon)
            }
        }

        // 移除标记
        for (polygon in polygonsToRemove) {
            // 打印移除的polygonid
            Log.d("TMapsView", "removePolygon sdkId: ${polygon.id}")
            // 从地图中移除
            polygon.remove()
            // 从列表中移除
            polygonList.remove(polygon)
            // 从映射中移除
            markerIdMap.entries.removeIf { it.value == polygon.id }
        }
    }

    internal fun addArcs(arcRecords: List<ArcRecord>) {
        // val arc = tencentMap?.addArc(arcRecord.toArcOptions(tencentMap ?: return))
        // 创建一个集合来存储所有新传入的arc的id

        val newArcIds = HashSet<String>()
        arcRecords?.forEach { arcRecord ->
            val arc = findArc(arcRecord)
            //先删除
            if (arc != null) {
                arc.remove()
                arcList.remove(arc)
            }
            val newArc =
                tencentMap?.addArc(arcRecord.toArcOptions(tencentMap ?: return@forEach))
            //                newArc?.color = 0xff660000.toInt()
            Log.d("TMapsView", "addArcs $arcRecord")

            // Log.d("TMapsView", "addPolylines $polylineRecords")
            if (newArc != null) {
                arcList.add(newArc)
                newArcIds.add(newArc.id)
                // 存储arcRecord.id与地图SDK生成的arc.id的映射关系
                markerIdMap[arcRecord.id] = newArc.id
                val pid = newArc.id
            }
        }

        // 找出需要移除的arcs（在arcList中但不在newArcIds中的）
        val arcsToRemove = ArrayList<Arc>()
        for (arc in arcList) {
            if (!newArcIds.contains(arc.id)) {
                arcsToRemove.add(arc)
            }
        }

        // 移除标记
        for (arc in arcsToRemove) {
            // 打印移除的arcid
            Log.d("TMapsView", "removeArc sdkId: ${arc.id}")
            // 从地图中移除
            arc.remove()
            // 从列表中移除
            arcList.remove(arc)
            // 从映射中移除
            markerIdMap.entries.removeIf { it.value == arc.id }
        }
    }
    internal fun addPolylines(polylineRecords: List<PolylineRecord>) {
        // val polyline = tencentMap?.addPolyline(polylineRecord.toPolylineOptions(tencentMap ?:
        // return))
        // 创建一个集合来存储所有新传入的polyline的id

        val newPolylineIds = HashSet<String>()
        polylineRecords?.forEach { polylineRecord ->
            val polyline = findPolyline(polylineRecord)

            if (polyline == null) {

                val newPolyline =
                        tencentMap?.addPolyline(
                                polylineRecord.toPolylineOptions(tencentMap ?: return@forEach)
                        )
                //                newPolyline?.color = 0xff6600
                Log.d("TMapsView", "addPolylines $polylineRecord")

                // Log.d("TMapsView", "addPolylines $polylineRecords")
                if (newPolyline != null) {
                    polylineList.add(newPolyline)
                    newPolylineIds.add(newPolyline.id)
                    // 存储polylineRecord.id与地图SDK生成的polyline.id的映射关系
                    markerIdMap[polylineRecord.id] = newPolyline.id
                    val pid = newPolyline.id
                }
            } else {
                val pid = polyline
                Log.d("TMapsView", "found Polylines $pid $polylineRecord")

                newPolylineIds.add(polyline?.id ?: return@forEach)

                updateOptionalProperty(polylineRecord.points) {
                    if (polyline.points != it) {
                        polyline.points = it.map { it.toLatLng() }
                    }
                }
                // 更新位置
                val opts = polyline.getPolylineOptions()
                // 更新其他属性

                polylineRecord.color?.also { opts.color(it) }
                polylineRecord.colorList?.also {
                    val mColorList = it.takeIf { it.isNotEmpty() } ?: intArrayOf(0)
                    val colorIndexList = (0 until mColorList.size).toList().toIntArray()
                    if (mColorList.size > 0) opts.colors(mColorList, colorIndexList)
                }
                polylineRecord.width?.also { opts.width(it) }
                polylineRecord.arrowLine?.also { opts.arrow(it) }
                polylineRecord.dottedLine?.also {
                    if (it == true) {
                        val pattern = listOf(35, 20)
                        opts.lineType(PolylineOptions.LineType.LINE_TYPE_DOTTEDLINE)
                        opts.pattern(pattern)
                    } else {
                        opts.lineType(PolylineOptions.LineType.LINE_TYPE_IMAGEINARYLINE)
                        opts.pattern(null)
                    }
                }
                polylineRecord.borderColor?.also { opts.borderColor(it) }
                polylineRecord.borderWidth?.also { opts.borderWidth(it) }
                polylineRecord.level?.also { opts.level(it) }

                polyline.polylineOptions = opts
            }
        }

        // 找出需要移除的polylines（在polylineList中但不在newPolylineIds中的）
        val polylinesToRemove = ArrayList<Polyline>()
        for (polyline in polylineList) {
            if (!newPolylineIds.contains(polyline.id)) {
                polylinesToRemove.add(polyline)
            }
        }

        // 移除标记
        for (polyline in polylinesToRemove) {
            // 打印移除的polylineid
            Log.d("TMapsView", "removePolyline sdkId: ${polyline.id}")
            // 从地图中移除
            polyline.remove()
            // 从列表中移除
            polylineList.remove(polyline)
            // 从映射中移除
            markerIdMap.entries.removeIf { it.value == polyline.id }
        }
    }

    internal fun addMarkers(markers: List<MarkerRecord>) {

        // 创建一个集合来存储所有新传入的marker的id
        val newMarkerIds = HashSet<String>()

        // 处理添加和更新markers
        markers?.forEach { markerRecord ->
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
                    if (markerRecord.infoWindowVisible == true) {
                        newMarker.showInfoWindow()
                    }
                    // 打印添加的markerid
                    Log.d(
                            "TMapsView",
                            "addMarker sdkId: ${newMarker.id}, recordId: ${markerRecord.id}"
                    )
                }
            } else {
                newMarkerIds.add(marker?.id ?: return@forEach)
                // 更新位置
                updateOptionalProperty(markerRecord.position) {
                    if (marker.position.latitude != it.lat || marker.position.longitude != it.lng) {
                        marker.setPosition(it.toLatLng())
                    }
                }

                // 更新锚点
                updateOptionalProperty(markerRecord.anchor) {
                    if (marker.anchorU != it.x || marker.anchorV != it.y) {
                        marker.setAnchor(it.x, it.y)
                    }
                }

                // 更新简单属性
                updatePropertyWithDefault(
                        markerRecord.alpha,
                        0.0f,
                        { marker.getAlpha() },
                        { marker.setAlpha(it) }
                )
                updatePropertyWithDefault(
                        markerRecord.rotation,
                        0.0f,
                        { marker.getRotation() },
                        { marker.setRotation(it) }
                )
                updatePropertyWithDefault(
                        markerRecord.level,
                        0.0f,
                        { marker.level.toFloat() },
                        { marker.setLevel(it.toInt()) }
                )
                updatePropertyWithDefault(
                        markerRecord.zIndex,
                        0,
                        { marker.zIndex },
                        { marker.zIndex = it.toInt() }
                )
                updatePropertyWithDefault(
                        markerRecord.visible,
                        false,
                        { marker.isVisible },
                        { marker.isVisible = it }
                )
                updatePropertyWithDefault(
                        markerRecord.draggable,
                        false,
                        { marker.isDraggable },
                        { marker.isDraggable = it }
                )
                updatePropertyWithDefault(
                        markerRecord.infoWindowEnable,
                        false,
                        { marker.isInfoWindowEnable },
                        { marker.isInfoWindowEnable = it }
                )

                // 更新标题和摘要
                updateOptionalPropertyWithComparison(
                        markerRecord.title,
                        { marker.title ?: "" },
                        { marker.title = it }
                )
                updateOptionalPropertyWithComparison(
                        markerRecord.snippet,
                        { marker.snippet ?: "" },
                        { marker.snippet = it }
                )

                // 更新信息窗口可见性
                if (markerRecord.infoWindowVisible == true) {
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
                    // tencentMap?.setMyLocationStyle(
                    //         MyLocationStyle().apply {
                    //             myLocationType(
                    //                     MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER
                    //             )
                    //         }
                    // )

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
                        false
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
