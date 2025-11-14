// Reexport the native module. On web, it will be resolved to ExpoWebViewModule.web.ts
import { EventSubscription } from 'expo-modules-core';
// and on native platforms to ExpoWebViewModule.ts


import TLBSLocationModule from './TLBSLocationModule';
import { TLBSErrorEvent, TLBSLocationEvent, TLBSSceneEnum, TLBStatusEvent } from './tlsb.types';


export function addLocationUpdateListener(listener: (event: TLBSLocationEvent) => void): EventSubscription {
    return TLBSLocationModule.addListener('onLocationUpdate', listener);
}

export function addStatusUpdateListener(listener: (event: TLBStatusEvent) => void): EventSubscription {
    return TLBSLocationModule.addListener('onStatusUpdate', listener);
}
export function addLocationErrorListener(listener: (event: TLBSErrorEvent) => void): EventSubscription {
    return TLBSLocationModule.addListener('onLocationError', listener);
}

export function startLocationUpdatesAsync(scene:TLBSSceneEnum): Promise<boolean> {
  return TLBSLocationModule.startLocationUpdatesAsync(scene);
}

export function stopLocationWithScene(scene:TLBSSceneEnum): Promise<void> {
  return TLBSLocationModule.stopLocationWithScene(scene);
}




export * from './tlsb.types';
export { TMapsView } from './TMapsView';

