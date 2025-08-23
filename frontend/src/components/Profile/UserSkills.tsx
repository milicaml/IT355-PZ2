import React, { useState, useEffect } from 'react';
import { Plus, Edit, Trash2, X, Save } from 'lucide-react';
import apiService from '../../services/api';
import type { Skill, SkillResponse } from '../../types';
import './UserSkills.css';

interface UserSkillsProps {
  userId?: number;
}

const UserSkills: React.FC<UserSkillsProps> = ({ userId }) => {
  const [userSkills, setUserSkills] = useState<SkillResponse[]>([]);
  const [availableSkills, setAvailableSkills] = useState<Skill[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [isAddingSkill, setIsAddingSkill] = useState(false);
  const [editingSkillId, setEditingSkillId] = useState<number | null>(null);
  const [selectedSkillId, setSelectedSkillId] = useState<number>(0);
  const [selectedProficiencyLevel, setSelectedProficiencyLevel] = useState<string>('BEGINNER');

  useEffect(() => {
    fetchUserSkills();
    fetchAvailableSkills();
  }, []);

  const fetchUserSkills = async () => {
    try {
      const skills = await apiService.getUserSkills();
      setUserSkills(skills);
    } catch (error: any) {
      setError('Failed to load user skills');
    } finally {
      setLoading(false);
    }
  };

  const fetchAvailableSkills = async () => {
    try {
      const skills = await apiService.getAllSkills();
      setAvailableSkills(skills);
    } catch (error: any) {
      console.error('Failed to load available skills:', error);
    }
  };

  const handleAddSkill = async () => {
    if (!selectedSkillId) return;

    try {
      const newSkill = await apiService.addUserSkill(selectedSkillId, selectedProficiencyLevel);
      setUserSkills([...userSkills, newSkill]);
      setIsAddingSkill(false);
      setSelectedSkillId(0);
      setSelectedProficiencyLevel('BEGINNER');
    } catch (error: any) {
      setError('Failed to add skill');
    }
  };

  const handleUpdateSkill = async (skillId: number) => {
    try {
      const updatedSkill = await apiService.updateUserSkill(skillId, selectedProficiencyLevel);
      setUserSkills(userSkills.map(skill => 
        skill.id === skillId ? updatedSkill : skill
      ));
      setEditingSkillId(null);
      setSelectedProficiencyLevel('BEGINNER');
    } catch (error: any) {
      setError('Failed to update skill');
    }
  };

  const handleRemoveSkill = async (skillId: number) => {
    try {
      await apiService.removeUserSkill(skillId);
      setUserSkills(userSkills.filter(skill => skill.id !== skillId));
    } catch (error: any) {
      setError('Failed to remove skill');
    }
  };

  const startEditing = (skill: SkillResponse) => {
    setEditingSkillId(skill.id);
    setSelectedProficiencyLevel(skill.proficiencyLevel || 'BEGINNER');
  };

  const cancelEditing = () => {
    setEditingSkillId(null);
    setSelectedProficiencyLevel('BEGINNER');
  };

  if (loading) {
    return <div className="loading">Loading skills...</div>;
  }

  return (
    <div className="user-skills">
      <div className="skills-header">
        <h3>My Skills</h3>
        {!isAddingSkill && (
          <button 
            onClick={() => setIsAddingSkill(true)} 
            className="btn btn-primary btn-sm"
          >
            <Plus size={16} />
            Add Skill
          </button>
        )}
      </div>

      {error && (
        <div className="error-message">
          {error}
        </div>
      )}

      {isAddingSkill && (
        <div className="add-skill-form">
          <div className="form-group">
            <label>Skill</label>
            <select
              value={selectedSkillId}
              onChange={(e) => setSelectedSkillId(Number(e.target.value))}
              className="form-select"
            >
              <option value={0}>Select a skill</option>
              {availableSkills
                .filter(skill => !userSkills.some(userSkill => userSkill.id === skill.id))
                .map(skill => (
                  <option key={skill.id} value={skill.id}>
                    {skill.title}
                  </option>
                ))}
            </select>
          </div>

          <div className="form-group">
            <label>Proficiency Level</label>
            <select
              value={selectedProficiencyLevel}
              onChange={(e) => setSelectedProficiencyLevel(e.target.value)}
              className="form-select"
            >
              <option value="BEGINNER">Beginner</option>
              <option value="INTERMEDIATE">Intermediate</option>
              <option value="ADVANCED">Advanced</option>
              <option value="EXPERT">Expert</option>
            </select>
          </div>

          <div className="form-actions">
            <button 
              onClick={handleAddSkill} 
              disabled={!selectedSkillId}
              className="btn btn-primary btn-sm"
            >
              <Save size={16} />
              Add
            </button>
            <button 
              onClick={() => {
                setIsAddingSkill(false);
                setSelectedSkillId(0);
                setSelectedProficiencyLevel('BEGINNER');
              }} 
              className="btn btn-secondary btn-sm"
            >
              <X size={16} />
              Cancel
            </button>
          </div>
        </div>
      )}

      <div className="skills-list">
        {userSkills.length === 0 ? (
          <p className="no-skills">No skills added yet. Add your first skill to get started!</p>
        ) : (
          userSkills.map(skill => (
            <div key={skill.id} className="skill-item">
              <div className="skill-info">
                <span className="skill-title">{skill.title}</span>
                {editingSkillId === skill.id ? (
                  <div className="skill-edit">
                    <select
                      value={selectedProficiencyLevel}
                      onChange={(e) => setSelectedProficiencyLevel(e.target.value)}
                      className="form-select form-select-sm"
                    >
                      <option value="BEGINNER">Beginner</option>
                      <option value="INTERMEDIATE">Intermediate</option>
                      <option value="ADVANCED">Advanced</option>
                      <option value="EXPERT">Expert</option>
                    </select>
                    <button 
                      onClick={() => handleUpdateSkill(skill.id)} 
                      className="btn btn-primary btn-sm"
                    >
                      <Save size={14} />
                    </button>
                    <button 
                      onClick={cancelEditing} 
                      className="btn btn-secondary btn-sm"
                    >
                      <X size={14} />
                    </button>
                  </div>
                ) : (
                  <div className="skill-actions">
                    <span className="proficiency-badge">
                      {skill.proficiencyLevel?.toLowerCase() || 'beginner'}
                    </span>
                    <button 
                      onClick={() => startEditing(skill)} 
                      className="btn btn-secondary btn-sm"
                    >
                      <Edit size={14} />
                    </button>
                    <button 
                      onClick={() => handleRemoveSkill(skill.id)} 
                      className="btn btn-danger btn-sm"
                    >
                      <Trash2 size={14} />
                    </button>
                  </div>
                )}
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
};

export default UserSkills;
