package sau.lpm_v3.service;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sau.lpm_v3.model.Student;
import sau.lpm_v3.repository.StudentRepository;

@Service
@RequiredArgsConstructor

public class UserDetailsServiceImpl implements UserDetailsService {
    private final StudentRepository studentRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Student student = studentRepository.findByUsername(username);

        if (student == null) {
            throw new UsernameNotFoundException(username);
        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(student.getUsername())
                .password(student.getPassword())
                .roles(student.getRole().toString())
                .disabled(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .build();
    }
}
