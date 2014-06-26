package sequence.machinelearning.naivebayes.bayesdemo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Main m=new Main();
		m.stringBufferDemo();
		//m.fileWriter("D:/test.txt");
		m.readF1();
	}
	
	public void fileWriter(String fileName) throws IOException{
        //创建一个FileWriter对象
        FileWriter fw = new FileWriter(fileName);
        //遍历clist集合写入到fileName中
        for (int i=0;i<10;i++){
            fw.write("第"+i+"行----");
            fw.write("\n");
        }
        //刷新缓冲区
        fw.flush();
        //关闭文件流对象
        fw.close();
    }

	
	
	/**
    * 利用StringBuffer写文件
    * 该方法可以设定使用何种编码，有效解决中文问题。
    * @throws IOException
    */
   
   public void stringBufferDemo() throws IOException
   {
       String src="D:/test.txt";
       delfile(src);
       File file=new File(src);
       if(file.exists())
           file.createNewFile();
       FileOutputStream out=new FileOutputStream(file,true);
       for(int i=0;i<10;i++)
       {
           StringBuffer sb=new StringBuffer();
           sb.append("这是第"+i+"行 \n");//如果不加"/n"则不能实现换行。
           System.out.print(sb.toString());
           
           out.write(sb.toString().getBytes("utf-8"));
       }
       out.close();
   }
   public void delfile(String filepath){
	   File file=new File(filepath);   
	       if(file.exists())   
	      {   
	           //file.createNewFile(); 
			   file.delete();   
	       }    

   }
	public void readF1() throws IOException {      
		
		//String filePath="scripts/clustering/canopy/canopy.dat";
		String filePath="D:/test.txt";
		BufferedReader br = new BufferedReader(new InputStreamReader(
       new FileInputStream(filePath)));
       for (String line = br.readLine(); line != null; line = br.readLine()) {
           if(line.length()==0||"".equals(line))continue;
       	String[] str=line.split(",");   
       	
       	   
       }
       br.close();
       
   }


}
