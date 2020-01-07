package com.star.common.test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * 自定义类加载器（遵从双亲委派模型）
 * @date 2020年1月7日
 * @version 1.0
 */
public class CustomClassLoader extends ClassLoader {
	// 自定义类加载器名称
	private String name;
	// 动态加载的外部class根路径
	private String classPath;
	
	public CustomClassLoader(String name) {
		super();
		this.name = name;
	}
	
	public CustomClassLoader(ClassLoader parent, String name) {
		super(parent);
		this.name = name;
	}

	@Override
	public Class<?> findClass(String className) {
		// 自定义类加载器此方法必须重写
		byte[] dataBytes = null;
		InputStream is = null;
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			className = className.replaceAll("[.]", "/");
			is = new FileInputStream(new File(this.classPath + "/" + className + ".class"));
			int b = 0;
			while (-1 != (b = is.read())) {
				baos.write(b);
			}
			is.close();
			dataBytes = baos.toByteArray();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
				baos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return this.defineClass(name, dataBytes, 0, dataBytes.length);
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
	public String getClassPath() {
		return this.classPath;
	}
	
	public void setClassPath(String classPath) {
		this.classPath = classPath;
	}
	
	
//	public static void main(String[] args) {
//		CustomClassLoader myClassLoader = new CustomClassLoader("myClassLoader");
//		myClassLoader.setClassPath("E:/dynamic");
//		try {
//			// 通过loadClass方法调用
//			//Class<?> clz = myClassLoader.loadClass("com.weijuju.base.common.test.DynamicClass");
//			
//			// 这个是Java自带的类加载器
//			//Class<?> clz = Class.forName("com.weijuju.base.common.test.DynamicClass");
//			// 需要引入包含IDynamicClass和DynamicClass的class，其实就是jar包
//			IDynamicClass dynamicClass = (IDynamicClass) clz.newInstance();
//			dynamicClass.run();
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		} catch (InstantiationException e) {
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			e.printStackTrace();
//		}
//	}
	
}
