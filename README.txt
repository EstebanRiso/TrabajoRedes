COMO SE INSTALO:

1.Primero que nada se envian los archivos del servidor mediante protocolo SFTP 
por filezilla con las sgtes credenciales 

ip: 146.83.194.14
puerto: 1040
nombre de usuario: grupo1
constraseña: grupo1#2021face

una vez ingresado, procedemos a colocar en la carpeta de grupo1 el servidor
y realizamos el procedimiento descrito abajo a traves de PUTTY
-------------------------------------------------------------------
-------------------------------------------------------------------
COMO SE USA EL PROGRAMA SERVIDOR:

1.Revise en su terminal su correspondiente directorio de archivos
para revisar esto puede usar en linux el comando "ls" o en windows
en su defecto el comando "dir".

2. Si existe en su directorio el archivo AVIUBB_servidor.java, 
compile utilizando el sgte comando: "javac AVIUBB_servidor.java".

3. Una vez compilado se creara un "AVIUBB_servidor.class".

4. Inicie el programa con el sgte comando "java AVIUBB_servidor".

5. Una vez iniciado trate de ver como usar el programa cliente.


--------------------------------------------------------------------
--------------------------------------------------------------------
COMO SE USA EL PROGRAMA CLIENTE:

1.Revise en su terminal su correspondiente directorio de archivos
para revisar esto puede usar en linux el comando "ls" o en windows
en su defecto el comando "dir".

2. Si existe en su directorio el archivo AVIUBB_cliente.java, 
compile utilizando el sgte comando: "javac AVIUBB_cliente.java".

3. Una vez compilado se creara un "AVIUBB_cliente.class".

4. Inicie el programa con el sgte comando "java AVIUBB_cliente".
(Debe procurar que este encendido el servidor TCP, recuerde que debe haber un 
handshake entre el servidor y el cliente o sino, no funcionará el programa y 
le dara un error)

5. El mismo programa le pedira ingresar con nuevo_usuario 
usted crea el usuario e ingresa al servidor TCP.

6. El resto es a su disposición, el mismo programa le dara las instrucciones para 
usar el programa.


NOTA: todos los archivos ya estan compilados en el sistema en la carpeta 
class, pero si de todas maneras quiere compilarlos de inicio a fin, 
puede hacerlo sin ningun problema

