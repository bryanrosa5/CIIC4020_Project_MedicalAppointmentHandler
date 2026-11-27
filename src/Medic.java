import java.util.Queue;
import java.util.LinkedList;

public class Medic extends Employee {
    
    public enum Specialty {
        GeneralOphthalmology,
        PediatricOphthalmology,
        NeuroOphthalmology,
        Retinology
    }

    private boolean available;
    private Specialty specialty;

    public Medic(int id, String name, Specialty specialty) {
        super(id, name);
        this.specialty = specialty;
        this.available = true;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
    public boolean getAvailable() {
        return available;
    }

    public Specialty getSpecialty() {
        return specialty;
    }

    private Queue<PatientWrapper.Patient> patientQueue = new LinkedList<>();

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

    public PatientWrapper.Patient attendPatient(){
    if(!patientQueue.isEmpty() && available){
        available = false;
        return patientQueue.poll();
    }
    else return null;
    }

    public void recordDiagnosis(PatientWrapper.Patient patient, String diagnosis){
        patient.getCurrentRecord().setDiagnosis(diagnosis);
    }

    public void recordTreatmentPlan(PatientWrapper.Patient patient, String treatmentPlan){
        patient.getCurrentRecord().setTreatmentPlan(treatmentPlan);
    }

    public void recordNotes(PatientWrapper.Patient patient, String notes){
        patient.getCurrentRecord().setNotes(notes);
    }

    public  void finishConsultation(PatientWrapper.Patient patient, Secretary secretary){
        secretary.addPatient(patient);
        available = true;
    }
}