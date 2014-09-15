package com.kdweibo.wxtagent.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class SHAUtils {
	/**
	 * 将 appSecret 、timestamp、nonce三个参数进行字典序排序
	 * appSecret 为"轻应用的密钥，格式为64位字符串，在连接微信配置页面获得",
	 * @param data
	 * @return
	 */
	public static String sha(String... data){
		String[] arr = data;
		Arrays.sort(arr);
		StringBuilder content = new StringBuilder();
		for (int i = 0; i < arr.length; i++) {
			content.append(arr[i]);
		}
		MessageDigest md = null;
		String signature = null;

		try {
			md = MessageDigest.getInstance("SHA-1");
			// 将三个参数字符串拼接成一个字符串进行sha1加密
			byte[] digest = md.digest(content.toString().getBytes());
			signature = SignUtil.byteToStr(digest);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return signature;
	}
}
