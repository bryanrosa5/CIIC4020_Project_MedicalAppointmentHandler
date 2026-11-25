import java.util.LinkedList;
import java.util.Queue;

public class Nurse extends Employee {
    private boolean available;
    private Queue<Patient> patientQueue = new LinkedList<>();

    public Nurse(int id, String name){
        super(id, name);
        this.available = true;
    }
}
