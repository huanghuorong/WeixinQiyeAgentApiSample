package com.kdweibo.wxtagent.sample;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.kdweibo.wxtagent.utils.SignUtil;

 
public class WeChatMicroblogControllerSample {
  
    //授权中心暂存签名使用时间映射，建议持久化到db
    private Map<String, Long> authorizationCenterSignatureAndAccesstime = new HashMap<String, Long>();
    private int signatureExpireTime = 30; //签名自生成到失效的过期时长，单位为秒
    private String appSecret = "XXXX";//云之家动态专用key 
 
	private boolean checkSignature(HttpServletRequest request, StringBuffer errorsb){
		String signature = request.getParameter("signature");
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        
    	//校验请求合法性 流程图见 http://192.168.0.22/cms/pages/viewpage.action?pageId=4325634
    	//1.TODO Check cookite is exist 
        HttpSession session = request.getSession();
        long login_timestamp = 0;
        long nowtime = System.currentTimeMillis();
        if(session.getAttribute("login_timestamp") !=null){
        	login_timestamp = (long)session.getAttribute("login_timestamp");
        }
        if(nowtime - login_timestamp<3600*1000){//Cookite 60分钟过期
        	System.out.println("Cookite login_timestamp :"+login_timestamp+" not expire,not need check signature.");
        	session.setAttribute("login_timestamp", nowtime); 
        	//如果签名没有使用，且签名合法，则使用掉它，避免被其他人拦截后使用
        	long signatureAccesstime = authorizationCenterSignatureAndAccesstime.get(signature)==null?
        			0:authorizationCenterSignatureAndAccesstime.get(signature);
        	if(signatureAccesstime<=0){
        		if (SignUtil.checkSignature(appSecret,signature, timestamp, nonce)) {  
        			authorizationCenterSignatureAndAccesstime.put(signature, nowtime);
        		}
        	}
        	return true;
        }
        
        //2.TODO 检查signature是否已被访问过（一次有效，查询授权中心）
        long signatureAccesstime = authorizationCenterSignatureAndAccesstime.get(signature)==null?
        		0:authorizationCenterSignatureAndAccesstime.get(signature);
        if(signatureAccesstime>0){
        	errorsb.append("Authentication signature has been access at time ["+signatureAccesstime+"],! "
        			+ "appSecret="+appSecret+",signature="+signature+",timestamp="+timestamp+",nonce="+nonce);
        	return false;
        }
        
        //3.Check checkSignature
        if (!SignUtil.checkSignature(appSecret,signature, timestamp, nonce)) {  
        	errorsb.append("Authentication signature from require not valid! "
        			+ "appSecret="+appSecret+",signature="+signature+",timestamp="+timestamp+",nonce="+nonce);
        	return false;
        } 
        
        //4.TODO 根据timestamp判断请求是否在有效期内（业务端规则）,如 当前时间-timestamp > 10s 则认为已过期，避免中途被拦截，其他终端冒名访问业务系统
        long signatureCreatetime = Long.valueOf(timestamp).longValue();
        if(nowtime - signatureCreatetime > signatureExpireTime*1000){
        	errorsb.append("Authentication signature from require has expire in "+signatureExpireTime+"s! "
        			+ "appSecret="+appSecret+",signature="+signature+",timestamp="+timestamp+",nonce="+nonce);
        	return false;
        }
        
        //5.TODO 业务端授权中心记录signature,timestamp[建议持久化到db]，
        authorizationCenterSignatureAndAccesstime.put(signature, nowtime);
        
        //6.TODO Set client cookite login status 
		session.setAttribute("login_timestamp", nowtime); 
		
		return true;
	}
}
