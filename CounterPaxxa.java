import java.util.Vector;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CounterPaxxa {
    public static class Counter {
        int counter = 0;
        public Counter(){}
        public void increment(){counter++;}
        public int getCount(){return counter;}
    }
    public static class ReentrantCounter extends Counter {
        int counter = 0;
        Lock lock = new ReentrantLock();

        @Override
        public void increment(){
            try {
                lock.lock();
                super.increment();
            }finally{lock.unlock();}
        }
        @Override
        public int getCount(){
            int ret;
            try {
                lock.lock();
                ret = super.getCount();
            }finally{lock.unlock();}
            return ret;
        }
    }
    public static class RWLockCounter extends Counter {
        int counter = 0;
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        Lock readLock = lock.readLock();
        Lock writeLock = lock.writeLock();

        @Override
        public void increment(){
            try {
                writeLock.lock();
                super.increment();
            }finally{writeLock.unlock();}
        }
        @Override
        public int getCount(){
            int ret;
            try {
                readLock.lock();
                ret = super.getCount();
            }finally{readLock.unlock();}
            return ret;
        }
    }

    public static class TaskWriter implements Runnable{
        Counter c;
        public TaskWriter(Counter c){
            this.c = c;
        }
        @Override
        public void run() {
            c.increment();
            System.out.println("Incremento");
        }
    }
    public static class TaskReader implements Runnable{
        Counter c;
        public TaskReader(Counter c){
            this.c = c;
        }
        @Override
        public void run() {
            System.out.println("Lettura: " + c.getCount());
        }
    }

    public static long Test(Counter c, ExecutorService e, int ntask){
        long tempoExec = System.currentTimeMillis();
        int i;

        Vector<Runnable> tw = new Vector<>();
        for(i = 0; i<ntask; i++) tw.add(new TaskWriter(c));
        Vector<Runnable> tr = new Vector<>();
        for(i = 0; i<ntask; i++) tr.add(new TaskReader(c));

        for(Runnable task: tw) e.submit(task);
        for(Runnable task: tr) e.submit(task);

        e.shutdown();
        try {
            e.awaitTermination(100000, TimeUnit.SECONDS);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }

        tempoExec -= System.currentTimeMillis();
        return -tempoExec;
    }

    public static void main(String[] args){
        long[] tempi = new long[11];
        int numeroTask = 100000;


        tempi[1] = Test(new ReentrantCounter(),Executors.newCachedThreadPool(),numeroTask);
        tempi[2] = Test(new RWLockCounter(),Executors.newCachedThreadPool(), numeroTask);
        tempi[3] = Test(new ReentrantCounter(),Executors.newFixedThreadPool(1), numeroTask);
        tempi[4] = Test(new ReentrantCounter(),Executors.newFixedThreadPool(2), numeroTask);
        tempi[5] = Test(new ReentrantCounter(),Executors.newFixedThreadPool(4), numeroTask);
        tempi[6] = Test(new ReentrantCounter(),Executors.newFixedThreadPool(10), numeroTask);
        tempi[7] = Test(new RWLockCounter(),Executors.newFixedThreadPool(1), numeroTask);
        tempi[8] = Test(new RWLockCounter(),Executors.newFixedThreadPool(2), numeroTask);
        tempi[9] = Test(new RWLockCounter(),Executors.newFixedThreadPool(4), numeroTask);
        tempi[10] = Test(new RWLockCounter(),Executors.newFixedThreadPool(10), numeroTask);

        tempi[0] = Test(new Counter(),Executors.newCachedThreadPool(), numeroTask);


        System.out.println("###############################################\nTEST:\n");
        for(int i = 0; i<11; i++) System.out.println(tempi[i]);

    }
}
