package entity;

import java.util.HashSet;
import java.util.Set;





public class Kpi {

    private String remote_addr;// 记录客户端的ip地址
    private String remote_user;// 记录客户端用户名称,忽略属性"-"
    private String time_local;// 记录访问时间与时区
    private String request;// 记录请求的url与http协议
    private String status;// 记录请求状态；成功是200
    private String body_bytes_sent;// 记录发送给客户端文件主体内容大小
    private String http_referer;// 用来记录从那个页面链接访问过来的
    private String http_user_agent;// 记录客户浏览器的相关信息

    private boolean valid = true;// 判断数据是否合法

    private static Kpi parser(String line) {
        //System.out.println(line);
        Kpi kpi = new Kpi();
        try {
			String[] arr = line.split(" ");
			if (arr.length > 11) {
			    kpi.setRemote_addr(arr[0]);
			    kpi.setRemote_user(arr[1]);
			    kpi.setTime_local(arr[3].substring(1));
			    kpi.setRequest(arr[6]);
			    kpi.setStatus(arr[8]);
			    kpi.setBody_bytes_sent(arr[9]);
			    kpi.setHttp_referer(arr[10]);
			    
			    if (arr.length > 12) {
			        kpi.setHttp_user_agent(arr[11] + " " + arr[12]);
			    } else {
			        kpi.setHttp_user_agent(arr[11]);
			    }

			    if (Integer.parseInt(kpi.getStatus()) >= 400) {// 大于400，HTTP错误
			        kpi.setValid(false);
			    }
			} else {
			    kpi.setValid(false);
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
        return kpi;
    }
    public static Kpi filterIPs(String line) {
        
    	Kpi kpi=new Kpi();
		try {
			kpi = parser(line);
			int n1=kpi.getRequest().indexOf(".php");
			int n2=kpi.getRequest().indexOf(".html");
			if((n1+n2)==-2){
				kpi.setValid(false);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return kpi;
    }
    public static Kpi filterPVs(String line) {
        
    	Kpi kpi=new Kpi();
		try {
			kpi = parser(line);
			int n1=kpi.getRequest().indexOf(".php");
			int n2=kpi.getRequest().indexOf(".html");
			if((n1+n2)==-2){
				kpi.setValid(false);
			}
			int n3=kpi.getRequest().indexOf("baidu");
			int n4=kpi.getRequest().indexOf("google");
			if((n3+n4)!=-2){
				kpi.setValid(false);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return kpi;
    }
    
	public String getRemote_addr() {
		return remote_addr;
	}

	public void setRemote_addr(String remote_addr) {
		this.remote_addr = remote_addr;
	}

	public String getRemote_user() {
		return remote_user;
	}

	public void setRemote_user(String remote_user) {
		this.remote_user = remote_user;
	}

	public String getTime_local() {
		return time_local;
	}

	public void setTime_local(String time_local) {
		this.time_local = time_local;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getBody_bytes_sent() {
		return body_bytes_sent;
	}

	public void setBody_bytes_sent(String body_bytes_sent) {
		this.body_bytes_sent = body_bytes_sent;
	}

	public String getHttp_referer() {
		return http_referer;
	}

	public void setHttp_referer(String http_referer) {
		this.http_referer = http_referer;
	}

	public String getHttp_user_agent() {
		return http_user_agent;
	}

	public void setHttp_user_agent(String http_user_agent) {
		this.http_user_agent = http_user_agent;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}
    
    
}
