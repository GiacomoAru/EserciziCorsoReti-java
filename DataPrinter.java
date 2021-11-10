import java.util.Calendar;

import static java.lang.Thread.sleep;

public class DataPrinter {
    public static void main (String args[]){

        while(true) {
            try {
                System.out.println("******************************************************\n" +
                    Calendar.getInstance().getTime()+"\nNome thread: " + Thread.currentThread().getName());

                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

