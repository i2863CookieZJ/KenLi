package com.sobey.cloud.webtv.bean;

import java.io.Serializable;

public class PhotoModel implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public String path;
		public boolean choosed;
		public int resId;
		public int type = 0;
		public boolean visiable;
		public boolean showDel = true;
	}