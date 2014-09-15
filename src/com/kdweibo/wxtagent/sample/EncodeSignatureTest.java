package com.kdweibo.wxtagent.sample;

import java.util.Random;

import com.kdweibo.wxtagent.utils.SHAUtils;

public class EncodeSignatureTest {
	
	public static void main(String[] args) {
		String appSecret = "微信通分配给当前开发者的密钥";
		String timestamp = System.currentTimeMillis()+"";
		String nonce = new Random(100).nextInt()+"";
		//生成签名
		String signature = SHAUtils.sha(new String[]{appSecret, timestamp, nonce});
		 
		System.out.println("signature is: "+signature);
	}
}
