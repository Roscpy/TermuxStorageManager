import { NativeModules, Platform } from 'react-native';

const { StorageManager } = NativeModules;

export interface FileInfo {
  name: string;
  mime: string;
  size: number;
  modified: number;
  isDirectory: boolean;
  uri: string;
}

export const useStorageManager = () => {
  const requestAccess = async (): Promise<boolean> => {
    if (Platform.OS !== 'android') return false;
    try {
      await StorageManager.requestStorageAccess();
      return true;
    } catch (error) {
      console.error('Storage access error:', error);
      return false;
    }
  };

  const listFiles = async (uri?: string): Promise<FileInfo[]> => {
    try {
      return await StorageManager.listFiles(uri || null);
    } catch (error) {
      console.error('List files error:', error);
      return [];
    }
  };

  const openWith = async (uri: string): Promise<boolean> => {
    try {
      await StorageManager.openFile(uri);
      return true;
    } catch (error) {
      console.error('Open file error:', error);
      return false;
    }
  };

  return { requestAccess, listFiles, openWith };
};
