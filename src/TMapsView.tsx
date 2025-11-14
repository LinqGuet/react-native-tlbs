import { requireNativeViewManager } from 'expo-modules-core';


import * as React from 'react';

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

  } = props;



  const onNativeMapLoaded = React.useCallback(() => {
    onLoad?.();
  }, [onLoad]);

  const onNativeMapClick = useNativeEvent(onMapClick);
  const onNativeMapLongClick = useNativeEvent(onMapLongClick);
  const onNativeCompassClick = useNativeEvent(onCompassClick);
  const onNativeCameraChange = useNativeEvent(onCameraChange);
  const onNativeMarkerClick = useNativeEvent(onMarkerClick);






  const parsedMarkers = markers ? (() => {
    // 生成随机ID的函数（同步版本）
    const generateRandomId = () => {
      // 使用时间戳和两个随机数组合生成唯一ID
      const timestamp = Date.now();
      const random1 = Math.random().toString(36).substring(2, 10);
      const random2 = Math.random().toString(36).substring(2, 10);
      return `marker-${timestamp}-${random1}-${random2}`;
    };

    // 步骤1: 为没有id的marker分配不重复的随机id
    const markersWithId = markers.map(marker => ({
      ...marker,
      id: marker.id || generateRandomId(),
    }));

    // 步骤2: 检查是否有重复的id
    const idSet = new Set<string>();
    for (const marker of markersWithId) {
      if (idSet.has(marker.id)) {
        console.error(`错误: 发现重复的marker ID: ${marker.id}`);
        throw new Error(`发现重复的marker ID: ${marker.id}`);
      }
      idSet.add(marker.id);
    }

    // 步骤3: 执行原始的map操作
    return markersWithId.map((marker) => ({
      ...marker,
      // @ts-expect-error
      icon: marker.icon?.__expo_shared_object_id__,
    }));
  })() : undefined;


  if (!NativeView) {
    return null;
  }
  return (
    <NativeView
      {...props}
      markers={parsedMarkers}
      onLoad={onNativeMapLoaded}
      onMapClick={onNativeMapClick}
      onMapLongClick={onNativeMapLongClick}
      onMarkerClick={onNativeMarkerClick}
      onCompassClick={onNativeCompassClick}
      onCameraChange={onNativeCameraChange}
    />
  );


}