import java.io.*;
import java.net.*;
import java.util.Hashtable;
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class Cliente{
    public static String EOL ="\\r\\n";
    private static int id = 0;
	private String cid;
	private Socket socket;
	private BufferedReader reader;
	private PrintWriter writer;
    private Timestamp time;

	Cliente(Socket s) throws IOException{
		socket = s;
		reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
    	  writer = new PrintWriter(s.getOutputStream(), true);
		cid = "Usuario_"+(++id);
        setTimeStamp();
	}

	private BufferedReader getReader(){
		return reader;
	}

	private PrintWriter getWriter(){
		return writer;
	}

    private Socket getSocket(){
        return socket;
    }

	public String getNombre(){
		return cid;
	}

	public void send(String mensaje){
		getWriter().println(mensaje+EOL);
        getWriter().flush();
	}

    public boolean ready() throws IOException{
        return getReader().ready();
    }


    public boolean isNULL(){
        return getSocket() == null;
    }

    public boolean isClosed(){
        return getSocket().isClosed();
    }

    public void close() throws IOException{
        getReader().close();
        getWriter().close();
        getSocket().close();
    }

    public String receive() throws IOException{
        return getReader().readLine();
    }

    public void setNombre(String name){
        cid = name;
    }

    public Timestamp getTimeStamp(){
        return time;
    }

    public void setTimeStamp(){
        time = new Timestamp(System.currentTimeMillis());
    }

    public long getDateDiff(Timestamp newTs) {
        long diffInMS = newTs.getTime() - getTimeStamp().getTime();
        return TimeUnit.MINUTES.convert(diffInMS, TimeUnit.MILLISECONDS);
    }
}
/*public class globales {
    public static ArrayList<String> mensajelista;
}*/
class AVIUBB_servidor extends Thread {
	private Hashtable<String,Cliente> clientes;
    public static String mensajelista[] = {};
 	private boolean ready, ver;
	public static String ERROR_COMANDO = "401: No se reconoce comando o bien esta incompleto.";
    public static String ERROR_DESTINATION = "402: No se reconoce destino.";
    public static String ERROR_PRIVATE = "403: No se reconoce formato mensaje PRIVATE.";
    public static String ERROR_NOMBRE = "404: Otro usuario tiene el mismo pseudonimo, escoja otro.";
    public static String SUCCESS_COMANDO = "200: OK.";
    public static String QUIT = "201: Goodbye.";
    
    public AVIUBB_servidor(){
      	ready = false;
        ver = false;
      	clientes = new Hashtable<String,Cliente>();
    }

    public void add(Socket connectionSocket) throws IOException{
	    ready = false;
	    Cliente cliente = new Cliente(connectionSocket);
        clientes.put(cliente.getNombre(), cliente);
	    ready = true;
    }

    public void remove(Cliente cliente) throws IOException{
        ready = false;
        if(!cliente.isClosed()) cliente.close();
        clientes.remove(cliente.getNombre());
        ready = true;
    }

    public String getString(){
	    Enumeration lista = clientes.elements();
	    String str = "";
	    int i = 1;
	    while(lista.hasMoreElements()){
			Cliente cliente = (Cliente) lista.nextElement();
			str= str+i+") "+cliente.getNombre()+"\n";
			++i;
		}
        return str;
    }

    public String getStringLast(){
	    Enumeration lista = clientes.elements();
	    String str = "";
	    while(lista.hasMoreElements()){
			Cliente cliente = (Cliente) lista.nextElement();
			str= cliente.getNombre();
		}
        return str;
    }

    public void broadcast(Cliente origen, String mensaje){
        Enumeration otros = clientes.elements();
        while(otros.hasMoreElements()){
            Cliente otro = (Cliente)otros.nextElement();
            if(otro!=origen) otro.send(mensaje);
        }
    }

    public boolean transmit(String destino, String mensaje){
        Cliente dst = clientes.get(destino);
        if(dst!=null) dst.send(mensaje);
        return dst!=null;
    }

