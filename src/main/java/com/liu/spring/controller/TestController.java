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
* @version ����ʱ�䣺2018��5��6�� ����10:21:19
* ��˵��
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
		/**ʹ��OutputStream�������ע�����⣺
		 * �ڷ������ˣ����������ĸ��������ģ���ô��Ҫ���ƿͻ������������Ӧ�����򿪣�
		 * ���磺outputStream.write("�й�".getBytes("UTF-8"));//ʹ��OutputStream����ͻ��������������ģ���UTF-8�ı���������
		 * ��ʱ��Ҫ���ƿͻ����������UTF-8�ı���򿪣�������ʾ��ʱ��ͻ�����������룬��ô�ڷ���������ο��ƿͻ������������UTF-8�ı�����ʾ�����أ�
		 * ����ͨ��������Ӧͷ�������������Ϊ�����磺
		 * response.setHeader("content-type", "text/html;charset=UTF-8");//ͨ��������Ӧͷ�����������UTF-8�ı�����ʾ����
		*/
		String data ="�й�";
		OutputStream outputStream = response.getOutputStream();
		response.setHeader("content-type", "text/html;charset=UTF-8");//ͨ��������Ӧͷ�����������UTF-8�ı�����ʾ���ݣ����������仰����ô�������ʾ�Ľ�������
        /**
         * data.getBytes()��һ�����ַ�ת�����ֽ�����Ĺ��̣����������һ����ȥ�����
         * ��������ĵĲ���ϵͳ������Ĭ�Ͼ��ǲ��Ҳ�GB2312�����
         * ���ַ�ת�����ֽ�����Ĺ��̾��ǽ������ַ�ת����GB2312������϶�Ӧ������
         * ���磺 "��"��GB2312������϶�Ӧ��������98
         *         "��"��GB2312������϶�Ӧ��������99
         */
        /**
         * getBytes()�������������������ô�ͻ���ݲ���ϵͳ�����Ի�����ѡ��ת�������������Ĳ���ϵͳ����ô��ʹ��GB2312�����
         */
        byte[] dataByteArr = data.getBytes("UTF-8");//���ַ�ת�����ֽ����飬ָ����UTF-8�������ת��
        outputStream.write(dataByteArr);//ʹ��OutputStream����ͻ�������ֽ�����
		
	}

}
