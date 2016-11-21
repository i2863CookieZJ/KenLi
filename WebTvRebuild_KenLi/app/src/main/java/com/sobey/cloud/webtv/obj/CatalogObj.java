package com.sobey.cloud.webtv.obj;

public class CatalogObj {
	public int index;
	public String id;
	public String name;
	public int resId;
	public String url;
	public CatalogType type;

	public CatalogObj() {

	}

	public CatalogObj(int index, String id, String name, int resId, CatalogType type) {
		this.index = index;
		this.id = id;
		this.name = name;
		this.resId = resId;
		this.type = type;
	}
}