    public void removeIdleClient(Cliente idleCliente){
        try{
            ready = false;
            if (idleCliente.isNULL()) remove(idleCliente);
            else{
                if(idleCliente.isClosed()) remove(idleCliente);
                else {
                    long elapsedTime = idleCliente.getDateDiff(new Timestamp(System.currentTimeMillis()));
                    if(elapsedTime> 1000) System.out.println(idleCliente.getNombre()+"/"+elapsedTime);
                }  
            }
            ready = true;
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public static String[] add_element(int n, String array[], String recado) 
    { 
        int i; 
 
        String newArray[] = new String[n + 1]; 
        for (i = 0; i<n; i++) newArray[i] = array[i]; 
        newArray[n] = recado; 
 
        return newArray; 
    } 

    public void run(){

	    String clientSentence;
	    while(clientes!=null){

		    Enumeration lista = clientes.elements();

		    while(lista.hasMoreElements()){
			    Cliente cliente = (Cliente)lista.nextElement();
			    if(ready){
			        try {
				        if(cliente.ready()){
                            cliente.setTimeStamp();
				            clientSentence = cliente.receive().trim();
				            //System.out.println("LOG Datos recibidos:"+clientSentence+" desde "+ cliente.getNombre());
                            if(clientSentence==null){
                                removeIdleClient(cliente);
                            }
                            else{
                                if(clientSentence.startsWith("NUEVO_USUARIO")){
                                    String[] parts = clientSentence.split(" ");
                                    if(parts.length >1){
                                        String newNombre = parts[1].trim();
                                        if(!clientes.containsKey(newNombre)){
                                            clientes.remove(cliente.getNombre());
                                            cliente.setNombre(newNombre);
                                            clientes.put(newNombre, cliente);
                                            cliente.send(SUCCESS_COMANDO);
                                            ver = true;
                                            clientSentence ="Lista de comandos:\nLIST (Nombre usuarios online).\n";
        			            			clientSentence = clientSentence + "RENAME <nombre> (Cambia tu nombre).\n";
		        				            clientSentence = clientSentence + "PRIVATE <nombre> <texto>(envia <texto> a un usuario.)\n";
            	        					clientSentence = clientSentence + "ALL <texto> (envia <texto> a todos)\n";
			                    			clientSentence = clientSentence + "SHOW (muestra mi nombre)\n"; //Diego si eliminaste Show trata de mostrar un comentario
                                            clientSentence = clientSentence + "FIN (Dejar el servicio)\n";
                                            clientSentence = clientSentence + "Ejemplo:\n";
                        				    clientSentence = clientSentence + "PRIVATE Pedro Hola (envia el mensaje \"hola\" al usuario registrado como Pedro)\n";
				                    		clientSentence = clientSentence + "Precaucion!!: Servidor diferencia entre letras mayusculas y minusculas\n";
            			        			cliente.send(clientSentence);
                                        }
                                        else{
                                            cliente.send(ERROR_NOMBRE+":"+newNombre);
                                        }
                                    }
                                    else cliente.send(ERROR_COMANDO+":"+clientSentence);
            						
			            		}
                                else if(clientSentence.startsWith("DEJAR_RECADO") && ver == true){
                                		String[]  parts = clientSentence.split(" ");
                                        if(parts.length > 2) {
                                            int index = clientSentence.indexOf(parts[2]);
                                            String destino = parts[1].trim();
                            				String mensaje = clientSentence.substring(index);
                                            mensajelista = add_element(mensajelista.length, mensajelista, destino+"/X/x(De "+cliente.getNombre()+"): "+mensaje);
                                                        
					                    }
                                        else{
                						    cliente.send(ERROR_PRIVATE+":"+clientSentence);
						                }
	                            }
                                else if(clientSentence.startsWith("CONSULTAR_RECADO") && ver == true){
                                        
                                        String[]  parts = clientSentence.split(" ");
                                        
                                        if(parts.length > 1) {
                                            
                                        String usuarioc = parts[1].trim();
                                        
                                        for (int i = 0; i < mensajelista.length; i++){
                                            if(mensajelista[i].startsWith(usuarioc+"/X/x")){
                                                    String[] recado = mensajelista[i].split("/X/x");
                                                    clientSentence = recado[1].trim();
                                                    cliente.send(clientSentence);
                                                }
                                            }
					                    }
                                        else{
                					        cliente.send(ERROR_PRIVATE+":"+clientSentence);
			                                }
                				}               
                                else if(clientSentence.equals("ULTIMO") && ver == true){ //                                funcionalidad construida por Esteban Risopatrón
                                         DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");  //dia mes fecha 
                                         LocalDateTime now = LocalDateTime.now();  
                                         String fecha_y_hora=dtf.format(now);
                                         clientSentence="La fecha y la hora son:"+fecha_y_hora;
                                         cliente.send(clientSentence);
                                      }
				                else if(clientSentence.equals("LIST") && ver == true){
                            					   clientSentence = "Lista de seudonimos:\n"+getString();
		        			                       cliente.send(clientSentence);
                            					}
                                else if(clientSentence.equals("MENSAJE_MAS_ANTIGUO") && ver == true){ //MENSAJE MAS ANTIGUO (TOTAL NO DE UN USUARIO)
                                    if(mensajelista.length>=1){                                                  //funcionalidad creada por Esteban Risopatrón
                                            clientSentence="El mensaje mas antiguo es "+mensajelista[0];
                                    }
                                        else{clientSentence="El servidor no posee mensajes registrados hasta el momento";}
                                                cliente.send(clientSentence);
                                    }  
                                else if(clientSentence.startsWith("TOTAL_RECADOS") && ver == true){ 

                                    String[]  parts = clientSentence.split(" ");
                                    int contador=0;
                                    System.out.println("PASO AQUI");
                                        
                                    if(parts.length > 1) {

                                            String usuarioc = parts[1].trim();

                                            for (int i = 0; i < mensajelista.length; i++){
                                                    if(mensajelista[i].startsWith(usuarioc+"/X/x")){
                                                        contador++;
                                                    }
                                            }
                                                                
                                            clientSentence="la cantidad de recados de "+usuarioc+" es:"+contador;
                                            cliente.send(clientSentence); 
                                            }

                                }
                                else if(clientSentence.equals("FIN") && ver == true){
                                    cliente.send(QUIT);
                                    remove(cliente);
                                    }
                                    else{
                                        if(ver == false) cliente.send("DEBE INGRESAR UN USUARIO");
                                            else cliente.send(ERROR_COMANDO+":"+clientSentence);
                                    } 
                                                        
                                                    
                                                
                                            
                                    
                                    
                                
                            }
				        }
			        } 
                    catch (IOException e) {
				        e.printStackTrace();
                        removeIdleClient(cliente);
			        }
			    }
                removeIdleClient(cliente);
            }
	    }
    }

    public static void main(String argv[]) throws Exception{

        ServerSocket welcomeSocket = new ServerSocket(10987);

        boolean run = true;
        AVIUBB_servidor server = new AVIUBB_servidor();
        server.start(); // hebra que atiende conexiones aceptadas

        while(run) {  // hebra principal escucha intentos de conexion
    	    Socket connectionSocket = welcomeSocket.accept();
    	    System.out.println("Agregando nuevo cliente:"+connectionSocket.getInetAddress());
    	    server.add(connectionSocket);
	    }
        welcomeSocket.close();
    }
}
