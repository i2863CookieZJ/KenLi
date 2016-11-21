package com.sobey.cloud.webtv.obj;

import java.util.ArrayList;

import org.json.JSONObject;

public class CacheData {
	private int pageIndex = 0;
	private String catalogId = null;
	private ArrayList<JSONObject> articles = new ArrayList<JSONObject>();
	private int total = 0;

	public CacheData(int pageIndex, String catalogId, ArrayList<JSONObject> articles, int total) {
		this.pageIndex = pageIndex;
		this.catalogId = catalogId;
		this.articles = articles;
		this.total = total;
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public String getCatalogId() {
		return catalogId;
	}

	public void setCatalogId(String catalogId) {
		this.catalogId = catalogId;
	}

	public ArrayList<JSONObject> getArticles() {
		return articles;
	}

	public void setArticles(ArrayList<JSONObject> articles) {
		this.articles = articles;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}
	
}
