import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
 
public class AppointmentsManager {
 
    private final Map<Integer, Appointments> appointmentsById = new HashMap<>();
 
    private final Deque<Appointments> recentlyCompleted = new LinkedList<>();
 
    public Appointments scheduleAppointments(long patientId, String doctorId,
                                             LocalDateTime when) throws AppointmentsException {
        if (doctorId == null || doctorId.trim().isEmpty()) {
            throw new AppointmentsException("Doctor ID cannot be empty.");
        }
        if (when == null || when.isBefore(LocalDateTime.now())) {
            throw new AppointmentsException("Appointment time must be in the future.");
        }
 
        for (Appointments a : appointmentsById.values()) {
            if (a.getStatus() == Appointments.Status.SCHEDULED
                    && a.getDoctorId().equals(doctorId)
                    && a.getDateTime().equals(when)) {
                throw new AppointmentsException("Doctor " + doctorId + " is already booked at " + when.format(Appointments.FORMAT));
            }
        }
 
        Appointments appt = new Appointments(patientId, doctorId, when);
        appointmentsById.put(appt.getId(), appt);
        return appt;
    }
 
    public void cancelAppointments(int appointmentId) throws AppointmentsException {
        Appointments appt = findScheduled(appointmentId);
        appt.setStatus(Appointments.Status.CANCELLED);
    }
 
    public void recordVisit(int appointmentId, PatientWrapper.Patient patient)
            throws AppointmentsException {
        if (patient == null) {
            throw new AppointmentsException("Patient cannot be null.");
        }
        Appointments appt = findScheduled(appointmentId);
        if (appt.getPatientId() != patient.getID()) {
            throw new AppointmentsException("Appointment #" + appointmentId + " belongs to patient " + appt.getPatientId() + ", not " + patient.getID() + ".");
        }
        appt.setStatus(Appointments.Status.COMPLETED);
        recentlyCompleted.push(appt);
        closeRecord(patient);
    }
 
    public void recordVisit(PatientWrapper.Patient patient) throws AppointmentsException {
        if (patient == null) {
            throw new AppointmentsException("Patient cannot be null.");
        }
        closeRecord(patient);
    }
 
    private void closeRecord(PatientWrapper.Patient patient) {
        patient.getCurrentRecord().setVisitDate(LocalDate.now());
        patient.saveCurrentRecord();
    }
 
    public void showPatientHistory(PatientWrapper.Patient patient) {
        System.out.println("Medical history for " + patient.getName()
                + " (ID " + patient.getID() + "):");
        Deque<PatientWrapper.MedicalRecord> history = patient.getMedicalHistory();
        if (history.isEmpty()) {
            System.out.println("  (no visits yet)");
            return;
        }
        Iterator<PatientWrapper.MedicalRecord> it = history.iterator();
        int n = 1;
        while (it.hasNext()) {
            System.out.println("--- Visit " + n++ + " ---");
            System.out.println(it.next());
        }
    }
 
    public List<Appointments> getTodaysAppointments() {
        List<Appointments> today = new ArrayList<>();
        LocalDate now = LocalDate.now();
        for (Appointments a : appointmentsById.values()) {
            if (a.getStatus() == Appointments.Status.SCHEDULED
                    && a.getDateTime().toLocalDate().equals(now)) {
                today.add(a);
            }
        }
        Collections.sort(today);
        return today;
    }
 
    public void showTodaysAppointments() {
        List<Appointments> today = getTodaysAppointments();
        System.out.println("Today's appointments:");
        if (today.isEmpty()) {
            System.out.println("  (none)");
        }
        for (Appointments a : today) {
            System.out.println("  " + a);
        }
    }
 
    public void showRecentlyCompleted(int max) {
        System.out.println("Recently completed appointments:");
        if (recentlyCompleted.isEmpty()) {
            System.out.println("  (none)");
            return;
        }
        Iterator<Appointments> it = recentlyCompleted.iterator();
        int count = 0;
        while (it.hasNext() && count < max) {
            System.out.println("  " + it.next());
            count++;
        }
    }
 
    private Appointments findScheduled(int appointmentId) throws AppointmentsException {
        Appointments appt = appointmentsById.get(appointmentId);
        if (appt == null) {
            throw new AppointmentsException("Appointment #" + appointmentId + " does not exist.");
        }
        if (appt.getStatus() != Appointments.Status.SCHEDULED) {
            throw new AppointmentsException("Appointment #" + appointmentId + " is already " + appt.getStatus() + ".");
        }
        return appt;
    }
}
 