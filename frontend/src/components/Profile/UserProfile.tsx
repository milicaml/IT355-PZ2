import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { ArrowLeft, Mail, Phone, MapPin, Calendar, User as UserIcon } from 'lucide-react';
import apiService from '../../services/api';
import type { User, SkillResponse } from '../../types';
import './UserProfile.css';

const UserProfile: React.FC = () => {
  const { userId } = useParams<{ userId: string }>();
  const [user, setUser] = useState<User | null>(null);
  const [userSkills, setUserSkills] = useState<SkillResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    if (userId) {
      fetchUserProfile();
    }
  }, [userId]);

  const fetchUserProfile = async () => {
    try {
      setLoading(true);
      const userData = await apiService.getUserById(parseInt(userId!));
      setUser(userData);
      
      // Fetch user skills if the user is a freelancer
      if (userData.userType === 'freelancer') {
        try {
          const skillsData = await apiService.getUserSkillsById(parseInt(userId!));
          setUserSkills(skillsData);
        } catch (skillsError) {
          console.error('Failed to load user skills:', skillsError);
          // Don't fail the entire profile load if skills fail
        }
      }
    } catch (error: any) {
      setError('Failed to load user profile');
    } finally {
      setLoading(false);
    }
  };

  const formatDate = (dateString: string) => {
    if (!dateString) {
      return 'N/A';
    }
    
    try {
      const date = new Date(dateString);
      if (isNaN(date.getTime())) {
        return 'N/A';
      }
      return date.toLocaleDateString();
    } catch (error) {
      console.error('Error formatting date:', dateString, error);
      return 'N/A';
    }
  };

  if (loading) {
    return (
      <div className="user-profile-container">
        <div className="loading">Loading user profile...</div>
      </div>
    );
  }

  if (error || !user) {
    return (
      <div className="user-profile-container">
        <div className="error-message">
          {error || 'User not found'}
        </div>
        <Link to="/job-applications" className="btn btn-secondary">
          <ArrowLeft size={16} />
          Back to Applications
        </Link>
      </div>
    );
  }

  return (
    <div className="user-profile-container">
      <div className="profile-header">
        <Link to="/job-applications" className="back-link">
          <ArrowLeft size={20} />
          Back to Applications
        </Link>
        <h1>User Profile</h1>
      </div>

      <div className="profile-card">
        <div className="profile-avatar">
          <UserIcon size={64} />
        </div>

        <div className="profile-info">
          <h2>{user.fullName}</h2>
          <p className="username">@{user.username}</p>
          
          <div className="user-type-badge">
            {user.userType}
          </div>

          <div className="profile-details">
            <div className="detail-item">
              <Mail size={16} />
              <span>{user.email}</span>
            </div>
            
            <div className="detail-item">
              <Phone size={16} />
              <span>{user.phone}</span>
            </div>
            
            <div className="detail-item">
              <MapPin size={16} />
              <span>{user.city}</span>
            </div>
            
            <div className="detail-item">
              <Calendar size={16} />
              <span>Member since {formatDate(user.createdAt)}</span>
            </div>
          </div>

          {user.bio && (
            <div className="bio-section">
              <h3>About</h3>
              <p>{user.bio}</p>
            </div>
          )}

          {/* Skills Section - Only show for freelancers */}
          {user.userType === 'freelancer' && (
            <div className="skills-section">
              <h3>Skills</h3>
              {userSkills.length > 0 ? (
                <div className="skills-list">
                  {userSkills.map((skill, index) => (
                    <div key={index} className="skill-item">
                      <span className="skill-name">{skill.title}</span>
                      {skill.proficiencyLevel && (
                        <span className="skill-level">{skill.proficiencyLevel}</span>
                      )}
                    </div>
                  ))}
                </div>
              ) : (
                <p className="no-skills">No skills listed</p>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default UserProfile;
