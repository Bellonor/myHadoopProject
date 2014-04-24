package classfier;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.hadoop.mapred.JobConf;
//第一步，MapReduce后产生：用户，浏览职位，职位薪水
/* 1.map:
* job.csv文件
* key:jobid,value:job:job,salary
* pv.csv文件
* key:jobid,value:user,userid
* 2.reduce:
* key:userid,
* value:jobid,salary
* */

//第二步， MapReduce后产生:  用户，浏览过职位薪水相加*0.8

//第三步：过滤: 推荐结果 > 用户浏览过职位平均薪水%80 .
public class Main {
    public static final String HDFS = "hdfs://192.168.0.200:9000/user/hdfs/week7/";
    public static final Pattern DELIMITER = Pattern.compile("[\t,]");
    
    public static final String PaodingFirstIn = HDFS+"in/";
    public static final String PaodingFirstOut = HDFS+"out/";
    
   
    
    public static void main(String[] args) throws Exception {
        
    	Map<String, String> path = new HashMap<String, String>();
    	
    	
        path.put("PaodingFirstIn", PaodingFirstIn);
        path.put("PaodingFirstOut", PaodingFirstOut);
        
    	
        PaodingFirst.run(path);
        //Step3.run(path);
       
        System.exit(0);
    }
	
    public static JobConf config() {
        JobConf conf = new JobConf(Main.class);
        conf.setJobName("Main");
        //conf.addResource("classpath:/hadoop/core-site.xml");
        //conf.addResource("classpath:/hadoop/hdfs-site.xml");
        //conf.addResource("classpath:/hadoop/mapred-site.xml");
        conf.set("io.sort.mb", "1024");
        return conf;
    }
}
