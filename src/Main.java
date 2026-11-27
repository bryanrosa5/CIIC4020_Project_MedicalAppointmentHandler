
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {
    private enum Screen { LOGIN, SECRETARY, NURSE, DOCTOR, PATIENT, EXIT }
    private enum Stage { CHECK_IN, INTAKE, DOCTOR_QUEUE, CHECK_OUT }

    private static class Visit {
        final PatientWrapper.Patient patient;
        final Appointments booking;
        final Medic doctor;
        Stage stage = Stage.CHECK_IN;

        Visit(PatientWrapper.Patient patient, Appointments booking, Medic doctor) {
            this.patient = patient;
            this.booking = booking;
            this.doctor = doctor;
        }
    }

    private final Scanner input = new Scanner(System.in);
    private int nextId = 100000;
    private final PatientWrapper wrapper = new PatientWrapper();
    private final EmployeeRegistry staff = new EmployeeRegistry();
    private final AppointmentsManager bookings = new AppointmentsManager();
    private final Map<Long, PatientWrapper.Patient> patients = new HashMap<>();
    private final Map<Long, Visit> openVisits = new HashMap<>();
    private final Deque<Visit> desk = new ArrayDeque<>();
    private final Deque<Visit> intake = new ArrayDeque<>();
    private Screen screen = Screen.LOGIN;
    private Employee employee;
    private PatientWrapper.Patient signedInPatient;

    public static void main(String[] args) {
        new Main().run();
    }

    private void run() {
        Secretary first = new Secretary(newId(), "Demo Secretary");
        staff.registerEmployee(first);
        System.out.println("Demo Secretary | ID " + first.getId());

        while(screen != Screen.EXIT) {
            try {
                switch(screen) {
                    case LOGIN: login(); break;
                    case SECRETARY: secretaryScreen(); break;
                    case NURSE: nurseScreen((Nurse) employee); break;
                    case DOCTOR: doctorScreen((Medic) employee); break;
                    case PATIENT: patientScreen(); break;
                    default: return;
                }
            } catch(AppointmentsException | IllegalArgumentException | DateTimeParseException e) {
                System.out.println(e.getMessage());
            } catch(NoSuchElementException endOfInput) {
                return;
            }
        }
    }

    private String ask(String prompt) {
        System.out.print(prompt + " > ");
        return input.nextLine().trim();
    }

    private int number(String prompt) {
        return Integer.parseInt(ask(prompt));
    }

    private LocalDate dob() {
        return LocalDate.parse(ask("Birth date (yyyy-MM-dd)"));
    }

    private int newId() {
        return nextId++;
    }

    private PatientWrapper.Patient lookupPatient(String name, LocalDate birth) {
        for(PatientWrapper.Patient p : patients.values()) {
            if(p.getName().equalsIgnoreCase(name) && birth.equals(p.getDateOfBirth())){
                return p;
            }
        }
        return null;
    }

    private PatientWrapper.Patient findPatient() {
        PatientWrapper.Patient p = lookupPatient(ask("Patient full name"), dob());
        System.out.println(p == null ? "No matching patient." : "Patient ID: " + p.getID());
        return p;
    }

    private void registerPatient() {
        String name = ask("Full name");
        LocalDate birth = dob();

        if(name.isEmpty() || birth.isAfter(LocalDate.now())){
            throw new IllegalArgumentException("Invalid name or birth date.");
        }

        PatientWrapper.Patient existing = lookupPatient(name, birth);

        if(existing != null) {
            System.out.println("Patient already registered. ID: " + existing.getID());
            return;
        }

        String sexInput = ask("Sex (one character)");

        if(sexInput.length() != 1){
            throw new IllegalArgumentException("Enter one character for sex.");
        }

        char sex = Character.toUpperCase(sexInput.charAt(0));
        int id = newId();

        patients.put((long) id, wrapper.new Patient(id, name, birth, sex));
        System.out.println("Patient registered. ID: " + id);
    }

    private void registerEmployee() {
        String name = ask("Full name");

        if(name.isEmpty()){
            throw new IllegalArgumentException("Name cannot be empty.");
        }

        int id = newId();
        Employee e;

        switch(ask("1 Secretary | 2 Nurse | 3 Doctor")) {
            case "1": e = new Secretary(id, name); break;
            case "2": e = new Nurse(id, name); break;
            case "3": e = new Medic(id, name, specialty()); break;
            default:
                System.out.println("Invalid employee type.");
                return;
        }

        staff.registerEmployee(e);
        System.out.println("Employee ID: " + id);
    }

    private Medic.Specialty specialty() {
        Medic.Specialty[] choices = Medic.Specialty.values();

        for(int i = 0; i < choices.length; i++){
            System.out.println((i + 1) + " " + choices[i]);
        }

        int choice = number("Specialty");

        if(choice < 1 || choice > choices.length){
            throw new IllegalArgumentException("Invalid specialty.");
        }

        return choices[choice - 1];
    }

    private void login() {
        employee = null;
        signedInPatient = null;

        switch(ask("SIGN IN | 1 ID | 2 Patient name and DOB | 0 Exit")) {
            case "1":
                int id = number("Person ID");
                employee = staff.findEmployee(id);
                signedInPatient = patients.get((long) id);
                break;
            case "2": signedInPatient = findPatient(); break;
            case "0": screen = Screen.EXIT; return;
            default: return;
        }

        if(employee instanceof Secretary) screen = Screen.SECRETARY;
        else if(employee instanceof Nurse) screen = Screen.NURSE;
        else if(employee instanceof Medic) screen = Screen.DOCTOR;
        else if(signedInPatient != null) screen = Screen.PATIENT;
        else System.out.println("Sign-in unsuccessful.");
    }

    private void switchUser() {
        employee = null;
        signedInPatient = null;
        screen = Screen.LOGIN;
    }

    private void secretaryScreen() throws AppointmentsException {
        System.out.println("\nSECRETARY | " + employee.getName());

        Visit next = desk.peekFirst();

        if(next == null) waiting();
        else System.out.println("Next: " + next.stage + " | " + next.patient.getName());

        System.out.println("1 Serve next | 2 Register patient | 3 Register employee | 4 Book");
        System.out.println("5 Announce arrival | 6 Cancel booking | 7 Today's bookings");
        System.out.println("8 Waiting patients | 9 Patient history | 10 Available doctors");
        System.out.println("11 Doctors by specialty | 12 Recent visits");
        System.out.println("S Switch user");

        switch(ask("Action")) {
            case "1": serveDesk(); break;
            case "2": registerPatient(); break;
            case "3": registerEmployee(); break;
            case "4": schedule(); break;

            case "5":
                PatientWrapper.Patient arriving = findPatient();
                if(arriving != null) arrive(arriving);
                break;

            case "6":
                int id = number("Appointment ID");

                for(Visit v : openVisits.values()) {
                    if(v.booking.getId() == id) {
                        System.out.println("Visit already open.");
                        return;
                    }
                }

                bookings.cancelAppointments(id);
                System.out.println("Cancelled.");
                break;

            case "7":
                bookings.showTodaysAppointments();
                break;

            case "8":
                showWaitingPatients();
                break;

            case "9":
                PatientWrapper.Patient p = findPatient();
                if(p != null) bookings.showPatientHistory(p);
                break;

            case "10":
                showDoctors(staff.findAvailableMedics());
                break;

            case "11":
                showDoctors(staff.findMedicsBySpecialty(specialty()));
                break;

            case "12":
                bookings.showRecentlyCompleted(10);
                break;

            case "S":
            case "s":
                switchUser();
                break;

            default:
                System.out.println("Invalid option.");
        }
    }

    private void showWaitingPatients() {
        if(openVisits.isEmpty()){
            waiting();
            return;
        }

        System.out.println("\nWAITING PATIENTS");

        for(Visit v : desk){
            System.out.println(v.patient.getID() + " | " + v.patient.getName() + " | " + v.stage);
        }

        for(Visit v : intake){
            System.out.println(v.patient.getID() + " | " + v.patient.getName() + " | Nurse intake");
        }

        for(PatientWrapper.Patient p : staff.getAllWaitingPatients()){
            System.out.println(p.getID() + " | " + p.getName() + " | Doctor queue");
        }
    }

    private void showDoctors(List<Medic> doctors) {
        if(doctors.isEmpty()){
            System.out.println("No matching doctors.");
        }

        for(Medic d : doctors) {
            System.out.println(d.getId() + " | " + d.getName() + " | " + d.getSpecialty());
        }
    }

    private void schedule() throws AppointmentsException {
        PatientWrapper.Patient p = findPatient();

        if(p == null) return;

        List<Medic> choices = staff.findMedicsBySpecialty(specialty());
        showDoctors(choices);

        if(choices.isEmpty()) return;

        int id = number("Listed doctor ID");
        Employee chosen = staff.findEmployee(id);

        if(!choices.contains(chosen)) {
            System.out.println("Choose a listed doctor.");
            return;
        }

        LocalDateTime when = LocalDateTime.parse(
            ask("Appointment (yyyy-MM-dd HH:mm)"), Appointments.FORMAT);

        System.out.println(bookings.scheduleAppointments(p.getID(), String.valueOf(id), when));
    }

    private void arrive(PatientWrapper.Patient p) {
        if(openVisits.containsKey(p.getID())) {
            System.out.println("Visit already open.");
            position(p);
            return;
        }

        List<Appointments> today = new ArrayList<>();

        for(Appointments a : bookings.getTodaysAppointments()) {
            if(a.getPatientId() == p.getID()){
                today.add(a);
            }
        }

        if(today.isEmpty()) {
            System.out.println("No scheduled appointment today.");
            return;
        }

        Appointments selected = today.get(0);

        if(today.size() > 1) {
            for(Appointments a : today){
                System.out.println(a);
            }

            int id = number("Choose appointment ID");
            selected = null;

            for(Appointments a : today){
                if(a.getId() == id) selected = a;
            }

            if(selected == null) {
                System.out.println("Choose a listed appointment.");
                return;
            }
        }

        Employee assigned = staff.findEmployee(Integer.parseInt(selected.getDoctorId()));

        if(!(assigned instanceof Medic)){
            System.out.println("Assigned doctor not found.");
            return;
        }

        Medic doctor = (Medic) assigned;
        Visit v = new Visit(p, selected, doctor);

        openVisits.put(p.getID(), v);
        desk.addLast(v);

        position(p);
    }

    private void serveDesk() throws AppointmentsException {
        Visit v = desk.peekFirst();

        if(v == null){
            waiting();
            return;
        }

        if(v.stage == Stage.CHECK_IN) {
            v.stage = Stage.INTAKE;
            intake.addLast(v);
            System.out.println("Checked in. Waiting for nurse.");
        }
        else if(v.stage == Stage.CHECK_OUT){
            bookings.recordVisit(v.booking.getId(), v.patient);
            openVisits.remove(v.patient.getID());
            System.out.println("Closed. Saved visits: " + v.patient.getMedicalHistory().size());
        }
        else{
            System.out.println("Invalid visit stage.");
            return;
        }

        desk.removeFirst();
    }

    private void nurseScreen(Nurse nurse) {
        System.out.println("\nNURSE | " + nurse.getName());

        Visit v = intake.peekFirst();

        if(v == null) {
            idle();
            return;
        }

        if(!proceed("Assess " + v.patient.getName())) return;

        if(!nurse.getAvailable()){
            System.out.println("Nurse unavailable.");
            return;
        }

        String reason = ask("Reason for visit");
        int systolic = number("Systolic pressure");
        int diastolic = number("Diastolic pressure");
        int temperature = number("Temperature in whole degrees Fahrenheit");

        if(reason.isEmpty() || systolic <= 0 || diastolic <= 0 || temperature <= 0){
            throw new IllegalArgumentException("Invalid assessment information.");
        }

        nurse.setAvailable(false);
        nurse.recordReasonForVisit(v.patient, reason);
        nurse.recordVitals(v.patient, systolic, diastolic, temperature);
        nurse.finishAssessment(v.patient, v.doctor);

        v.stage = Stage.DOCTOR_QUEUE;
        intake.removeFirst();

        System.out.println("Assessment completed. Patient sent to doctor.");
    }

    private void doctorScreen(Medic doctor) {
        System.out.println("\nDOCTOR | " + doctor.getName());

        PatientWrapper.Patient patient = doctor.getWaitingPatients().peek();

        if(patient == null) {
            idle();
            return;
        }

        if(!proceed("See " + patient.getName())) return;

        Visit v = openVisits.get(patient.getID());

        if(v == null){
            System.out.println("No open visit for this patient.");
            return;
        }

        System.out.println("Reason: " + patient.getCurrentRecord().getReasonForVisit());
        System.out.println(patient.getCurrentRecord().getVitals());

        String diagnosis = ask("Diagnosis");
        String treatment = ask("Treatment plan");
        String notes = ask("Notes");

        if(diagnosis.isEmpty() || treatment.isEmpty()){
            throw new IllegalArgumentException("Diagnosis and treatment cannot be empty.");
        }

        if(doctor.attendPatient() == null){
            System.out.println("Doctor unavailable.");
            return;
        }

        doctor.recordDiagnosis(v.patient, diagnosis);
        doctor.recordTreatmentPlan(v.patient, treatment);
        doctor.recordNotes(v.patient, notes);

        v.stage = Stage.CHECK_OUT;
        desk.addLast(v);
        doctor.setAvailable(true);

        System.out.println("Consultation completed. Patient sent to secretary.");
    }

    private void patientScreen() {
        System.out.println("\nPATIENT | " + signedInPatient.getName());

        position(signedInPatient);

        switch(ask("1 Announce arrival | 2 My history | R Refresh | S Switch user")) {
            case "1":
                arrive(signedInPatient);
                break;

            case "2":
                bookings.showPatientHistory(signedInPatient);
                break;

            case "R":
            case "r":
                break;

            case "S":
            case "s":
                switchUser();
                break;

            default:
                System.out.println("Invalid option.");
        }
    }

    private <T> void showPosition(String line, Iterable<T> queue, T target) {
        int ahead = 0;

        for(T item : queue) {
            if(item == target) {
                System.out.println(line + " | Position " + (ahead + 1) + " | Waiting ahead: " + ahead);
                return;
            }

            ahead++;
        }
    }

    private void position(PatientWrapper.Patient p) {
        Visit v = openVisits.get(p.getID());

        if(v == null){
            System.out.println("No open visit.");
            return;
        }

        switch(v.stage) {
            case CHECK_IN:
                showPosition("Secretary check-in", desk, v);
                break;

            case CHECK_OUT:
                showPosition("Secretary checkout", desk, v);
                break;

            case INTAKE:
                showPosition("Nurse intake", intake, v);
                break;

            case DOCTOR_QUEUE:
                showPosition(v.doctor.getName(), v.doctor.getWaitingPatients(), p);
                break;
        }
    }

    private boolean proceed(String task) {
        String choice = ask("1 " + task + " | S Switch user");

        if(choice.equalsIgnoreCase("S")){
            switchUser();
        }

        return choice.equals("1");
    }

    private void waiting() {
        System.out.println("No patients in line. Waiting for next patient.");
    }

    private void idle() {
        waiting();

        if(ask("R Refresh | S Switch user").equalsIgnoreCase("S")){
            switchUser();
        }
    }
}
