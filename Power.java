import java.util.concurrent.Callable;

public class Power implements Callable<Double> {

    private final int exp;
    private final int base;

    public Power(int base, int exp){
        this.base = base;
        this.exp = exp;
    }

    @Override
    public Double call() throws Exception {
        System.out.println("Esecuzione " + base + "^"+ exp + " in "+ Thread.currentThread().getName());
        return  Math.pow(base,exp);
    }
}
