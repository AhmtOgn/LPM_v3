package sau.lpm_v3.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import sau.lpm_v3.dtos.StudentDTO;
import sau.lpm_v3.model.Student;
import sau.lpm_v3.repository.StudentRepository;
import sau.lpm_v3.service.StudentService;
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
    private final StudentRepository studentRepository;

    public StudentController(StudentService studentService, StudentRepository studentRepository) {
        this.studentService = studentService;
        this.studentRepository = studentRepository;
    }

    @GetMapping("all")
    public String getAllStudents(Model model, Authentication auth) {
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // Tüm mantık servisin içinde
        List<StudentDTO> students = studentService.getAllStudentsFiltered(isAdmin, auth.getName());

        model.addAttribute("students", students);
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
    public String updateStudent(@ModelAttribute("student") StudentDTO studentDto, Authentication auth) {
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        studentService.updateStudent(studentDto.getId(), studentDto, isAdmin, auth.getName());
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



