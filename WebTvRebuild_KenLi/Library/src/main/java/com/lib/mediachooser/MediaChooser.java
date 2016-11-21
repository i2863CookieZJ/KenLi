/*
 * Copyright 2013 - learnNcode (learnncode@gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.lib.mediachooser;


public class MediaChooser {

	/**
	 * Video file selected broadcast action.
	 */
	public static final String VIDEO_SELECTED_ACTION_FROM_MEDIA_CHOOSER = "lNc_videoSelectedAction"; 

	/**
	 *  Image file selected broadcast action.
	 */
	public static final String IMAGE_SELECTED_ACTION_FROM_MEDIA_CHOOSER = "lNc_imageSelectedAction"; 

	/**
	 * To set file size limit for image selection.
	 * 
	 * @param size
	 *  			int file size in mb.
	 *  			default is set to 20 mb.
	 */
	public static void setImageSize(int size) {
		MediaChooserConstants.SELECTED_IMAGE_SIZE_IN_MB = size;
	}

	/**
	 * To set file size limit for video selection.
	 * 
	 * @param size
	 *  			int file size in mb.
	 *  			default is set to 20 mb.
	 */
	public static void setVideoSize(int size) {
		MediaChooserConstants.SELECTED_VIDEO_SIZE_IN_MB = size;
	}

	/**
	 * To set number of image that can be selected. 
	 * 
	 * @param limit
	 *   			int value.
	 *   			Default is 100.
	 */
	public static void setSelectionImageLimit(int limit) {
		MediaChooserConstants.MAX_IMAGE_LIMIT = limit;
	}
	
	/**
	 * To set number of video that can be selected. 
	 * 
	 * @param limit
	 *   			int value.
	 *   			Default is 100.
	 */
	public static void setSelectionVideoLimit(int limit) {
		MediaChooserConstants.MAX_VIDEO_LIMIT = limit;
	}
	
	/**
	 * Show selected num or remains
	 */
	public static void showSelectedNum(boolean showFlag) {
		MediaChooserConstants.SHOW_SELECTED_NUM = showFlag;
	}

	/**
	 * To display images only.
	 */
	public static void showOnlyImageTab() {
		MediaChooserConstants.fileType = "image";
	}

	/**
	 * To display video and images.
	 */
	public static void showImageVideoTab() {
		MediaChooserConstants.fileType = "all";
	}

	/**
	 * To display videos only.
	 */
	public static void showOnlyVideoTab() {
		MediaChooserConstants.fileType = "video";
	}
	
	/**
	 * set footerbar ok btn's text
	 */
	public static void setFooterBarOkText(String string) {
		MediaChooserConstants.footerBarOkString = string;
	}
	
	/**
	 * set footerbar cancel btn's text
	 */
	public static void setFooterBarCancelText(String string) {
		MediaChooserConstants.footerBarCancelString = string;
	}

}
