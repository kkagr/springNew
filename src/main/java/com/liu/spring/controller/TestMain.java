package com.liu.spring.controller;

import java.io.File;
import java.net.URL;

/**
* @author kkagr
* @version ����ʱ�䣺2018��5��6�� ����12:45:01
* ��˵��
*/

public class TestMain {
    public static void main(String[] args) {
        TestMain t = new TestMain();
        String packageName="com.liu.spring";
        String packageName1= packageName.replaceAll(".", "/");
        System.out.println(t.getClass().getClassLoader().getResource("com"));
        URL url = t.getClass().getClassLoader().getResource(packageName);
        File classDir = new File(url.getFile());
		for(File file:classDir.listFiles()){
			if(file.isDirectory()){
				System.out.println(1);
			}else{
				String className = packageName+"."+file.getName().replaceAll(".", "");
			}
		}
    }
}
