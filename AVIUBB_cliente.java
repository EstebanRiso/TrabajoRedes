import java.io.*;
import java.net.*;

class AVIUBB_cliente extends Thread{
    public static String EOL ="\\r\\n"; 
    private Socket clientSocket;
    private PrintWriter outToServer;
    private BufferedReader inFromServer;
    private BufferedReader inFromUser;
    private boolean stop;

    public AVIUBB_cliente() throws Exception{

        clientSocket = new Socket("127.0.0.1", 10987);
        outToServer = new PrintWriter(clientSocket.getOutputStream(), true);
        inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        inFromUser = new BufferedReader(new InputStreamReader(System.in));
        stop = false;
        start();
    }

    BufferedReader getReaderKeyBoard(){
        return inFromUser;
    }

    BufferedReader getReader(){
        return inFromServer;
    }

    boolean getStop(){
        return stop;
    }

    void setStop(boolean v){
        stop = v;
    }
    void close() throws IOException{
        inFromUser.close();
        inFromServer.close();
        clientSocket.close();
    }

    public void run(){

        String sentence="";

        while(!getStop()){
            try{
                sentence = getReaderKeyBoard().readLine();
            }
            catch(Exception e){
                e.printStackTrace();
            }
            outToServer.println(sentence);
            if(sentence.startsWith("FIN")) setStop(true);
        }
    }

    public static void main(String argv[]) throws Exception{
        AVIUBB_cliente cliente = new AVIUBB_cliente();
        int index;
        String c;
	    //System.out.println("Se ha conectado con exito, por favor escriba un mensaje: ");
	    while(!cliente.getStop()){
            c = cliente.getReader().readLine();
            index = c.indexOf(EOL);
            if(index >= 0) System.out.println(c.substring(0,index));
            else System.out.println(c);
        }
        cliente.close();
    }
}
