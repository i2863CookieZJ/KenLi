package com.sobey.cloud.webtv.utils;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.sobey.cloud.webtv.bean.GroupRequestMananger;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class UpLoadTask {

	private int mTaskId;
	private Map<Integer, String> map = null;
	private Handler mHandler;
	private int totalSize;
	private final String TAG = this.getClass().getName();
	private List<String> upLoadfiles;

	public UpLoadTask(Map<Integer, String> map, Handler handler,
			List<String> upLoadfiles) {
		this.mHandler = handler;
		this.map = map;
		this.upLoadfiles = upLoadfiles;
		this.totalSize = upLoadfiles.size();

	}

	public int sendPices() {
		int successId = -1;
		for (int i = 0; i < upLoadfiles.size(); i++) {
			mTaskId = i;
			String fileName = upLoadfiles.get(i);
			String uploadFilePaht = BaseUtil.compressPhoto(fileName);
			String result = sendFile(uploadFilePaht);
			try {
				successId = Integer.parseInt(result);
			} catch (NumberFormatException e) {
				successId = -1;
			}
			if (successId < 0) {
				break;
			}
			sendMsg();
		}
		return successId;
	}

	private String sendFile(String filePath) {
		File file = new File(filePath);
		Log.i(TAG, "要上传文件大小：" + file.length());

		Log.i(TAG, "task:--" + mTaskId + "--开始执行>>");
		String result = GroupRequestMananger.getInstance()
				.sendHttpMultipartRequest(file);
		map.put(mTaskId, result);
		Log.i(TAG, "task:--" + mTaskId + "--执行结束,result:" + result);
//		FileUtil.delFile(file);
		return result;
	}

	private void sendMsg() {
		Message msg = new Message();
		msg.what = SobeyConstants.CODE_FOR_UPLOAD_FILE_UPLOADING;
		Bundle bundle = new Bundle();
		// 已上传完的大小
		bundle.putInt("uploadedSize", map.size());
		// 总大小
		bundle.putInt("totalSize", totalSize);
		msg.setData(bundle);
		mHandler.sendMessage(msg);
	}
}
