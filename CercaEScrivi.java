import java.io.*;

public class CercaEScrivi {

    private static final String pathIniziale = "D:\\jack\\jack-\\VirtualBox ide\\RETI\\Fine\\esercizioCartelle\\dir1";
    private static OutputStream outFile;
    private static OutputStream outDir;
    private static int lettura = 0;

    static {
        try {
            outDir = new FileOutputStream(new File("D:\\jack\\jack-\\VirtualBox ide\\RETI\\Fine\\esercizioCartelle\\directories.txt"));
            outFile = new FileOutputStream(new File("D:\\jack\\jack-\\VirtualBox ide\\RETI\\Fine\\esercizioCartelle\\files.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        try {
            writeDF(new File(pathIniziale));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeDF(File f) throws IOException {
        if(f.isDirectory()){

            File l[] = f.listFiles();
            String toWrite = lettura++ + ": " + f.getName() + "   File contenuti: " + l.length + "\n";
            outDir.write(toWrite.getBytes());

            for(int i = 0; i< l.length; i++){
                writeDF(l[i]);
            }
        }else{
            String toWrite = lettura++ + ": " + f.getName() + "\n";
            outFile.write(toWrite.getBytes());
        }
    }
}