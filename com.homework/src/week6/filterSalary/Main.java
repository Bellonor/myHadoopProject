package filterSalary;

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
    public static final String HDFS = "hdfs://192.168.0.200:9000/user/hdfs/week6/";
    public static final Pattern DELIMITER = Pattern.compile("[\t,]");
    
    public static final String Step0In = HDFS+"mahoutInput";
    public static final String Step0Out = HDFS+"step0Out/";
    
    public static final String Step1In = HDFS+"step1In/";
    public static final String Step1Out = HDFS+"step1Out/";
    
    public static final String Step2In = Step1Out;
    public static final String Step2Out = HDFS+"step2Out/";
    
    public static final String Step3In1 = Step2Out;
    public static final String Step3In2 = Step0Out;
    public static final String Step3Out = HDFS+"step3Out/";
    
    public static final String Step4In1 = Step0Out;
    public static final String Step4In2 = Step3Out;
    public static final String Step4Out = HDFS+"step4Out/";
    
    public static void main(String[] args) throws Exception {
        
    	Map<String, String> path = new HashMap<String, String>();
    	path.put("ToHdfsData1", "datafile/week6/job.csv");
    	path.put("ToHdfsData2", "datafile/week6/pv.csv");
    	
        path.put("Step0In", Step0In);
        path.put("Step0Out", Step0Out);
        
    	path.put("Step1In", Step1In);
        path.put("Step1Out", Step1Out);
        
        path.put("Step2In", Step2In);
        path.put("Step2Out", Step2Out);
        
        path.put("Step3In1", Step3In1);
        path.put("Step3In2", Step3In2);
        path.put("Step3Out", Step3Out);
        
        path.put("Step4In1", Step4In1);
        path.put("Step4In2", Step4In2);
        path.put("Step4Out", Step4Out);
        //Step0.run(path);
        //Step1.run(path);
        //Step2.run(path);
        Step3.run(path);
        
       //Step4.run(path);
        
        //Step4_Update.run(path);
        //Step4_Update2.run(path);
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
