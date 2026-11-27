import java.util.LinkedList;
import java.util.Queue;

public class Nurse extends Employee {
    private boolean available;
    private Queue<PatientWrapper.Patient> patientQueue = new LinkedList<>();

    public Nurse(int id, String name){
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

    public void addPatient(PatientWrapper.Patient patient){
        if(patient == null){
            return;
        }
        for(PatientWrapper.Patient waitingPatient : patientQueue){
            if(waitingPatient.getID() == patient.getID()){
                return;
            }
        }
        patientQueue.offer(patient);
    }

    public PatientWrapper.Patient attendPatient() {

    if(!patientQueue.isEmpty() && available){
        available = false;
        return patientQueue.poll();
    }
    else return null;
    }

    public  void finishAssessment(PatientWrapper.Patient patient, Medic medic){
        medic.addPatient(patient);
        available = true;
    }
    
    public void recordVitals(PatientWrapper.Patient patient, int systolicPressure, int diastolicPressure, int bodyTemperature){
        patient.getCurrentRecord().setBloodPressure(systolicPressure, diastolicPressure);
        patient.getCurrentRecord().setBodyTemperature(bodyTemperature);
    }

    public void recordReasonForVisit(PatientWrapper.Patient patient, String reasonForVisit){
        patient.getCurrentRecord().setReasonForVisit(reasonForVisit);
    }
}

