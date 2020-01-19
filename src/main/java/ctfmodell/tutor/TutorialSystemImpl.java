package ctfmodell.tutor;

import ctfmodell.model.StudentExample;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TutorialSystemImpl extends UnicastRemoteObject implements TutorialSystem {

    private Queue<StudentExample> requests;
    private List<StudentExample> finishedRequests;

    public TutorialSystemImpl() throws RemoteException {
        super();
        this.requests = new ConcurrentLinkedQueue<>();
        this.finishedRequests = new ArrayList<>();
    }

    public Boolean sendRequest(StudentExample example) {
        requests.add(example);
        return true;
    }

    public StudentExample checkResponse(String id) {
        System.out.println(id);

        StudentExample answer = null;
        for (StudentExample example : this.finishedRequests) {
            if (example.getStudentId().equals(id)) {
                answer = example;
                this.finishedRequests.remove(example);
                break;
            }
        }

        return answer;
    }

    public StudentExample loadNextRequest() {
        return this.requests.poll();
    }

    void saveAnswer(StudentExample example) {
        this.finishedRequests.add(example);
    }

}
