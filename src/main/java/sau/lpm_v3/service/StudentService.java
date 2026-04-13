package sau.lpm_v3.service;

import sau.lpm_v3.dtos.StudentDTO;
import sau.lpm_v3.model.Student;

import java.util.List;

public interface StudentService {
    public List<StudentDTO> getAllStudentsFiltered(boolean isAdmin, String username);
    public List<StudentDTO> getAllStudents();
    public StudentDTO getStudentById(Long id);
    public StudentDTO createStudent(StudentDTO studentDto);
    public StudentDTO updateStudent(Long id, StudentDTO studentDto, boolean isAdmin, String currentUsername);
    public void deleteStudent(Long id);
}