import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { User, LogOut, Menu, X } from 'lucide-react';
import './Header.css';

const Header: React.FC = () => {
  const { user, isAuthenticated, logout } = useAuth();
  const navigate = useNavigate();
  const [isMenuOpen, setIsMenuOpen] = useState(false);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const toggleMenu = () => {
    setIsMenuOpen(!isMenuOpen);
  };

  return (
    <header className="header">
      <div className="header-container">
        <Link to="/" className="logo">
          <h1>JobMarket</h1>
        </Link>

        <nav className={`nav ${isMenuOpen ? 'nav-open' : ''}`}>
          <Link to="/jobs" className="nav-link">Jobs</Link>
          {isAuthenticated && user?.userType === 'employer' && (
            <>
              <Link to="/my-jobs" className="nav-link">My Jobs</Link>
              <Link to="/jobs/create" className="nav-link">Create Job</Link>
              <Link to="/job-applications" className="nav-link">Job Applications</Link>
            </>
          )}
          {isAuthenticated && user?.userType === 'freelancer' && (
            <Link to="/applications" className="nav-link">My Applications</Link>
          )}
          {isAuthenticated && (
            <Link to="/profile" className="nav-link">Profile</Link>
          )}
        </nav>

        <div className="header-actions">
          {isAuthenticated ? (
            <div className="user-menu">
              <div className="user-info">
                <User size={20} />
                <span>{user?.fullName}</span>
              </div>
              <button onClick={handleLogout} className="logout-btn">
                <LogOut size={16} />
                Logout
              </button>
            </div>
          ) : (
            <div className="auth-buttons">
              <Link to="/login" className="btn btn-secondary">Login</Link>
              <Link to="/register" className="btn btn-primary">Register</Link>
            </div>
          )}
        </div>

        <button className="menu-toggle" onClick={toggleMenu}>
          {isMenuOpen ? <X size={24} /> : <Menu size={24} />}
        </button>
      </div>
    </header>
  );
};

export default Header;
