
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class HTTP_server {

    public static void main(String[] args) throws InterruptedException {
        Thread server = new Thread(new core(6969));
        Scanner scan = new Scanner(System.in);

        server.setDaemon(true);
        server.start();

        int fine = scan.nextInt();
        while(fine != 0){
            fine = scan.nextInt();
        }
        System.out.println("Goodbye");
    }

    private static class core implements Runnable{

        private final int port;
        public core(int port){
            this.port = port;
        }

        @Override
        public void run() {
            ServerSocket s = null;
            try {
                s = new ServerSocket(port);
                System.out.println("Socket aperto porta:  " +port);

                Socket clientSocket = null;
                while(true) {
                    try {
                        clientSocket = s.accept();
                        System.out.println("Client connesso -> " + clientSocket.getRemoteSocketAddress().toString());

                        Thread t =  new Thread(new requestHandler(clientSocket));
                        t.start();
                    } catch (SocketException e) {
                        System.err.println("serverChiuso");
                    }catch (IOException e) {
                        System.err.println("errore accept");
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private static class requestHandler implements Runnable {
        private final Socket s;

        public requestHandler(Socket s){
            this.s = s;
        }
        @Override
        public void run() {
            try(BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                BufferedOutputStream out = new BufferedOutputStream(s.getOutputStream());
            ){

                String linea = in.readLine();
                String info[] = linea.split(" ");

                System.out.println("Richiesta: " + info[0]  + " || " + info[1] + " || "  + info[2]);

                File f = new File(info[1]);

                if(f.exists()) {
                    if(f.isDirectory()){
                        String httpResponse = "HTTP/1.1 406 Not Acceptable\r\n\r\n<html><body><h1>Il file richiesto e' una cartella</h1></body></html>";
                        out.write(httpResponse.getBytes("UTF-8"));
                    }
                    else {
                        String httpResponse = "HTTP/1.1 202 Accepted\r\n\r\n";
                        out.write(httpResponse.getBytes("UTF-8"));
                        FileInputStream fis = new FileInputStream(f);

                        out.write(fis.readAllBytes());
                        fis.close();
                    }
                }
                else{
                    String httpResponse = "HTTP/1.1 404 Not Found\r\n\r\n<html><body><h1>File non trovato</h1></body></html>";
                    out.write(httpResponse.getBytes("UTF-8"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException ne){
                System.err.println("Get senza file");
            }finally {
                try {
                    s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
