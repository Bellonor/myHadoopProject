package recommendJob;


import java.io.BufferedReader;  
import java.io.File;  
import java.io.FileReader;  
import java.io.IOException;  
import java.util.HashMap;  
import java.util.HashSet;  
import java.util.List;  
import java.util.Map;  
import java.util.Set;  

import org.apache.mahout.cf.taste.common.TasteException;  
import org.apache.mahout.cf.taste.impl.common.FastIDSet;  
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;  
import org.apache.mahout.cf.taste.model.DataModel;  
import org.apache.mahout.cf.taste.recommender.IDRescorer;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.impl.model.GenericBooleanPrefDataModel;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericBooleanPrefUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.CityBlockSimilarity;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

public class UserCityBlock {

	final static int neighborhoodNum=2;
	final static int recommendNum=3;
	
	public static void main(String[] args) throws TasteException, IOException {
		String file="datafile/week6/pv.csv";
		DataModel dataModel=new GenericBooleanPrefDataModel(GenericBooleanPrefDataModel.toDataMap(new FileDataModel(new File(file))));
		UserSimilarity userSimilarity=new CityBlockSimilarity(dataModel);
		UserNeighborhood userNeighborhood=new NearestNUserNeighborhood(neighborhoodNum,userSimilarity,dataModel);
		Recommender recommender=new GenericBooleanPrefUserBasedRecommender(dataModel, userNeighborhood, userSimilarity);
        LongPrimitiveIterator iterator=dataModel.getUserIDs();
        Map<Long, Double> averSalary = getAverSalary("datafile/week6/job.csv", dataModel);  
        while(iterator.hasNext()){
        	long uid=iterator.nextLong();
        	
        	Set<Long> jobids = getSalaryJobID(uid, "datafile/week6/job.csv", averSalary);  
            IDRescorer rescorer = new JobRescorer(jobids); 
        	
        	List<RecommendedItem> list=recommender.recommend(uid, recommendNum,rescorer);
        	 System.out.printf("uid:%s", uid);
        	for(RecommendedItem ritem:list){
        		System.out.printf("(%s,%f)", ritem.getItemID(), ritem.getValue());
        	}
        	System.out.println();
        }
    }
	
	public static Set<Long> getSalaryJobID(long uid, String file, Map<Long, Double> averSalary) throws IOException {  
        BufferedReader br = new BufferedReader(new FileReader(new File(file)));  
        Set<Long> jobids = new HashSet<Long>();  
        String s = null;  
        while ((s = br.readLine()) != null) {  
                String[] cols = s.split(",");  
                double salary = Double.valueOf(cols[2]);  
                if (salary < averSalary.get(uid)) {  
                        jobids.add(Long.parseLong(cols[0]));  
                }  
        }  
        br.close();  
        return jobids;  
    }  
      
    // 获取每个用户的基准比较工资：aver(浏览过的工资)*0.8  
    public static Map<Long, Double> getAverSalary(String file, DataModel dataModel)   
                            throws NumberFormatException, IOException, TasteException{  
            Map<Long, Double> salaries = new HashMap<Long, Double>();  
            BufferedReader br = new BufferedReader(new FileReader(new File(file)));  
        String s = null;  
        while ((s = br.readLine()) != null) {  
                String[] cols = s.split(",");  
                salaries.put(Long.parseLong(cols[0]), Double.valueOf(cols[2]));  
        }  
        br.close();  
  
  
            Map<Long, Double> averSalaries = new HashMap<Long, Double>();  
            LongPrimitiveIterator iter = dataModel.getUserIDs();  
            while (iter.hasNext()) {  
            long uid = iter.nextLong();  
            FastIDSet items = dataModel.getItemIDsFromUser(uid);  
            LongPrimitiveIterator itemsIter = items.iterator();  
            double sum = 0;  
            int count = 0;  
            double aver = 0.0;  
            while (itemsIter.hasNext()) {  
                long item = itemsIter.nextLong();  
                double salary = salaries.get(item);  
                sum += salary;  
                count ++;  
            }  
            if(count > 0) aver = 0.8*sum/count;  
            averSalaries.put(uid, aver);  
            }  
            return averSalaries;  
    }  
  

}
