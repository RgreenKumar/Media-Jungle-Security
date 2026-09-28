import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import MediaHomeScreen from './user/MediaHomeScreen';
import UserLogin from './user/UserLogin';
import UserRegister from './user/UserRegister';
import Dashboard from './admin/Dashboard';
import Login from './admin/login';
import ProtectedRoute from './components/ProtectedRoute';
import './App.css';

const App = () => {
  return (
    <Router>
      <Routes>
        {/* User / Consumer OTT Streaming Routes */}
        <Route path="/" element={<MediaHomeScreen />} />
        <Route path="/login" element={<UserLogin />} />
        <Route path="/register" element={<UserRegister />} />

        {/* Administrator Portal Routes */}
        <Route path="/admin" element={<Login />} />
        <Route path="/admin/login" element={<Login />} />
        <Route
          path="/admin/Dashboard"
          element={
            <ProtectedRoute requiredRole="ADMIN">
              <Dashboard />
            </ProtectedRoute>
          }
        />
        <Route
          path="/security-standards"
          element={
            <ProtectedRoute requiredRole="ADMIN">
              <Dashboard />
            </ProtectedRoute>
          }
        />

        {/* Fallback */}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </Router>
  );
};

export default App;
