import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import java.util.Scanner;

public class FrequenzaChar {

    public static void main(String[] Args){
        Scanner s = new Scanner(System.in);
        System.out.println("Inserire il path del file da analizzare:");
        String filePath = s.nextLine();

        try(FileChannel source = (new FileInputStream(filePath).getChannel());
            FileChannel dest = (new FileOutputStream("D:\\jack\\jack-\\VirtualBox ide\\RETI\\Fine\\esercizioCartelle\\out.txt")).getChannel()){
            analize(source, dest);
        } catch(Exception e){
            e.printStackTrace();
            System.exit(-1);
        }
        System.out.println("\nFile analizzato!");
    }

    private static void analize(FileChannel src, FileChannel dest) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocateDirect(1000 * 1024);//1024 byte per volta
        Vector<Character> charV = new Vector<>();
        Vector<Integer> intV = new Vector<>();
        while (src.read(buffer) != -1) {
            // prepararsi a leggere i byte che sono stati inseriti nel buffer
            buffer.flip();

            while(buffer.hasRemaining()){
                //Character cLetto = Character.toUpperCase((char) buffer.get());
                Character cLetto = (char) buffer.get();
                if(!cLetto.equals('\r') && !cLetto.equals('\n') /*&& !cLetto.equals('\f')*/) {
                    if (!charV.contains(cLetto)) {
                        charV.add(cLetto);
                        intV.add(1);
                    } else intV.set(charV.indexOf(cLetto), 1 + intV.get(charV.indexOf(cLetto)));

                    //System.out.println(charV);
                    //System.out.println(intV);
                }
            }
            // non è detto che tutti i byte siano trasferiti, dipende da quanti
            // bytes la write ha scaricato sul file di output
            // compatta i bytes rimanenti all'inizio del buffer
            // se il buffer è stato completamente scaricato, si comporta come clear()
            buffer.compact();
        }

        buffer.clear();
        String s = "Frequenza caratteri:\n";
        buffer.put(s.getBytes());
        buffer.flip();
        while(buffer.hasRemaining()) dest.write(buffer);


        for(int i = 0; i < charV.size(); i++){
            buffer.clear();
            s = "'" + charV.get(i) + "'" +" = " + intV.get(i) + "\n";
            buffer.put(s.getBytes());
            buffer.flip();
            while(buffer.hasRemaining()) dest.write(buffer);
        }

        buffer.clear();
        s = "FINE\n";
        buffer.put(s.getBytes());
        buffer.flip();
        while(buffer.hasRemaining()) dest.write(buffer);
    }
}
