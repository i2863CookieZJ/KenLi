package com.dylan.common.utils;

import java.io.File;

public class FileManager {
	/**
	 * delete directory and all files in it
	 * 
	 * @param folderPath
	 *            String absolutepath
	 */
	public static void deleteDirectory(String folderPath) {
		try {
			File file = new File(folderPath);
			if (!file.exists()) {
				return;
			}
			deleteAllFile(folderPath);
			File lastFile = new File(folderPath);
			if (lastFile.exists()) {
				lastFile.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * detete all files in directory
	 * 
	 * @param path
	 *            String absolutepath
	 */
	public static void deleteAllFile(String path) {
		try {
			File file = new File(path);
			if (!file.exists()) {
				return;
			}
			if (file.isFile()) {
				file.delete();
			} else if (file.isDirectory()) {
				String[] tempList = file.list();
				File temp = null;
				for (int i = 0; i < tempList.length; i++) {
					if (path.endsWith(File.separator)) {
						temp = new File(path + tempList[i]);
					} else {
						temp = new File(path + File.separator + tempList[i]);
					}
					if (temp.isFile()) {
						temp.delete();
					}
					if (temp.isDirectory()) {
						deleteAllFile(path + "/" + tempList[i]);
						deleteDirectory(path + "/" + tempList[i]);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
