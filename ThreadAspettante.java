public class ThreadAspettante extends Thread {
    private Thread t = null;
    public ThreadAspettante(Thread t){
        this.t = t;
    }
    public void run(){
        if( t == null){
            System.out.println("Non aspetto NESSUNO");
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("ciao");
        }
        else{
            System.out.println("aspetto: "+t.getName());
            try {
                t.join();
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println(t.getName() + " arrivato. ciao");
        }
    }
}
