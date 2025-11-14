
import {
    Button, Text, View, StyleSheet, Switch,
    PermissionsAndroid,
    Platform,
} from 'react-native';
import { LatLng, MapType, TMapsCameraPosition, TMapsUiSettings, TMapsView } from 'react-native-tlbs';
import React, { useState ,useEffect} from 'react';

export default function UiSettingsScence() {

    const [center, setCenter] = useState<LatLng>({
        lat: 39.909,
        lng: 116.39742,
    });

    const [uiSettings, setUiSettings] = useState<TMapsUiSettings>({
        compassEnabled: true,
        rotateGesturesEnabled: true,
        scrollGesturesEnabled: true,
        tiltGesturesEnabled: true,
        zoomGesturesEnabled: true,
        scaleViewEnabled: true,
        zoomControlsEnabled: true,
        myLocationButtonEnabled: true,
        myLocationEnabled: true,
    });

    useEffect(() => {
        if (uiSettings?.myLocationEnabled) {
            requestLocationPermission();
        }
    }, [uiSettings?.myLocationEnabled]);

    // 请求定位权限
    const requestLocationPermission = async () => {
        if (Platform.OS === 'android') {
            try {
                const granted = await PermissionsAndroid.request(
                    PermissionsAndroid.PERMISSIONS.ACCESS_FINE_LOCATION,
                    {
                        title: '位置权限请求',
                        message: 'SOS功能需要您的位置权限来发送准确的位置信息',
                        buttonNeutral: '稍后决定',
                        buttonNegative: '取消',
                        buttonPositive: '确定',
                    }
                );
                console.log('granted', granted)
                return granted === PermissionsAndroid.RESULTS.GRANTED;
            } catch (err) {
                console.error('请求权限失败:', err);
                return false;
            }
        }
        return true;
    };




    return (
        <View style={styles.container}>
            <TMapsView
                mapType={MapType.MAP_TYPE_NEW_3D_IMMERSIVE}
                style={styles.map}
                uiSettings={uiSettings}
            />

            <View style={styles.controlPanel}>
                <View style={styles.controlItem}>
                    <Text style={styles.controlLabel}>指南针</Text>
                    <Switch
                        value={uiSettings?.compassEnabled}
                        onValueChange={v => {
                            setUiSettings({
                                ...uiSettings,
                                compassEnabled: v,
                            })
                        }}
                    />
                </View>
                <View style={styles.controlItem}>
                    <Text style={styles.controlLabel}>比例尺</Text>
                    <Switch
                        value={uiSettings?.scaleViewEnabled}
                        onValueChange={v => {
                            setUiSettings({
                                ...uiSettings,
                                scaleViewEnabled: v,
                            })
                        }}
                    />
                </View>
                <View style={styles.controlItem}>
                    <Text style={styles.controlLabel}>缩放控件</Text>
                    <Switch
                        value={uiSettings?.zoomControlsEnabled}
                        onValueChange={v => {
                            setUiSettings({
                                ...uiSettings,
                                zoomControlsEnabled: v,
                            })
                        }}
                    />
                </View>
                <View style={styles.controlItem}>
                    <Text style={styles.controlLabel}>定位按钮</Text>
                    <Switch
                        value={uiSettings?.myLocationButtonEnabled}
                        onValueChange={async v => {
                            setUiSettings({
                                ...uiSettings,
                                myLocationButtonEnabled: v,
                            })
                        }}
                    />
                </View>

                <View style={styles.controlItem}>
                    <Text style={styles.controlLabel}>显示我的位置</Text>
                    <Switch
                        value={uiSettings?.myLocationEnabled}
                        onValueChange={async v => {
                            setUiSettings({
                                ...uiSettings,
                                myLocationEnabled: v,
                            })
                        }}
                    />
                </View>
                <View style={styles.controlItem}>
                    <Text style={styles.controlLabel}>允许旋转手势</Text>
                    <Switch
                        value={uiSettings?.rotateGesturesEnabled}
                        onValueChange={async v => {
                            setUiSettings({
                                ...uiSettings,
                                rotateGesturesEnabled: v,
                            })
                        }}
                    />
                </View>
                <View style={styles.controlItem}>
                    <Text style={styles.controlLabel}>允许滚动手势</Text>
                    <Switch
                        value={uiSettings?.scrollGesturesEnabled}
                        onValueChange={async v => {
                            setUiSettings({
                                ...uiSettings,
                                scrollGesturesEnabled: v,
                            })
                        }}
                    />
                </View>
                <View style={styles.controlItem}>
                    <Text style={styles.controlLabel}>允许倾斜手势</Text>
                    <Switch
                        value={uiSettings?.tiltGesturesEnabled}
                        onValueChange={async v => {
                            setUiSettings({
                                ...uiSettings,
                                tiltGesturesEnabled: v,
                            })
                        }}
                    />
                </View>
                <View style={styles.controlItem}>
                    <Text style={styles.controlLabel}>允许缩放手势</Text>
                    <Switch
                        value={uiSettings?.zoomGesturesEnabled}
                        onValueChange={async v => {
                            setUiSettings({
                                ...uiSettings,
                                zoomGesturesEnabled: v,
                            })
                        }}
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