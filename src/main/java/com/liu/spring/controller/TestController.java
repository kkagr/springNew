package com.liu.spring.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.connector.Request;

import com.liu.spring.annotation.LCHAutorited;
import com.liu.spring.annotation.LCHController;
import com.liu.spring.annotation.LCHRequestMapping;
import com.liu.spring.annotation.LCHRequestParam;
import com.liu.spring.service.TestService;

/**
* @author kkagr
* @version 创建时间：2018年5月6日 上午10:21:19
* 类说明
*/
@LCHController
@LCHRequestMapping("/test")
public class TestController {
	@LCHAutorited
	private TestService testService;
	@LCHRequestMapping("/json_testMethod")
	public void testMethod(HttpServletRequest requset,
						   HttpServletResponse response,
						   @LCHRequestParam(value ="name") String name) throws IOException{
		/**使用OutputStream输出中文注意问题：
		 * 在服务器端，数据是以哪个码表输出的，那么就要控制客户端浏览器以相应的码表打开，
		 * 比如：outputStream.write("中国".getBytes("UTF-8"));//使用OutputStream流向客户端浏览器输出中文，以UTF-8的编码进行输出
		 * 此时就要控制客户端浏览器以UTF-8的编码打开，否则显示的时候就会出现中文乱码，那么在服务器端如何控制客户端浏览器以以UTF-8的编码显示数据呢？
		 * 可以通过设置响应头控制浏览器的行为，例如：
		 * response.setHeader("content-type", "text/html;charset=UTF-8");//通过设置响应头控制浏览器以UTF-8的编码显示数据
		*/
		String data ="中国";
		OutputStream outputStream = response.getOutputStream();
		response.setHeader("content-type", "text/html;charset=UTF-8");//通过设置响应头控制浏览器以UTF-8的编码显示数据，如果不加这句话，那么浏览器显示的将是乱码
        /**
         * data.getBytes()是一个将字符转换成字节数组的过程，这个过程中一定会去查码表，
         * 如果是中文的操作系统环境，默认就是查找查GB2312的码表，
         * 将字符转换成字节数组的过程就是将中文字符转换成GB2312的码表上对应的数字
         * 比如： "中"在GB2312的码表上对应的数字是98
         *         "国"在GB2312的码表上对应的数字是99
         */
        /**
         * getBytes()方法如果不带参数，那么就会根据操作系统的语言环境来选择转换码表，如果是中文操作系统，那么就使用GB2312的码表
         */
        byte[] dataByteArr = data.getBytes("UTF-8");//将字符转换成字节数组，指定以UTF-8编码进行转换
        outputStream.write(dataByteArr);//使用OutputStream流向客户端输出字节数组
		
	}

}
