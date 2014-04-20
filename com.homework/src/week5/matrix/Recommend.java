package matrix;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.hadoop.mapred.JobConf;


public class Recommend {

    public static final String HDFS = "hdfs://192.168.0.200:9000/user/hdfs/";
    public static final Pattern DELIMITER = Pattern.compile("[\t,]");

    public static void main(String[] args) throws Exception {
        Map<String, String> path = new HashMap<String, String>();
        //hdfs://localhost:9000/user/hdfs/in/
        path.put("matrixMult", HDFS+"Mult/");
        path.put("matrixMultOut", HDFS+"/Mult/Out/");
        //Step1.run(path);
        // MyTest.run(path);
        SparseMatrix.run(path);
        //Step3.run1(path);
        //Step3.run2(path);
//        Step4.run(path);
        
        //Step4_Update.run(path);
        //Step4_Update2.run(path);
        
        
//        // hadoop fs -cat /user/hdfs/recommend/step4/part-00000
//        JobConf conf = config();
//        HdfsDAO hdfs = new HdfsDAO(HDFS, conf);
//        hdfs.cat("/user/hdfs/recommend/step4/part-00000");
        
        System.exit(0);
    }

    public static JobConf config() {
        JobConf conf = new JobConf(Recommend.class);
        conf.setJobName("Recommand");
        //conf.addResource("classpath:/hadoop/core-site.xml");
        //conf.addResource("classpath:/hadoop/hdfs-site.xml");
        //conf.addResource("classpath:/hadoop/mapred-site.xml");
        conf.set("io.sort.mb", "1024");
        return conf;
    }

}
