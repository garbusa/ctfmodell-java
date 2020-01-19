package ctfmodell.tutor;

import ctfmodell.model.StudentExample;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface TutorialSystem extends Remote {

    Boolean sendRequest(StudentExample example) throws RemoteException;

    StudentExample checkResponse(String id) throws RemoteException;

    StudentExample loadNextRequest() throws RemoteException;
}
