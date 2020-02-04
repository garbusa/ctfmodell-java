package ctfmodell.tutor;

import ctfmodell.model.StudentExample;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface für das RMI Remote-Objekt
 *
 * @author Nick Garbusa
 */
public interface TutorialSystem extends Remote {

    void sendRequest(StudentExample example) throws RemoteException;

    StudentExample checkResponse(String id) throws RemoteException;

    StudentExample loadNextRequest() throws RemoteException;
}
