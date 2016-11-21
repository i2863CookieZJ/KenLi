package com.higgses.griffin.volley.inputstreambox;

import com.higgses.griffin.volley.NetworkResponse;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.higgses.griffin.volley.Response;
import com.higgses.griffin.volley.Response.ErrorListener;
import com.higgses.griffin.volley.Response.Listener;
import com.higgses.griffin.volley.SystemTools;
import com.higgses.griffin.volley.toolbox.HttpHeaderParser;

public class FileRequest extends StreamRequest<File> {
	/**
	 * 保存文件
	 */
	private String directory;
	private String fileName;
	private String suffixal;

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getSuffixal() {
		return suffixal;
	}

	public void setSuffixal(String suffixal) {
		this.suffixal = suffixal;
	}

	public FileRequest(int method, String url, String directory,
			String filename, String suffixal, Listener<File> listener,
			ErrorListener errorListener) {
		super(method, url, listener, errorListener);
		this.directory = directory;
		this.fileName = filename;
		this.suffixal = suffixal;
	}

	@SuppressWarnings("resource")
	@Override
	protected Response<File> parseNetworkResponse(NetworkResponse response) {
		File result = null;
		BufferedOutputStream stream = null;
		try {
			result = SystemTools.getInstance(null).createFile(directory,
					fileName, suffixal);
			FileOutputStream out = new FileOutputStream(result);
			stream = new BufferedOutputStream(out);
			stream.write(response.data);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (stream != null) {
					stream.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return Response.success(
				new File(result.getAbsoluteFile(), result.getName()),
				HttpHeaderParser.parseCacheHeaders(response));
	}
}
