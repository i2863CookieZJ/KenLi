package com.sobey.cloud.webtv.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * 序列化工具类 Created by lazy on 13-12-28.
 */
public class BeanUtil {

	/**
	 * 序列化集合和为字符
	 * 
	 * @param o
	 * @return
	 * @throws IOException
	 */
	public static String serializable(Serializable o) throws IOException {
		ArrayList<Serializable> list = new ArrayList<Serializable>();
		list.add(o);
		return serializable(list);
	}

	/**
	 * 序列化集合和为字符
	 *
	 * @param o
	 * @param file
	 * @throws IOException
	 */
	public static void serializable(Serializable o, File file) throws IOException {
		ArrayList<Serializable> list = new ArrayList<Serializable>();
		list.add(o);
		serializable(list, file);
	}

	/**
	 * 序列化集合和为字符
	 *
	 * @param serializables
	 * @return
	 * @throws IOException
	 */
	public static String serializable(ArrayList<Serializable> serializables) throws IOException {
		ByteArrayOutputStream baos = (ByteArrayOutputStream) serializable(serializables, new ByteArrayOutputStream());
		return java.net.URLEncoder.encode(baos.toString("ISO-8859-1"), "UTF-8");
	}

	/**
	 * 序列化集合和为字符
	 *
	 * @param serializables
	 * @param outputStream
	 * @return
	 * @throws IOException
	 */
	public static OutputStream serializable(ArrayList<Serializable> serializables, OutputStream outputStream)
			throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(outputStream);
		for (Serializable s : serializables) {
			// 序列化对象
			out.writeObject(s);
		}
		out.writeObject(null);// 解决循环读取时，EOF的关键，加入一个空的对象
		out.close();
		outputStream.close();
		return outputStream;
	}

	/**
	 * 序列化集合和为字符
	 *
	 * @param serializables
	 * @param file
	 * @throws IOException
	 */
	public static void serializable(ArrayList<Serializable> serializables, File file) throws IOException {
		serializable(serializables, new FileOutputStream(file));
	}

	/**
	 * 反序列化回多个对象
	 *
	 * @param serializables
	 * @param <T>
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static <T extends Serializable> ArrayList<T> unSerializable(String serializables)
			throws IOException, ClassNotFoundException {
		ByteArrayInputStream bais = new ByteArrayInputStream(
				java.net.URLDecoder.decode(serializables, "UTF-8").getBytes("ISO-8859-1"));
		return unSerializable(bais);
	}

	/**
	 * 反序列化回多个对象
	 *
	 * @param file
	 * @param <T>
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static <T extends Serializable> ArrayList<T> unSerializable(File file)
			throws IOException, ClassNotFoundException {
		return unSerializable(new FileInputStream(file));
	}

	/**
	 * 反序列化回多个对象
	 *
	 * @param input
	 * @param <T>
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static <T extends Serializable> ArrayList<T> unSerializable(InputStream input)
			throws IOException, ClassNotFoundException {
		ArrayList<T> serializable = new ArrayList<T>();
		ObjectInputStream in = new ObjectInputStream(input);
		Object obj = null;
		while ((obj = in.readObject()) != null) {
			serializable.add((T) obj);
		}
		in.close();
		input.close();
		return serializable;
	}

}
