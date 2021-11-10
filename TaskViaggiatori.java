import java.util.concurrent.ThreadLocalRandom;

public class TaskViaggiatori implements Runnable{
    private final String name;
    private final ThreadLocalRandom r = ThreadLocalRandom.current();

    public TaskViaggiatori(String name){
        this.name = name;
    }
    public void run() {
        System.out.println("Viaggiatore {"+ name + "}: sto acquistando un biglietto...");
        try {
            Thread.sleep(r.nextInt(1001));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Viaggiatore {" + name + "}: ho acquistato il biglietto");
    }

}
