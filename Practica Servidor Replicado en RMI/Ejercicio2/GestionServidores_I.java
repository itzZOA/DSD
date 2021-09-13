import java.rmi.Remote;
import java.rmi.RemoteException;


public interface GestionServidores_I extends Remote {
    public GestionServidores_I getReplica1() throws RemoteException;
    public GestionServidores_I getReplica2() throws RemoteException;
    public Boolean existeUsuario(String usuario) throws RemoteException;
    public int getSize() throws RemoteException;
    public void introducirUsuario(String usuario) throws RemoteException;
    public Double getSubtotal() throws RemoteException;
    public Double getPrimero() throws RemoteException;
    public Double getSegundo() throws RemoteException;
    public Double getTercero() throws RemoteException;
    public void sumarDonacion(String usuario, Double donacion) throws RemoteException;
    public Double getDonacionUsuario(String usuario) throws RemoteException;
}
