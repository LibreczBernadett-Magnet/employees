package employees;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Supplier;

@Service
@Slf4j
@AllArgsConstructor
public class EmployeesService {

 //   private final ModelMapper modelMapper;

    private final EmployeeMapper employeeMapper;
    private final EmployeesRepository repository;
    private final AddressesGateway addressesGateway;

     public List<EmployeeDto> listEmployees(QueryParameters queryParameters) {
        return repository.findAll().stream()
                .map(employeeMapper::toDto)
                .toList();
    }

    public EmployeeDto findEmployeeById(long id) {
         return employeeMapper.toDto(repository.findById(id).orElseThrow(notFoundException(id)));
    }

    public EmployeeDto createEmployee(CreateEmployeeCommand command) {
        Employee employee = new Employee(command.getName());
        employee = repository.save(employee);
        log.info("Employee has been created");
        log.debug("Employee has been created with name {}", employee.getName());
        return employeeMapper.toDto(employee);
    }

    @Transactional
    public EmployeeDto updateEmployee(long id, UpdateEmployeeCommand command) {
         Employee employee = repository.findById(id).orElseThrow(notFoundException(id));
         employee.setName(command.getName());
         return employeeMapper.toDto(employee);
    }

    public void deleteEmployee(long id) {
        repository.deleteById(id);
    }

    public void deleteAllEmployees() {
        repository.deleteAll();
    }

    private static Supplier<EmployeeNotFoundException> notFoundException(long id){
        return () -> new EmployeeNotFoundException("Employee not found: %d".formatted(id));
    }

    public AddressDto findAddressById(long id) {
         Employee employee = repository.findById(id).orElseThrow(notFoundException(id));
         return addressesGateway.findAddressByName(employee.getName());
    }
}
