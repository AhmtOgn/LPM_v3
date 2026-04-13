package sau.lpm_v3.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
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
@Slf4j
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("all")
    public String getAllStudents(Model model){
        List<StudentDTO> studentDtos = studentService.getAllStudents();
        model.addAttribute("students", studentDtos);
        return "students/all";
    }

    @GetMapping(value = "/{id}")
    public String getStudent(@PathVariable Long id, Model model){
        model.addAttribute("student", studentService.getStudentById(id));
        return "students/_show";
    }

    @GetMapping(value = "/add")
    public String addStudent(Model model) {
        model.addAttribute("student", new StudentDTO());
        return "students/_add";
    }

    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String addStudent(@ModelAttribute("student") StudentDTO studentDto) {

        // It is going to add USER DETAILS who performed to action
        log.info("A new Student [{}] ADDED.", studentDto.getName());

        // Converting operating made internally
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

        // It is going to add USER DETAILS who performed to action
        log.info("Student [{}] UPDATED", studentDto.getName());

        // Converting operating made internally
        studentService.updateStudent(studentDto.getId(), studentDto);
        return "redirect:/student/all";
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteStudent(@PathVariable Long id) {

        // It is going to add USER DETAILS who performed to action
        log.warn("Student [{}] DELETED", studentService.getStudentById(id).getName());

        studentService.deleteStudent(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}



