import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.rmi.*;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;

public class GestionDonaciones extends UnicastRemoteObject implements GestionDonaciones_I, 
GestionServidores_I {
    private String nombre;
    private int num;
    private String replica, replica2;
    private Double subtotal, primero, segundo, tercero;
    private HashMap<String, Double> clientes;

    /****Gestion de donaciones*****/

    public GestionDonaciones(String nombre, int num) throws RemoteException {
        super();
        this.nombre = nombre;
        this.num = num;
        if (this.num == 0) {
            	this.replica = "servidor1";
            	this.replica2 = "servidor2";
        } else if (this.num == 1) {
            	this.replica = "servidor0";
            	this.replica2 = "servidor2";
        } else {
        	this.replica = "servidor0";
        	this.replica2 = "servidor1";
        
	}
        subtotal = new Double (0.0);
        primero = new Double (0.0);
        segundo = new Double (0.0);
        tercero = new Double (0.0);
        clientes = new HashMap<>();
    }

    public Boolean registro(String usuario) throws RemoteException {

        Boolean existe = false;

        // Miramos si existe en nuestra replica
        if (clientes.get(usuario) == null) {

            	GestionServidores_I replicaServ1 = getReplica1();
            // Si no es así miramos en la otra réplica
            if (!replicaServ1.existeUsuario(usuario))
            {
            	GestionServidores_I replicaServ2 = getReplica2();
            if (!replicaServ2.existeUsuario(usuario))
            {
                //Si no existe en ninguna de las réplicas 
                //la introducimos en la que tenga menos clientes
               if (this.clientes.size() <= replicaServ1.getSize()){
               	if (this.clientes.size() <= replicaServ2.getSize()){ //La replica 0 es menor que la 1 y 2
                    		this.introducirUsuario(usuario);
                    		System.out.println("Se ha introducido un usuario en el " + this.nombre);
               	}
                	else //La replica 1 es menor que la 0 y 2
                	{
                    		replicaServ2.introducirUsuario(usuario);
                    		System.out.println("Se ha introducido un usuario en el " + this.replica2);
                	}
                }else
                {
                	replicaServ1.introducirUsuario(usuario);
                    	System.out.println("Se ha introducido un usuario en el " + this.replica);
                }
            }
            else
            {
                existe = true;
            }
            }
            else
            {
                existe = true;
            }


        }
        else
        {
            existe = true;
        }

        return existe;
    }

//Comprueba que el usuario existe e inicia sesion
    public Boolean iniciarSesion(String usuario)throws RemoteException
    {
        Boolean existe = false;
        GestionServidores_I replicaServ1 = getReplica1();
        GestionServidores_I replicaServ2 = getReplica2();
        if (clientes.get(usuario) != null || replicaServ1.existeUsuario(usuario) || replicaServ2.existeUsuario(usuario))
            existe = true;

        return existe;
    }

//Funcion para que un usaurio done en la replica en la que esta registrado
    public Boolean donar(String usuario, Double donacion) throws RemoteException
    {
        Boolean existe = false;
        GestionServidores_I replicaServ1 = getReplica1();
        GestionServidores_I replicaServ2 = getReplica2();
        if (donacion != null)
        {
            if (clientes.get(usuario) != null)
            {
                existe = true;
                this.sumarDonacion(usuario, donacion);


            }
            else if (replicaServ1.existeUsuario(usuario))
            {
                existe = true;
                replicaServ1.sumarDonacion(usuario, donacion);

            }
            else if (replicaServ2.existeUsuario(usuario))
            {
                existe = true;
                replicaServ2.sumarDonacion(usuario, donacion);

            }
        }

        return existe;   
    }

//Da el total de donaciones en los servidores
    public Double getTotal () throws RemoteException
    {
        Double total = new Double(0.0);
        total += this.subtotal;
        GestionServidores_I replicaServ1 = this.getReplica1();
        total += replicaServ1.getSubtotal();
        GestionServidores_I replicaServ2 = this.getReplica2();
        total += replicaServ2.getSubtotal();
        return total;
    }
    
    //Da el total de donaciones en los servidores
    public String getRanking () throws RemoteException
    {
        Double primero = new Double(0.0);
        Double segundo = new Double(0.0);
        Double tercero = new Double(0.0);
        Double p = new Double(0.0);
        Double s = new Double(0.0);
        primero = this.primero;
        segundo = this.primero;
        tercero = this.primero;
        
        GestionServidores_I replicaServ1 = this.getReplica1();
        GestionServidores_I replicaServ2 = this.getReplica2();
        
        // Si el primero es el del servidor 1
        if ((replicaServ1.getPrimero() > this.primero) && (replicaServ1.getPrimero() > replicaServ2.getPrimero())){ 
        	primero = replicaServ1.getPrimero();
        	
        	// si el segundo es el segundo del servidor 1
        	if ((replicaServ1.getSegundo() > this.primero) && (replicaServ1.getSegundo() > replicaServ2.getPrimero())){ 
        		segundo = replicaServ1.getSegundo();
        		
        		// Si el tercero es el tercero del servidor 1
        		if ((replicaServ1.getTercero() > this.primero) && (replicaServ1.getTercero() > replicaServ2.getPrimero())){ 
        			tercero = replicaServ1.getTercero();
        		
        		//Si el tercero es el primero del servidor 2
        		}else if ((replicaServ2.getPrimero() > primero) && (replicaServ2.getPrimero() > replicaServ1.getTercero())){ 
        			tercero = replicaServ2.getPrimero();
        			
        		//Si el tercero es el primero del servidor 0
        		}else if ((this.primero > replicaServ2.getPrimero()) && (primero > replicaServ1.getTercero())){ 
        			tercero = this.primero;
        		}
        		
        	// Si el segundo es el primero del servidor 2	
        	}else if ((replicaServ2.getPrimero() > this.primero) && (replicaServ2.getPrimero() > replicaServ1.getSegundo())){ 
        		segundo = replicaServ2.getPrimero();
        		
        		// Si el tercero es el segundo del servidor 2
        		if ((replicaServ2.getSegundo() > this.primero) && (replicaServ2.getSegundo() > replicaServ1.getSegundo())){ 
        			tercero = replicaServ2.getSegundo();
        		
        		//Si el tercero es el segundo del servidor 1
        		}else if ((replicaServ1.getSegundo() > this.primero) && (replicaServ1.getSegundo() > replicaServ2.getSegundo())){ 
        			tercero = replicaServ1.getSegundo();
        			
        		//Si el tercero es el primero del servidor 0
        		}else if ((this.primero > replicaServ1.getSegundo()) && (this.primero > replicaServ2.getSegundo())){ 
        			tercero = this.primero;
        		}
        	
        	//Si el segundo es el primero del servidor 0	
        	}else if ((primero > replicaServ2.getPrimero()) && (primero > replicaServ1.getSegundo())){ 
        		segundo = this.primero;
        		
        		// Si el tercero es el segundo del servidor 0
        		if ((this.segundo > replicaServ2.getPrimero()) && (segundo > replicaServ1.getSegundo())){ 
        			tercero = this.segundo;
        			
        		//Si el tercero es el segundo del servidor 1
        		}else if ((replicaServ1.getSegundo() > this.segundo) && (replicaServ1.getSegundo() > replicaServ2.getPrimero())){ 
        			tercero = replicaServ1.getSegundo();
        			
        		//Si el tercero es el primero del servidor 2
        		}else if ((replicaServ2.getPrimero() > this.segundo) && (replicaServ2.getPrimero() > replicaServ1.getSegundo())){ 
        			tercero = replicaServ2.getPrimero();
        		}
        	}
        	
        }else if ((replicaServ2.getPrimero() > primero) && (replicaServ2.getPrimero() > replicaServ1.getPrimero())){ //Si el primero es el primero del servidor 2
        	primero = replicaServ2.getPrimero();
        	
        	//Si el segundo es el segundo del servidor 2
        	if ((replicaServ2.getSegundo() > this.primero) && (replicaServ2.getSegundo() > replicaServ1.getPrimero())){
        		segundo = replicaServ2.getSegundo();	
        		
        		//Si el tercero es el tercero del servidor 2
        		if ((replicaServ2.getTercero() > this.primero) && (replicaServ2.getTercero() > replicaServ1.getPrimero())){
        			tercero = replicaServ2.getTercero();
        			
        		//Si el tercero es el primero del servidor 1
        		}else if ((replicaServ1.getPrimero() > this.primero) && (replicaServ1.getPrimero() > replicaServ2.getTercero())){ 
        			tercero = replicaServ1.getPrimero();
        			
        		//Si el tercero es el primero del servidor 0
        		}else if ((this.primero  > replicaServ1.getPrimero())&& ( this.primero > replicaServ2.getTercero())){ 
        			tercero = this.primero;
        		}
        		
        	//Si el segundo es el primero del servidor 1
        	}else if ((replicaServ1.getPrimero() > this.primero) && (replicaServ1.getPrimero() > replicaServ2.getSegundo())){
        		segundo = replicaServ1.getPrimero();
        		
        		//Si el tercero es el segundo del servidor 1
        		if ((replicaServ1.getSegundo() > this.primero) && (replicaServ1.getSegundo() > replicaServ2.getSegundo())){
        			tercero = replicaServ1.getSegundo();
        		
        		//Si el tercero es el segundo del servidor 2
        		}else if ((replicaServ2.getSegundo() > this.primero) && (replicaServ2.getSegundo() > replicaServ1.getSegundo())){ 
        			tercero = replicaServ2.getSegundo();
        			
        		//Si el tercero es el primero del servidor 0
        		}else if ((this.primero > replicaServ2.getSegundo()) && (this.primero > replicaServ1.getSegundo())){ 
        			tercero = this.primero;
        		}
        		
        	//Si el segundo es el primero del servidor 0
        	}else if ((this.primero > replicaServ1.getPrimero()) && (this.primero > replicaServ2.getSegundo())){
        		segundo = this.primero;
        		
        		//Si el tercero es el segundo del servidor 2
        		if ((replicaServ2.getSegundo() > this.segundo) && (replicaServ2.getSegundo() > replicaServ1.getPrimero())){
        			tercero = replicaServ2.getSegundo();
        			
        		//Si el tercero es el primero del servidor 1
        		}else if ((replicaServ1.getPrimero() > this.segundo) && (replicaServ1.getPrimero() > replicaServ2.getSegundo())){ 
        			tercero = replicaServ1.getPrimero();
        			
        		//Si el tercero es el segundo del servidor 0
        		}else if ((this.segundo > replicaServ2.getSegundo()) && (this.segundo > replicaServ1.getPrimero())){ 
        			tercero = this.segundo;
        		}
        	}
        	
        // si el primero es el primero del servidor 0
        }else if ((this.primero > replicaServ1.getPrimero()) && (this.primero > replicaServ2.getPrimero())){ 
        primero = this.primero;
        
        	//Si el segundo es el segundo del servidor 0
        	if ((this.segundo > replicaServ1.getPrimero()) && (this.segundo > replicaServ1.getPrimero())){
        		segundo = this.segundo;	
        	
        		//Si el tercero es el tercero del servidor 0
        		if ((this.tercero > replicaServ1.getPrimero()) && (this.tercero> replicaServ2.getPrimero())){
        			tercero = this.tercero;
        			
        		//Si el tercero es el primero del servidor 1
        		}else if ((replicaServ1.getPrimero() > this.tercero) && (replicaServ1.getPrimero() > replicaServ2.getPrimero())){ 
        			tercero = replicaServ1.getPrimero();
        			
        		//Si el tercero es el primero del servidor 2
        		}else if ((replicaServ2.getPrimero() > this.tercero) && (replicaServ2.getPrimero() > replicaServ1.getPrimero())){ 
        			tercero = replicaServ2.getPrimero();
        		}
        		
        	//Si el segundo es el primero del servidor 1
        	}else if ((replicaServ1.getPrimero() > this.segundo) && (replicaServ1.getPrimero() > replicaServ2.getPrimero())){ 
        		segundo = replicaServ1.getPrimero();
        		
        		//Si el tercero es el segundo del servidor 0
        		if ((this.segundo > replicaServ1.getSegundo()) && (this.segundo > replicaServ2.getPrimero())){
        			tercero = this.segundo;
        			
        		//Si el tercero es el segundo del servidor 1
        		}else if ((replicaServ1.getSegundo() > this.segundo) && (replicaServ1.getSegundo() > replicaServ2.getPrimero())){ 
        			tercero = replicaServ1.getSegundo();
        			
        		//Si el tercero es el primero del servidor 2
        		}else if ((replicaServ2.getPrimero() > this.segundo) && (replicaServ2.getPrimero() > replicaServ1.getSegundo())){ 
        			tercero = replicaServ2.getPrimero();
        		}
        		
        	//Si el segundo es el primero del servidor 2
        	}else if ((replicaServ2.getPrimero() > this.segundo) && (replicaServ2.getPrimero() > replicaServ1.getPrimero())){ 
        		segundo = replicaServ2.getPrimero();
        		
        		//Si el tercero es el segundo del servidor 0
        		if ((this.segundo > replicaServ2.getSegundo()) && (this.segundo > replicaServ1.getPrimero())){
        			tercero = this.segundo;
        			
        		//Si el tercero es el primero del servidor 1
        		}else if ((replicaServ1.getPrimero() > this.segundo) && (replicaServ1.getPrimero() > replicaServ2.getSegundo())){ 
        			tercero = replicaServ1.getPrimero();
        		//Si el tercero es el segundo del servidor 2
        		
        		}else if ((replicaServ2.getSegundo() > this.segundo) && (replicaServ2.getSegundo() > replicaServ1.getPrimero())){ 
        			tercero = replicaServ2.getSegundo();
        		}
        		
        	}
        }
        String ranking = "RANKING DE DONACIONES \n 1. " + primero + "\n 2. " + segundo + "\n 3. " + tercero + "\n";
    	return ranking;
    }
    
   

    public Double getUsuario(String usuario) throws RemoteException
    {
        Double donacion = new Double (0.0);
        GestionServidores_I replicaServ1 = this.getReplica1();
        GestionServidores_I replicaServ2 = this.getReplica2();
        if (clientes.get(usuario) != null)
        {
            donacion = clientes.get(usuario);

        }
        else if (replicaServ1.existeUsuario(usuario))
        {
            donacion = replicaServ1.getDonacionUsuario(usuario);
        }
        else if (replicaServ2.existeUsuario(usuario))
        {
            donacion = replicaServ2.getDonacionUsuario(usuario);
        }

        return donacion;

    }


    /****Gestion de servidores*****/
    
//Comprueba que existe ya el usuario
    public Boolean existeUsuario (String usuario) throws RemoteException
    {
        Boolean existe = false;

        if (this.clientes.get(usuario) != null)
            existe = true;
            
        return existe;
    }

//Crea la replica 1 del servidor 
    public GestionServidores_I getReplica1() throws RemoteException
    {
        GestionServidores_I servReplica1 = null;
        try {
            Registry registry1 = LocateRegistry.getRegistry("127.0.0.1", 1099);
            servReplica1 = (GestionServidores_I) registry1.lookup(this.replica);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return servReplica1;
    }
    
//Crea la replica 2 del servidor 
    public GestionServidores_I getReplica2() throws RemoteException
    {
        GestionServidores_I servReplica2 = null;
        try {
            Registry registry2 = LocateRegistry.getRegistry("127.0.0.2", 1099);
            servReplica2 = (GestionServidores_I) registry2.lookup(this.replica2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return servReplica2;
    }

//Crea un usuario
    public void introducirUsuario (String usuario) throws RemoteException
    {
        this.clientes.put(usuario, 0.0);
    }

//Da el numero de clientes que hay registrados en un servidor
    public int getSize() throws RemoteException
    {
        return this.clientes.size();
    }

//Da las donaciones totales en un servidor
    public Double getSubtotal()
    {
        return this.subtotal;
    }
    
    public Double getPrimero()
    {
        return this.primero;
    }
    
    public Double getSegundo()
    {
        return this.segundo;
    }
    
    public Double getTercero()
    {
        return this.tercero;
    }

//Suma todas las donaciones de los usuarios en un servidor
    public void sumarDonacion(String usuario, Double donacion)
    {
        this.clientes.put(usuario, clientes.get(usuario) + donacion);
        this.subtotal += donacion; //Lo suma al total
        
    	///Muestra la donacion que acaba de hacer el cliente
        System.out.println("Se han donado " + donacion + "$ al " + this.nombre);
        
        
        Double aux_p = new Double (0.0);
        Double aux_s = new Double (0.0);
        if (donacion > this.primero){
        	aux_p = this.primero;
        	this.primero = donacion;
        	aux_s = this.segundo;
        	this.segundo = aux_p;
        	this.tercero = aux_s;	
        }else if (donacion > this.segundo){
        	aux_s = this.segundo;
        	this.segundo = donacion;
        	this.tercero = aux_s;
        }else if (donacion > this.tercero){
        	this.tercero = donacion;
        }
    }
    
	
//Devuelve la cantidad que ha donado un usuario
    public Double getDonacionUsuario(String usuario) throws RemoteException
    {
        Double donacion = new Double (0.0);
        donacion = this.clientes.get(usuario);
        return donacion;
    }
    
   

}
