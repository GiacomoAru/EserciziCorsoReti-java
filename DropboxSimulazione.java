import java.util.concurrent.ThreadLocalRandom;

public class DropboxSimulazione{

    private static class Dropbox{
        private int buffer = -1;

        public synchronized int take(boolean pariDispari) throws InterruptedException {//true allora pari
            while((pariDispari != (buffer%2 == 0)) || buffer < 0) wait();

            int ret = buffer;
            System.out.println("Consumato: " + buffer);
            buffer = -1;
            notifyAll();
            return ret;
        }
        public synchronized void put(int nextInt) throws InterruptedException {//c'è solo un producer
            while(buffer >= 0){
                    wait();
            }
            buffer = nextInt;
            System.out.println("Prodotto: " + buffer);
            notifyAll();
        }
    }
    private static class producerTask implements Runnable{
        private final Dropbox d;
        private final ThreadLocalRandom r = ThreadLocalRandom.current();

        public producerTask(Dropbox d){this.d = d;}
        @Override
        public void run() {
            while(true){
                try {
                    d.put(r.nextInt(100));
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private static class consumerTask implements Runnable{
        private final Dropbox d;
        private final boolean pariDispari;

        public consumerTask(Dropbox d, boolean pd){this.d = d; this.pariDispari = pd;}
        @Override
        public void run() {
            while(true) {
                try {
                    d.take(pariDispari);
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String... args){
        Dropbox dropbox = new Dropbox();
        Thread producer = new Thread(new producerTask(dropbox));
        Thread consumer1 = new Thread(new consumerTask(dropbox, true));
        Thread consumer2 = new Thread(new consumerTask(dropbox, false));


        producer.setDaemon(true);
        producer.start();

        consumer1.setDaemon(true);
        consumer1.start();

        consumer2.setDaemon(true);
        consumer2.start();

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}














