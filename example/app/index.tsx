import { Button,  ScrollView, Text, View, StyleSheet } from 'react-native';
import React from 'react';
import { Link } from 'expo-router';

export default function App() {

  return (
    <View style={styles.container}>
      <ScrollView contentContainerStyle={styles.scrollContent}>
        <Text style={styles.title}>地图示例应用</Text>
        <Text style={styles.subtitle}>请选择您想要查看的功能：</Text>
        
        <View style={styles.linkContainer}>
          <Link href="/map" asChild>
            <Button 
              title="地图展示页面" 
              style={styles.linkButton}
              onPress={() => console.log('导航到地图页面')}
            />
          </Link>
        </View>
        
        <View style={styles.linkContainer}>
          <Link href="/cameraPosition" asChild>
            <Button 
              title="地图视野控制" 
              style={styles.linkButton}
              onPress={() => console.log('地图视野')}
            />
          </Link>
        </View>
        <View style={styles.linkContainer}>
          <Link href="/uiSettings" asChild>
            <Button 
              title="地图UI设置" 
              style={styles.linkButton}
              onPress={() => console.log('地图UI设置')}
            />
          </Link>
        </View>
        <View style={styles.linkContainer}>
          <Link href="/markers" asChild>
            <Button 
              title="地图标注" 
              style={styles.linkButton}
              onPress={() => console.log('地图标注')}
            />
          </Link>
        </View>
        <View style={styles.linkContainer}>
          <Link href="/polylines" asChild>
            <Button 
              title="地图折线" 
              style={styles.linkButton}
              onPress={() => console.log('地图折线')}   
            />
          </Link>
        </View>
         <View style={styles.linkContainer}>
          <Link href="/polygons" asChild>
            <Button 
              title="地图多边形" 
              style={styles.linkButton}
              onPress={() => console.log('地图多边形')}     
            />
          </Link>
        </View>
        <View style={styles.linkContainer}>
          <Link href="/circles" asChild>
            <Button 
              title="地图圆" 
              style={styles.linkButton}
              onPress={() => console.log('地图圆')}   
            />
          </Link>
        </View>
         <View style={styles.linkContainer}>
          <Link href="/arcs" asChild>
            <Button 
              title="地图弧线" 
              style={styles.linkButton}
              onPress={() => console.log('地图弧线')}     
            />
          </Link>
        </View>
      </ScrollView>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  scrollContent: {
    flexGrow: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 20,
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 8,
    textAlign: 'center',
  },
  subtitle: {
    fontSize: 16,
    color: '#666',
    marginBottom: 30,
    textAlign: 'center',
  },
  linkContainer: {
    width: '100%',
    marginBottom: 20,
  },
  linkButton: {
    paddingVertical: 15,
  },
});
