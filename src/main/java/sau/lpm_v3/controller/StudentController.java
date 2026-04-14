package sau.lpm_v3.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import sau.lpm_v3.dtos.StudentDTO;
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
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @GetMapping("all")
    public String getAllStudents(Model model, Authentication auth) {
        boolean isAdmin = isAdmin(auth);
        model.addAttribute("students", studentService.getAllStudents(isAdmin, auth.getName()));
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
    public String addStudent(@ModelAttribute("student") StudentDTO studentDto, Authentication auth) {
        log.info("[USER: {}] ACTION: Adding new student with name: [{}]",
                auth.getName(), studentDto.getName());

        studentService.createStudent(studentDto);
        return "redirect:/student/all";
    }

    @GetMapping("/update/{id}")
    public String updateStudent(@PathVariable Long id, Model model, Authentication auth) {
        boolean isAdmin = isAdmin(auth);
        model.addAttribute("student", studentService.getStudentByIdSecure(id, isAdmin, auth.getName()));
        return "students/_update";
    }

    @PostMapping("/update")
    public String updateStudent(@ModelAttribute("student") StudentDTO studentDto, Authentication auth) {
        log.info("[USER: {}] ACTION: Updating student ID: [{}]",
                auth.getName(), studentDto.getId());

        boolean isAdmin = isAdmin(auth);
        studentService.updateStudent(studentDto.getId(), studentDto, isAdmin, auth.getName());
        return "redirect:/student/all";
    }

    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<String> deleteStudent(@PathVariable Long id, Authentication auth,
                                                HttpServletRequest request, HttpServletResponse response) {
        log.warn("[USER: {}] ACTION: Deleting student ID: [{}]",
                auth.getName(), id);

        boolean isAdmin = isAdmin(auth);
        String result = studentService.deleteStudent(id, isAdmin, auth.getName());

        if ("SELF_DELETED".equals(result)) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private boolean isAdmin(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}



