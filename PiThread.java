import java.lang.Math;
import java.util.Scanner;

public class PiThread {

    public static class PiTask implements Runnable{
        private final double accuracy;
        public PiTask(Double accuracy){
            this.accuracy = accuracy;
        }
        public void run() {
            double pi = 0;
            double i = 0;
            int interr = 0;

            while (!Thread.currentThread().isInterrupted() && !(Math.abs(pi - Math.PI) < accuracy)) {

                pi += Math.pow(-1, i) * 4 * (1 / (2 * i + 1));
                i++;
            }
            if (Thread.currentThread().isInterrupted()) interr = 1;

            System.out.println("Valore calcolato: "+ pi);
            System.out.println("Motivo interruzione: " + interr);// 1 = interr, 0 = acc
        }
    }

    public static void main (String... args){
        Scanner s = new Scanner(System.in);

        System.out.println("Inserire accuratezza: ");
        double accuracy = s.nextDouble();
        System.out.println("Inserire atempo max (millisecondi): ");
        int tmax = s.nextInt();

        System.out.println("*********************\nAcc:"+ accuracy + "  Tmax:" + tmax);
        Thread t = new Thread(new PiTask(accuracy));
        t.start();

        System.out.println("calcolo...");

        try {
            t.join(tmax);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (t.isAlive()) t.interrupt();
    }

}
