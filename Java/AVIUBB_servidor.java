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
	private String cid;
	private Socket socket;
	private BufferedReader reader;
	private PrintWriter writer;
    private Timestamp time;

	Cliente(Socket s) throws IOException{
		socket = s;
		reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
    	writer = new PrintWriter(s.getOutputStream(), true);
		cid = "Usuario sin nombre"; // no se puede usar servidor con ese nombre
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
class AVIUBB_servidor extends Thread {
	private Hashtable<String,Cliente> clientes;
    public static String mensajelista[] = {}; // lista en la que se guarda mensajes, con su destinatario y emisor
    public static String ultimo_r[] = {"x", "x"}; // lista donde se guarda el usuario ingresado, junto con la fecha y hora
 	private boolean ready;
	public static String ERROR_COMANDO = "401: No se reconoce comando o bien esta incompleto.";
    public static String ERROR_FORMATO = "403: No se reconoce formato mensaje.";
    public static String ERROR_NOMBRE = "404: Otro usuario tiene el mismo pseudonimo, escoja otro.";
    public static String COMANDO_CORRECTO = "200: OK.";
    public static String SALIR = "201: Adios.";
    
    public AVIUBB_servidor(){
      	ready = false;
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
    
    public static String[] add_element(int n, String array[], String recado) //funcion para agregar mensajes a la lista creada por Diego Ramirez
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
                            if(clientSentence==null){
                                removeIdleClient(cliente);
                            }
                            else{
                                if(clientSentence.startsWith("NUEVO_USUARIO") ){ // se crea el usuario, guardando su nombre y la fecha de ingreso
                                    String[] parts = clientSentence.split(" "); // funcion creada por Diego Ramirez
                                    if(parts.length >1){
                                        String newNombre = parts[1].trim();
                                        if(!clientes.containsKey(newNombre)){
                                            clientes.remove(cliente.getNombre());
                                            cliente.setNombre(newNombre);
                                            clientes.put(newNombre, cliente);
                                            cliente.send(COMANDO_CORRECTO);   
                                            //fechas de date time para usarlas en la funcion de ULTIMO creada por Esteban Risopatron
                                            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");  //dia : mes : anio 
                                            LocalDateTime now = LocalDateTime.now();  
                                            String fecha_y_hora=dtf.format(now);
                                            ultimo_r[0] = cliente.getNombre();
                                            ultimo_r[1] = fecha_y_hora;                                                                                         //aqui se mandan las instrucciones para que el 
                                                                                                                                                                //usuario sepa como usar el programa- creado por Esteban Risopatron
        			            			clientSentence = clientSentence + "DEJAR_RECADO <nombre> <texto> (Cambia tu nombre).\n";
		        				            clientSentence = clientSentence + "CONSULTAR_RECADO <nombre> (consulta el recado dado un nombre de usuario)\n";
            	        					clientSentence = clientSentence + "ULTIMO  (genera fecha y hora del ultimo que ingreso al servidor)\n";
			                    			clientSentence = clientSentence + "LISTA (Nombre usuarios online).\n";
                                            clientSentence = clientSentence + "MENSAJE_MAS_ANTIGUO (Nombre usuarios online).\n";
                                            clientSentence = clientSentence + "TOTAL_RECADOS <nombre> (Contabiliza cuantos recados tiene tal usuario).\n";
                                            clientSentence = clientSentence + "FIN (Dejar el servicio)\n";
				                    		clientSentence = clientSentence + "Precaucion!!: Servidor diferencia entre letras mayusculas y minusculas\n";
            			        			cliente.send(clientSentence);
                                        }
                                        else{
                                            cliente.send(ERROR_NOMBRE+":"+newNombre);
                                        }
                                    }
                                    else cliente.send(ERROR_COMANDO+":"+clientSentence);
            						
			            		}
                                else if(clientSentence.startsWith("DEJAR_RECADO") && cliente.getNombre() != "Usuario sin nombre"){ // se guarda el mensaje, destinatario y emisor, al servidor
                                		String[]  parts = clientSentence.split(" ");                                               // funcion creada por Diego Ramirez
                                        if(parts.length > 2) {
                                            int index = clientSentence.indexOf(parts[2]);
                                            String destino = parts[1].trim();
                            				String mensaje = clientSentence.substring(index);
                                            mensajelista = add_element(mensajelista.length, mensajelista, destino+"/X/x(De "+cliente.getNombre()+"): "+mensaje);
                                                        
					                    }
                                        else{
                						    cliente.send(ERROR_FORMATO+":"+clientSentence);
						                }
	                            }
                                else if(clientSentence.startsWith("CONSULTAR_RECADO") &&  cliente.getNombre() != "Usuario sin nombre"){ // se hace un recorrido en la lista de los mensajes, seleccionando los del usuario seleccionado
                                                                                                                                        // funcion creada por Diego Ramirez
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
                					        cliente.send(ERROR_FORMATO+":"+clientSentence);
			                            }
                				}               
                                else if(clientSentence.equals("ULTIMO") && cliente.getNombre() != "Usuario sin nombre"){    // nos dice la hora,dia y fecha registrada por el ultimo que ingreso al servidor (en la sesion)
                                                                                                                                // funcionalidad construida por Esteban Risopatron
                                         clientSentence="Ultimo usuario:\n"+ultimo_r[0]+" "+ultimo_r[1];
                                         cliente.send(clientSentence);
                                      }
				                else if(clientSentence.equals("LISTA") && cliente.getNombre() != "Usuario sin nombre"){
                            					   clientSentence = "Lista de seudonimos:\n"+getString();
		        			                       cliente.send(clientSentence);
                            					}
                                else if(clientSentence.equals("MENSAJE_MAS_ANTIGUO") && cliente.getNombre() != "Usuario sin nombre"){ //MENSAJE MAS ANTIGUO (TOTAL NO DE UN USUARIO)
                                    if(mensajelista.length>=1){                                                                         //funcionalidad creada por Esteban Risopatron
                                            String[] antiguo = mensajelista[0].split("/X/x");                                        
                                            clientSentence="El mensaje mas antiguo es "+antiguo[0]+" "+antiguo[1];
                                    }
                                        else{clientSentence="El servidor no posee mensajes registrados hasta el momento";}
                                                cliente.send(clientSentence);
                                    }  
                                else if(clientSentence.startsWith("TOTAL_RECADOS") && cliente.getNombre() != "Usuario sin nombre"){ 

                                    String[]  parts = clientSentence.split(" ");
                                    int contador=0;
                                        
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
                                else if(clientSentence.equals("FIN") && cliente.getNombre() != "Usuario sin nombre"){ // se envia un mensaje al cliente para salir del servidor
                                        cliente.send(SALIR);                                                          // funcion creada por Diego Ramirez
                                        remove(cliente);
                                        


                                    }
                                    else{
                                        if(cliente.getNombre() == "Usuario sin nombre") cliente.send("DEBE INGRESAR UN USUARIO");
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

        ServerSocket welcomeSocket = new ServerSocket(20011);

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
