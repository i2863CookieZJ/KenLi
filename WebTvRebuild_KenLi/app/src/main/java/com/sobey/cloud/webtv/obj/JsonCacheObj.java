package com.sobey.cloud.webtv.obj;

public class JsonCacheObj {
	private String id;
	private String type;
	private Object content;
	private long deadLine;

	public JsonCacheObj() {
	}

	public JsonCacheObj(String id, String type, Object content) {
		this.id = id;
		this.type = type;
		this.content = content;
	}
	
	public JsonCacheObj(String id, String type, Object content, long deadLine) {
		this.id = id;
		this.type = type;
		this.content = content;
		this.setDeadLine(deadLine);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Object getContent() {
		return content;
	}

	public void setContent(Object content) {
		this.content = content;
	}

	public long getDeadLine() {
		return deadLine;
	}

	public void setDeadLine(long deadLine) {
		this.deadLine = deadLine;
	}
}
