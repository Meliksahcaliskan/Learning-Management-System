package com.lms.lms.modules.Student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class StudentController {

    private final StudentService studentService;

    @Autowired
    public StudentController (StudentService studentService) {
        this.studentService = studentService;
    }
    
}
