package employees;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Service
@Slf4j
@AllArgsConstructor
public class EmployeesService {
    public static final String EMPLOYEES_CREATED_COUNTER = "employees.created";

    //   private final ModelMapper modelMapper;

    private final EmployeeMapper employeeMapper;
    private final EmployeesRepository repository;
    private final AddressesGateway addressesGateway;
    private final EventStoreGateway eventStoreGateway;
    private ApplicationEventPublisher publisher;

    private MeterRegistry meterRegistry;

    @PostConstruct
    public void init(){
        Counter.builder(EMPLOYEES_CREATED_COUNTER)
                .baseUnit("employees")
                .description("Number of created employees")
                .register(meterRegistry);
        ;
    }

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
        eventStoreGateway.sendMesssage(command.getName());
        meterRegistry.counter(EMPLOYEES_CREATED_COUNTER).increment();
        publisher.publishEvent(new AuditApplicationEvent("anonymus", "employee_has_been_created",
                Map.of("name", command.getName())));
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
