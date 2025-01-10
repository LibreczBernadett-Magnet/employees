package employees;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class EmployeesService {

    private final ModelMapper modelMapper;

    private final AtomicLong idGenerator = new AtomicLong();

    public EmployeesService(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    private final List<Employee> employees = Collections.synchronizedList(new ArrayList<>(List.of(
            new Employee(idGenerator.incrementAndGet(), "John Doe"),
            new Employee(idGenerator.incrementAndGet(), "Jack Doe"),
            new Employee(idGenerator.incrementAndGet(), "Jack Smith")
    )));

    public List<EmployeeDto> listEmployees(QueryParameters queryParameters) {
        Type targetListType = new TypeToken<List<EmployeeDto>>() {}.getType();
        List<Employee> filtered =
        employees.stream()
                .filter(e -> queryParameters.getPrefix() == null || e.getName().toLowerCase().startsWith(queryParameters.getPrefix().toLowerCase()))
                .filter(e -> queryParameters.getSuffix() == null || e.getName().toLowerCase().endsWith(queryParameters.getSuffix().toLowerCase()))
                .toList();
        return modelMapper.map(filtered, targetListType);
    }

    public EmployeeDto findEmployeeById(long id) {
        return modelMapper.map(employees.stream()
                .filter(e -> e.getId() == id).findAny()
                .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + id)),
                EmployeeDto.class);
    }

    public EmployeeDto createEmployee(CreateEmployeeCommand command) {
        Employee employee = new Employee(idGenerator.incrementAndGet(), command.getName());
        employees.add(employee);
        return modelMapper.map(employee, EmployeeDto.class);
    }

    public EmployeeDto updateEmployee(long id, UpdateEmployeeCommand command) {
        Employee employee = employees.stream()
                .filter(e -> e.getId() == id)
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Employee not found: " + id));
        employee.setName(command.getName());
        return modelMapper.map(employee, EmployeeDto.class);
    }

    public void deleteEmployee(long id) {
        Employee employee = employees.stream()
                .filter(e -> e.getId() == id)
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Employee not found: " + id));
        employees.remove(employee);
    }
}
