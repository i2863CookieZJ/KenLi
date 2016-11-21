package com.sobey.cloud.webtv.fragment.utils;
import java.io.Serializable;

/**
 * �첽����ִ��
 * 
 * @author kaifa
 * 
 */
public interface IAsynTask {

	public Serializable run();

	public void updateUI(Serializable runData);
	
}
