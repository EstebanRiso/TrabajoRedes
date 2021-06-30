import java.io.*; 
import java.net.*; 

class AVIUBB_cliente{ 

    public static void main(String argv[]) throws Exception 
    { 
        String sentence; 
        String echoSentence; 
      while(true){
        BufferedReader inFromUser = 
          new BufferedReader(new InputStreamReader(System.in)); 

        Socket clientSocket = new Socket("127.0.0.1", 1234); 

        PrintWriter outToServer = new PrintWriter(clientSocket.getOutputStream(), true); 

	      BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); 

        sentence = inFromUser.readLine(); 

        //outToServer.println(sentence+" Puerto:"+clientSocket.getLocalPort()); 
        outToServer.println(sentence); 

        echoSentence = inFromServer.readLine(); 

        System.out.println("DEL SERVIDOR: " + echoSentence); 
        }
        //clientSocket.close(); 
                   
    } 
} 