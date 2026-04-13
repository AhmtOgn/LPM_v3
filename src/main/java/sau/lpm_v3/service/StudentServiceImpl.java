package sau.lpm_v3.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import sau.lpm_v3.dtos.StudentDTO;
import sau.lpm_v3.enums.Role;
import sau.lpm_v3.exception.ErrorMessages;
import sau.lpm_v3.exception.ResourceAlreadyExistsException;
import sau.lpm_v3.exception.ResourceNotFoundException;
import sau.lpm_v3.model.Student;
import sau.lpm_v3.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private FileStorageService fileStorageService;

    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public StudentDTO getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.ERROR_STUDENT_NOT_FOUND + ": " + id)).viewAsStudentDTO();
    }

    public Student getStudentEntityById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.ERROR_STUDENT_NOT_FOUND + ": " + id));
    }

    public List<StudentDTO> getAllStudents() {
        return studentRepository.findAll().stream().map(Student::viewAsStudentDTO).toList();
    }

    public StudentDTO createStudent(StudentDTO studentDto) {
        // Convert DTO to Entity internally
        Student student = studentDto.toEntity();

        if (studentRepository.findById(student.getId()).isPresent()) {
            throw new ResourceAlreadyExistsException(ErrorMessages.ERROR_STUDENT_ALREADY_EXIST + ": " + student.getId());
        }

        student.setPassword(passwordEncoder.encode(studentDto.getPassword()));

        if (studentDto.getImageFile() != null && !studentDto.getImageFile().isEmpty()) {
            // Dosyayı kaydet ve dönen ismi al
            String savedFileName = fileStorageService.saveFile(studentDto.getImageFile());

            if (savedFileName != null) {
                // Başına /images/ ekleyerek kaydediyoruz ki HTML'de kolay çağıralım
                student.setImageURL("/images/" + savedFileName);
                log.info("Student [{}] profile picture path set to: [{}]", student.getUsername(), student.getImageURL());
            }

            log.info("IMAGE UPLOAD: Student [{}] profile picture saved as [{}]",
                    student.getUsername(), savedFileName);
        }

        if (student.getRole() == null) { student.setRole(Role.USER); }

        Student savedStudent = studentRepository.save(student);

        log.info("A new Student [{}] REGISTERED. Username: [{}], Role: [{}]",
                savedStudent.getName(), savedStudent.getUsername(), savedStudent.getRole());

        return savedStudent.viewAsStudentDTO();
    }

    public StudentDTO updateStudent(Long id, StudentDTO studentDto) {
        // Convert DTO to Entity internally
        Student student = studentDto.toEntity();

        studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.ERROR_STUDENT_NOT_FOUND + ": " + id));

        student.setId(id);

        if (studentDto.getImageFile() != null && !studentDto.getImageFile().isEmpty()) {
            // Yeni resim yüklendi: Eskisini (isteğe bağlı) sil ve yenisini kaydet
            String newFileName = fileStorageService.saveFile(studentDto.getImageFile());
            student.setImageURL("/images/" + newFileName);
            log.info("Image updated for student: [{}]", student.getUsername());
        }
        return studentRepository.save(student).viewAsStudentDTO();
    }

    public void deleteStudent(Long id) {
        studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.ERROR_STUDENT_NOT_FOUND + ": " + id));
        studentRepository.deleteById(id);
    }

}