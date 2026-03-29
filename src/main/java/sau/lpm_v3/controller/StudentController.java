package sau.lpm_v3.controller;

import sau.lpm_v3.dtos.StudentDTO;
import sau.lpm_v3.service.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {

    //For Logging Requirement
    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);
    private StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("all")
    public String getAllStudents(Model model){
        List<StudentDTO> studentDtos = studentService.getAllStudents();
        model.addAttribute("students", studentDtos);
        return "students/all";
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public String getStudent(@PathVariable Long id, Model model){
        model.addAttribute("student", studentService.getStudentById(id));
        return "students/_show";
    }

    @GetMapping(value = "/add")
    public String addStudent(Model model) {
        model.addAttribute("student", new StudentDTO());
        return "students/_add";
    }

    @PostMapping(value = "/add")
    public String addStudent(@ModelAttribute("student") StudentDTO studentDto) {
        // Logs when adding a new entity
        // It is going to add USER DETAILS who performed to action
        logger.info("A new Student [{}] ADDED.", studentDto.getName());

        // Converting operating made in internally
        studentService.createStudent(studentDto);
        return "redirect:/student/all";
    }

    @GetMapping("/update/{id}")
    public String updateStudent(@PathVariable Long id, Model model) {
        //  Already getStudentById converts DTO
        model.addAttribute("student", studentService.getStudentById(id));
        return "students/_update";
    }


    @PostMapping("/update")
    public String updateStudent(@ModelAttribute("student") StudentDTO studentDto) {
        // Logs when updated an entity
        // It is going to add USER DETAILS who performed to action
        logger.info("Student [{}] UPDATED", studentDto.getName());

        // Converting operating made in internally
        studentService.updateStudent(studentDto.getId(),studentDto);
        return "redirect:/student/all";
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteStudent(@PathVariable Long id) {
        // Logs when deletint an entity
        // It is going to add USER DETAILS who performed to action
        logger.warn("Student [{}] DELETED", studentService.getStudentById(id).getName());

        studentService.deleteStudent(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}



