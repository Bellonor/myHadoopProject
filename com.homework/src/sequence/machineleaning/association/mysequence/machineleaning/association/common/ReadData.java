package mysequence.machineleaning.association.common;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class ReadData {

	
	public static Map<String,String> dataMap=new HashMap<String,String>();
	public static final void readF1() throws IOException {      
		
		//String filePath="scripts/clustering/canopy/canopy.dat";
		String filePath="datafile/association/items";
		BufferedReader br = new BufferedReader(new InputStreamReader(
        new FileInputStream(filePath)));
        for (String line = br.readLine(); line != null; line = br.readLine()) {
            if(line.length()==0||"".equals(line))continue;
        	String[] str=line.split("\t");               
        	dataMap.put(str[0], str[1].trim());
            //System.out.println(line);               
        }
        br.close();
        
    }
}
