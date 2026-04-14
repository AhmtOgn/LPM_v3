package sau.lpm_v3.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.http.MediaType;
import sau.lpm_v3.dtos.StudentDTO;
import sau.lpm_v3.service.StudentService;

@Controller
@Slf4j
public class AuthController {

    private final StudentService studentService;

    public AuthController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("student", new StudentDTO());
        return "register";
    }

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String registerStudent(@ModelAttribute("student") StudentDTO studentDto) {
        log.info("New registration request for username: [{}]", studentDto.getUsername());

        studentService.createStudent(studentDto);
        return "redirect:/login?registered";
    }
}