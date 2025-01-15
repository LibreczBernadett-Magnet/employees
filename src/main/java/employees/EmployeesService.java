package employees;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

@Service
public class EmployeesService {

 //   private final ModelMapper modelMapper;

    private final EmployeeMapper employeeMapper;

    public EmployeesService(EmployeeMapper employeeMapper) {
        this.employeeMapper = employeeMapper;
    }

    private final AtomicLong idGenerator = new AtomicLong();

//    public EmployeesService(ModelMapper modelMapper) {
//        this.modelMapper = modelMapper;
//    }

    private final List<Employee> employees = Collections.synchronizedList(new ArrayList<>(List.of(
            new Employee(idGenerator.incrementAndGet(), "John Doe"),
            new Employee(idGenerator.incrementAndGet(), "Jack Doe"),
            new Employee(idGenerator.incrementAndGet(), "Jack Smith")
    )));

    public List<EmployeeDto> listEmployees(QueryParameters queryParameters) {
//        Type targetListType = new TypeToken<List<EmployeeDto>>() {}.getType();
        List<Employee> filtered =
        employees.stream()
                .filter(e -> queryParameters.getPrefix() == null || e.getName().toLowerCase().startsWith(queryParameters.getPrefix().toLowerCase()))
                .filter(e -> queryParameters.getSuffix() == null || e.getName().toLowerCase().endsWith(queryParameters.getSuffix().toLowerCase()))
                .toList();
//        return modelMapper.map(filtered, targetListType);
        return employeeMapper.toDto(filtered);
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

    private static Supplier<EmployeeNotFoundException> notFoundException(long id){
        return () -> new EmployeeNotFoundException("Employee not found: %d".formatted(id));
    }
}
