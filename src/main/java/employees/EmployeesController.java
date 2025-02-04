package employees;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
@Tag(name = "Operations on employees")
@Slf4j
public class EmployeesController {

    private final EmployeesService employeesService;

    public EmployeesController(EmployeesService employeesService) {
        this.employeesService = employeesService;
    }

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<EmployeeDto> listEmployees(QueryParameters queryParameters, Principal principal){
        //log.info("Logged user: " + principal.getName());
        return employeesService.listEmployees(queryParameters);
    }

    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public EmployeeDto findEmployeeById(@PathVariable("id") long id){
        return employeesService.findEmployeeById(id);
    }

    @GetMapping("/{id}/address")
    public AddressDto findAddressById(@PathVariable("id") long id){
        return employeesService.findAddressById(id);
    }

    @PostMapping
    @Operation(summary = "creates an employee")
    @ApiResponse(responseCode = "201", description = "employee has been created")
    public ResponseEntity<EmployeeDto> createEmployee(@Valid @RequestBody CreateEmployeeCommand command,
                                      UriComponentsBuilder uri){
        log.info("Create employee");
        log.debug("Create employee with name {}", command.getName());
        EmployeeDto employeeDto = employeesService.createEmployee(command);
        return ResponseEntity
                .created(uri.path("/api/employees/{id}").buildAndExpand(employeeDto.getId()).toUri())
                .body(employeeDto);
    }

    @PutMapping("/{id}")
    public EmployeeDto updateEmployee(@PathVariable("id") long id, @RequestBody UpdateEmployeeCommand command){
        return employeesService.updateEmployee(id, command);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEmployee(@PathVariable("id") long id){
        employeesService.deleteEmployee(id);
    }
}
