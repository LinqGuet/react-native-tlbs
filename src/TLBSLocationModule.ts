import { NativeModule, requireNativeModule } from 'expo';

import { TLBSSceneEnum,LBSLocationModuleEvents } from './tlsb.types';



declare class LBSModule extends NativeModule<LBSLocationModuleEvents> {
  startLocationUpdatesAsync: (scene:TLBSSceneEnum) => Promise<boolean>;
  stopLocationWithScene: (scene:TLBSSceneEnum) => Promise<void>;
}


// This call loads the native module object from the JSI.
export default requireNativeModule<LBSModule>('TLBSLocation');