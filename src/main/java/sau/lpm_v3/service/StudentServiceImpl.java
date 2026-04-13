package sau.lpm_v3.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import sau.lpm_v3.dtos.StudentDTO;
import sau.lpm_v3.enums.Role;
import sau.lpm_v3.exception.ErrorMessages;
import sau.lpm_v3.exception.ResourceNotFoundException;
import sau.lpm_v3.model.Student;
import sau.lpm_v3.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;

    @Override
    public StudentDTO getStudentByIdSecure(Long id, boolean isAdmin, String currentUsername) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.ERROR_STUDENT_NOT_FOUND));

        if (!isAdmin && !student.getUsername().equals(currentUsername)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Yetkisiz erişim denemesi!");
        }
        return student.viewAsStudentDTO();
    }

    @Override
    public List<StudentDTO> getAllStudents(boolean isAdmin, String username) {
        if (isAdmin) {
            return studentRepository.findAll().stream().map(Student::viewAsStudentDTO).toList();
        }
        Student student = studentRepository.findByUsername(username);
        return (student != null) ? List.of(student.viewAsStudentDTO()) : List.of();
    }


    @Override
    public StudentDTO createStudent(StudentDTO studentDto) {
        Student student = studentDto.toEntity();
        student.setPassword(passwordEncoder.encode(studentDto.getPassword()));

        if (studentDto.getImageFile() != null && !studentDto.getImageFile().isEmpty()) {
            student.setImageURL("/images/" + fileStorageService.saveFile(studentDto.getImageFile()));
        }

        if (student.getRole() == null) student.setRole(Role.USER);

        log.info("NEW STUDENT: [{}] created by registration.", student.getUsername()); //
        return studentRepository.save(student).viewAsStudentDTO();
    }

    @Override
    public StudentDTO updateStudent(Long id, StudentDTO studentDto, boolean isAdmin, String currentUsername) {
        Student existing = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.ERROR_STUDENT_NOT_FOUND));

        if (!isAdmin && !existing.getUsername().equals(currentUsername)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sadece kendinizi güncelleyebilirsiniz.");
        }

        existing.setName(studentDto.getName());
        existing.setDepartment(studentDto.getDepartment());

        if (isAdmin && studentDto.getRole() != null) {
            existing.setRole(studentDto.getRole());
        }

        if (studentDto.getImageFile() != null && !studentDto.getImageFile().isEmpty()) {
            existing.setImageURL("/images/" + fileStorageService.saveFile(studentDto.getImageFile()));
        }

        log.info("STUDENT UPDATED: [{}] by user [{}].", existing.getUsername(), currentUsername); //
        return studentRepository.save(existing).viewAsStudentDTO();
    }

    @Override
    public String deleteStudent(Long id, boolean isAdmin, String currentUsername) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.ERROR_STUDENT_NOT_FOUND));

        if (!isAdmin && !student.getUsername().equals(currentUsername)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Silme yetkiniz yok.");
        }

        studentRepository.deleteById(id);
        log.warn("STUDENT DELETED: [{}] by user [{}].", student.getUsername(), currentUsername); //

        return (!isAdmin && student.getUsername().equals(currentUsername)) ? "SELF_DELETED" : "DELETED";
    }

    @Override
    public StudentDTO getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.ERROR_STUDENT_NOT_FOUND))
                .viewAsStudentDTO();
    }

}