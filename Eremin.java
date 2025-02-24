package HumanResourcesManagement;

import HumanResourcesManagement.EmployeeAndDepartment.Employee;
import HumanResourcesManagement.EmployeeAndDepartment.EmployeeEvent;
import HumanResourcesManagement.EmployeeAndDepartment.EmployeeListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Directorate implements EmployeeListener {
    private final List<String> _history = new ArrayList<>();

    public String makeReport() {
        StringBuilder report = new StringBuilder();

        Iterator<String> it = _history.iterator(); int index = 1;
        while(it.hasNext()) {
            report.append(index).append(": ").append(it.next());

            if (it.hasNext()) {
                report.append("\n");
                ++index;
            }
        }

        return report.toString();
    }

    @Override
    public void employeeEnrolled(EmployeeEvent e) {
        _history.add("Employee " + employeeToString(e.getSource()) + " was enrolled in department \"" + e.getCurrentDepartment().getName() + "\"");
    }

    @Override
    public void employeeTransferred(EmployeeEvent e) {
        _history.add("Employee " + employeeToString(e.getSource()) + " was transferred from department \"" + e.getPrevDepartment().getName() + "\"  to department \"" + e.getCurrentDepartment().getName() + "\"");
    }

    @Override
    public void employeeDismissed(EmployeeEvent e) {
        _history.add("Employee " + employeeToString(e.getSource()) + " was dismissed from department \"" + e.getPrevDepartment().getName() + "\"");
    }

    @Override
    public void employeeStudied(EmployeeEvent e) {
        _history.add("Employee " + employeeToString(e.getSource()) + " was received a new qualification \"" + e.getCurrentEducation() + "\"");
    }

    private String employeeToString(Employee e) {
        String log = e.getFIO();
        if(e.getIdCard() != null) {
            log = log + " (ID: " + e.getIdCard().getNumber() + ")";
        }
        return log;
    }
}
