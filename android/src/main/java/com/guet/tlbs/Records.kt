package com.guet.tlbs

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.ui.geometry.Offset
import com.tencent.tencentmap.mapsdk.maps.TencentMap
import com.tencent.tencentmap.mapsdk.maps.model.*
import com.tencent.tencentmap.mapsdk.maps.model.LatLng
import expo.modules.kotlin.records.Field
import expo.modules.kotlin.records.Record
import expo.modules.kotlin.sharedobjects.SharedRef
import expo.modules.kotlin.types.Either
import expo.modules.kotlin.types.toKClass
import java.util.UUID

fun getIconDescriptor(icon: Either<SharedRef<Drawable>, SharedRef<Bitmap>>?): BitmapDescriptor? {

        return icon?.let { icon ->
                val bitmap =
                        if (icon.`is`(toKClass<SharedRef<Drawable>>())) {
                                (icon.get(toKClass<SharedRef<Drawable>>()).ref as? BitmapDrawable)
                                        ?.bitmap
                        } else {
                                icon.get(toKClass<SharedRef<Bitmap>>()).ref
                        }

                bitmap?.let { BitmapDescriptorFactory.fromBitmap(it) }
        }
}

/** 自定义经纬度类型，实现Record接口以便Expo可以正确转换 */
data class MapLatLng(
        @Field val lat: Double = 0.0,
        @Field val lng: Double = 0.0,
        @Field val height: Double = 0.0,
) : Record {
        fun toLatLng(): LatLng {
                return LatLng(lat, lng, height)
        }
}

data class MyLocationStyleRecord(
        @Field val myLocationType: Int? = MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER,

        /** 定位图标的锚点位置，默认为 (0.5f, 0.5f) */
        @Field val anchor: AnchorRecord? = null,
        /** 定位点的图标 */
        @Field val icon: Either<SharedRef<Drawable>, SharedRef<Bitmap>>? = null,
        /** 以圆形表示的定位精度的填充颜色 */
        @Field val fillColor: Int? = null,
        /** 以圆形表示的定位精度的描边颜色 */
        @Field val strokeColor: Int? = null,
        /** 以圆形表示的定位精度的描边宽度 */
        @Field val strokeWidth: Int? = null,
) : Record

data class TMapsUiSettings(
        @Field val myLocationButtonEnabled: Boolean? = true,
        /** 是否显示比例尺 */
        @Field val scaleViewEnabled: Boolean? = false,
        // 修复重复定义的compassEnabled
        @Field val compassEnabled: Boolean? = false,
        /** 是否显示缩放控件 */
        @Field val zoomControlsEnabled: Boolean? = false,
        @Field val myLocationEnabled: Boolean? = false,

        /** 是否允许旋转手势 */
        @Field val rotateGesturesEnabled: Boolean? = false,
        /** 是否允许滚动手势 */
        @Field val scrollGesturesEnabled: Boolean? = false,
        /** 是否允许倾斜手势 */
        @Field val tiltGesturesEnabled: Boolean? = false,
        /** 是否允许缩放手势 */
        @Field val zoomGesturesEnabled: Boolean? = false,

        /** 定位样式 */
        @Field val myLocationStyle: MyLocationStyleRecord = MyLocationStyleRecord(),
) : Record

data class TMapsCameraPosition(
        /** 地图中心点 */
        @Field val center: MapLatLng? = null,
        /** 地图缩放级别 */
        @Field val zoom: Float? = 0.0f,
        /** 地图最小缩放级别 */
        @Field val minZoom: Float? = 0.0f,
        /** 地图最大缩放级别 */
        @Field val maxZoom: Float? = 0.0f,
        /** 地图旋转角度 */
        @Field val rotation: Float? = 0.0f,
        /** 地图俯仰角度 */
        @Field val pitch: Float? = 0.0f,
        @Field val timespan: Long? = 0L,
) : Record

data class AnchorRecord(@Field val x: Float = 0.5f, @Field val y: Float = 1.0f) : Record {
        fun toOffset() = Offset(x = x, y = y)
}

data class OffsetRecord(@Field val x: Int = 0, @Field val y: Int = 0) : Record

