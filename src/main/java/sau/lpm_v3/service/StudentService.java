package sau.lpm_v3.service;

import sau.lpm_v3.dtos.StudentDTO;
import sau.lpm_v3.model.Student;

import java.util.List;

public interface StudentService {
    public List<StudentDTO> getAllStudents();
    public StudentDTO getStudentById(Long id);
    public Student getStudentEntityById(Long id);
    public StudentDTO createStudent(StudentDTO studentDto);
    public StudentDTO updateStudent(Long id, StudentDTO studentDto);
    public void deleteStudent(Long id);
}