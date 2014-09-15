package com.kdweibo.wxtagent.utils;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

public class SignUtil {
	
	
    /** 
     * 验证签名 
     *  
     * @param signature 
     * @param timestamp 
     * @param nonce 
     * @return 
     */  
    public static boolean checkSignature(String signature, String timestamp, String nonce) {  
    	return checkSignature("yunzhijiatuandui", signature, timestamp, nonce);
    } 
    
    
    /**
     * 产生六位随机字符串
     * @return
     */
    public static String createNonce(){
    	
        String result = "";
        for(int i=0;i<6;i++){
            int intVal=(int)(Math.random()*26+97);
            result=result+(char)intVal;
        }
        return result;
    }
    
	// 随机生成固定位数的位字符串
    public static String getRandomStr(int n) {
		String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < n; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}
    
    /**
     * 发送消息给讯通时生成签名
     * @param no
     * @param pubno
     * @param timestamp
     * @param nonce
     * @return
     */
    public static String createXtToken(String no, String pubno, String pubkey, String timestamp, String nonce) { 
    	String token = "";
    	if(!StringUtils.hasText(no)||!StringUtils.hasText(pubno)||!StringUtils.hasText(timestamp)||!StringUtils.hasText(nonce)){
    		return null;
    	}    	
    	
    	String[] arr = new String[] { pubkey,no,pubno, timestamp, nonce }; 
    	
    	Arrays.sort(arr);  
    	
    	 StringBuilder content = new StringBuilder();  
         for (int i = 0; i < arr.length; i++) {  
             content.append(arr[i]);  
         }  
         MessageDigest md = null;    
         try {  
             md = MessageDigest.getInstance("SHA-1");  
             
             byte[] digest = md.digest(content.toString().getBytes());  
             token = byteToStr(digest);  
         } catch (NoSuchAlgorithmException e) {  
             e.printStackTrace();  
         }  

         return token;
    	
    	
    }
    
    /** 
     * 验证签名 
     *  
     * @param signature 
     * @param timestamp 
     * @param nonce 
     * @return 
     */  
    public static boolean checkSignature(String token,String signature, String timestamp, String nonce) {  
    	if(!StringUtils.hasText(signature)||!StringUtils.hasText(timestamp)||!StringUtils.hasText(nonce)){
    		return false;
    	}
        String[] arr = new String[] { token, timestamp, nonce };  
        // 将token、timestamp、nonce三个参数进行字典序排序  
        Arrays.sort(arr);  
        StringBuilder content = new StringBuilder();  
        for (int i = 0; i < arr.length; i++) {  
            content.append(arr[i]);  
        }  
        MessageDigest md = null;  
        String tmpStr = null;  
  
        try {  
            md = MessageDigest.getInstance("SHA-1");  
            // 将三个参数字符串拼接成一个字符串进行sha1加密  
            byte[] digest = md.digest(content.toString().getBytes());  
            tmpStr = byteToStr(digest);  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        }  
  
        content = null;  
        // 将sha1加密后的字符串可与signature对比，标识该请求来源于微信  
        return tmpStr != null ? tmpStr.equals(signature.toUpperCase()) : false;  
    } 
  
    /** 
     * 微信新的验证签名方式 
     *  
     * @param signature 
     * @param timestamp 
     * @param nonce 
     * @return 
     */  
    public static boolean checkSignatureNew(String msg_signature,String msg_encrypt, String timestamp, String nonce,String token) {
    	if(!StringUtils.hasText(msg_encrypt)||!StringUtils.hasText(timestamp)||!StringUtils.hasText(nonce)){
    		return false;
    	} 
        String[] arr = new String[] {token,  timestamp, nonce,msg_encrypt };  
        // 将token、timestamp、nonce三个参数进行字典序排序  
        Arrays.sort(arr);  
        StringBuilder content = new StringBuilder();  
        for (int i = 0; i < arr.length; i++) {  
            content.append(arr[i]);  
        }  
        MessageDigest md = null;  
        String tmpStr = null;  
  
        try {  
            md = MessageDigest.getInstance("SHA-1");  
            // 将三个参数字符串拼接成一个字符串进行sha1加密  
            byte[] digest = md.digest(content.toString().getBytes());  
            tmpStr = byteToStr(digest);  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        }  
   
        content = null;  
        // 将sha1加密后的字符串可与signature对比，标识该请求来源于微信  
        return tmpStr != null ? tmpStr.equals(msg_signature.toUpperCase()) : false;  
    } 
    
    /** 
     * 将字节数组转换为十六进制字符串 
     *  
     * @param byteArray 
     * @return 
     */  
    public static String byteToStr(byte[] byteArray) {  
        String strDigest = "";  
        for (int i = 0; i < byteArray.length; i++) {  
            strDigest += byteToHexStr(byteArray[i]);  
        }  
        return strDigest;  
    }  
  
    /** 
     * 将字节转换为十六进制字符串 
     *  
     * @param mByte 
     * @return 
     */  
    private static String byteToHexStr(byte mByte) {  
        char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };  
        char[] tempArr = new char[2];  
        tempArr[0] = Digit[(mByte >>> 4) & 0X0F];  
        tempArr[1] = Digit[mByte & 0X0F];  
  
        String s = new String(tempArr);  
        return s;  
    }  
}
