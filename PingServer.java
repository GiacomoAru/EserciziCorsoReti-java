import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;

public class PingServer{

    public static void main (String[]Args) {
        int port = 0;
        long seed = 0;

        double percFall = 25;
        int attesaMax = 300;

        try{
            if (Args.length < 1) {
                throw new NumberFormatException();
            }
            port = Integer.parseInt(Args[0]);
            if(port > 65535 || port < 1) throw new NumberFormatException();

        }catch(NumberFormatException e){
            System.err.println("ERR: -arg 0");
            System.exit(-1);
        }
        if(Args.length > 1){
            try{
                seed = Long.parseLong(Args[1]);
            }catch(NumberFormatException e){
                System.err.println("ERR: -arg 1");
                System.exit(-1);
            }
        }else seed = System.nanoTime();

        Random rand = new Random(seed);
        try (DatagramSocket socket = new DatagramSocket(port)) {
            while (true) {
                try {
                    double isLost = rand.nextDouble() * 100;
                    int lat = rand.nextInt(attesaMax);

                    DatagramPacket request = new DatagramPacket(new byte[1024], 1024);
                    socket.receive(request);

                    int clientPort = request.getPort();
                    String data = new String(request.getData());
                    InetAddress clientAddress = request.getAddress();

                    System.out.print("{host:" + clientAddress+" || port:"+clientPort+"} -> "+data+" =");

                    if (isLost < percFall) System.out.println(" Pacchetto perso");
                    else {
                        Thread.sleep(lat);
                        socket.send(request);
                        System.out.println(" Pacchetto inviato con ritardo di: " + lat + "ms");
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

    }
}
