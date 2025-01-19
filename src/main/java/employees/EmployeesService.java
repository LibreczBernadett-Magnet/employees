package employees;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

@Service
@Slf4j
@AllArgsConstructor
public class EmployeesService {

 //   private final ModelMapper modelMapper;

    private final EmployeeMapper employeeMapper;


    private final EmployeesDao employeesDao;

     public List<EmployeeDto> listEmployees(QueryParameters queryParameters) {
        return employeesDao.findAll().stream()
                .map(employeeMapper::toDto)
                .toList();
    }

    public EmployeeDto findEmployeeById(long id) {
         return employeeMapper.toDto(employeesDao.findById(id));
    }

    public EmployeeDto createEmployee(CreateEmployeeCommand command) {
        Employee employee = new Employee(command.getName());
        employee = employeesDao.createEmployee(employee);
        log.info("Employee has been created");
        log.debug("Employee has been created with name {}", employee.getName());
        return employeeMapper.toDto(employee);
    }

    public EmployeeDto updateEmployee(long id, UpdateEmployeeCommand command) {
         Employee employee = new Employee(id, command.getName());
         employeesDao.updateEmployee(employee);
         return employeeMapper.toDto(employee);
    }

    public void deleteEmployee(long id) {
        employeesDao.deleteEmployee(id);
    }

    public void deleteAllEmployees() {
        employeesDao.deleteAllEmployees();
    }

    private static Supplier<EmployeeNotFoundException> notFoundException(long id){
        return () -> new EmployeeNotFoundException("Employee not found: %d".formatted(id));
    }
}
