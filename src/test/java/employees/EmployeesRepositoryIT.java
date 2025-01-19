package employees;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
public class EmployeesRepositoryIT {

    @Autowired
    EmployeesRepository repository;

    @Test
    void testPersist(){
        Employee employee = new Employee("John Doe");
        repository.save(employee);
        List<Employee> employees = repository.findAll();

        assertThat(employees)
                .extracting(Employee::getName)
                .containsOnly("John Doe");
    }
}
