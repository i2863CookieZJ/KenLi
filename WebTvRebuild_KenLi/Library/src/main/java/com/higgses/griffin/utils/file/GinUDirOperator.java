package com.higgses.griffin.utils.file;

import java.io.File;

/**
 * 文件操作类，主要是迭代文件夹以及目录
 * 
 * @author Carlton
 *
 */
public class GinUDirOperator
{
	private IFileIteration mFileIteration;
	private File mTargetFile;

	public GinUDirOperator(File file, IFileIteration fileIteration)
	{
		mTargetFile = file;
		mFileIteration = fileIteration;
	}

	/**
	 * 迭代指定目录
	 * 
	 * @param file
	 */
	public void iterationFile(File file)
	{
		if (file == null)
		{
			return;
		}
		File[] list = file.listFiles();
		for (int i = 0; list != null && i < list.length; ++i)
		{
			File isFile = list[i];
			if (isFile.isDirectory() && !mFileIteration.onDir(isFile))
			{
				iterationFile(isFile);
			}
			if (isFile.exists() && isFile.isFile())
			{
				if (mFileIteration.onFile(isFile))
				{
					continue;
				}
			}
		}
	}

	/**
	 * 开始迭代
	 */
	public void iteration()
	{
		if (mTargetFile != null)
		{
			iterationFile(mTargetFile);
			mFileIteration.onFinish();
		}
	}
}
