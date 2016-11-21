package com.sobey.cloud.webtv.utils;


import android.text.TextUtils;

public final class BufferUtil {

	{
		String path=FileUtil.CACHE+"home";
		if(FileUtil.hasFile(path)==false)
			FileUtil.createDirectory(FileUtil.HOME_CACHE_DIR);
	}
	private BufferUtil() 
	{
	}
	
	public static void saveTextData(String fileName,String data)
	{
		if(FileUtil.hasStorage())
		{
			String path=FileUtil.HOME_CACHE_DIR+"/"+fileName;
			if(FileUtil.hasFile(path)==false)
				FileUtil.createFilePath(FileUtil.HOME_CACHE_DIR, fileName, FileUtil.TXT);
			boolean saved=FileUtil.saveTextFile(path, data);
			if(!saved)
			{
				FileUtil.saveTextDataDir(fileName, data);
			}
		}
		else
		{
			FileUtil.saveTextDataDir(fileName, data);
		}
	}
	
	public static String getTextData(String fileName)
	{
		if(FileUtil.hasStorage())
		{
			String strText=FileUtil.readTextFile(FileUtil.HOME_CACHE_DIR+"/"+fileName);
			if(TextUtils.isEmpty(strText))
			{
				strText=FileUtil.readTextFromDataDir(fileName);
			}
			return strText;
		}
		else
		{
			String strText=FileUtil.readTextFromDataDir(fileName);
			return strText;
		}
	}

}
