import React, { useState, useEffect } from 'react';
import { View, Text, FlatList, TouchableOpacity, StyleSheet } from 'react-native';
import { useStorageManager, FileInfo } from '../hooks/useStorageManager';

export const FileTree: React.FC = () => {
  const { listFiles, openWith } = useStorageManager();
  const [files, setFiles] = useState<FileInfo[]>([]);

  useEffect(() => {
    loadFiles();
  }, []);

  const loadFiles = async () => {
    const fileList = await listFiles();
    setFiles(fileList);
  };

  const handleFilePress = (file: FileInfo) => {
    if (!file.isDirectory) {
      openWith(file.uri);
    }
  };

  const renderFile = ({ item }: { item: FileInfo }) => (
    <TouchableOpacity style={styles.fileItem} onPress={() => handleFilePress(item)}>
      <Text style={styles.icon}>{item.isDirectory ? '📁' : '📄'}</Text>
      <Text style={styles.fileName}>{item.name}</Text>
    </TouchableOpacity>
  );

  return (
    <View style={styles.container}>
      <Text style={styles.header}>📂 Storage Manager</Text>
      <FlatList data={files} renderItem={renderFile} keyExtractor={(item) => item.uri} />
    </View>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#1e1e1e' },
  header: { fontSize: 24, fontWeight: 'bold', color: '#00d4ff', padding: 16 },
  fileItem: { flexDirection: 'row', alignItems: 'center', padding: 12, backgroundColor: '#2d2d2d', margin: 4, borderRadius: 8 },
  icon: { fontSize: 24, marginRight: 12 },
  fileName: { color: '#ffffff', fontSize: 16 },
});
