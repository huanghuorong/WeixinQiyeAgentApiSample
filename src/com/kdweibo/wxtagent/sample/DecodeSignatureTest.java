package com.kdweibo.wxtagent.sample;

import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.kdweibo.wxtagent.utils.SHAUtils;
import com.kdweibo.wxtagent.utils.SignUtil;

public class DecodeSignatureTest {

	//TODO 此处仅是模拟http请求 
    public String getMicroBlogs(HttpServletRequest request) {
    	//校验请求合法性 流程图见 http://192.168.0.22/cms/pages/viewpage.action?pageId=4325634
    	//1.TODO Check cookite is exist
		String signature = request.getParameter("signature");
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce"); 
        //2.TODO 检查signature是否已被访问过（一次有效，查询授权中心）
        //3.Check checkSignature
        if(!checkSignature(signature, timestamp, nonce)){
        	System.out.println("Authentication from request not valid!");
        	return "redirect:error.jsp";
        }else{
        	System.out.println("Authentication from request check ok!");
        }  
        //4.TODO 根据timestamp判断请求是否在有效期内（业务端规则）,如 当前时间-timestamp > 10s 则认为已过期，避免中途被拦截，其他终端冒名访问业务系统
        //5.TODO 业务端授权中心记录signature,timestamp[建议持久化到db]，
        //6.TODO Set client cookite
        return "redirect:http://kdwiebo.com/snsapi/testrest";
    }
    
	private boolean checkSignature(String signature, String timestamp, String nonce){
		String appSecret = "微信通分配给当前开发者的密钥";
		System.out.println("Authentication signature from require not valid! "
    			+ "appSecret="+appSecret
    			+",signature="+signature
    			+",timestamp=" +timestamp
    			+",nonce="+nonce);
		if (!SignUtil.checkSignature(appSecret,signature, timestamp, nonce)) {  
			return false;
        }else{
        	return true;
        } 
	}
	
	public static void main(String[] args) {
		String appSecret = "微信通分配给当前开发者的密钥";
		String timestamp = System.currentTimeMillis()+"";
		String nonce = new Random(100).nextInt()+"";
		//生成签名
		String signature = SHAUtils.sha(new String[]{appSecret, timestamp, nonce});
		String code = "微信OAuth1 code";//预留
		
		HttpServletRequest request = null;
		HttpSession session = request.getSession();
		session.setAttribute("timestamp", timestamp);
		session.setAttribute("nonce", nonce);
		session.setAttribute("signature", signature);
		session.setAttribute("code", code);
		
		new DecodeSignatureTest().getMicroBlogs(request);
	}
}
