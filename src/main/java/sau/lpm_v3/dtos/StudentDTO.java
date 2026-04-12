package sau.lpm_v3.dtos;

import org.springframework.web.multipart.MultipartFile;
import sau.lpm_v3.model.Student;
import sau.lpm_v3.enums.Role;
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
    private String username;
    private String password;
    private String imageURL;
    private MultipartFile imageFile;
    private Role role;

    public Student toEntity() {
        return new Student(this);
    }
}


