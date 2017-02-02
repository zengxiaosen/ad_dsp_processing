/**
 * Created by Administrator on 2016/12/30.
 */
public class LCS_calculate {

    public static int max(int a, int b){
        if(a >= b) return a;
        else return b;
    }

    public static String LCS_calculate(String s1, String s2){
        int size1 = s1.length();
        int size2 = s2.length();
        int chess[][] = new int[s1.length()+1][s2.length()+1];
        chess[0][0] = 0;
        for(int i=1; i<=size1; i++){
            for(int j=1; j<size2; j++){
                if(s1.charAt(i-1) == s2.charAt(i-1)){
                    chess[i][j] = chess[i-1][j-1] + 1;
                }else{
                    chess[i][j] = max(chess[i][j-1], chess[i-1][j]);
                }
            }
        }
        int i = size1;
        int j = size2;
        StringBuffer sb = new StringBuffer();
        while((i != 0) && (j != 0)){
            //利用上面得到的矩阵计算子序列，从最右下脚往左上走
            if(s1.charAt(i-1) == s2.charAt(j-1)){
                sb.append(s1.charAt(i-1));//相同时即为相同的子串
                i--;
                j--;
            }else{
                if(chess[i][j-1] > chess[i-1][j]){
                    j--;
                }else{
                    i--;
                }
            }
        }
        System.out.println((double)sb.length()/s2.length() + "," + (double)sb.length()/s1.length());
        return sb.reverse().toString();//记得反转
    }

}
