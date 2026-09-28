import React from 'react';
import { Link } from 'react-router-dom';

const Usersidebar = () => {
  return (
    <nav className="navbar navbar-expand-lg navbar-dark bg-dark mb-3 px-3">
      <Link className="navbar-brand text-warning" to="/">Media Jungle</Link>
      <div className="navbar-nav">
        <Link className="nav-link" to="/">Dashboard</Link>
        <Link className="nav-link" to="/admin">Admin</Link>
      </div>
    </nav>
  );
};

export default Usersidebar;
