import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class SimulazioneLaboratorioM {
    private static class Computer{
        private final int idPC;
        public Computer(int id){
            this.idPC = id;
        }
        public synchronized void usaPc(int tempoMS) throws InterruptedException {
            System.out.println("[" + Thread.currentThread().getName() + "]---->uso il PC <" + idPC + "> per " + tempoMS + "ms");
            Thread.sleep(tempoMS);
            System.out.println("[" + Thread.currentThread().getName() + "]---->rilascio il PC <" + idPC + ">" );
        }
    }

    private static class Laboratorio{

        private final int nComputer;
        private final Vector<Computer> PCs;

        private int docAttesa = 0;
        private int PCInUso = 0;

        private int[] code;
        private int[] prenotazioneTesisti;

        public Laboratorio(int nComputer){
            this.nComputer = nComputer;

            this.code = new int[nComputer];
            for(int i = 0; i<nComputer; i++) this.code[i] = 0;
            this.prenotazioneTesisti = new int[nComputer];
            for(int i = 0; i<nComputer; i++) this.prenotazioneTesisti[i] = 0;

            this.PCs = new Vector<>(nComputer);
            for(int i = 0; i<nComputer; i++) PCs.add(i, new Computer(i));
        }

        public void bloccaEUsaPC(int tempo) throws InterruptedException {
            int idPC = 0;
            synchronized(this){
                //trova un pc abbastanza disponiile
                for(int i = 0; i<nComputer; i++) if(code[i] < code[idPC])idPC = i;

                while(docAttesa != 0 && prenotazioneTesisti[idPC] != 0) wait();

                PCInUso++;
                code[idPC]++;
            }
            PCs.get(idPC).usaPc(tempo); //utilizzo pc
            synchronized (this){
                PCInUso--;
                code[idPC]--;
                notifyAll();
            }
        }
        public void bloccaEUsaPC(int idPC, int tempo) throws InterruptedException {
            synchronized (this){
                while(docAttesa != 0) wait();

                PCInUso++;
                code[idPC]++;
                prenotazioneTesisti[idPC]++;
            }
            PCs.get(idPC).usaPc(tempo);
            synchronized (this){
                PCInUso--;
                code[idPC]--;
                prenotazioneTesisti[idPC]++;
                notifyAll();
            }
        }
        public synchronized void bloccaEUsaLab(int tempo) throws InterruptedException {
            docAttesa++;
            while(PCInUso != 0) wait();

            System.out.println("[" + Thread.currentThread().getName() + "]---->uso il laboratorio per " + tempo + "ms");
            Thread.sleep(tempo);
            System.out.println("[" + Thread.currentThread().getName() + "]---->rilascio il laboratorio");

            docAttesa--;
            notifyAll();
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

        SimulazioneLaboratorioM.Laboratorio lab = new SimulazioneLaboratorioM.Laboratorio(nComputer);

        List<SimulazioneLaboratorioM.Utente> tlist = new ArrayList<>();

        int i;
        for(i = 0; i<studenti; i++) tlist.add(new SimulazioneLaboratorioM.Studente(lab, "Studente "+ i));
        for(i = 0; i<tesisti; i++) tlist.add(new SimulazioneLaboratorioM.Tesista(lab, "Tesista "+ i, r.nextInt(nComputer)));
        for(i = 0; i<docenti; i++) tlist.add(new SimulazioneLaboratorioM.Docente(lab, "Docente " + i));

        Collections.shuffle(tlist);
        for(SimulazioneLaboratorioM.Utente u: tlist) u.start();
    }

    private abstract static class Utente extends Thread{
        protected final ThreadLocalRandom r;
        protected final int usiLab;
        protected final int pausaMillis;
        protected final int usoLabMillis;
        protected final SimulazioneLaboratorioM.Laboratorio lab;

        public Utente(SimulazioneLaboratorioM.Laboratorio lab, String name){
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
    private static class Studente extends SimulazioneLaboratorioM.Utente {

        public Studente(SimulazioneLaboratorioM.Laboratorio lab, String name) {
            super(lab, name);
        }

        public void run(){
            for(int i = 0; i < usiLab; i++){
                try {
                    lab.bloccaEUsaPC(usoLabMillis);
                    Thread.sleep(pausaMillis);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }
    private static class Tesista extends SimulazioneLaboratorioM.Utente {
        private final int idComputer;
        public Tesista(SimulazioneLaboratorioM.Laboratorio lab, String name, int idComputer) {
            super(lab, name);
            this.idComputer = idComputer;
        }

        public void run(){
            for(int i = 0; i < usiLab; i++){
                try {
                    lab.bloccaEUsaPC(idComputer, usoLabMillis);
                    Thread.sleep(pausaMillis);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }
    private static class Docente extends SimulazioneLaboratorioM.Utente {
        public Docente(SimulazioneLaboratorioM.Laboratorio lab, String name) {
            super(lab, name);
        }

        public void run(){
            for(int i = 0; i < usiLab; i++){
                try {
                    lab.bloccaEUsaLab(usoLabMillis);
                    Thread.sleep(pausaMillis);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
