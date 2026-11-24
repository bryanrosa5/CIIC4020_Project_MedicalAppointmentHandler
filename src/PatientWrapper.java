import java.util.ArrayDeque;
import java.util.Deque;
import java.time.LocalDate;

public class PatientWrapper {
    
    public interface MedicalRecordEntry{
        public LocalDate getVisitDate();
        public void setVisitDate(LocalDate visitDate);

        public String getReasonForVisit();
        public void setReasonForVisit(String reasonForVisit);

        public String getVitals();

        public String getDiagnosis();
        public void setDiagnosis(String diagnosis);

        public String getTreatmentPlan();
        public void setTreatmentPlan(String treatmentPlan);

        public String getNotes();
        public void setNotes(String notes);

        public String getBloodPressure();
        public void setBloodPressure(int systolicPressure, int distolicPressure);

        public double getBodyTemperature();
        public void setBodyTemperature(int bodyTemperature);


    }

    public interface PatientProfile {
        public long getID();
        public String getName();
        public LocalDate getDateOfBirth();
        public char getSex();
        public Deque<MedicalRecord> getMedicalHistory();
        public MedicalRecord getCurrentRecord();
    }
    

    public class MedicalRecord implements MedicalRecordEntry{
        private LocalDate visitDate;
        private String reasonForVisit;
        private String diagnosis;
        private String treatmentPlan;
        private String notes;

        private String bloodPressure;
        private double bodyTemperature;

        public MedicalRecord(){
            this.visitDate = null;
            this.reasonForVisit = null;
            this.bloodPressure = null;
            this.bodyTemperature = 0.0;
            this.diagnosis = null;
            this.treatmentPlan = null;
            this.notes = null;
        }

        public void setVisitDate(LocalDate visitDate){
            this.visitDate = visitDate;
        }

        public LocalDate getVisitDate(){
            return this.visitDate;
        }

        public void setReasonForVisit(String reasonForVisit){
            this.reasonForVisit = reasonForVisit;
        }

        public String getReasonForVisit(){
            return this.reasonForVisit;
        }

        public String getVitals(){
            String pressure = this.bloodPressure == null ? "Not recorded" : bloodPressure;

            String temperature = this.bodyTemperature == 0.0 ? "Not recorded" : bodyTemperature + " °F";

            return "Blood Pressure: " + pressure
                    + "\nBody Temperature: " + temperature;
        }

        public void setDiagnosis(String diagnosis){
            this.diagnosis = diagnosis;
        }

        public String getDiagnosis(){
            return this.diagnosis;
        }

        public void setTreatmentPlan(String treatmentPlan){
            this.treatmentPlan = treatmentPlan;
        }

        public String getTreatmentPlan(){
            return this.treatmentPlan;
        }

        public void setNotes(String notes){
            this.notes = notes;
        }

        public String getNotes(){
            return this.notes;
        }

        public void setBloodPressure(int systolicPressure, int distolicPressure){
            this.bloodPressure = systolicPressure + "/" + distolicPressure;
        }

        public String getBloodPressure(){
            return this.bloodPressure;
        }

        public void setBodyTemperature(int bodyTemperature){
            this.bodyTemperature = bodyTemperature;
        }

        public double getBodyTemperature(){
            return this.bodyTemperature;
        }

        @Override
        public String toString(){
            return "Date: " + this.getVisitDate() +
            "\nReason for Visit: " + this.getReasonForVisit() +
            "\nVitals: \n" + this.getVitals() + 
            "\nDiagnosis: " + this.getDiagnosis() +
            "\nTreatment Plan: " + this.getTreatmentPlan() +
            "\nNotes: " + this.getNotes();
        }

    }

    public class Patient implements PatientProfile{
        private long id;
        private String name;
        private LocalDate dateOfBirth;
        private char sex;
        private Deque<MedicalRecord> medicalHistory;
        private MedicalRecord currentRecord;

        public Patient(long id, String name, LocalDate dateOfBirth, char sex){
            this.id = id;
            this.name = name;
            this.dateOfBirth = dateOfBirth;
            this.sex = sex;
            this.medicalHistory = new ArrayDeque<>();
            this.currentRecord = new MedicalRecord();
        }

        public long getID(){
            return this.id;
        }

        public String getName(){
            return this.name;
        }

        public LocalDate getDateOfBirth(){
            return this.dateOfBirth;
        }

        public char getSex(){
            return this.sex;
        }

        public Deque<MedicalRecord> getMedicalHistory(){
            return this.medicalHistory;
        }

        public MedicalRecord getCurrentRecord(){
            return this.currentRecord;
        }

        public void saveCurrentRecord(){
            this.medicalHistory.add(this.currentRecord);
        }

        public MedicalRecord getPreviousRecord(){
            return this.medicalHistory.peek();
        }

        @Override
        public String toString(){
            return "Patient ID: " + this.getID() +
            "\nPatient Name: " + this.getName() +
            "\nBirth Date: " + this.getDateOfBirth() +
            "\nSex: " + this.getSex() +
            "\nPrevious Medical Appointment: \n" + this.getPreviousRecord();
        }


    }

}
