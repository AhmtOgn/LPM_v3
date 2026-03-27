package sau.lpm_v3.service;

import sau.lpm_v3.dtos.StudentDTO;
import sau.lpm_v3.exception.ErrorMessages;
import sau.lpm_v3.exception.ResourceAlreadyExistsException;
import sau.lpm_v3.exception.ResourceNotFoundException;
import sau.lpm_v3.model.Student;
import sau.lpm_v3.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;

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

    public StudentDTO createStudent(Student student) {
        if (studentRepository.findById(student.getId()).isPresent()) {
            throw new ResourceAlreadyExistsException(ErrorMessages.ERROR_STUDENT_ALREADY_EXIST + ": " + student.getId());
        }
        return studentRepository.save(student).viewAsStudentDTO();
    }

    public StudentDTO updateStudent(Long id, Student student) {
        studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.ERROR_STUDENT_NOT_FOUND + ": " + id));
        student.setId(id);
        return studentRepository.save(student).viewAsStudentDTO();
    }

    public void deleteStudent(Long id) {
        studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.ERROR_STUDENT_NOT_FOUND + ": " + id));
        studentRepository.deleteById(id);
    }

}