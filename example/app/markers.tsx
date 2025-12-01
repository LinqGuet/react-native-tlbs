
import { Button, Text, View, StyleSheet, Switch, Image } from 'react-native';
import { LatLng, MapType, TMapsCameraPosition, TMapsUiSettings, TMapsView, MarkerRecord } from 'react-native-tlbs';
import React, { useState } from 'react';
import Slider from '@react-native-community/slider';
import {useImage} from 'expo-image';

export default function MarkersScence() {

    const [center, setCenter] = useState<LatLng>({
        lat: 39.909,
        lng: 116.39742,
    });
    const image = useImage(require('../assets/favicon.png'));

    const [marker, setMarker] = useState<MarkerRecord>({
        position: center,
        title: '标注点',
        infoWindowEnable: true,
        viewInfoWindow: true,
        alpha: 1,
        infoWindowVisible: false,
        icon:require('../assets/favicon.png'),
    });
    console.log('MarkersScence',marker,require('../assets/favicon.png').__expo_shared_object_id__)






    return (
        <View style={styles.container}>
            <TMapsView

                mapType={MapType.MAP_TYPE_NEW_3D_IMMERSIVE}
                style={styles.map}
                cameraPosition={{
                    zoom: 15,
                }}
                markers={[marker]}


            />

            <View style={styles.controlPanel}>
                <View style={styles.controlItem}>
                    <Text style={styles.controlLabel}>显示InfoWindow</Text>
                    <Switch
                        value={marker?.infoWindowVisible || false}
                        onValueChange={(value) => setMarker(prev => ({
                            ...prev,
                            infoWindowVisible: value,
                        }))}
                    />
                </View>


                {/* 控制 rotation 值 */}
                <View style={styles.sliderContainer}>
                    <Text style={styles.controlLabel}>旋转角度 (rotation): {Math.round(marker?.rotation || 0)}°</Text>
                    <Slider
                        style={styles.slider}
                        minimumValue={0}
                        maximumValue={360}
                        value={marker?.rotation || 0}

                        onSlidingComplete={(value) => {
                            console.log('onValueChange 旋转角度', value);
                            setMarker(prev => ({
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
                <View style={styles.sliderContainer}>
                    <Text style={styles.controlLabel}>透明度 (alpha): {marker?.alpha || 0}</Text>
                    <Slider
                        style={styles.slider}
                        minimumValue={0}
                        maximumValue={1}
                        value={marker?.alpha || 0}

                        onSlidingComplete={(value) => {
                            console.log('onValueChange 透明度', value);
                            setMarker(prev => ({
                                ...prev,
                                alpha: value
                            }));
                        }}
                        minimumTrackTintColor="#007AFF"
                        maximumTrackTintColor="#D1D1D6"
                    />
                    <View style={styles.sliderLabels}>
                        <Text style={styles.sliderLabel}>0</Text>       
                        <Text style={styles.sliderLabel}>1</Text>
                    </View>
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