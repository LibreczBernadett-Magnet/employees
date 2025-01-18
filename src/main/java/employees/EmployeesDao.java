package employees;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AllArgsConstructor
public class EmployeesDao {

    private JdbcTemplate jdbcTemplate;

    public List<Employee> findAll() {
        return jdbcTemplate.query("select id, emp_name from employees",
                (rs, rowNum) -> {
                    Long id = rs.getLong("id");
                    String name = rs.getString("emp_name");
                    Employee employee = new Employee(id, name);
                    return employee;
                });
    }
}
