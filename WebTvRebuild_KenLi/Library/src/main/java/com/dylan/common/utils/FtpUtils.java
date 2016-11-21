package com.dylan.common.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
/**
 * 上传下载的类
 * @author lgx
 *
 */
public class FtpUtils {
	public enum UploadStatus {
		Create_Directory_Fail, // 远程服务器相应目录创建失败
		Create_Directory_Success, // 远程服务器闯将目录成功
		Upload_New_File_Success, // 上传新文件成功
		Upload_New_File_Failed, // 上传新文件失败
		File_Exits, // 文件已经存在
		Remote_Bigger_Local, // 远程文件大于本地文件
		Upload_From_Break_Success, // 断点续传成功
		Upload_From_Break_Failed, // 断点续传失败
		Delete_Remote_Faild, // 删除远程文件失败
		Upload_Stop; // 上传停止
	}

	public enum DownloadStatus {
		Remote_File_Noexist, // 远程文件不存在
		Local_Bigger_Remote, // 本地文件大于远程文件
		Download_From_Break_Success, // 断点下载文件成功
		Download_From_Break_Failed, // 断点下载文件失败
		Download_New_Success, // 全新下载文件成功
		Download_New_Failed, // 全新下载文件失败
		Download_Stop; // 下载停止
	}

	private String TAG = "FtpUtils";
	private Handler handler;
	public FTPClient ftpClient = new FTPClient();
	public boolean uploadStatus = true;
	public boolean downloadStatus = true;

	public FtpUtils() {
	}

	public FtpUtils(Handler handler) {
		this.handler = handler;
	}

