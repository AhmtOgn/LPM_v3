package sau.lpm_v3.dtos;

import sau.lpm_v3.model.Student;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentDTO {
    private long id;
    private String name;
    private String department;

    public Student toEntity() {
        Student student = new Student();
        student.setId(this.id);
        student.setName(this.name);
        student.setDepartment(this.department);
        return student;
    }
}


