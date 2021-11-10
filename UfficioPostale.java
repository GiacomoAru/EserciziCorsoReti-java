import java.util.Random;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.*;

public class UfficioPostale {

    private final Random r = new Random(System.currentTimeMillis());
    private final ThreadPoolExecutor threadPool;
    private final BlockingQueue<Runnable> codaSalaPrincipale;
    private final Thread gestoreCode;
    private boolean terminazione = false;

    public UfficioPostale(int k, boolean keepWorkerAlive){
        if(keepWorkerAlive)
            this.threadPool = new ThreadPoolExecutor(4,4,0, TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<>(k));
        else
            this.threadPool = new ThreadPoolExecutor(0,4,500, TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<>(k));
        this.codaSalaPrincipale = new LinkedBlockingQueue<>();

        gestoreCode = new Thread(new TaskGestoreCode(this));
        gestoreCode.start();
    }

    public void arrivoPersone(Vector<String> v) throws InterruptedException {
        for (String s: v) {
            codaSalaPrincipale.put(new TaskPoste(s, r.nextInt(1000)));
        }
    }
    public void arrivoPersona(String s) throws InterruptedException {
        codaSalaPrincipale.put(new TaskPoste(s, r.nextInt(2000)));
    }

    private void aggiornaCode() throws InterruptedException {
        boolean passato = true;
        Runnable task = null;
        while(!terminazione) {
            while (!codaSalaPrincipale.isEmpty() || !passato) {
                if (passato) task = codaSalaPrincipale.take();
                try {
                    threadPool.execute(task);
                    passato = true;
                } catch (RejectedExecutionException e) {
                    passato = false;
                }
            }

        }
        Thread.currentThread().interrupt();
    }

    public void finisciETermina() throws InterruptedException {
        terminazione = true;
        this.termina();
    }
    public void termina() throws InterruptedException {
        if(terminazione) gestoreCode.join();
        else gestoreCode.interrupt();

        threadPool.shutdown();
        threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
    }

    public static void main(String... args) throws InterruptedException {
        Scanner s = new Scanner(System.in);
        System.out.println("inserire k: ");
        int k = s.nextInt();

        System.out.println("inserire il numero di persone totale: ");
        int n = s.nextInt();

        Vector<String> vPersone = new Vector<>();
        for(int i = 0; i<n; i++) vPersone.add("Signore " + i);

        System.out.println("Soluzione 1 arrivo in blocco");
        System.out.println("...si parte...");
        UfficioPostale uff = new UfficioPostale(k, false);
        uff.arrivoPersone(vPersone);
        uff.finisciETermina();
        System.out.println("...fine...");

        System.out.println("Soluzione 2 arrivo continuo");
        System.out.println("inserire l'intervallo tra ogni arrivo (millisecondi): ");
        int intArrivo = s.nextInt();
        System.out.println("...si parte...");
        uff = new UfficioPostale(k, false);
        for (String str: vPersone) {
            uff.arrivoPersona(str);
            Thread.sleep(intArrivo);
        }
        uff.finisciETermina();
        System.out.println("...fine...");
    }

    private static class TaskPoste implements Runnable{
        private final String nome;
        private final int waitTime;
        private TaskPoste(String nome, int waitTime){
            this.nome = nome;
            this.waitTime = waitTime;
        }
        @Override
        public void run() {
            System.out.println("{" + Thread.currentThread() + "}" + nome + ": operando operosamente per " + waitTime + " millisecondi");
            try {
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(nome + ": finito! me ne vado");
        }
    }
    private static class TaskGestoreCode implements Runnable{
        private final UfficioPostale up ;
        public TaskGestoreCode(UfficioPostale p){
            this.up = p;
        }

        @Override
        public void run() {
            try {
                up.aggiornaCode();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}














