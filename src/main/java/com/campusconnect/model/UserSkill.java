package com.campusconnect.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class UserSkill implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum SkillType {
        TEACH, LEARN
    }

    public enum ProficiencyLevel {
        BEGINNER, INTERMEDIATE, ADVANCED
    }

    private int id;
    private int userId;
    private int skillId;
    private String skillName;
    private String skillCategory;
    private SkillType skillType;
    private ProficiencyLevel proficiencyLevel = ProficiencyLevel.INTERMEDIATE;
    private Timestamp createdAt;

    public UserSkill() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getSkillId() { return skillId; }
    public void setSkillId(int skillId) { this.skillId = skillId; }

    public String getSkillName() { return skillName; }
    public void setSkillName(String skillName) { this.skillName = skillName; }

    public String getSkillCategory() { return skillCategory; }
    public void setSkillCategory(String skillCategory) { this.skillCategory = skillCategory; }

    public SkillType getSkillType() { return skillType; }
    public void setSkillType(SkillType skillType) { this.skillType = skillType; }

    public ProficiencyLevel getProficiencyLevel() { return proficiencyLevel; }
    public void setProficiencyLevel(ProficiencyLevel proficiencyLevel) { this.proficiencyLevel = proficiencyLevel; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}