data class MarkerRecord(
        @Field val id: String = UUID.randomUUID().toString(),
        @Field val clusterId: String? = null,
        @Field val isClusterable: Boolean? = false,
        @Field val icon: Either<SharedRef<Drawable>, SharedRef<Bitmap>>? = null,
        /** 标记的位置 */
        @Field val position: MapLatLng? = MapLatLng(),
        @Field val anchor: AnchorRecord = AnchorRecord(0.5f, 0.5f),
        @Field val alpha: Float? = 1.0f,
        @Field val flat: Boolean? = false,
        @Field val rotation: Float? = 0.0f,
        @Field val clockwise: Boolean? = true,
        @Field val level: Int? = OverlayLevel.OverlayLevelAboveLabels,
        @Field val zIndex: Float? = 0.0f,
        @Field val visible: Boolean? = true,
        @Field val draggable: Boolean? = false,
        @Field val fastLoad: Boolean? = true,
        @Field val infoWindowEnable: Boolean? = true,
        @Field val infoWindowVisible: Boolean? = false,
        @Field val infoWindowAnchor: AnchorRecord = AnchorRecord(0.5f, 1.0f),
        @Field val infoWindowOffset: OffsetRecord = OffsetRecord(0, 0),
        @Field val viewInfoWindow: Boolean? = false,
        @Field val title: String? = null,
        @Field val snippet: String? = null,
) : Record {
        // MarkerCollisionRelation.ALONE

        fun toMarkerOptions(map: TencentMap): MarkerOptions {
                return MarkerOptions()
                        .position(position?.toLatLng() ?: LatLng(0.0, 0.0))
                        .icon(getIconDescriptor(icon))
                        .anchor(anchor.x, anchor.y)
                        .alpha(alpha ?: 1.0f)
                        .flat(flat ?: false)
                        .rotation(rotation ?: 0.0f)
                        .clockwise(clockwise ?: true)
                        .level(level ?: OverlayLevel.OverlayLevelAboveLabels)
                        .zIndex(zIndex ?: 0.0f)
                        .visible(visible ?: true)
                        .draggable(draggable ?: false)
                        .fastLoad(fastLoad ?: true)
                        .infoWindowEnable(infoWindowEnable ?: true)
                        .infoWindowAnchor(infoWindowAnchor.x, infoWindowAnchor.y)
                        .infoWindowOffset(infoWindowOffset.x, infoWindowOffset.y)
                        .viewInfoWindow(viewInfoWindow ?: false)
                        .title(title)
                        .snippet(snippet)
        }
}

/** 位置信息数据类 */
data class LocationData(
        @Field val latitude: Double = 0.0,
        @Field val longitude: Double = 0.0,
        @Field val altitude: Double = 0.0,
        @Field val accuracy: Float = 0f,
        @Field val speed: Float = 0f,
        @Field val bearing: Float = 0f,
        @Field val time: Long = 0L,
        @Field val address: String? = null,
        @Field val province: String? = null,
        @Field val city: String? = null,
        @Field val district: String? = null,
        @Field val town: String? = null,
        @Field val village: String? = null,
        @Field val street: String? = null,
        @Field val streetNo: String? = null
) : Record

/** 定位错误数据类 */
data class LocationError(@Field val errorCode: Int, @Field val errorMessage: String) : Record

/** 定位请求配置 */
data class LocationOptions(
        @Field val interval: Long = 1000L, // 定位间隔，单位毫秒
        @Field val requestLevel: Int = 1, // 定位精度等级
        @Field val allowGPS: Boolean = true, // 是否允许GPS定位
        @Field val allowDirection: Boolean = true, // 是否允许方向传感器
        @Field val allowCache: Boolean = true // 是否允许使用缓存
) : Record

data class PolylineRecord(
        @Field val id: String = UUID.randomUUID().toString(),
        @Field val points: List<MapLatLng>? = null,

        /** 线的颜色 */
        @Field val color: Int?,
        /** 彩虹线 Int[] */
        @Field val colorList: IntArray = intArrayOf(0),

        /** 线的宽度 */
        @Field val width: Float?,
        /** 是否虚线 */
        @Field val dottedLine: Boolean?,
        /** 带箭头的线 */
        @Field val arrowLine: Boolean?,
        /** 线的边框颜色 */
        @Field val borderColor: Int?,
        /** 线的厚度 */
        @Field val borderWidth: Float?,
        /** 压盖关系 */
        @Field val level: Int?,
) : Record {
        // MarkerCollisionRelation.ALONE

        fun toPolylineOptions(map: TencentMap): PolylineOptions {
                val mColorList = colorList?.takeIf { it.isNotEmpty() } ?: intArrayOf(0)
                val colorIndexList = (0 until mColorList.size).toList().toIntArray()
                val opts =
                        PolylineOptions()
                                .addAll(points?.map { it.toLatLng() } ?: emptyList())
                                .colorType(PolylineOptions.ColorType.LINE_COLOR_ARGB)
                width?.let { opts.width(it) }
                borderWidth?.let { opts.borderWidth(it) }
                dottedLine?.let {
                        if (dottedLine == true) {
                                val pattern = listOf(35, 20)
                                opts.lineType(PolylineOptions.LineType.LINE_TYPE_DOTTEDLINE)
                                opts.pattern(pattern)
                        } else {
                                opts.lineType(PolylineOptions.LineType.LINE_TYPE_IMAGEINARYLINE)
                                opts.pattern(null)
                        }
                }
                color?.let { opts.color(it) }
                mColorList.size.let { if (it > 1) opts.colors(mColorList, colorIndexList) }
                // opts.colors(mColorList, colorIndexList)
                arrowLine?.let { opts.arrow(it) }
                borderColor?.let { opts.borderColor(it) }
                level?.let { opts.level(it ?: OverlayLevel.OverlayLevelAboveLabels) }

                return opts
        }
}

