package com.bajajfinserv.webhook.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SqlSolutionService {

    private static final Logger logger = LoggerFactory.getLogger(SqlSolutionService.class);

    public String getSqlSolutionForEvenRegNo() {
        // Since regNo ends with 82 (even), this is for Question 2
        // Calculate the number of employees who are younger than each employee, grouped by departments

        String sqlQuery = """
            SELECT 
                e.EMP_ID,
                e.FIRST_NAME,
                e.LAST_NAME,
                d.DEPARTMENT_NAME,
                COUNT(younger.EMP_ID) as YOUNGER_EMPLOYEES_COUNT
            FROM EMPLOYEE e
            INNER JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID
            LEFT JOIN EMPLOYEE younger ON (
                younger.DEPARTMENT = e.DEPARTMENT 
                AND younger.DOB > e.DOB
            )
            GROUP BY e.EMP_ID, e.FIRST_NAME, e.LAST_NAME, d.DEPARTMENT_NAME, e.DOB
            ORDER BY e.EMP_ID DESC;
            """;

        logger.info("Generated SQL solution for even regNo (Question 2)");
        return sqlQuery.trim();
    }

    public String getSqlSolutionForOddRegNo() {
        // This would be for Question 1 if regNo ended with odd number
        String sqlQuery = "SELECT * FROM customers ORDER BY customer_id;";
        logger.info("Generated SQL solution for odd regNo (Question 1)");
        return sqlQuery.trim();
    }
}