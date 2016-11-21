package com.sobey.cloud.webtv.utils;


public class SobeyFileNameConstants {
	
	public static enum FileName
	{
		tushuo,news,today,zhibo,tuwen,zhuanti,videolist,dianbo
	}

	public static String NAME_GROUP = FileUtil.createFilePath(FileUtil.CONTENT, "group", FileUtil.TXT);
	/**
	 * 首页关注中图说缓存
	 * */
	public static String TUSHUO_CACHE= FileUtil.createFilePath(FileUtil.HOME_CACHE_DIR, FileName.tushuo.name(), FileUtil.TXT);
	/**
	 * 首页新闻缓存
	 * */
	public static String NEWS_CACHE= FileUtil.createFilePath(FileUtil.HOME_CACHE_DIR, FileName.news.name(), FileUtil.TXT);
	/**
	 * 首页今日搜索缓存
	 * */
	public static String TODAY_CACHE= FileUtil.createFilePath(FileUtil.HOME_CACHE_DIR, FileName.today.name(), FileUtil.TXT);
	/**
	 * 首页直播缓存
	 * */
	public static String ZHIBO_CACHE= FileUtil.createFilePath(FileUtil.HOME_CACHE_DIR, FileName.zhibo.name(), FileUtil.TXT);
	/**
	 * 首页视频点播缓存
	 * */
	public static String DIANBO_CACHE= FileUtil.createFilePath(FileUtil.HOME_CACHE_DIR, FileName.dianbo.name(), FileUtil.TXT);
	/**
	 * 首页资讯专题缓存
	 * */
	public static String ZHUANTI_CACHE= FileUtil.createFilePath(FileUtil.HOME_CACHE_DIR, FileName.zhuanti.name(), FileUtil.TXT);
	/**
	 * 首页图文缓存
	 * */
	public static String TUWEN_CACHE= FileUtil.createFilePath(FileUtil.HOME_CACHE_DIR, FileName.tuwen.name(), FileUtil.TXT);
	/**
	 * 首页资讯视频缓存
	 * */
	public static String VIDEO_LIST_CACHE= FileUtil.createFilePath(FileUtil.HOME_CACHE_DIR, FileName.videolist.name(), FileUtil.TXT);
}

