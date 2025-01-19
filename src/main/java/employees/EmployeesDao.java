package employees;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Repository
@AllArgsConstructor
public class EmployeesDao {

    private JdbcTemplate jdbcTemplate;

    public List<Employee> findAll() {
        return jdbcTemplate.query("select id, emp_name from employees",
                EmployeesDao::mapRow);
    }

    public Employee findById(Long id) {
        return jdbcTemplate.queryForObject("select id, emp_name from employees where id = ?",
                EmployeesDao::mapRow, id);
    }

    public Employee createEmployee(Employee employee) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement("insert into employees (emp_name) values (?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, employee.getName());
            return ps;
        }, keyHolder );
        employee.setId(keyHolder.getKey().longValue());
        return employee;
    }

    public void updateEmployee(Employee employee) {
        jdbcTemplate.update("update employees set emp_name = ? where id = ?",
                employee.getName(), employee.getId());
    }

    public void deleteEmployee(Long id) {
        jdbcTemplate.update("delete from employees where id = ?", id);
    }

    public void deleteAllEmployees() {
        jdbcTemplate.update("delete from employees");
    }

    private static Employee mapRow(ResultSet rs, int rowNum) throws SQLException {
        Long id = rs.getLong("id");
        String name = rs.getString("emp_name");
        return new Employee(id, name);
    }
}