	public boolean connect(String hostname, int port, String username, String password) throws IOException {
		try {
			Log.d(TAG, "start connect...  hostname:" + hostname + " port:" + port + " username:" + username + " password:" + password);
			ftpClient.setControlEncoding("utf-8");// 设置字符集，必须在connect之前设置
			ftpClient.connect(hostname, port);// 地址和端口
			if (ftpClient.login(username, password)) {
				Log.d(TAG, "connect success");
				return true;
			}
			Log.d(TAG, "connect fail");
			disconnect();
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	public void disconnect() throws IOException {
		if (ftpClient.isConnected()) {
			ftpClient.disconnect();
		}
	}

	/**
	 * 下载文件
	 * 
	 * @param remote
	 * @param local
	 * @return
	 * @throws IOException
	 */
	public DownloadStatus download(String remote, String local) throws IOException {
		DownloadStatus result;

		ftpClient.enterLocalActiveMode();// 设置主动模式
		// ftpClient.enterLocalPassiveMode();// 设置被动模式
		ftpClient.setFileType(FTP.BINARY_FILE_TYPE); // 设置以二进制方式传输

		// 检查远程文件是否存在
		FTPFile[] files = ftpClient.listFiles(new String(remote.getBytes("utf-8"), "utf-8"));
		if (files.length != 1) {
			Log.d(TAG, "远程文件不存在");
			return DownloadStatus.Remote_File_Noexist;
		}

		long lRemoteSize = files[0].getSize();
		File f = new File(local);
		// 本地存在文件，进行断点下载
		if (f.exists()) {
			long localSize = f.length();
			// 判断本地文件大小是否大于远程文件大小
			if (localSize >= lRemoteSize) {
				Log.d(TAG, "本地文件大于远程文件，下载中止");
				return DownloadStatus.Local_Bigger_Remote;
			}
			// 进行断点续传，并记录状态
			FileOutputStream out = new FileOutputStream(f, true);
			ftpClient.setRestartOffset(localSize);
			InputStream in = ftpClient.retrieveFileStream(new String(remote.getBytes("utf-8"), "utf-8"));
			byte[] bytes = new byte[1024];
			long step = lRemoteSize / 100;
			long process = localSize / step;
			int c;
			while (downloadStatus && (c = in.read(bytes)) != -1) {// 断开连接后不到10秒，报错
				out.write(bytes, 0, c);
				localSize += c;
				long nowProcess = localSize / step;
				if (nowProcess > process) {
					process = nowProcess;
					if (process % 10 == 0) {
						Log.d(TAG, "下载进度：" + process);
					}
				}
			}
			in.close();
			out.close();
			boolean isDo = ftpClient.completePendingCommand();
			if (downloadStatus) {
				if (isDo) {
					result = DownloadStatus.Download_From_Break_Success;
				} else {
					result = DownloadStatus.Download_From_Break_Failed;
				}
			} else {
				result = DownloadStatus.Download_Stop;
			}
		} else {
			OutputStream out = new FileOutputStream(f);
			InputStream in = ftpClient.retrieveFileStream(new String(remote.getBytes("utf-8"), "utf-8"));
			byte[] bytes = new byte[1024];
			long step = lRemoteSize / 100;
			long process = 0;
			long localSize = 0L;
			int c;
			while (downloadStatus && (c = in.read(bytes)) != -1) {
				out.write(bytes, 0, c);
				localSize += c;
				long nowProcess = localSize / step;
				if (nowProcess > process) {
					process = nowProcess;
					if (process % 10 == 0) {
						Log.d(TAG, "下载进度：" + process);
					}
				}
			}
			in.close();
			out.close();
			boolean upNewStatus = ftpClient.completePendingCommand();
			if (downloadStatus) {
				if (upNewStatus) {
					result = DownloadStatus.Download_New_Success;
				} else {
					result = DownloadStatus.Download_New_Failed;
				}
			} else {
				result = DownloadStatus.Download_Stop;
			}
		}
		return result;
	}

	/**
	 * 停止当前下载任务
	 */
	public void stopDownloadFile() {
		downloadStatus = false;
	}

	/**
	 * 上传文件
	 * 
	 * @param local
	 *            本地文件路径
	 * @param remote
	 *            远程文件地址
	 * @return
	 * @throws IOException
	 */
	public UploadStatus upload(String local, String remote) throws IOException {
		UploadStatus result;

		uploadStatus = true;
		ftpClient.enterLocalPassiveMode(); // 设置PassiveMode传输
		// ftpClient.enterLocalActiveMode(); // 设置ActiveMode传输
		ftpClient.setBufferSize(1024);
		ftpClient.setControlEncoding("UTF-8");
		ftpClient.setFileType(FTP.BINARY_FILE_TYPE); // 设置以二进制流的方式传输

		// 创建服务器远程目录结构，创建失败直接返回
		String remoteFileName = remote;
		if (remote.contains("/")) {
			remoteFileName = remote.substring(remote.lastIndexOf("/") + 1);
			if (CreateDirecroty(remote, ftpClient) == UploadStatus.Create_Directory_Fail) {
				return UploadStatus.Create_Directory_Fail;
			}
		}
		Log.d(TAG, "远程文件路径：" + new String(remoteFileName.getBytes("utf-8"), "utf-8"));

		// 检查远程是否存在文件
		FTPFile[] files = ftpClient.listFiles(new String(remoteFileName.getBytes("utf-8"), "utf-8"));
		if (files.length == 1) {
			long remoteSize = files[0].getSize();
			File f = new File(local);
			long localSize = f.length();
			if (remoteSize == localSize) {
				Log.d(TAG, "服务器中文件等于要上传文件，所以不上传");
				return UploadStatus.File_Exits;
			} else if (remoteSize > localSize) {
				Log.d(TAG, "服务器中文件大于要上传文件，所以不上传");
				return UploadStatus.Remote_Bigger_Local;
			}
			// 尝试移动文件内读取指针,实现断点续传
			result = uploadFile(remoteFileName, f, ftpClient, remoteSize);
			// 如果断点续传没有成功，则删除服务器上文件，重新上传
			if (result == UploadStatus.Upload_From_Break_Failed) {
				if (!ftpClient.deleteFile(remoteFileName)) {
					return UploadStatus.Delete_Remote_Faild;
				}
				result = uploadFile(remoteFileName, f, ftpClient, 0);
			}
		} else {
			result = uploadFile(remoteFileName, new File(local), ftpClient, 0);
		}
		return result;
	}

	/**
	 * 递归创建远程服务器目录
	 * 
	 * @param remote
	 *            远程服务器文件绝对路径
	 * @param ftpClient
	 *            FTPClient 对象
	 * @return 目录创建是否成功
	 * @throws IOException
	 */
	public UploadStatus CreateDirecroty(String remote, FTPClient ftpClient) throws IOException {
		UploadStatus status = UploadStatus.Create_Directory_Success;
		String directory = remote.substring(0, remote.lastIndexOf("/") + 1);
		Log.d(TAG, "创建远程目录名：" + new String(directory.getBytes("utf-8"), "utf-8"));
		if (!directory.equalsIgnoreCase("/") && !ftpClient.changeWorkingDirectory(new String(directory.getBytes("utf-8"), "utf-8"))) {
			// 如果远程目录不存在，则递归创建远程服务器目录
			int start = 0;
			int end = 0;
			if (directory.startsWith("/")) {
				start = 1;
			} else {
				start = 0;
			}
			end = directory.indexOf("/", start);
			while (true) {
				String subDirectory = new String(remote.substring(start, end).getBytes("utf-8"), "utf-8");
				if (!ftpClient.changeWorkingDirectory(subDirectory)) {
					if (ftpClient.makeDirectory(subDirectory)) {
						ftpClient.changeWorkingDirectory(subDirectory);
					} else {
						Log.d(TAG, "创建目录失败");
						return UploadStatus.Create_Directory_Fail;
					}
				}
				start = end + 1;
				end = directory.indexOf("/", start);
				// 检查所有目录是否创建完毕
				if (end <= start) {
					break;
				}
			}
		}
		return status;
	}

	/**
	 * 上传文件到服务器,新上传和断点续传
	 * 
	 * @param remoteFile
	 *            远程文件名，在上传之前已经将服务器工作目录做了改变
	 * @param localFile
	 *            本地文件 File句柄，绝对路径
	 * @param ftpClient
	 *            FTPClient 引用
	 * @param processStep
	 *            需要显示的处理进度步进值
	 * @return
	 * @throws IOException
	 */
	public UploadStatus uploadFile(String remoteFile, File localFile, FTPClient ftpClient, long remoteSize) throws IOException {
		UploadStatus status;
		// 显示进度的上传
		long step = localFile.length() / 100;
		long process = 0;
		long localreadbytes = 0L;
		RandomAccessFile raf = new RandomAccessFile(localFile, "r");
		OutputStream out = ftpClient.appendFileStream(new String(remoteFile.getBytes("utf-8"), "utf-8"));
		// 断点续传
		if (remoteSize > 0) {
			ftpClient.setRestartOffset(remoteSize);
			process = remoteSize / step;
			raf.seek(remoteSize);
			localreadbytes = remoteSize;
		}
		byte[] bytes = new byte[1024];
		int c;
		long oldTime = 0;
		long nowTime = 0;
		while (uploadStatus && (c = raf.read(bytes)) != -1) {
			out.write(bytes, 0, c);
			localreadbytes += c;
			nowTime = System.currentTimeMillis();
			if (nowTime - oldTime > 5000) {
				oldTime = nowTime;
				process = localreadbytes / step;
				if (handler != null) {
					Message msg = handler.obtainMessage();
					msg.arg1 = (int) process;
					handler.sendMessage(msg);
				}
				Log.d(TAG, "上传进度:" + process);
			}
		}
		out.flush();
		raf.close();
		out.close();
		boolean result = ftpClient.completePendingCommand();
		if (uploadStatus) {
			if (remoteSize > 0) {
				status = result ? UploadStatus.Upload_From_Break_Success : UploadStatus.Upload_From_Break_Failed;
			} else {
				status = result ? UploadStatus.Upload_New_File_Success : UploadStatus.Upload_New_File_Failed;
			}
		} else {
			status = UploadStatus.Upload_Stop;
		}
		return status;
	}

	/**
	 * 停止当前上传任务
	 */
	public void stopUploadFile() {
		uploadStatus = false;
	}

}
