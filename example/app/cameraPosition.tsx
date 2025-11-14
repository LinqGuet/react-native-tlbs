
import { Button, Text, View, StyleSheet, Switch, ScrollView } from 'react-native';
import Slider from '@react-native-community/slider';
import { LatLng, MapType, TMapsCameraPosition, TMapsUiSettings, TMapsView } from 'react-native-tlbs';
import React, { useState } from 'react';

export default function CameraPositionScence() {
    const [mapType, setMapType] = useState<MapType>(MapType.MAP_TYPE_NEW_3D_IMMERSIVE);
    const [trafficEnabled, setTrafficEnabled] = useState<boolean>(false);

    const [center, setCenter] = useState<LatLng>({
        lat: 39.909,
        lng: 116.39742,
    });

    const [cameraPosition, setCameraPosition] = useState<TMapsCameraPosition>({
        center,
        zoom: 16,
        minZoom: 14,
        maxZoom: 20,
        rotation: 0,
        pitch: 0,
    });

    const [uiSettings, setUiSettings] = useState<TMapsUiSettings>({
        rotateGesturesEnabled: true,
        scrollGesturesEnabled: true,
        zoomGesturesEnabled: true,
    });





    return (
        <View style={styles.container}>
            <TMapsView
                mapType={MapType.MAP_TYPE_NEW_3D_IMMERSIVE}
                cameraPosition={cameraPosition}
                uiSettings={uiSettings}
                style={styles.map}
                onCameraChange={(position) => {
                    console.log('onCameraChange', position);
                    setCameraPosition({
                        ...position,
                    });
                }}
            />

            <ScrollView style={styles.controlPanel}>
                {/* 控制 zoom 值 */}
                <View style={styles.sliderContainer}>
                    <Text style={styles.controlLabel}>缩放级别 (zoom): {cameraPosition?.zoom?.toFixed(1) || 'N/A'}</Text>
                    <Slider
                        style={styles.slider}
                        minimumValue={cameraPosition?.minZoom || 3}
                        maximumValue={cameraPosition?.maxZoom || 20}
                        value={cameraPosition?.zoom || 16}
                        onSlidingComplete={(value) => {
                            setCameraPosition(prev => ({
                                ...prev,
                                zoom: Math.round(value)
                            }));
                        }}
                        minimumTrackTintColor="#007AFF"
                        maximumTrackTintColor="#D1D1D6"
                    />
                    <View style={styles.sliderLabels}>
                        <Text style={styles.sliderLabel}>{cameraPosition?.minZoom || '3'}</Text>
                        <Text style={styles.sliderLabel}>{cameraPosition?.maxZoom || '20'}</Text>
                    </View>
                </View>

                {/* 控制 rotation 值 */}
                <View style={styles.sliderContainer}>
                    <Text style={styles.controlLabel}>旋转角度 (rotation): {Math.round(cameraPosition?.rotation || 0)}°</Text>
                    <Slider
                        style={styles.slider}
                        minimumValue={0}
                        maximumValue={360}
                        value={cameraPosition?.rotation || 0}
                        
                        onSlidingComplete={(value) => {
                            console.log('onValueChange 旋转角度', value);
                            setCameraPosition(prev => ({
                                ...prev,
                                rotation: value
                            }));
                        }}
                        minimumTrackTintColor="#007AFF"
                        maximumTrackTintColor="#D1D1D6"
                    />
                    <View style={styles.sliderLabels}>
                        <Text style={styles.sliderLabel}>0°</Text>
                        <Text style={styles.sliderLabel}>360°</Text>
                    </View>
                </View>

                {/* 控制 pitch 值 */}
                <View style={styles.sliderContainer}>
                    <Text style={styles.controlLabel}>俯仰角度 (pitch): {cameraPosition?.pitch?.toFixed(1) || 'N/A'}°</Text>
                    <Slider
                        style={styles.slider}
                        minimumValue={0}
                        maximumValue={45}
                        value={cameraPosition?.pitch || 0}
                        onSlidingComplete={(value) => {
                            setCameraPosition(prev => ({
                                ...prev,
                                pitch: value
                            }));
                        }}
                        minimumTrackTintColor="#007AFF"
                        maximumTrackTintColor="#D1D1D6"
                    />
                    <View style={styles.sliderLabels}>
                        <Text style={styles.sliderLabel}>0°</Text>
                        <Text style={styles.sliderLabel}>45°</Text>
                    </View>
                </View>

                {/* 控制 minZoom 和 maxZoom 值 */}
                <View style={styles.sliderContainer}>
                    <Text style={styles.controlLabel}>最小缩放 (minZoom): {cameraPosition?.minZoom?.toFixed(1) || 'N/A'}</Text>
                    <Slider
                        style={styles.slider}
                        minimumValue={3}
                        maximumValue={cameraPosition?.maxZoom || 20}
                        value={cameraPosition?.minZoom || 14}   
                        onSlidingComplete={(value) => {
                            setCameraPosition(prev => ({
                                ...prev,
                                minZoom: Math.round(value)
                            }));
                        }}
                        minimumTrackTintColor="#007AFF"
                        maximumTrackTintColor="#D1D1D6"
                    />
                    <View style={styles.sliderLabels}>
                        <Text style={styles.sliderLabel}>3</Text>
                        <Text style={styles.sliderLabel}>{cameraPosition?.maxZoom || '20'}</Text>
                    </View>
                </View>

                <View style={styles.sliderContainer}>
                    <Text style={styles.controlLabel}>最大缩放 (maxZoom): {cameraPosition?.maxZoom?.toFixed(1) || 'N/A'}</Text>
                    <Slider
                        style={styles.slider}
                        minimumValue={cameraPosition?.minZoom || 3}
                        maximumValue={20}
                        value={cameraPosition?.maxZoom || 20}
                        onSlidingComplete={(value) => {
                            setCameraPosition(prev => ({
                                ...prev,
                                maxZoom: Math.round(value)
                            }));
                        }}
                        minimumTrackTintColor="#007AFF"
                        maximumTrackTintColor="#D1D1D6"
                    />
                    <View style={styles.sliderLabels}>
                        <Text style={styles.sliderLabel}>{cameraPosition?.minZoom || '3'}</Text>
                        <Text style={styles.sliderLabel}>20</Text>
                    </View>
                </View>
            </ScrollView>
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
        maxHeight: '60%',
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
        marginBottom: 8,
    },
    sliderContainer: {
        marginBottom: 24,
    },
    slider: {
        width: '100%',
        height: 40,
    },
    sliderLabels: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        paddingHorizontal: 4,
    },
    sliderLabel: {
        fontSize: 12,
        color: '#666',
    },
});