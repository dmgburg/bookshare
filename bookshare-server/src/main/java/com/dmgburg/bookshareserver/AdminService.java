package com.dmgburg.bookshareserver;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;

@RestController
public class AdminService {

    private final JdbcTemplate jdbcTemplate;

    public AdminService(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);;
    }

    @PostMapping("/admin/do")
    public String admin(@RequestParam String request){
        try {
            jdbcTemplate.execute(request);
            return "success";
        } catch (Exception e){
            return e.getMessage();
        }
    }
}
