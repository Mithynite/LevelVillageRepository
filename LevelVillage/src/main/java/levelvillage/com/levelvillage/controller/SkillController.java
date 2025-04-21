package levelvillage.com.levelvillage.controller;

import levelvillage.com.levelvillage.model.Skill;
import levelvillage.com.levelvillage.service.SkillService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * Controller for handling skill-related requests.
 * @author Jakub Hofman
 */
@RestController
@RequestMapping("/api/skills")
@CrossOrigin(origins = "http://138.3.255.133") //TODO změnit
public class SkillController {
    private final SkillService skillService;

    /**
     * Constructor for SkillController.
     *
     * @param skillService the service to handle skill-related operations
     */
    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    /**
     * Endpoint to fetch all available skills.
     *
     * @return ResponseEntity containing a list of all available skills.
     * The HTTP status code will be 200 (OK) if the request is successful.
     */
    @GetMapping
    public ResponseEntity<List<Skill>> getAllSkills() {
        List<Skill> skills = skillService.getAllSkills();
        return ResponseEntity.ok(skills);
    }

}
