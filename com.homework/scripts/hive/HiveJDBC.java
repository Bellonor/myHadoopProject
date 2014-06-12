package com.hive.jdbc;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class HiveJDBC {

	public static void main(String[] args) {
        try {
            Class.forName("org.apache.hadoop.hive.jdbc.HiveDriver");
            // 查询语句
            String querySQL = "SELECT * FROM t_rp";
            // 链接hive
            Connection con = DriverManager.getConnection("jdbc:hive://192.168.0.100:10000/default", "hive", "hive");
            Statement stmt = con.createStatement();
            // 执行查询语句
            ResultSet res = stmt.executeQuery(querySQL);
            while (res.next()) {
                System.out.println("Result: key:" + res.getString(1) + "  –>  value:" + res.getString(2));
            }
            stmt.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}


/*package com.javabloger.hive;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
public class HiveTestCase {
    public static void main(String[] args) throws  Exception {
        Class.forName("org.apache.hadoop.hive.jdbc.HiveDriver");
        
        String dropSQL="drop table javabloger";
        String createSQL="create table javabloger (key int, value string)";
        String insterSQL="LOAD DATA LOCAL INPATH '/work/hive/examples/files/kv1.txt' OVERWRITE INTO TABLE javabloger";
        String querySQL="SELECT a.* FROM javabloger a";
        
        Connection con = DriverManager.getConnection("jdbc:hive://192.168.20.213:10000/default", "", "");
        Statement stmt = con.createStatement();
        stmt.executeQuery(dropSQL);  // 执行删除语句
        stmt.executeQuery(createSQL);  // 执行建表语句
        stmt.executeQuery(insterSQL);  // 执行插入语句
        ResultSet res = stmt.executeQuery(querySQL);   // 执行查询语句
        
          while (res.next()) {
            System.out.println("Result: key:"+res.getString(1) +"  –>  value:" +res.getString(2));
        }
    }
}*/
