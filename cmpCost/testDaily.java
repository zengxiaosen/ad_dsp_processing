import java.io.PrintStream;

/**
 * Created by Administrator on 2017/2/10.
 */
public class testDaily {
    public static void main(String[] args){
        int a = 10;
        int b = 10;
        //Integer a1 = new Integer(a);

        method(a, b);
        System.out.println("a="+a);
        System.out.println("b="+b);
    }

    public static void method( int a,   int b) {
        System.setOut(new PrintStream(System.out, true){
            public void println(String x){
                String split = x.split("=")[0];
                super.println(x+"0");
            }
        });
    }
}