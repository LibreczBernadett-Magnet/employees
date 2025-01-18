package employees;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

@Service
@Slf4j
public class EmployeesService {

 //   private final ModelMapper modelMapper;

    private final EmployeeMapper employeeMapper;

    private final AtomicLong idGenerator = new AtomicLong();

    private final EmployeesDao employeesDao;

    public EmployeesService(EmployeeMapper employeeMapper, EmployeesDao employeesDao) {
        this.employeeMapper = employeeMapper;
        this.employeesDao = employeesDao;
    }

    private final List<Employee> employees = Collections.synchronizedList(new ArrayList<>(List.of(
            new Employee(idGenerator.incrementAndGet(), "John Doe"),
            new Employee(idGenerator.incrementAndGet(), "Jack Doe"),
            new Employee(idGenerator.incrementAndGet(), "Jack Smith")
    )));

    public List<EmployeeDto> listEmployees(QueryParameters queryParameters) {
        return employeesDao.findAll().stream()
                .map(employeeMapper::toDto)
                .toList();
    }

    public EmployeeDto findEmployeeById(long id) {
//        return modelMapper.map(employees.stream()
        return employeeMapper.toDto(employees.stream()
                .filter(e -> e.getId() == id).findAny()
                .orElseThrow(notFoundException(id)));
    }

    public EmployeeDto createEmployee(CreateEmployeeCommand command) {
        Employee employee = new Employee(idGenerator.incrementAndGet(), command.getName());
        employees.add(employee);
//        return modelMapper.map(employee, EmployeeDto.class);
        log.info("Employee has been created");
        log.debug("Employee has been created with name {}", employee.getName());
        return employeeMapper.toDto(employee);
    }

    public EmployeeDto updateEmployee(long id, UpdateEmployeeCommand command) {
        Employee employee = employees.stream()
                .filter(e -> e.getId() == id)
                .findFirst().orElseThrow(notFoundException(id));
        employee.setName(command.getName());
//        return modelMapper.map(employee, EmployeeDto.class);
        return employeeMapper.toDto(employee);
    }

    public void deleteEmployee(long id) {
        Employee employee = employees.stream()
                .filter(e -> e.getId() == id)
                .findFirst().orElseThrow(notFoundException(id));
        employees.remove(employee);
    }

    public void deleteAllEmployees() {
        idGenerator.set(0);
        employees.clear();
    }

    private static Supplier<EmployeeNotFoundException> notFoundException(long id){
        return () -> new EmployeeNotFoundException("Employee not found: %d".formatted(id));
    }
}
