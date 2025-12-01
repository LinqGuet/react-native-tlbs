import { SharedRefType } from 'expo';
import type { Ref } from 'react';

import type { StyleProp, ViewStyle, ProcessedColorValue } from 'react-native';

export type LatLng = {
  lat: number;
  lng: number;
  height?: number;
};

export enum MapType {
  MAP_TYPE_NONE = 0,
  MAP_TYPE_NORMAL = 1000,
  MAP_TYPE_DARK = 1008,
  MAP_TYPE_SATELLITE = 1011,
  MAP_TYPE_NEW_3D_IMMERSIVE = 1016,
  MAP_TYPE_TRAFFIC_NAVI = 1009,
  MAP_TYPE_TRAFFIC_NIGHT = 1010,
  MAP_TYPE_NIGHT = 1013,
  MAP_TYPE_NAVI = 1012,
}

export enum MyLocationStyleType {
  /** 连续定位，但不会移动到地图中心点，并且会跟随设备移动 */
  LOCATION_TYPE_FOLLOW_NO_CENTER = 2,
  /** 连续定位，且将视角移动到地图中心，定位点依照设备方向旋转，并且会跟随设备移动,默认是此种类型 */
  LOCATION_TYPE_LOCATION_ROTATE = 0,
  /** 连续定位，但不会移动到地图中心点，定位点依照设备方向旋转，并且跟随设备移动 */
  LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER = 1,
  /** 连续定位，但不会移动到地图中心点，地图依照设备方向旋转，并且会跟随设备移动 */
  LOCATION_TYPE_MAP_ROTATE_NO_CENTER = 3,
}

export type MyLocationStyle = {
  /** 定位类型 */
  myLocationType?: MyLocationStyleType;

  /** 定位图标的锚点位置，默认为 (0.5f, 0.5f) */
  anchor?: MapsAnchor;
  /** 定位点的图标 */
  icon?: SharedRefType<'image'>;
  /** 以圆形表示的定位精度的填充颜色 */
  fillColor?: ProcessedColorValue | string;
  /** 以圆形表示的定位精度的描边颜色 */
  strokeColor?: ProcessedColorValue | string;
  /** 以圆形表示的定位精度的描边宽度 */
  strokeWidth?: number;
}


export type TMapsUiSettings = {
  /** 是否显示定位按钮 */
  myLocationButtonEnabled?: Boolean;
  /** 是否显示指南针 */
  compassEnabled?: Boolean;
  /** 是否显示比例尺 */
  scaleViewEnabled?: Boolean;

  /** 是否显示缩放控件 */
  zoomControlsEnabled?: Boolean;


  /** 是否启用定位功能 */
  myLocationEnabled?: Boolean;



  /** 是否显示比例尺 */
  rotateGesturesEnabled?: Boolean;
  /** 是否显示滚动手势 */
  scrollGesturesEnabled?: Boolean;
  /** 是否显示倾斜手势 */
  tiltGesturesEnabled?: Boolean;
  /** 是否显示缩放手势 */
  zoomGesturesEnabled?: Boolean;

  /** 定位样式 */
  myLocationStyle?: MyLocationStyle;


}



export type TMapsCameraPosition = {


  // 地图中心点
  center?: LatLng;
  // 地图缩放级别，3~20
  zoom?: Number;
  //地图最小缩放级别，最小是3
  minZoom?: Number;
  //地图最大缩放级别，最大是20
  maxZoom?: Number;
  //地图旋转角度，目标旋转角 0~360°
  rotation?: Number;
  //地图俯仰角度，目标倾斜角[0.0 ~ 45.0] (垂直地图时为0)
  pitch?: Number;

  /** 地图状态变更时间戳 */
  timespan?: Number;


};


/**
 * @platform android
 */
export type MapsAnchor = {
  /**
   * The normalized horizontal anchor point from 0.0 (left edge) to 1.0 (right edge).
   */
  x: number;

  /**
   * The normalized vertical anchor point from 0.0 (top edge) to 1.0 (bottom edge).
   */
  y: number;
};

export type TMapsViewType = {

  // setCameraPosition: (config?: SetCameraPositionConfig) => void;
};


export type TMapsViewProps = {
  // 地图容器的样式

  ref?: Ref<TMapsViewType>;
  style?: StyleProp<ViewStyle>;

  // 地图UI设置
  uiSettings?: TMapsUiSettings;

  // 视野范围设置
  cameraPosition?: TMapsCameraPosition;

  //地图类型
  mapType?: MapType;

  //是否开启离线地图
  offlineMapEnable?: Boolean;

  /** 是否开启交通流量 */
  trafficEnabled?: Boolean;

  markers?: MarkerRecord[];

  polylines?: PolylineRecord[];

  arcs?: ArcRecord[];

  circles?: CircleRecord[];

  polygons?: PolygonRecord[];



  onLoad?: () => void;

  onMapClick?: (position: LatLng) => void;

  onMapLongClick?: (position: LatLng) => void;

  onCompassClick?: (event: { nativeEvent: {} }) => void;

  onCameraChange?: (camera: TMapsCameraPosition) => void;
  onMarkerClick?: (marker: MarkerRecord) => void;



};

