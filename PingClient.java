import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class PingClient {


    public static void main(String[]Args) {
        int port = 0;
        int pacchettiTotali = 10;

        String host = null;
        try{
            if (Args.length < 2) {
                System.err.println("ERR : Missing Arguments");
                System.exit(-1);
            }
            host = Args[0];
            port = Integer.parseInt(Args[1]);
            if(port > 65535 || port < 1) {
                System.err.println("ERR : -arg 0");
                System.exit(-1);
            }

        }catch(NumberFormatException e){
            System.err.println("ERR: -arg 1");
            System.exit(-1);
        }

        int pPersi = 0;
        int pInviati = 0;
        int pRicevuti = 0;

        double rttAvg = 0;
        double rttMin = Double.MAX_VALUE;
        double rttMax = Double.MIN_VALUE;

        try(DatagramSocket socket = new DatagramSocket(0)) {
            socket.setSoTimeout(200);
            InetAddress hostAddress = InetAddress.getByName(host);

            for(int i = 0; i < pacchettiTotali; i++){

                long timestamp = System.nanoTime();
                byte[] buffer = ("PING " + i + " " + timestamp ).getBytes();
                String bufferToString = new String(buffer);

                DatagramPacket request = new DatagramPacket(buffer, buffer.length, hostAddress , port);
                DatagramPacket response = new DatagramPacket(new byte[1024], 1024);

                socket.send(request);
                pInviati++;

                try {
                    socket.receive(response);
                    double RTT = (double) (System.nanoTime() - timestamp) / (1000000);
                    System.out.print(bufferToString);
                    System.out.printf(" RTT = %.2f ms\n", RTT);

                    rttAvg += RTT;
                    if(rttMin > RTT) rttMin = RTT;
                    if(rttMax < RTT) rttMax = RTT;
                    pRicevuti++;
                }
                catch (SocketTimeoutException e) {
                    pPersi++;
                    System.out.println(bufferToString + " *");
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        rttAvg /= (double) pRicevuti;
        System.out.println("\n----------- PING Statistics -----------");
        System.out.println(pInviati + " packets transmitted, " + pRicevuti + " packets received, "
                + (100 * pPersi)/pInviati + "% packet loss");
        System.out.printf("round-trip (ms) min/avg/max = %.2f/%.2f/%.2f\n", rttMin, rttAvg, rttMax);
    }
}
