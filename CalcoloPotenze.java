import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.*;

public class CalcoloPotenze {
    public static void main(String... args){
        ExecutorService tp = Executors.newCachedThreadPool();
        Scanner s = new Scanner(System.in);

        System.out.println("inserire N:");
        int n = s.nextInt();
        int i;
        Vector<Future<Double>> vd = new Vector<>();
        for(i = 0; i<49; i++){
            Future<Double> dummyF = tp.submit(new Power(n, i + 2));
            vd.add(dummyF);
        }

        long l = 0;
        for (Future<Double> f: vd) {
            try {
                l += f.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Valore calcolato: " + l);
        tp.shutdown();
    }
}
