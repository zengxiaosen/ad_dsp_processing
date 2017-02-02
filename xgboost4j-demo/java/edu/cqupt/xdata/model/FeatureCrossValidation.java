package java.edu.cqupt.xdata.model;


import org.dmlc.xgboost4j.DMatrix;
import org.dmlc.xgboost4j.util.Trainer;
import org.dmlc.xgboost4j.util.XGBoostError;

/**
 * Created by Administrator on 2017/1/2.
 */
public class FeatureCrossValidation {
    static String arff = ".arff";
    static String txt = ".txt";
    static String csv = ".csv";
    static String libsvm = ".libsvm.txt";

    public static void main(String[] args){
        //load train mat
        //		 String  inputTrainFilePath ="C:\\Users\\dell\\Desktop\\competeDianzi\\data\\all\\";
        String inputTrainFilePath = "";
        //		 String  filename = "train_final_nouid";
        String filename = args[0];
        final double eta = Double.parseDouble(args[1]);
        final double max_depth = Double.parseDouble(args[2]);
        double round = Double.parseDouble(args[3]);
        //	    Txt2LibSvm.uci2Libsvm(inputTrainFilePath+filename+csv,inputTrainFilePath+filename+libsvm);
        try {
            DMatrix trainMat = new DMatrix(inputTrainFilePath + filename + libsvm);
        } catch (XGBoostError xgBoostError) {
            xgBoostError.printStackTrace();
        }
        //set params


        // do 5-fold cross validation
        int rounds = (int) round; // 1520
        int nfold = 10;
        // set additional eval_metrics
        String[] metrics = null;





    }
}
