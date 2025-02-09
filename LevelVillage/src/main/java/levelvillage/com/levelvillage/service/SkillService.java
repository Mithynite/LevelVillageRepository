package levelvillage.com.levelvillage.service;

import levelvillage.com.levelvillage.repository.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import levelvillage.com.levelvillage.model.Skill;

import java.util.List;

@Service
public class SkillService {

    private final SkillRepository skillRepository;

    @Autowired
    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    /**
     * Retrieve all skills from the database.
     * @return List of all available skills.
     */
    public List<Skill> getAllSkills() {
        return skillRepository.findAll();
    }

}
