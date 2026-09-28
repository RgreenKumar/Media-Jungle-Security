import React from 'react';
import { Navigate, Link } from 'react-router-dom';

const ProtectedRoute = ({ children, requiredRole }) => {
  const token = sessionStorage.getItem('tokenn') || sessionStorage.getItem('token') || sessionStorage.getItem('userToken');
  const role = sessionStorage.getItem('role') || 'USER';

  if (!token) {
    // If route requires admin, redirect to admin login, otherwise user login
    if (requiredRole === 'ADMIN') {
      return <Navigate to="/admin" replace />;
    }
    return <Navigate to="/login" replace />;
  }

  // If route requires ADMIN role, verify role is ADMIN or SUBADMIN
  if (requiredRole === 'ADMIN' && role !== 'ADMIN' && role !== 'SUBADMIN') {
    return (
      <div style={{
        minHeight: '100vh',
        backgroundColor: '#0f1218',
        color: '#e1e7ec',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif'
      }}>
        <div className="card text-center p-4 border-danger shadow-lg" style={{ backgroundColor: '#171b23', maxWidth: '480px' }}>
          <div className="text-danger mb-3" style={{ fontSize: '3rem' }}>
            <i className="bi bi-shield-slash"></i>
          </div>
          <h4 className="fw-bold text-danger mb-2">Access Denied</h4>
          <p className="text-secondary small mb-4">
            Administrator privileges are required to view the Media Jungle Admin Management Console and Security Compliance Suite.
            Your current role is <span className="badge bg-secondary text-uppercase">{role}</span>.
          </p>
          <div className="d-flex justify-content-center gap-2">
            <Link to="/" className="btn btn-warning btn-sm fw-semibold">
              <i className="bi bi-house-door me-1"></i> Return to Streaming Site
            </Link>
            <Link to="/admin" className="btn btn-outline-danger btn-sm">
              <i className="bi bi-box-arrow-in-right me-1"></i> Login as Admin
            </Link>
          </div>
        </div>
      </div>
    );
  }

  return children;
};

export default ProtectedRoute;
