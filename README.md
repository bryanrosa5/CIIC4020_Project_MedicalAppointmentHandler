# Medical Appointment Handler

## 1. Project Title:

Medical Appointment Handler - CIIC 4020 Data Structures Project.

## 2. Description:

Medical Appointment Handler is a Java-based application designed to manage the daily operations of an ophthalmology clinic.

The system allows users to register patients and employees, schedule and cancel appointments, manage waiting queues, record medical consultations, and access patient medical histories.

The application supports four ophthalmology specialties:

- General Ophthalmology
- Pediatric Ophthalmology
- Neuro-Ophthalmology
- Retinology

The project implements data structures such as HashMaps, Queues, Lists, and Deques to efficiently organize information and manage clinic operations.

## 3. Installation

### Requirements

- Java Development Kit (JDK 21 recommended)
- Git
- A terminal or an IDE such as Visual Studio Code

### Installation Steps

1. Clone the repository:

git clone https://github.com/bryanrosa5/CIIC4020_Project_MedicalAppointmentHandler.git

2. Navigate to the project directory:

cd CIIC4020_Project_MedicalAppointmentHandler

<img width="824" height="91" alt="image" src="https://github.com/user-attachments/assets/a2a13856-467f-4ab2-8101-17589d3dc00c" />


3. Compile the Java files:

javac -d out src/*.java

<img width="713" height="86" alt="image" src="https://github.com/user-attachments/assets/dc6c6d33-0c41-4df4-9d07-6386ee90152d" />


4. Run the application:

java -cp out Main

<img width="626" height="112" alt="image" src="https://github.com/user-attachments/assets/6f1f62ef-7b05-4e20-a4c7-db7351ccd438" />


## 4. Usage

When the application starts, a default secretary account is automatically created with ID `100000`.

Select option `1` and enter this ID to access the secretary menu.

The system provides different menus depending on the user's role.

### Secretary

- Register new patients and employees.
- Schedule and cancel appointments.
- Register patient arrivals.
- Manage waiting patients.
- Search doctors by specialty.
- View available doctors.
- View today's appointments.
- Access patient medical histories.
- Close medical consultations.
- View recently completed appointments.

<img width="460" height="193" alt="image" src="https://github.com/user-attachments/assets/7597fc0e-c1fa-44e3-84a6-2e9c0d1488e4" />

### Nurse

- Attend waiting patients.
- Record the reason for the visit.
- Record blood pressure and body temperature.
- Send patients to their assigned doctor.

<img width="320" height="108" alt="image" src="https://github.com/user-attachments/assets/1f770a31-5e34-4d62-9064-e850bf1a7ea3" />

### Doctor

- Attend assigned patients.
- Review the reason for the visit and vital signs.
- Record diagnoses, treatment plans, and medical notes.
- Send patients back to the secretary to complete their visit.

<img width="372" height="162" alt="image" src="https://github.com/user-attachments/assets/bd0a0553-dcf0-473e-af89-a2db1b7d1697" />


### Patient

- Announce arrival.
- View waiting status.
- Access personal medical history.

<img width="476" height="75" alt="image" src="https://github.com/user-attachments/assets/f831c00c-8484-41e9-b35e-a0808962521d" />

### Medical Consultation Workflow

1. The secretary registers the patient.
2. An appointment is scheduled with a doctor.
3. The patient's arrival is registered.
4. The patient enters the secretary's waiting queue.
5. The secretary sends the patient to the nurse.
6. The nurse records the patient's vital signs and reason for the visit.
7. The doctor performs the medical consultation.
8. The secretary closes the visit and saves the medical record.

### Data Structures

Data Structure Purpose:
  -HashMap: Employee, patient, and appointment storage and ID lookup.
  -Queue: Management of waiting patients using FIFO order.
  -Deque: Patient medical histories and recently completed appointments.
  -List: Organization and display of doctors and appointments.

## 5. Configuration

No external libraries, databases, environment variables, or additional configuration files are required.

The application runs through a console interface using the Java Standard Library.

All information is stored in memory during execution. Data is not retained after the application is closed.

## 6. Authors and Acknowledgments

Developed by:

- Rafael J. Granado Martinez
- Bryan Rosa Lee
- Alejandro Vázquez

University of Puerto Rico at Mayagüez  
CIIC 4020 - Data Structures  
November 2026
