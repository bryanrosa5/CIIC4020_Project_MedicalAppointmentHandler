import java.util.LinkedList;
import java.util.Queue;

public class Secretary extends Employee {
    private boolean available;
    private Queue<PatientWrapper.Patient> patientQueue = new LinkedList<>();

    public Secretary(int id, String name) {
        super(id, name);
        this.available = true;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean getAvailable() {
        return available;
    }

    public Queue<PatientWrapper.Patient> getWaitingPatients(){
    return patientQueue;
    }

    public void addPatient(PatientWrapper.Patient patient) {
        patientQueue.offer(patient);
    }

    public PatientWrapper.Patient attendPatient() {
    
        if(!patientQueue.isEmpty() && available){
        available = false;
        return patientQueue.poll();
    }
    else return null;
    }

    public void sendToNurse(PatientWrapper.Patient patient, Nurse nurse){
        nurse.addPatient(patient);
        available = true;
    }

    public void finishVisit(PatientWrapper.Patient patient) {
        patient.saveCurrentRecord();
        available = true;
    }
}
