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

    public List<StudentDTO> getAllStudents() {
        return studentRepository.findAll().stream().map(Student::viewAsStudentDTO).toList();
    }

    @Override
    public List<StudentDTO> getAllStudentsFiltered(boolean isAdmin, String username) {
        if (isAdmin) {
            // Admin her şeyi görür [cite: 11]
            return studentRepository.findAll().stream()
                    .map(Student::viewAsStudentDTO).toList();
        } else {
            // User sadece kendini görür [cite: 12]
            Student student = studentRepository.findByUsername(username);
            return (student != null) ? List.of(student.viewAsStudentDTO()) : List.of();
        }
    }

    public StudentDTO createStudent(StudentDTO studentDto) {
        Student student = studentDto.toEntity();

        if (studentRepository.findById(student.getId()).isPresent()) {
            throw new ResourceAlreadyExistsException(ErrorMessages.ERROR_STUDENT_ALREADY_EXIST + ": " + student.getId());
        }

        student.setPassword(passwordEncoder.encode(studentDto.getPassword()));

        if (studentDto.getImageFile() != null && !studentDto.getImageFile().isEmpty()) {
            String savedFileName = fileStorageService.saveFile(studentDto.getImageFile());

            if (savedFileName != null) {
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

    @Override
    public StudentDTO updateStudent(Long id, StudentDTO studentDto, boolean isAdmin, String currentUsername) {
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Öğrenci bulunamadı: " + id));

        // GÜVENLİK KONTROLÜ: Admin değilse ve başkasını güncelliyorsa engelle
        if (!isAdmin && !existingStudent.getUsername().equals(currentUsername)) {
            throw new RuntimeException("Yetkisiz işlem! Sadece kendi profilinizi güncelleyebilirsiniz.");
        }

        // Bilgileri aktar
        existingStudent.setName(studentDto.getName());
        existingStudent.setDepartment(studentDto.getDepartment());

        // ROL KORUMASI: Sadece Admin rol değiştirebilir [cite: 11]
        if (isAdmin && studentDto.getRole() != null) {
            existingStudent.setRole(studentDto.getRole());
        }

        // Resim işlemleri (mevcut mantığın devamı) [cite: 16]
        if (studentDto.getImageFile() != null && !studentDto.getImageFile().isEmpty()) {
            String savedFileName = fileStorageService.saveFile(studentDto.getImageFile());
            existingStudent.setImageURL("/images/" + savedFileName);
        }

        // Yeni kayıt logu (İşlemi yapanın detayıyla) [cite: 14]
        log.info("Student [{}] updated by [{}]. Role: [{}]",
                existingStudent.getUsername(), currentUsername, existingStudent.getRole());

        return studentRepository.save(existingStudent).viewAsStudentDTO();
    }

    public void deleteStudent(Long id) {
        studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.ERROR_STUDENT_NOT_FOUND + ": " + id));
        studentRepository.deleteById(id);
    }

}