package com.guet.tlbs

import android.content.Context
import android.util.Log
import android.view.View
import com.tencent.map.geolocation.*
import com.tencent.tencentmap.mapsdk.maps.CameraUpdateFactory
import com.tencent.tencentmap.mapsdk.maps.model.*
import com.tencent.tencentmap.mapsdk.maps.model.CameraPosition
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import android.graphics.Color;


class TMapsViewModule : Module() {
  // 上下文和定位管理器
  private val context: Context
    get() = requireNotNull(appContext.reactContext) { "React Context is null" }
  private var locationManager: TencentLocationManager? = null
  private var isLocationStarted = false
  private var scene: Int = 0

  override fun definition() = ModuleDefinition {
    // Module name
    Name("TMapsView")

    // Register view component
    View(TMapsView::class) {
      /** 地图加载完成事件 */
      Events(
              "onLoad",
              "onMapClick",
              "onMapLongClick",
              "onCompassClick",
              "onCameraChange",
              "onMarkerClick"
      )
      /*
       * 地图类型
       */
      Prop("mapType") { view: TMapsView, mapId: Int -> view.tencentMap?.mapType = mapId }

      /** 是否开启交通流量 */
      Prop("trafficEnabled") { view: TMapsView, enabled: Boolean ->
        enabled.also { view.tencentMap?.isTrafficEnabled = it }
      }

      /*
       * 地图UI设置
       */
      Prop("uiSettings") { view: TMapsView, settings: TMapsUiSettings ->
        // 打印settings

        if (view.tencentMap == null) {
          Log.e("TMapsViewModule", "mapUiSettings tencentMap is null")
          return@Prop
        }
        val uiSettings = view.tencentMap?.uiSettings
        Log.d("TMapsViewModule", "mapUiSettings $settings")

        settings.compassEnabled?.also { uiSettings?.isCompassEnabled = it }
        settings.scaleViewEnabled?.also { uiSettings?.isScaleViewEnabled = it }
        settings.zoomControlsEnabled?.also { uiSettings?.isZoomControlsEnabled = it }
        settings.rotateGesturesEnabled?.also { uiSettings?.isRotateGesturesEnabled = it }
        settings.scrollGesturesEnabled?.also { uiSettings?.isScrollGesturesEnabled = it }
        settings.tiltGesturesEnabled?.also { uiSettings?.isTiltGesturesEnabled = it }
        settings.zoomGesturesEnabled?.also { uiSettings?.isZoomGesturesEnabled = it }
        settings.myLocationButtonEnabled?.also { uiSettings?.isMyLocationButtonEnabled = it }
        settings.myLocationEnabled?.also { view.tencentMap?.isMyLocationEnabled = it }

        if (settings.myLocationEnabled == true) {
          Log.d("TMapsViewModule", "strokeColor ${settings.myLocationStyle.strokeColor}")
          val myStyle = MyLocationStyle().apply {
            myLocationType (settings.myLocationStyle.myLocationType?: MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER)
            strokeWidth (settings.myLocationStyle.strokeWidth ?: 10)
            fillColor (settings.myLocationStyle.fillColor ?: 0xFF0000FF.toInt())
            strokeColor (settings.myLocationStyle.strokeColor ?: 0xFF0000FF.toInt())
            anchor(settings.myLocationStyle.anchor?.x ?: 0.5f, settings.myLocationStyle.anchor?.y ?: 0.5f)
          }
           //设置填充颜色为红色
          view.tencentMap?.setMyLocationStyle(myStyle)
         
          Log.d("TMapsViewModule", "${settings.myLocationStyle.fillColor} myLocationStyle ${myStyle}")
        }

        // return@Prop
      }

      /*
       * 地图相机位置
       */
      Prop("cameraPosition") { view: TMapsView, camera: TMapsCameraPosition ->
        // 打印position
        Log.d("TMapsViewModule", "cameraPosition $camera")
        val cameraPosition = view.tencentMap?.cameraPosition
        // 如果camera.minZoom与camera.minZoomLevel不相等则执行
        if (camera.minZoom != view.tencentMap?.minZoomLevel) {
          camera.minZoom?.also { view.tencentMap?.setMinZoomLevel(it.toInt()) }
        }
        // 如果camera.maxZoom与camera.maxZoomLevel不相等则执行
        if (camera.maxZoom != view.tencentMap?.maxZoomLevel) {
          camera.maxZoom?.also { view.tencentMap?.setMaxZoomLevel(it.toInt()) }
        }

        // 判断camera的值是否与cameraPosition的值是否相等
        if (camera.center?.lat == cameraPosition?.target?.latitude &&
                        camera.center?.lng == cameraPosition?.target?.longitude &&
                        camera.rotation == cameraPosition?.bearing &&
                        camera.zoom == cameraPosition?.zoom &&
                        camera.pitch == cameraPosition?.tilt
        ) {
          return@Prop
        }

        // 使用空安全操作符和let函数简化相机参数设置
        val center = camera.center?.toLatLng() ?: cameraPosition?.target
        val bearing = camera.rotation ?: cameraPosition?.bearing ?: 0.0f
        val zoom = camera.zoom ?: cameraPosition?.zoom ?: 0.0f
        val pitch = camera.pitch ?: cameraPosition?.tilt ?: 0.0f

        val cameraSigma =
                center?.let {
                  CameraUpdateFactory.newCameraPosition(CameraPosition(it, zoom, pitch, bearing))
                }
        // 执行相机位置更新
        cameraSigma?.also { view.tencentMap?.animateCamera(it) }
      }

      Prop("markers") { view: TMapsView, markers: List<MarkerRecord> ->
        // 打印markers
        Log.d("TMapsViewModule", "markers $markers")
        view.addMarkers(markers)
      }

      Prop("polylines") { view: TMapsView, polylines: List<PolylineRecord> ->
        // 打印markers
        Log.d("TMapsViewModule", "polylines $polylines")
        view.addPolylines(polylines)  
      }
    }
  }
}
