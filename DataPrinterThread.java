import java.util.Calendar;

public class DataPrinterThread extends Thread{
    public void run(){
        while(true) {
            try {
                System.out.println("******************************************************\n" +
                        Calendar.getInstance().getTime()+"\nNome thread: " + Thread.currentThread().getName() +
                        "\n******************************************************");

                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public static void main (String... args){
        Thread t = new DataPrinterThread();
        t.start();
        try {
            System.out.println("Nome: " + Thread.currentThread().getName());
            sleep(3000);
            System.out.println("Nome: " + Thread.currentThread().getName());
            sleep(3000);
            System.out.println("Nome: " + Thread.currentThread().getName());
            sleep(3000);
            System.out.println("Nome: " + Thread.currentThread().getName());
        }catch(InterruptedException e){e.printStackTrace();}
    }
}
