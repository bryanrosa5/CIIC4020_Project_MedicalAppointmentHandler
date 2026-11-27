import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
 
public class Appointments implements Comparable<Appointments> {
 
    public enum Status { SCHEDULED, CANCELLED, COMPLETED }
 
    public static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
 
    private static int nextId = 1;
 
    private final int id;
    private final long patientId;
    private final String doctorId;
    private final LocalDateTime dateTime;
    private Status status;
 
    public Appointments(long patientId, String doctorId, LocalDateTime dateTime) {
        this.id = nextId++;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.dateTime = dateTime;
        this.status = Status.SCHEDULED;
    }
 
    public int getId() {
        return id;
    }
 
    public long getPatientId() {
        return patientId;
    }
 
    public String getDoctorId() {
        return doctorId;
    }
 
    public LocalDateTime getDateTime() {
        return dateTime;
    }
 
    public Status getStatus() {
        return status;
    }
 
    public void setStatus(Status s) {
        this.status = s;
    }
 
    @Override
    public int compareTo(Appointments other) {
        return this.dateTime.compareTo(other.dateTime);
    }
 
    @Override
    public String toString() {
        return "Appointment #" + id + " | " + dateTime.format(FORMAT) + " | Patient: " + patientId + " | Doctor: " + doctorId + " | " + status;
    }
}
 