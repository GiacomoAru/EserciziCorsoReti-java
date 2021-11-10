import java.util.Vector;

import static java.lang.Thread.sleep;

public class Prova1 {


    public static void main(String... args){
        System.out.println("ciao");
        Vector vt = new Vector<Thread>(10);

        vt.add(0, new ThreadAspettante(null));
        for(int i = 1; i<10; i++){
            vt.add(i, new ThreadAspettante((Thread) vt.get(i-1)));
        }

        for(int i = 0; i<10; i++) ((Thread)vt.get(i)).start();

    }
}
