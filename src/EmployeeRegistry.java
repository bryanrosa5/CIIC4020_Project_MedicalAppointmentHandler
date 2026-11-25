import java.util.ArrayList;
import java.util.HashMap;

public class EmployeeRegistry {

    private HashMap<Integer, Employee> employees = new HashMap<>();

    public void registerEmployee(Employee employee) {
        if(!employees.containsKey(employee.getId())) {
            employees.put(employee.getId(), employee);
            System.out.println("Employee registered"); 
        }
        else {
            System.out.println("Error: The ID is already registered in the system.");
        }
    }

    public Employee findEmployee(int id){
        return employees.get(id);
    }

    

    public ArrayList<Medic> findMedicsBySpecialty(Medic.Specialty specialty){
        ArrayList <Medic> medics = new ArrayList<>();

        for(Employee employee : employees.values()) {
            if (employee instanceof Medic) {
                Medic medic = (Medic) employee;
                if(medic.getSpecialty().equals(specialty)){
                    medics.add(medic);
                }
            }
        }
        return medics;
    }

    public ArrayList<Medic> findAvailableMedics(){
        ArrayList <Medic> medics = new ArrayList<>();
        
        for(Employee employee : employees.values()) {
            if (employee instanceof Medic) {
                Medic medic = (Medic) employee;
                if(medic.getAvailable() == true){
                    medics.add(medic);
                }
            }
        }
        return medics;
    }
}