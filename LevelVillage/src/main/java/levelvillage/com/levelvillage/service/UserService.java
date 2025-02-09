package levelvillage.com.levelvillage.service;

import levelvillage.com.levelvillage.dto.UserDTO;
import levelvillage.com.levelvillage.model.Skill;
import levelvillage.com.levelvillage.model.User;
import levelvillage.com.levelvillage.model.UserSkill;
import levelvillage.com.levelvillage.repository.SkillRepository;
import levelvillage.com.levelvillage.repository.UserRepository;
import levelvillage.com.levelvillage.repository.UserSkillRepository;
import levelvillage.com.levelvillage.util.JWTTokenUtil;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final UserSkillRepository userSkillsRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JWTTokenUtil jwtTokenUtil;

    public UserService(UserRepository userRepository, SkillRepository skillRepository, UserSkillRepository userSkillsRepository, JWTTokenUtil jwtTokenUtil) {
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
        this.userSkillsRepository = userSkillsRepository;
        this.jwtTokenUtil = jwtTokenUtil;
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.emptyList()
        );
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    public String authenticateAndGenerateToken(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password!"));

        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password!");
        }

        return jwtTokenUtil.generateToken(user.getUsername());
    }

    public User registerUser(String username, String email, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username is already taken!");
        }

        boolean emailExists = userRepository.findAll().stream()
                .anyMatch(user -> user.getEmail().equals(email));
        if (emailExists) {
            throw new IllegalArgumentException("Email is already registered!");
        }

        String encodedPassword = bCryptPasswordEncoder.encode(password);
        User user = new User(username, email, encodedPassword);
        return userRepository.save(user);
    }

    public long getTokenExpiration(String token) {
        return jwtTokenUtil.extractExpiration(token);
    }

    public User updateUserProfile(String username, UserDTO userDTO) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found!"));

        if (userDTO.getUsername() != null) {
            user.setUsername(userDTO.getUsername());
        }
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail());
        }
        if (userDTO.getBio() != null) {
            user.setBio(userDTO.getBio());
        }
        if (userDTO.getSkills() != null && !userDTO.getSkills().isEmpty()) {
            userSkillsRepository.deleteByUserId(user.getId());

            List<UserSkill> userSkills = userDTO.getSkills().stream().map(skillDTO -> {
                Skill skill = skillRepository.findById(skillDTO.getId())
                        .orElseThrow(() -> new IllegalArgumentException("Skill not found with ID: " + skillDTO.getId()));
                return new UserSkill(user, skill);
            }).collect(Collectors.toList());

            userSkillsRepository.saveAll(userSkills);
        }

        return userRepository.save(user);
    }

    public List<Skill> getUserSkills(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found!"));

        return userSkillsRepository.findByUser(user).stream()
                .map(UserSkill::getSkill)
                .collect(Collectors.toList());
    }

    public void assignSkillsToUser(Long userId, List<Long> skillIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found!"));

        userSkillsRepository.deleteByUserId(userId);

        List<UserSkill> userSkills = skillIds.stream()
                .map(skillId -> {
                    Skill skill = skillRepository.findById(skillId)
                            .orElseThrow(() -> new IllegalArgumentException("Skill not found with ID: " + skillId));
                    return new UserSkill(user, skill);
                }).collect(Collectors.toList());

        userSkillsRepository.saveAll(userSkills);
    }
}

