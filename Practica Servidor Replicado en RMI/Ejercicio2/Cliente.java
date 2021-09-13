import java.rmi.registry.Registry;
import java.util.Scanner;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;


public class Cliente {

    public String usuario;
    private final Scanner teclado;
    private final GestionDonaciones_I gestor;

    public Cliente(final GestionDonaciones_I gestor)
    {
        this.usuario = null;
        this.gestor = gestor;
        teclado = new Scanner(System.in);
    }

    public void cerrarTeclado()
    {
        this.teclado.close();
    }

    public String getUsuario()
    {
        return this.usuario;
    }

    public void registro()throws RemoteException
    {
        System.out.println("Escriba su nombre de usuario:");
        String usuarioT = teclado.nextLine();
        Boolean existe = gestor.registro(usuarioT);

        if (existe)
            System.out.println("El usuario ya existe");

        this.usuario = usuarioT;
        System.out.println("Ha iniciado sesión como: " + this.getUsuario());
    }

    public void iniciarSesion() throws RemoteException
    {
        System.out.println("Escriba su nombre de usuario:");
        String usuarioT = teclado.nextLine();
        Boolean existe = gestor.iniciarSesion(usuarioT);
        if (existe)
            this.usuario = usuarioT;
        else
            System.out.println("Usted no esta registrado");
    }

    public void donar() throws RemoteException
    {
        System.out.println("¿Cuanto quiere donar?:");
        String donacion = teclado.nextLine();
        Double donacionD = null;

        try {
            donacionD = Double.parseDouble(donacion);
        } catch (NumberFormatException e) {
            System.err.println("Escriba un numero sin o con dos decimales por favor");
        }

        Boolean existe = gestor.donar(this.getUsuario(), donacionD);
        if (existe)
            System.out.println("La donacion se ha realizado con éxito");
        else
            System.out.println("No está registrado en el sistema");
        
    }

    public void consultarTotal() throws RemoteException
    {
        Double totalDonado = gestor.getTotal();
        System.out.println("El total donado es: " + totalDonado.toString() + "$");
    }
    

    public void consultarUsuario() throws RemoteException
    {
        if (this.usuario != null)
        {
            Double donacionUsuario = gestor.getUsuario(this.usuario);
            System.out.println(this.usuario + " ha donado " + donacionUsuario.toString() + " $");
        }
        else
        {
            System.out.println("NO PUEDE: No esta registrado");
        }
    }
    
       public void consultarRanking() throws RemoteException
    {	
        if (this.usuario != null)
        {
            String ranking = gestor.getRanking();
            System.out.println(ranking);
        }
        else
        {
            System.out.println("NO PUEDE: No esta registrado");
        }
    }



    //MAIN
    public static void main(final String[] args) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        try {

            final Registry registry = LocateRegistry.getRegistry("127.0.0.1", 1099);
            
            final GestionDonaciones_I gestor = (GestionDonaciones_I) registry.lookup("servidor0");
            
            System.out.println("BIENVENIDO AL SISTEMA DE DONACIONES");

            final String menu = "\n¿QUE DESEA HACER?:"+
            "\n\t1: Registrarme"+
            "\n\t2: Iniciar sesión" +
            "\n\t3: Donar"+
            "\n\t4: Consultar El Total De Donaciones"+
            "\n\t5: Consultar Mis Donaciones"+
            "\n\t6: Ranking De Donaciones"+
            "\n\t7: Salir";

            final Scanner teclado = new Scanner (System.in);
            String opcion;
            final Cliente cliente = new Cliente(gestor);

            do {

                System.out.println(menu);
                opcion = teclado.nextLine();
                opcion = opcion.toUpperCase();
                
                switch(opcion)
                {
                    case "1":
                        cliente.registro();
                        break;

                    case "2":
                        cliente.iniciarSesion();
                        break;

                    case "3":
                        cliente.donar();
                        break;
                    case "4":
                        cliente.consultarTotal();
                        break;
                    case "5":
                        cliente.consultarUsuario();
                        break;
                    case "6":
                        cliente.consultarRanking();
                        break;
                    case "7":
                        System.out.println("Gracias, vuelva pronto");
                        break;
                    default:
                        System.out.println("Debe elegir una opcion");
                        break;
                }

            }while (!opcion.equals("7"));
            
            teclado.close();
            cliente.cerrarTeclado();
            
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

}
