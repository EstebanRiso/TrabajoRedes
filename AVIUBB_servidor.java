import java.io.*; 
import java.net.*; 
import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime;  

class AVIUBB_servidor{ 


    public static boolean agregar_nuevo_usuario(String dato_recepcionado){
        //recibo cliente 
        if(dato_recepcionado.contains("NUEVO_USUARIO")){
            return true;
        }

        return false;
    }


    public static boolean dejar_recado(String dato_recepcionado){
        //recibo cliente 
        if(dato_recepcionado.contains("DEJAR_RECADO")){
            return true;
        }

        return false;
    }

    public static boolean consultar_recado(String dato_recepcionado){
        //recibo cliente 
        if(dato_recepcionado.contains("CONSULTAR_RECADO")){
            return true;
        }

        return false;
    }

    public static boolean ultimo(String dato_recepcionado){
        //recibo cliente 
        if(dato_recepcionado.contains("ULTIMO")){
            return true;
        }

        return false;
    }

    public static boolean fin(String dato_recepcionado){
        //recibo cliente 
        if(dato_recepcionado.contains("FIN")){
            return true;
        }

        return false;
    }


  public static void main(String argv[]) throws Exception 
    { 
      String clientSentence; 
      Boolean minuscula=null;
      Boolean mostrardia=false;
      ServerSocket welcomeSocket = new ServerSocket(1234); 
      DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
      LocalDateTime now = LocalDateTime.now();  
  
      while(true) { 



           Socket connectionSocket = welcomeSocket.accept(); 

           BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream())); 
       
	        PrintWriter outToClient =  new PrintWriter(connectionSocket.getOutputStream(), true); 

           

           clientSentence = inFromClient.readLine(); 


            if(agregar_nuevo_usuario(clientSentence)){

            }
            else if(dejar_recado(clientSentence)){
           
            }
            else if(consultar_recado(clientSentence))
            {

            }
            else if(ultimo(clientSentence)){

            }
            else if(fin(clientSentence)){

            }
            


        } 
     }
     // si se usa una condicion para quebrar el ciclo while, se deben cerrar los sockets!
     // connectionSocket.close(); 
     // welcomeSocket.close(); 
    
} 
 