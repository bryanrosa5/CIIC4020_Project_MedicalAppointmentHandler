import java.util.Queue;
import java.util.LinkedList;

public class Medico extends Empleado {
    private String especialidad;
    private boolean disponible;

    public Medico(int id, String nombre, String especialidad) {
        super(id, nombre);
        this.especialidad = especialidad;
        this.disponible = true;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public boolean getDisponible() {
        return disponible;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    Queue<Paciente> colaPacientes = new LinkedList<>();

    public void agregarPaciente(Paciente paciente) {
        colaPacientes.offer(paciente);
    }
}
