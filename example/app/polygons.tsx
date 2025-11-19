
import { Button, Text, View, StyleSheet, Switch } from 'react-native';
import { LatLng, MapType, TMapsCameraPosition, TMapsUiSettings, TMapsView, MarkerRecord, PolylineRecord, PolygonRecord } from 'react-native-tlbs';
import React, { useState } from 'react';
import Slider from '@react-native-community/slider';
import { processColor } from 'react-native'


export default function PolygonsScene() {

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
        {
            lat: 39.909,
            lng: 116.39742,
        },
    ]);

    const [polygon, setPolygon] = useState<PolygonRecord>({ 
        points: points,
        
        dottedLine: true,
        strokeWidth: 10,
        fillColor: '#f00',
        strokeColor: '#f60',
        
    });



    console.log('polygon', polygon)


    return (
        <View style={styles.container}>
            <TMapsView

                mapType={MapType.MAP_TYPE_NEW_3D_IMMERSIVE}
                style={styles.map}
                cameraPosition={{
                    center: points[0],
                    zoom: 15,
                }}
                polygons={[polygon]}


            />

            <View style={styles.controlPanel}>
                <View style={styles.controlItem}>

                    <Text style={styles.controlLabel}>{polygon?.fillColor?.toString()}</Text>
                    <Button
                        title={`切换颜色: `}
                        onPress={() => {
                            const randomColor = '#' + Math.floor(Math.random() * 16777215).toString(16);
                            setPolygon(prev => ({
                                ...prev,
                                fillColor: randomColor
                            }));
                        }}
                    />
                </View>

                {/* 控制 rotation 值 */}
                <View style={styles.sliderContainer}>
                    <Text style={styles.controlLabel}>线宽: {Math.round(polygon?.strokeWidth || 0)}°</Text>
                    <Slider
                        style={styles.slider}
                        minimumValue={5}
                        maximumValue={100}
                        value={polygon?.strokeWidth || 0}

                        onSlidingComplete={(value) => {
                            setPolygon(prev => ({
                                ...prev,
                                strokeWidth: value
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
                    <Text style={styles.controlLabel}>是否虚线</Text>
                    <Switch
                        value={polygon?.dottedLine || false}
                        onValueChange={(value) => setPolygon(prev => ({
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