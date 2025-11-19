
import { Button, Text, View, StyleSheet, Switch } from 'react-native';
import { LatLng, MapType, TMapsCameraPosition, TMapsUiSettings, TMapsView, MarkerRecord, PolylineRecord } from 'react-native-tlbs';
import React, { useState } from 'react';
import Slider from '@react-native-community/slider';
import { processColor } from 'react-native'


export default function PolylinesScene() {

    const [points, setPoints] = useState<LatLng[]>([
        {
            lat: 39.909,
            lng: 116.39742,
        },

        {
            lat: 39.939,
            lng: 116.39742,
        },
        {
            lat: 39.939,
            lng: 116.42742,
        },
        {
            lat: 39.909,
            lng: 116.42742,
        },
    ]);

    const [polyline, setPolyline] = useState<PolylineRecord>({
        points: points,
        color: '#f00',
        borderColor: '#f60',
        borderWidth: 10,
        width: 50,
        arrowLine: true,
        dottedLine: true,
    });


    console.log('polyline', polyline)


    return (
        <View style={styles.container}>
            <TMapsView

                mapType={MapType.MAP_TYPE_NEW_3D_IMMERSIVE}
                style={styles.map}
                cameraPosition={{
                    center: points[0],
                    zoom: 15,
                }}
                polylines={[polyline]}


            />

            <View style={styles.controlPanel}>
                <View style={styles.controlItem}>

                    <Text style={styles.controlLabel}>{polyline?.color?.toString()}</Text>
                    <Button
                        title={`切换颜色: `}
                        onPress={() => {
                            const randomColor = '#' + Math.floor(Math.random() * 16777215).toString(16);
                            setPolyline(prev => ({
                                ...prev,
                                color: randomColor
                            }));
                        }}
                    />
                </View>

                {/* 控制 rotation 值 */}
                <View style={styles.sliderContainer}>
                    <Text style={styles.controlLabel}>线宽: {Math.round(polyline?.width || 0)}°</Text>
                    <Slider
                        style={styles.slider}
                        minimumValue={5}
                        maximumValue={100}
                        value={polyline?.width || 0}

                        onSlidingComplete={(value) => {
                            setPolyline(prev => ({
                                ...prev,
                                width: value
                            }));
                        }}
                        minimumTrackTintColor="#007AFF"
                        maximumTrackTintColor="#D1D1D6"
                    />
                    <View style={styles.sliderLabels}>
                        <Text style={styles.sliderLabel}>5</Text>
                        <Text style={styles.sliderLabel}>100</Text>
                    </View>
                </View>
                <View style={styles.sliderContainer}>
                    <Text style={styles.controlLabel}>边框宽度: {Math.round(polyline?.borderWidth || 0)}</Text>
                    <Slider
                        style={styles.slider}
                        minimumValue={5}
                        maximumValue={100}
                        value={polyline?.borderWidth || 0}



                        onSlidingComplete={(value) => {
                            setPolyline(prev => ({
                                ...prev,
                                borderWidth: value
                            }));
                        }}
                        minimumTrackTintColor="#007AFF"
                        maximumTrackTintColor="#D1D1D6"
                    />
                    <View style={styles.sliderLabels}>
                        <Text style={styles.sliderLabel}>5</Text>
                        <Text style={styles.sliderLabel}>100</Text>
                    </View>
                </View>
                <View style={styles.controlItem}>
                    <Text style={styles.controlLabel}>是否显示箭头</Text>
                    <Switch
                        value={polyline?.arrowLine || false}
                        onValueChange={(value) => setPolyline(prev => ({
                            ...prev,
                            arrowLine: value,
                        }))}
                    />
                </View>
                <View style={styles.controlItem}>
                    <Text style={styles.controlLabel}>是否虚线</Text>
                    <Switch
                        value={polyline?.dottedLine || false}
                        onValueChange={(value) => setPolyline(prev => ({
                            ...prev,
                            dottedLine: value,
                        }))}
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