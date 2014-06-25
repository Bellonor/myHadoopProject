package sequence.machinelearning.decisiontree.c45;

import java.util.Comparator;

public class SequenceComparator implements Comparator {  

	public int compare(Object o1, Object o2) throws ClassCastException {   
	        String str1 = (String) o1;   
	        String str2 = (String) o2;   
	return str1.compareTo(str2);   
	    }   
	} 

