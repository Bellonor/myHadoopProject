package recommend;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;



import org.apache.hadoop.mapred.JobConf;

public class MainPodium {
    public static final String HDFS = "hdfs://192.168.0.200:9000/user/hdfs/week5/";
    public static final Pattern DELIMITER = Pattern.compile("[\t,]");
    public static final String Step1In = HDFS+"step1In/";
    public static final String Step1Out = HDFS+"step1Out/";
    
    public static final String Step2In = Step1Out;
    public static final String Step2Out = HDFS+"step2Out/";
    
    public static final String Step3In = Step1Out;
    public static final String Step3Out = HDFS+"step3Out/";
    
    public static final String Step4In1 = Step2Out;
    public static final String Step4In2 = Step3Out;
    public static final String Step4Out = HDFS+"step4Out/";
    
    
    public static void main(String[] args) throws Exception {
        
    	Map<String, String> path = new HashMap<String, String>();
    	path.put("data", "datafile/week5/small.csv");
        path.put("Step1In", Step1In);
        path.put("Step1Out", Step1Out);
        
        path.put("Step2In", Step2In);
        path.put("Step2Out", Step2Out);
        
        path.put("Step3In", Step3In);
        path.put("Step3Out", Step3Out);
        
        path.put("Step4In1", Step4In1);
        path.put("Step4In2", Step4In2);
        path.put("Step4Out", Step4Out);
        Step1.run(path);
        Step2.run(path);
        Step3.run(path);
        
       Step4.run(path);
        
        //Step4_Update.run(path);
        //Step4_Update2.run(path);
        
        
//        // hadoop fs -cat /user/hdfs/recommend/step4/part-00000
//        JobConf conf = config();
//        HdfsDAO hdfs = new HdfsDAO(HDFS, conf);
//        hdfs.cat("/user/hdfs/recommend/step4/part-00000");
        
        System.exit(0);
    }
    public static JobConf config() {
        JobConf conf = new JobConf(MainPodium.class);
        conf.setJobName("Recommand");
        //conf.addResource("classpath:/hadoop/core-site.xml");
        //conf.addResource("classpath:/hadoop/hdfs-site.xml");
        //conf.addResource("classpath:/hadoop/mapred-site.xml");
        conf.set("io.sort.mb", "1024");
        return conf;
    }
}
