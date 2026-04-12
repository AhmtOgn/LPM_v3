package sau.lpm_v3.model;

import sau.lpm_v3.dtos.StudentDTO;
import sau.lpm_v3.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Student {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(length = 64)
    private String name;
    @Column(length = 128)
    private String department;
    @Column(unique = true, length = 32, nullable = false)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(length = 255)
    private String imageURL;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;


    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<Reservation> reservations;

    public Student(StudentDTO studentDTO) {
        this.id = studentDTO.getId();
        this.name = studentDTO.getName();
        this.department = studentDTO.getDepartment();
        this.username = studentDTO.getUsername();
        this.password = studentDTO.getPassword();
        this.imageURL = studentDTO.getImageURL();
        this.role = studentDTO.getRole();
    }

    public StudentDTO viewAsStudentDTO() {
       return new StudentDTO(id, name, department, username, null, imageURL, null, role);
    }
}
