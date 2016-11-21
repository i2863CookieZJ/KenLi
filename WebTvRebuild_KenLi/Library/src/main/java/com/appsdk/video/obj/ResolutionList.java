package com.appsdk.video.obj;

import java.util.ArrayList;

public class ResolutionList {
	private ArrayList<ResolutionObj> resolutions;
	private int resolutionIndex;

	public ResolutionList(ArrayList<ResolutionObj> resolutions, int resolutionIndex) throws Exception {
		if (resolutions == null || resolutions.size() <= 0) {
			throw new Exception("title and resolutions should be initialized");
		}
		if (resolutionIndex > resolutions.size() || resolutionIndex < 0) {
			throw new Exception("index is invalid");
		}
		this.setResolutions(resolutions);
		this.resolutionIndex = resolutionIndex;
	}

	public int getResolutionIndex() {
		return resolutionIndex;
	}

	public void setResolutionIndex(int resolutionIndex) {
		this.resolutionIndex = resolutionIndex;
	}

	public ArrayList<ResolutionObj> getResolutions() {
		return resolutions;
	}

	public void setResolutions(ArrayList<ResolutionObj> resolutions) {
		this.resolutions = resolutions;
	}
}
