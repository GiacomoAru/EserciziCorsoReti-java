import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;

public class FileCrawler {

    static String init = "DIRECTORY CARTELLA";
    public static void main(String args[]){
        final sincrCoda<String> coda = new sincrCoda<>();
        Scanner s = new Scanner(System.in);
        int nThread = 5;

        String path = init;

        System.out.println("INIZIO\n");
        Thread producer = new Thread(new ProducerTask(path, coda));;



        Thread[] consumer = new Thread[nThread];
        for(int i = 0; i<nThread; i++){
            consumer[i] = new Thread(new ConsumerTask(coda));
            consumer[i].start();
        }

        //start thread
        producer.start();
        for(int i = 0; i<nThread; i++) consumer[i].start();

        try {
            producer.join();
            for(int i = 0; i<nThread; i++) consumer[i].join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.print("FINE");
    }

    private static class sincrCoda<E>{
        private final ArrayList<E> list= new ArrayList<>();
        private boolean term = false;
        public synchronized void add(E el){
            list.add(el);
            this.notifyAll();
        }
        public synchronized E get() throws InterruptedException {
            while(list.isEmpty() && !term) this.wait();
            if(list.isEmpty() && term) return null;
            return list.remove(0);
        }
        public synchronized void terminate(){this.term = true;}
    }

    private static class ProducerTask implements Runnable{
        private final String path;
        private final sincrCoda<String> sincronizedCoda;
        public ProducerTask(String path, sincrCoda<String> coda){
            this.path = path;
            this.sincronizedCoda = coda;
        }
        @Override
        public void run() {
            File f = new File(path);
            if(f.isDirectory()){
                sincronizedCoda.add(f.getAbsolutePath());
                checkDir(f.getAbsolutePath());
            }
            sincronizedCoda.terminate();
        }
        private void checkDir(String path){
            File dir = new File(path);
            File[] contenuto = dir.listFiles();

            for (File f : contenuto) {
                if (f.isDirectory()) {
                    sincronizedCoda.add(f.getAbsolutePath());
                    checkDir(f.getAbsolutePath());
                }
            }
        }
    }
    private static class ConsumerTask implements Runnable{
        private final sincrCoda<String> sincronizedCoda;
        private final int buffSize = 1000;
        public ConsumerTask(sincrCoda<String> coda){
            this.sincronizedCoda = coda;
        }
        @Override
        public void run() {
            while(true) {
                String path = null;
                try {
                    path = sincronizedCoda.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(path == null) return;
                File dir = new File(path);

                byte[] carattere;

                File[] contenuto = dir.listFiles();
                int i = 0;
                //stampa = stampa.concat("[DIR]" + file.getName() + ": " + contenuto.length + "\n\t");


                for (File f : contenuto) {
                    String stampa = "";
                    carattere = new byte[1];

                    stampa = stampa.concat(dir.getName() + "-->" + f.getName());
                    if (!f.isDirectory()) {
                        stampa = stampa.concat(":\n");
                        try {
                            FileInputStream fis = new FileInputStream(f);
                            BufferedInputStream text = new BufferedInputStream(fis, buffSize);
                            Scanner s = new Scanner(text);

                            while(s.hasNextLine()){
                                stampa = stampa.concat("\t" + s.nextLine() + "\n");
                            }

                            s.close();
                            text.close();
                            fis.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    stampa = stampa.concat("\n");
                    System.out.print(stampa);
                    i++;
                }
            }
        }
    }
}