data class ArcRecord(
        @Field val id: String = UUID.randomUUID().toString(),
        @Field val start: MapLatLng? = null,
        @Field val end: MapLatLng? = null,
        @Field val pass: MapLatLng? = null,

        /** 线的颜色 */
        @Field val color: Int?,

        /** 线的宽度 */
        @Field val width: Float?,
        @Field val angle: Float? = null,

/** 压盖关系 */
// @Field val level: Int?,
) : Record {
        // MarkerCollisionRelation.ALONE

        fun toArcOptions(map: TencentMap): ArcOptions {
                val opts =
                        ArcOptions()
                                .points(start?.toLatLng(), end?.toLatLng())
                                .pass(pass?.toLatLng())
                angle?.let { opts.angle(it) }
                width?.let { opts.width(it) }
                color?.let { opts.color(it) }
                // level?.let { opts.level(it ?: OverlayLevel.OverlayLevelAboveLabels) }

                return opts
        }
}

data class PolygonRecord(
        @Field val id: String = UUID.randomUUID().toString(),
        @Field val points: List<MapLatLng>? = null,

        /** 线的颜色 */
        @Field val fillColor: Int?,

        /** 线的颜色 */
        @Field val strokeColor: Int?,

        /** 线的宽度 */
        @Field val strokeWidth: Float?,

        /** 是否虚线 */
        @Field val dottedLine: Boolean?,
        @Field val zIndex: Int? = null,

/** 压盖关系 */
// @Field val level: Int?,
) : Record {
        // MarkerCollisionRelation.ALONE

        fun toPolygonOptions(map: TencentMap): PolygonOptions {
                val opts = PolygonOptions().addAll(points?.map { it.toLatLng() } ?: emptyList())
                strokeWidth?.let { opts.strokeWidth(it) }
                fillColor?.let { opts.fillColor(it) }
                strokeColor?.let { opts.strokeColor(it) }
                zIndex?.let { opts.zIndex(it) }
                dottedLine?.let {

                        if (dottedLine == true) {
                                val pattern = listOf(35, 20)
                                opts.pattern(pattern)
                        } else {
                                opts.pattern(null)
                        }
                }
                return opts
        }
}



data class CircleRecord(
        @Field val id: String = UUID.randomUUID().toString(),
        @Field val center: MapLatLng? = null,

        @Field val radius: Double? = null,

        /** 线的颜色 */
        @Field val fillColor: Int?,

        /** 线的颜色 */
        @Field val strokeColor: Int?,

        /** 线的宽度 */
        @Field val strokeWidth: Float?,

        /** 是否虚线 */
        @Field val dottedLine: Boolean?,
        @Field val zIndex: Int? = null,

/** 压盖关系 */
// @Field val level: Int?,
) : Record {
        // MarkerCollisionRelation.ALONE

        fun toCircleOptions(map: TencentMap): CircleOptions {
                val opts = CircleOptions().center(center?.toLatLng() ) 
                radius?.let { opts.radius(it) }
                strokeWidth?.let { opts.strokeWidth(it) }
                fillColor?.let { opts.fillColor(it) }
                strokeColor?.let { opts.strokeColor(it) }
                zIndex?.let { opts.zIndex(it) }
                dottedLine?.let {
                        opts.borderType(if (dottedLine == true) CircleOptions.CircleBorderType.DOTTEDLINE_SQUARE else CircleOptions.CircleBorderType.REALLINE_DEFAULT)
                }
                return opts
        }
}

