package recommendJob;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.GenericBooleanPrefDataModel;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.recommender.GenericBooleanPrefItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.IDRescorer;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

public class ItemLoglikelihood {

	final static int neighborhoodNum=2;
	final static int recommendNum=3;
	public static void main(String[] args) throws TasteException, IOException {
		String file="datafile/week6/pv.csv";
		DataModel dataModel=new GenericBooleanPrefDataModel(GenericBooleanPrefDataModel.toDataMap(new FileDataModel(new File(file))));
		ItemSimilarity itemSimilarity=new LogLikelihoodSimilarity(dataModel);
		Recommender recommender=new GenericBooleanPrefItemBasedRecommender(dataModel,itemSimilarity);
		
        LongPrimitiveIterator iterator=dataModel.getUserIDs();
        while(iterator.hasNext()){
        	long uid=iterator.nextLong();
            Set<Long> jobids = getOutdateJobID("datafile/week6/job.csv");
            IDRescorer rescorer = new JobRescorer(jobids);
        	List<RecommendedItem> list=recommender.recommend(uid, recommendNum,rescorer);
        	//System.out.printf("uid:%s", uid);
        	System.out.printf("%s",uid);
        	for(RecommendedItem ritem:list){
        		//System.out.printf("(%s,%f)", ritem.getItemID(), ritem.getValue());
        		System.out.printf("%s",","+ ritem.getItemID());
        		
        	}
        	System.out.println();
        }
	}
	
    public static Set<Long> getOutdateJobID(String file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(new File(file)));
        Set<Long> jobids = new HashSet<Long>();
        String s = null;
        while ((s = br.readLine()) != null) {
            String[] cols = s.split(",");
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date date = null;
            try {
                date = df.parse(cols[1]);
                if (date.getTime() < df.parse("2013-01-01").getTime()) {
                    jobids.add(Long.parseLong(cols[0]));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        br.close();
        return jobids;
    }

}
class JobRescorer implements IDRescorer {
    final private Set<Long> jobids;

    public JobRescorer(Set<Long> jobs) {
        this.jobids = jobs;
    }

    @Override
    public double rescore(long id, double originalScore) {
        return isFiltered(id) ? Double.NaN : originalScore;
    }

    @Override
    public boolean isFiltered(long id) {
        return jobids.contains(id);
    }
}
