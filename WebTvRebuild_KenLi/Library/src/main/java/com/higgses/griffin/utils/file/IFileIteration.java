package com.higgses.griffin.utils.file;

import java.io.File;

/**
 * 文件迭代器
 * 
 * @author Carlton
 *
 */
public interface IFileIteration
{
	/**
	 * 当前文件是一个文件
	 * @param file
	 */
	public boolean onFile(File file);
	/**
	 * 当前文件是一个目录
	 * @param dir
	 * @return
	 */
	public boolean onDir(File dir);
	
	/**
	 * 遍历完成
	 */
	public void onFinish();
}
