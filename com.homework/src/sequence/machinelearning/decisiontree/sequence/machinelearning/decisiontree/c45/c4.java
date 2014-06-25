package sequence.machinelearning.decisiontree.c45;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet; 

public class c4 {
	private DecisionTreeNode root;
	private boolean[] visable;  
	private static final int NOT_FOUND = -1;  
	private static final int DATA_START_LINE = 1;  
	private Object[] trainingArray;  
	private String[] columnHeaderArray;  
	private int nodeIndex;  
	/** 
	     * @param args 
	     */ 
	@SuppressWarnings("boxing")   
	public static void main(String[] args) {   
	        Object[] array = new Object[] {   
	new String[] { "age",          "income",   "student", "credit_rating", "buys_computer" },   
	new String[] { "youth",        "high",     "no",      "fair",          "no"  },   
	new String[] { "youth",        "high",     "no",      "excellent",     "no"  },   
	new String[] { "middle_aged",  "high",     "no",      "fair",          "yes" },   
	new String[] { "senior",       "medium",   "no",      "fair",          "yes" },   
	new String[] { "senior",       "low",      "yes",     "fair",          "yes" },   
	new String[] { "senior",       "low",      "yes",     "excellent",     "no"  },   
	new String[] { "middle_aged",  "low",      "yes",     "excellent",     "yes" },   
	new String[] { "youth",        "medium",   "no",      "fair",          "no"  },   
	new String[] { "youth",        "low",      "yes",     "fair",          "yes" },   
	new String[] { "senior",       "medium",   "yes",     "fair",          "yes" },   
	new String[] { "youth",        "medium",   "yes",     "excellent",     "yes" },   
	new String[] { "middle_aged",  "medium",   "no",      "excellent",     "yes" },   
	new String[] { "middle_aged",  "high",     "yes",     "fair",          "yes" },   
	new String[] { "senior",       "medium",   "no",      "excellent",     "no"  },   
	        };  

	        c4 tree = new c4();   
	        tree.create(array, 4);   
	        System.out.println("===============END PRINT TREE===============");   
	        System.out.println("===============DECISION RESULT===============");   
	        //tree.forecast(printData, tree.root); 
	    }  


/** 
 * @param printData 
 * @param node 
 */ 
public void forecast(String[] printData, DecisionTreeNode node) {   
int index = getColumnHeaderIndexByName(node.nodeName);   
if (index == NOT_FOUND) {   
        System.out.println(node.nodeName);   
    }   
    DecisionTreeNode[] childs = node.childNodesArray;   
for (int i = 0; i < childs.length; i++) {   
if (childs[i] != null) {   
if (childs[i].parentArrtibute.equals(printData[index])) {   
                forecast(printData, childs[i]);   
            }   
        }   
    }   
}  

/** 
 * @param array 
 * @param index 
 */ 
public void create(Object[] array, int index) {   
this.trainingArray = Arrays.copyOfRange(array, DATA_START_LINE,   
            array.length);   
    init(array, index);   
    createDecisionTree(this.trainingArray);   
    printDecisionTree(root);
}  
                          
/** 
 * @param array 
 * @return Object[] 
 */ 
@SuppressWarnings("boxing")   
public Object[] getMaxGain(Object[] array) {   
    Object[] result = new Object[2];   
double gain = 0;   
int index = -1;  

for (int i = 0; i < visable.length; i++) {   
if (!visable[i]) {   
//TODO ID3 change to C4.5 
double value = gainRatio(array, i, this.nodeIndex);   
            System.out.println(value);   
if (gain < value) {   
                gain = value;   
                index = i;   
            }   
        }   
    }   
    result[0] = gain;   
    result[1] = index;   
//TODO throws can't forecast this model exception 
if (index != -1) {   
        visable[index] = true;   
    }   
return result;   
}  

/** 
 * @param array 
 */ 
public void createDecisionTree(Object[] array) {   
    Object[] maxgain = getMaxGain(array);   
if (root == null) {   
        root = new DecisionTreeNode();   
        root.parentNode = null;   
        root.parentArrtibute = null;   
        root.arrtibutesArray = getArrtibutesArray(((Integer) maxgain[1])   
                .intValue());   
        root.nodeName = getColumnHeaderNameByIndex(((Integer) maxgain[1])   
                .intValue());   
        root.childNodesArray = new DecisionTreeNode[root.arrtibutesArray.length];   
        insertDecisionTree(array, root);   
    }   
}  

/** 
 * @param array 
 * @param parentNode 
 */ 
public void insertDecisionTree(Object[] array, DecisionTreeNode parentNode) {   
    String[] arrtibutes = parentNode.arrtibutesArray;   
for (int i = 0; i < arrtibutes.length; i++) {   
        Object[] pickArray = pickUpAndCreateSubArray(array, arrtibutes[i],   
                getColumnHeaderIndexByName(parentNode.nodeName));   
        Object[] info = getMaxGain(pickArray);   
double gain = ((Double) info[0]).doubleValue();   
if (gain != 0) {   
int index = ((Integer) info[1]).intValue();   
            DecisionTreeNode currentNode = new DecisionTreeNode();   
            currentNode.parentNode = parentNode;   
            currentNode.parentArrtibute = arrtibutes[i];   
            currentNode.arrtibutesArray = getArrtibutesArray(index);   
            currentNode.nodeName = getColumnHeaderNameByIndex(index);   
            currentNode.childNodesArray = new DecisionTreeNode[currentNode.arrtibutesArray.length];   
            parentNode.childNodesArray[i] = currentNode;   
            insertDecisionTree(pickArray, currentNode);   
        } else {   
            DecisionTreeNode leafNode = new DecisionTreeNode();   
            leafNode.parentNode = parentNode;   
            leafNode.parentArrtibute = arrtibutes[i];   
            leafNode.arrtibutesArray = new String[0];   
            leafNode.nodeName = getLeafNodeName(pickArray,this.nodeIndex);   
            leafNode.childNodesArray = new DecisionTreeNode[0];   
            parentNode.childNodesArray[i] = leafNode;   
        }   
    }   
}  

/** 
 * @param node 
 */ 
public void printDecisionTree(DecisionTreeNode node) {   
    System.out.println(node.nodeName);   
    DecisionTreeNode[] childs = node.childNodesArray;   
for (int i = 0; i < childs.length; i++) {   
if (childs[i] != null) {   
            System.out.println(childs[i].parentArrtibute);   
            printDecisionTree(childs[i]);   
        }   
    }   
}  

/** 
 * init data 
 *  
 * @param dataArray 
 * @param index 
 */ 
public void init(Object[] dataArray, int index) {   
this.nodeIndex = index;   
//init data 
this.columnHeaderArray = (String[]) dataArray[0];   
    visable = new boolean[((String[]) dataArray[0]).length];   
for (int i = 0; i < visable.length; i++) {   
if (i == index) {   
            visable[i] = true;   
        } else {   
            visable[i] = false;   
        }   
    }   
}  

/** 
 * @param array 
 * @param arrtibute 
 * @param index 
 * @return Object[] 
 */ 
public Object[] pickUpAndCreateSubArray(Object[] array, String arrtibute,   
int index) {   
    List list = new ArrayList();   
for (int i = 0; i < array.length; i++) {   
        String[] strs = (String[]) array[i];   
if (strs[index].equals(arrtibute)) {   
            list.add(strs);   
        }   
    }   
return list.toArray();   
}  

/** 
 * gain(A) 
 *  
 * @param array 
 * @param index 
 * @return double 
 */ 
public double gain(Object[] array, int index, int nodeIndex) {   
int[] counts = separateToSameValueArrays(array, nodeIndex);   
    String[] arrtibutes = getArrtibutesArray(index);   
double infoD = infoD(array, counts);   
double infoaD = infoaD(array, index, nodeIndex, arrtibutes);   
return infoD - infoaD;   
}  

/** 
 * @param array 
 * @param nodeIndex 
 * @return 
 */ 
public int[] separateToSameValueArrays(Object[] array, int nodeIndex) {   
    String[] arrti = getArrtibutesArray(nodeIndex);   
int[] counts = new int[arrti.length];   
for (int i = 0; i < counts.length; i++) {   
        counts[i] = 0;   
    }   
for (int i = 0; i < array.length; i++) {   
        String[] strs = (String[]) array[i];   
for (int j = 0; j < arrti.length; j++) {   
if (strs[nodeIndex].equals(arrti[j])) {   
                counts[j]++;   
            }   
        }   
    }   
return counts;   
}  

/** 
 * gainRatio = gain(A)/splitInfo(A) 
 *  
 * @param array 
 * @param index 
 * @param nodeIndex 
 * @return 
 */ 
public double gainRatio(Object[] array,int index,int nodeIndex){   
double gain = gain(array,index,nodeIndex);   
int[] counts = separateToSameValueArrays(array, index);   
double splitInfo = splitInfoaD(array,counts);   
if(splitInfo != 0){   
return gain/splitInfo;   
    }   
return 0;   
}  

/** 
 * infoD = -E(pi*log2 pi) 
 *  
 * @param array 
 * @param counts 
 * @return 
 */ 
public double infoD(Object[] array, int[] counts) {   
double infoD = 0;   
for (int i = 0; i < counts.length; i++) {   
        infoD += DecisionTreeUtil.info(counts[i], array.length);   
    }   
return infoD;   
}   

/** 
 * splitInfoaD = -E|Dj|/|D|*log2(|Dj|/|D|) 
 *  
 * @param array 
 * @param counts 
 * @return 
 */ 
public double splitInfoaD(Object[] array, int[] counts) {   
return infoD(array, counts);   
}  

/** 
 * infoaD = E(|Dj| / |D|) * info(Dj) 
 *  
 * @param array 
 * @param index 
 * @param arrtibutes 
 * @return 
 */ 
public double infoaD(Object[] array, int index, int nodeIndex,   
        String[] arrtibutes) {   
double sv_total = 0;   
for (int i = 0; i < arrtibutes.length; i++) {   
        sv_total += infoDj(array, index, nodeIndex, arrtibutes[i],   
                array.length);   
    }   
return sv_total;   
}  

/** 
 * ((|Dj| / |D|) * Info(Dj)) 
 *  
 * @param array 
 * @param index 
 * @param arrtibute 
 * @param allTotal 
 * @return double 
 */ 
public double infoDj(Object[] array, int index, int nodeIndex,   
        String arrtibute, int allTotal) {   
    String[] arrtibutes = getArrtibutesArray(nodeIndex);   
int[] counts = new int[arrtibutes.length];   
for (int i = 0; i < counts.length; i++) {   
        counts[i] = 0;   
    }  

for (int i = 0; i < array.length; i++) {   
        String[] strs = (String[]) array[i];   
if (strs[index].equals(arrtibute)) {   
for (int k = 0; k < arrtibutes.length; k++) {   
if (strs[nodeIndex].equals(arrtibutes[k])) {   
                    counts[k]++;   
                }   
            }   
        }   
    }  

int total = 0;   
double infoDj = 0;   
for (int i = 0; i < counts.length; i++) {   
        total += counts[i];   
    }   
for (int i = 0; i < counts.length; i++) {   
        infoDj += DecisionTreeUtil.info(counts[i], total);   
    }   
return DecisionTreeUtil.getPi(total, allTotal) * infoDj;   
}  

/** 
 * @param index 
 * @return String[] 
 */ 
@SuppressWarnings("unchecked")   
public String[] getArrtibutesArray(int index) {   
    TreeSet set = new TreeSet(new SequenceComparator());   
for (int i = 0; i < trainingArray.length; i++) {   
        String[] strs = (String[]) trainingArray[i];   
        set.add(strs[index]);   
    }   
    String[] result = new String[set.size()];   
return (String[]) set.toArray(result);   
}  

/** 
 * @param index 
 * @return String 
 */ 
public String getColumnHeaderNameByIndex(int index) {   
for (int i = 0; i < columnHeaderArray.length; i++) {   
if (i == index) {   
return columnHeaderArray[i];   
        }   
    }   
return null;   
}  

/** 
 * @param array 
 * @return String 
 */ 
public String getLeafNodeName(Object[] array,int nodeIndex) {   
if (array != null && array.length > 0) {   
        String[] strs = (String[]) array[0];   
return strs[nodeIndex];   
    }   
return null;   
}  

/** 
 * @param name 
 * @return int 
 */ 
public int getColumnHeaderIndexByName(String name) {   
for (int i = 0; i < columnHeaderArray.length; i++) {   
if (name.equals(columnHeaderArray[i])) {   
return i;   
        }   
    }   
return NOT_FOUND;   
}   
}  




