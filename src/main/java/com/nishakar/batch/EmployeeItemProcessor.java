package com.nishakar.batch;

import com.nishakar.entity.Employee;
import org.springframework.batch.infrastructure.item.ItemProcessor;

public class EmployeeItemProcessor implements ItemProcessor<Employee, Employee> {

    @Override
    public Employee process(Employee item) {
        if(item.getSalary() == null) {
            return null; // skip invalid record
        }
        item.setDepartment(item.getDepartment().toUpperCase());
        return item;
    }
}
