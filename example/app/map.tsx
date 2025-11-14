
import { Button, Text, View, StyleSheet, Switch } from 'react-native';
import { LatLng, MapType, TMapsCameraPosition, TMapsUiSettings, TMapsView } from 'react-native-tlbs';
import React, { useState } from 'react';

export default function MapScence() {
    const [mapType, setMapType] = useState<MapType>(MapType.MAP_TYPE_NEW_3D_IMMERSIVE);
    const [trafficEnabled, setTrafficEnabled] = useState<boolean>(false);

    const [center, setCenter] = useState<LatLng>({
        lat: 39.909,
        lng: 116.39742,
    });


    const toggleMapType = () => {
        setMapType(prevType => {
            switch (prevType) {
                case MapType.MAP_TYPE_NEW_3D_IMMERSIVE:
                    return MapType.MAP_TYPE_NORMAL;
                case MapType.MAP_TYPE_NORMAL:
                    return MapType.MAP_TYPE_SATELLITE;
                case MapType.MAP_TYPE_SATELLITE:
                    return MapType.MAP_TYPE_DARK;
                case MapType.MAP_TYPE_DARK:
                    return MapType.MAP_TYPE_NEW_3D_IMMERSIVE;
                default:
                    return MapType.MAP_TYPE_NORMAL;
            }
        });
    };

    const getMapTypeText = () => {
        switch (mapType) {
            case MapType.MAP_TYPE_NEW_3D_IMMERSIVE:
                return '3D地图';
            case MapType.MAP_TYPE_NORMAL:
                return '标准地图';
            case MapType.MAP_TYPE_SATELLITE:
                return '卫星地图';
            case MapType.MAP_TYPE_DARK:
                return '暗色地图';
        }
    };

    return (
        <View style={styles.container}>
            <TMapsView 
                mapType={mapType} 
                style={styles.map}
                trafficEnabled={trafficEnabled}
                
            />
            
            <View style={styles.controlPanel}>
                <View style={styles.controlItem}>
                    
                    <Text style={styles.controlLabel}>{getMapTypeText()}</Text>
                    <Button 
                        title={`切换地图类型: `} 
                        onPress={toggleMapType}
                    />
                </View>
                
                <View style={styles.controlItem}>
                    <Text style={styles.controlLabel}>实时路况</Text>
                    <Switch
                        value={trafficEnabled}
                        onValueChange={setTrafficEnabled}
                    />
                </View>
                
               
            </View>
        </View>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        flexDirection: 'column',
    },
    map: {
        flex: 1,
        width: '100%',
    },
    controlPanel: {
        backgroundColor: 'white',
        padding: 16,
        borderTopWidth: 1,
        borderTopColor: '#e0e0e0',
    },
    controlItem: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'center',
        marginBottom: 16,
    },
    controlLabel: {
        fontSize: 16,
        fontWeight: '500',
    },
});