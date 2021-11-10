import java.util.Random;
import java.util.concurrent.*;

public class SalaBiglietteria {
    private final ExecutorService ThreadPool;

    public SalaBiglietteria(){
        ThreadPool = new ThreadPoolExecutor(5, 5, 0, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(10));
    }
    public void close(){ThreadPool.shutdown();}
    private void viaggiatoreArrivato(String nome){
        try {
            ThreadPool.execute(new TaskViaggiatori(nome));
        }catch(RejectedExecutionException e){System.out.println("Viaggiatore {" + nome + "}: sala esaurita");}
    }
    public static void main(String... args) throws InterruptedException {
        SalaBiglietteria sb = new SalaBiglietteria();

        for(int i = 0; i < 50; i++){
            sb.viaggiatoreArrivato("Signor " + i);
            Thread.sleep(50);
        }
        sb.close();
    }
}
