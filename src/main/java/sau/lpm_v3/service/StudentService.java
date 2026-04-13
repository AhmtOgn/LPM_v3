package sau.lpm_v3.service;

import sau.lpm_v3.dtos.StudentDTO;
import java.util.List;

public interface StudentService {
    List<StudentDTO> getAllStudents(boolean isAdmin, String username);
    StudentDTO getStudentByIdSecure(Long id, boolean isAdmin, String currentUsername);
    StudentDTO createStudent(StudentDTO studentDto);
    StudentDTO updateStudent(Long id, StudentDTO studentDto, boolean isAdmin, String currentUsername);
    String deleteStudent(Long id, boolean isAdmin, String currentUsername);
    StudentDTO getStudentById(Long id);
}