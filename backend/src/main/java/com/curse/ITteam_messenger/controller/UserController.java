    package com.curse.ITteam_messenger.controller;

    import com.curse.ITteam_messenger.dto.UserShortDTO;
    import com.curse.ITteam_messenger.model.User;
    import com.curse.ITteam_messenger.repository.UserRepository;
    import lombok.RequiredArgsConstructor;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;
    import java.util.stream.Collectors;

    @RestController
    @RequestMapping("/api/users")
    @RequiredArgsConstructor
    public class UserController {
        private final UserRepository userRepository;


        @GetMapping("/by-team/{teamName}")
        public ResponseEntity<List<User>> getUsersByTeam(@PathVariable String teamName) {
            List<User> all = userRepository.findAll();
            List<User> filtered = all.stream()
                    .filter(user -> user.getTeam() != null && teamName.equalsIgnoreCase(user.getTeam().getName()))
                    .collect(Collectors.toList());

            System.out.println("🔍 Поиск по команде " + teamName + ": найдено " + filtered.size() + " пользователей");

            return ResponseEntity.ok(filtered);
        }


        @GetMapping
        public ResponseEntity<List<UserShortDTO>> getAllUsers() {
            List<UserShortDTO> users = userRepository.findAll().stream().map(user -> {
                UserShortDTO dto = new UserShortDTO();
                dto.setId(user.getId());
                dto.setUsername(user.getUsername());
                dto.setEmail(user.getEmail());
                dto.setFirstName(user.getFirstName());
                dto.setLastName(user.getLastName());
                dto.setTeamRole(user.getTeamRole());
                dto.setExperienceLevel(user.getExperienceLevel());
                return dto;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(users);
        }
    }

