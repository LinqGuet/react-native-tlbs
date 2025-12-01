import { requireNativeViewManager } from 'expo-modules-core';


import * as React from 'react';

import { processColor } from 'react-native'

import { TMapsViewProps } from './tlsb.types';
const NativeView = requireNativeViewManager('TMapsView');


function useNativeEvent<T>(userHandler?: (data: T) => void) {
  return React.useCallback(
    // TODO(@kitten): We unwrap a native payload here, but this isn't reflected in NativeView's prop types
    (event: any) => {
      userHandler?.(event.nativeEvent);
    },
    [userHandler]
  );
}


export const TMapsView = function ExpoWebView(props: TMapsViewProps) {
  const {
    onLoad,
    onMapClick,
    onCompassClick,
    onCameraChange,
    onMapLongClick,
    onMarkerClick,
    markers,
    polylines,
    arcs,
    circles,
    polygons,
    uiSettings

  } = props;



  const onNativeMapLoaded = React.useCallback(() => {
    onLoad?.();
  }, [onLoad]);

  const onNativeMapClick = useNativeEvent(onMapClick);
  const onNativeMapLongClick = useNativeEvent(onMapLongClick);
  const onNativeCompassClick = useNativeEvent(onCompassClick);
  const onNativeCameraChange = useNativeEvent(onCameraChange);
  const onNativeMarkerClick = useNativeEvent(onMarkerClick);

  const generateRandomId = (type: string) => {
    // 使用时间戳和两个随机数组合生成唯一ID
    const timestamp = Date.now();
    const random1 = Math.random().toString(36).substring(2, 10);
    const random2 = Math.random().toString(36).substring(2, 10);
    return `${type}-${timestamp}-${random1}-${random2}`;
  };

  const parsedPolylines = polylines ? (() => {
    // 步骤1: 为没有id的marker分配不重复的随机id
    const polylinesWithId = polylines.map(polyline => {
      polyline.id = polyline.id || generateRandomId('polyline')
      return polyline;
    });

    // 步骤2: 检查是否有重复的id
    const idSet = new Set<string>();
    for (const polyline of polylinesWithId) {
      if (idSet.has(polyline?.id || '')) {
        console.error(`错误: 发现重复的polyline ID: ${polyline?.id}`);
        throw new Error(`发现重复的polyline ID: ${polyline?.id}`);
      }
      idSet.add(polyline?.id || '');
    }

    // 步骤3: 执行原始的map操作
    return polylinesWithId.map((polyline) => ({
      ...polyline,
      color: processColor(polyline.color) ?? undefined,
      borderColor: processColor(polyline.borderColor) ?? undefined,
    }));
  })() : undefined;



  const parsedPolygons = polygons ? (() => {
    // 步骤1: 为没有id的marker分配不重复的随机id
    const polygonsWithId = polygons.map(polygon => {
      polygon.id = polygon.id || generateRandomId('polygon')
      return polygon;
    });

    // 步骤2: 检查是否有重复的id
    const idSet = new Set<string>();
    for (const polygon of polygonsWithId) {
      if (idSet.has(polygon?.id || '')) {
        console.error(`错误: 发现重复的polygon ID: ${polygon?.id}`);
        throw new Error(`发现重复的polygon ID: ${polygon?.id}`);
      }
      idSet.add(polygon?.id || '');
    }

    // 步骤3: 执行原始的map操作
    return polygonsWithId.map((polygon) => ({
      ...polygon,
      fillColor: processColor(polygon.fillColor) ?? undefined,
      strokeColor: processColor(polygon.strokeColor) ?? undefined,
    }));
  })() : undefined;



  const parsedCircles = circles ? (() => {
    // 步骤1: 为没有id的marker分配不重复的随机id
    const circlesWithId = circles.map(circle => {
      circle.id = circle.id || generateRandomId('circle')
      return circle;
    });

    // 步骤2: 检查是否有重复的id
    const idSet = new Set<string>();
    for (const circle of circlesWithId) {
      if (idSet.has(circle?.id || '')) {
        console.error(`错误: 发现重复的circle ID: ${circle?.id}`);
        throw new Error(`发现重复的circle ID: ${circle?.id}`);
      }
      idSet.add(circle?.id || '');
    }

    // 步骤3: 执行原始的map操作
    return circlesWithId.map((circle) => ({
      ...circle,
      fillColor: processColor(circle.fillColor) ?? undefined,
      strokeColor: processColor(circle.strokeColor) ?? undefined, 
    }));
  })() : undefined;




  const parsedArcs = arcs ? (() => {
    // 步骤1: 为没有id的marker分配不重复的随机id
    const arcsWithId = arcs.map(arc => {
      arc.id = arc.id || generateRandomId('arc')
      return arc;
    });

    // 步骤2: 检查是否有重复的id
    const idSet = new Set<string>();
    for (const arc of arcsWithId) {
      if (idSet.has(arc?.id || '')) {
        console.error(`错误: 发现重复的arc ID: ${arc?.id}`);
        throw new Error(`发现重复的arc ID: ${arc?.id}`);
      }
      idSet.add(arc?.id || '');
    }

    // 步骤3: 执行原始的map操作
    return arcsWithId.map((arc) => ({
      ...arc,
      color: processColor(arc.color) ?? undefined,
    }));
  })() : undefined;



  const parsedMarkers = markers ? (() => {

    // 步骤1: 为没有id的marker分配不重复的随机id
    const markersWithId = markers.map(marker => {
      marker.id = marker.id || generateRandomId('marker');
      return marker;
    });

    // 步骤2: 检查是否有重复的id
    const idSet = new Set<string>();
    for (const marker of markersWithId) {
      if (idSet.has(marker?.id || '')) {
        console.error(`错误: 发现重复的marker ID: ${marker?.id}`);
        throw new Error(`发现重复的marker ID: ${marker?.id}`);
      }
      idSet.add(marker?.id || '');
    }
    // @ts-expect-error
    console.log('markers',markers,markers[0]?.icon.__expo_shared_object_id__)

    // 步骤3: 执行原始的map操作
    return markersWithId.map((marker) => ({
      ...marker,
      // @ts-expect-error
      icon: marker.icon?.__expo_shared_object_id__,
    }));
  })() : undefined;


  const parsedUiSettings = uiSettings ? ({
    ...uiSettings,
    myLocationStyle: {
      ...uiSettings?.myLocationStyle,
      fillColor: processColor(uiSettings?.myLocationStyle?.fillColor) ?? undefined,
      strokeColor: processColor(uiSettings?.myLocationStyle?.strokeColor) ?? undefined,
    },
  }) : undefined;


  if (!NativeView) {
    return null;
  }
  return (
    <NativeView
      {...props}
      uiSettings={parsedUiSettings}
      markers={parsedMarkers}
      polylines={parsedPolylines}
      circles={parsedCircles}
      polygons={parsedPolygons}
      arcs={parsedArcs}
      onLoad={onNativeMapLoaded}
      onMapClick={onNativeMapClick}
      onMapLongClick={onNativeMapLongClick}
      onMarkerClick={onNativeMarkerClick}
      onCompassClick={onNativeCompassClick}
      onCameraChange={onNativeCameraChange}
    />
  );


}