/**
 * 定位信息接口
 */
export type LocationData = {
  docId?: string;
  latitude: number;
  longitude: number;
  altitude: number;
  accuracy: number;
  speed: number;
  bearing: number;
  time: number;
  address?: string;
  province?: string;
  city?: string;
  district?: string;
  town?: string;
  village?: string;
  street?: string;
  streetNo?: string;
};

/**
 * 定位错误接口
 */
export type LocationError = {
  errorCode: number;
  errorMessage: string;
};


export enum TLBSSceneEnum {
  SIGN_IN_SCENE = 10, // 签到场景
  SPORT_SCENE = 11, // 运动场景
  TRANSPORT_SCENE = 12, // 交通场景
}
export type TLBStatusEvent = {
  scene: TLBSSceneEnum;
}

export type TLBSLocationEvent = {
  location: LocationData;
}

export type TLBSErrorEvent = {
  error: LocationError;
}


export type LBSLocationModuleEvents = {
  onLocationUpdate: (params: TLBSLocationEvent) => void;
  onLocationError: (params: TLBSErrorEvent) => void;
  onStatusUpdate: (params: TLBStatusEvent) => void;
};

export type PolylineRecord = {
  // 折线的唯一标识符
  id?: string;
  // 折线的路径点
  points?: LatLng[];
  // 折线的颜色
  color?: ProcessedColorValue | string;
  // 折线的颜色列表
  colorList?: ProcessedColorValue[] | string[];
  // 折线的宽度
  width?: number;
  // 是否为虚线
  dottedLine?: boolean;
  // 是否显示箭头
  arrowLine?: boolean;
  // 折线的边框颜色
  borderColor?: ProcessedColorValue | string;
  // 折线的边框宽度
  borderWidth?: number;
  // 折线的层级 
  level?: number;
}

export type ArcRecord = {
  // 折线的唯一标识符
  id?: string;
  // 折线的路径点
  start?: LatLng;
  end?: LatLng;
  pass?: LatLng;
  // 折线的颜色
  color?: ProcessedColorValue | string;
  // 折线的宽度
  width?: number;
  // 折线的角度
  angle?: number;
}

export type PolygonRecord = {
  // 折线的唯一标识符
  id?: string;
  // 折线的路径点
  points?: LatLng[];
  // 折线的颜色
  strokeColor?: ProcessedColorValue | string;

  fillColor?: ProcessedColorValue | string;
  // 折线的宽度
  strokeWidth?: number;
  // 是否为虚线
  dottedLine?: boolean;
  /** 折线的层级 */
  zIndex?: number;
}


export type CircleRecord = {
  // 折线的唯一标识符
  id?: string;
  // 折线的路径点
  center?: LatLng;
  // 圆的半径
  radius?: number;
  // 折线的颜色
  strokeColor?: ProcessedColorValue | string;

  fillColor?: ProcessedColorValue | string;
  // 折线的宽度
  strokeWidth?: number;
  // 是否为虚线
  dottedLine?: boolean;
  /** 折线的层级 */
  zIndex?: number;
}


export type MarkerRecord = {
  // 标记的唯一标识符
  id?: string;
  // 标记的聚类ID
  clusterId?: string;
  // 是否可聚类
  isClusterable?: boolean;
  // 标记的标题
  title?: string;
  // 标记的层级
  zIndex?: number;
  icon?: SharedRefType<'image'>;

  /** 标记的位置 */
  position?: LatLng;

  anchor?: MapsAnchor;
  alpha?: number;
  flat?: boolean;
  rotation?: number;
  clockwise?: boolean;
  level?: number;
  visible?: boolean;
  draggable?: boolean;
  fastLoad?: boolean;
  infoWindowEnable?: boolean;
  infoWindowAnchor?: MapsAnchor;
  infoWindowOffset?: MapsAnchor;
  infoWindowVisible?: boolean;
  viewInfoWindow?: boolean;
  snippet?: string;
}

export type CalloutRecord = {
  content?: string;
  color?: string;
  fontSize?: number;
  borderRadius?: number;
  borderWidth?: number;
  borderColor?: string;
  bgColor?: string;
  padding?: number;
  display?: string;
  textAlign?: string;
  anchorX?: number;
  anchorY?: number;
  collision?: string;


}

export type CustomCalloutRecord = {
  display?: string;
  anchorX?: number;
  anchorY?: number;

}

export type LabelRecord = {
  content?: string;
  color?: string;
  fontSize?: number;

  anchorX?: number;
  anchorY?: number;
  borderWidth?: number;
  borderColor?: string;
  borderRadius?: number;
  bgColor?: string;
  padding?: number;
  textAlign?: string;
  collision?: string;

}

