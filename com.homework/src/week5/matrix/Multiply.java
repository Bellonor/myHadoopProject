package matrix;

public class Multiply {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
        int[][] a={{1,0,3,-1},{2,1,0,2}};
        int[][] b={{4,1,0},{-1,1,3},{2,0,1},{1,3,4}};
        int[][] c=new int[2][3];
        //int[][] c;
        for(int i=0;i<a.length;i++){
            for(int j=0;j<b[0].length;j++){
            	for(int k=0;k<b.length;k++){
            		c[i][j]=c[i][j]+a[i][k]*b[k][j];
            	}
            }
        }
       
        for(int i=0;i<c.length;i++){
        	for(int j=0;j<c[0].length;j++){
        		System.out.print(c[i][j]+"  ");
        	}
        	System.out.println();
        }
        
		
	}

}
