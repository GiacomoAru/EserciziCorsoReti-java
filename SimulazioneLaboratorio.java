import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class SimulazioneLaboratorio {

    private static class Laboratorio{//thread safe
        private final int nComputer;


        private final Lock lockLabor = new ReentrantLock();
        private final Condition attesaLabor = lockLabor.newCondition();
        private int docAttesa = 0;
        private int PCInUso = 0;
        private int[] code;
        private int[] prenotazioneTesisti;

        private final Vector<Lock> lockPC;

        public Laboratorio(int nComputer){
            this.nComputer = nComputer;

            this.code = new int[nComputer];
            for(int i = 0; i<nComputer; i++) this.code[i] = 0;
            this.prenotazioneTesisti = new int[nComputer];
            for(int i = 0; i<nComputer; i++) this.prenotazioneTesisti[i] = 0;

            this.lockPC = new Vector<>(nComputer);
            for(int i = 0; i<nComputer; i++) lockPC.add(i, new ReentrantLock());
        }

        public void bloccaEUsaPC(int tempo){
            int idPC = 0;
            lockLabor.lock();
            try{
                //trova un pc abbastanza disponiile
                for(int i = 0; i<nComputer; i++) if(code[i] < code[idPC])idPC = i;

                while(docAttesa != 0 && prenotazioneTesisti[idPC] != 0) attesaLabor.await();

                PCInUso++;
                code[idPC]++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally{lockLabor.unlock();}


            lockPC.get(idPC).lock();
            try{

                System.out.println("[" + Thread.currentThread().getName() + "]---->uso il PC <" + idPC + "> per " + tempo + "ms");
                Thread.sleep(tempo);
                System.out.println("[" + Thread.currentThread().getName() + "]---->rilascio il PC <" + idPC + ">" );

            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally{lockPC.get(idPC).unlock();}


            lockLabor.lock();
            try{
                PCInUso--;
                code[idPC]--;
                attesaLabor.signalAll();
            }finally{lockLabor.unlock();}
        }
        public void bloccaEUsaPC(int idPC, int tempo){
            lockLabor.lock();
            try{
                while(docAttesa != 0) attesaLabor.await();

                PCInUso++;
                code[idPC]++;
                prenotazioneTesisti[idPC]++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally{lockLabor.unlock();}


            lockPC.get(idPC).lock();
            try{

                System.out.println("[" + Thread.currentThread().getName() + "]----> uso il PC <" + idPC + "> per " + tempo + "ms");
                Thread.sleep(tempo);
                System.out.println("[" + Thread.currentThread().getName() + "]----> rilascio il PC <" + idPC + ">");

            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally{lockPC.get(idPC).unlock();}


            lockLabor.lock();
            try{
                PCInUso--;
                code[idPC]--;
                prenotazioneTesisti[idPC]++;
                attesaLabor.signalAll();
            }finally{lockLabor.unlock();}
        }
        public void bloccaEUsaLab(int tempo){
            lockLabor.lock();
            try{

                docAttesa++;
                while(PCInUso != 0) attesaLabor.await();

                System.out.println("[" + Thread.currentThread().getName() + "]---->uso il laboratorio per " + tempo + "ms");
                Thread.sleep(tempo);
                System.out.println("[" + Thread.currentThread().getName() + "]---->rilascio il laboratorio");

                docAttesa--;
                attesaLabor.signalAll();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally{lockLabor.unlock();}
        }
    }

    public static void main(String[] args){

        int studenti;
        int tesisti;
        int docenti;
        int nComputer = 20;

        Scanner s = new Scanner(System.in);
        System.out.println("Inserire il numero di studenti:");
        studenti = s.nextInt();
        System.out.println("Inserire il numero di tesisti:");
        tesisti = s.nextInt();
        System.out.println("Inserire il numero di docenti:");
        docenti = s.nextInt();
        ThreadLocalRandom r = ThreadLocalRandom.current();

        Laboratorio lab = new Laboratorio(nComputer);

        List<Utente> tlist = new ArrayList<>();

        int i;
        for(i = 0; i<studenti; i++) tlist.add(new Studente(lab, "Studente "+ i));
        for(i = 0; i<tesisti; i++) tlist.add(new Tesista(lab, "Tesista "+ i, r.nextInt(nComputer)));
        for(i = 0; i<docenti; i++) tlist.add(new Docente(lab, "Docente " + i));

        Collections.shuffle(tlist);
        for(Utente u: tlist) u.start();
    }

    private abstract static class Utente extends Thread{
        protected final ThreadLocalRandom r;
        protected final int usiLab;
        protected final int pausaMillis;
        protected final int usoLabMillis;
        protected final Laboratorio lab;

        public Utente(Laboratorio lab, String name){
            //pause e tempo di utilizzo uguali per ogni utente, buon compromesso tra astrazione e semplicità implementativa
            r  = ThreadLocalRandom.current();//serve per ogni possibile utente
            this.usiLab = r.nextInt(1, 11);//da 1 a 10
            this.pausaMillis = r.nextInt(100, 250);//tempo di pausa tra 2 usi di laboratorio
            this.usoLabMillis = r.nextInt(150, 300);//tempo di uso del laboratorio
            this.lab = lab;

            this.setName(name);
        }
        @Override
        public void run(){
            //fai qualcosa se sei un figlio, se no nulla
        }
    }
    private static class Studente extends Utente{

        public Studente(Laboratorio lab, String name) {
            super(lab, name);
        }

        public void run(){
            for(int i = 0; i < usiLab; i++){
                lab.bloccaEUsaPC(usoLabMillis);
                try {
                    Thread.sleep(pausaMillis);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }
    private static class Tesista extends Utente{
        private final int idComputer;
        public Tesista(Laboratorio lab, String name, int idComputer) {
            super(lab, name);
            this.idComputer = idComputer;
        }

        public void run(){
            for(int i = 0; i < usiLab; i++){
                lab.bloccaEUsaPC(idComputer, usoLabMillis);
                try {
                    Thread.sleep(pausaMillis);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }
    private static class Docente extends Utente{
        public Docente(Laboratorio lab, String name) {
            super(lab, name);
        }

        public void run(){
            for(int i = 0; i < usiLab; i++){
                lab.bloccaEUsaLab(usoLabMillis);
                try {
                    Thread.sleep(pausaMillis);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}















