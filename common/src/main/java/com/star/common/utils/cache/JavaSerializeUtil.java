package com.star.common.utils.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


public class JavaSerializeUtil {
	
	public static <T extends Serializable> byte[] serialize(T object) throws Exception{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(object);
		return baos.toByteArray();
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Serializable> T unSerialize(byte[] bytes) throws Exception{
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		ObjectInputStream ois = new ObjectInputStream(bais);
		return (T) ois.readObject();
	}

}
