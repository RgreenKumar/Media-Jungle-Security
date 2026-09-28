import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import API_URL from '../Config';
import Swal from 'sweetalert2';

const UserLogin = () => {
  const [credentials, setCredentials] = useState({ email: '', password: '' });
  const [loading, setLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');
  const navigate = useNavigate();

  const handleChange = (e) => {
    setCredentials({ ...credentials, [e.target.name]: e.target.value });
    if (errorMessage) setErrorMessage('');
  };

  const handleFillDemo = (email, password) => {
    setCredentials({ email, password });
    if (errorMessage) setErrorMessage('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrorMessage('');
    setLoading(true);

    try {
      const response = await fetch(`${API_URL}/api/v2/login`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          email: credentials.email.trim(),
          password: credentials.password,
        }),
      });

      const data = await response.json().catch(() => ({}));

      if (response.ok) {
        const token = data.token;
        const name = data.name || credentials.email.split('@')[0];
        const role = data.role || 'USER';

        sessionStorage.setItem('tokenn', token);
        sessionStorage.setItem('userToken', token);
        sessionStorage.setItem('username', name);
        sessionStorage.setItem('userEmail', data.email || credentials.email);
        sessionStorage.setItem('userId', data.userId || '1');
        sessionStorage.setItem('role', role);

        Swal.fire({
          icon: 'success',
          title: 'Welcome Back!',
          text: `Signed in as ${name}`,
          timer: 1500,
          showConfirmButton: false,
        });

        navigate('/');
      } else if (response.status === 423) {
        setErrorMessage(data.message || 'Account is temporarily locked due to multiple failed login attempts. Please try again later.');
      } else {
        setErrorMessage(data.message || 'Invalid email or password. Please try again.');
      }
    } catch (err) {
      setErrorMessage('Unable to connect to the backend server. Please verify the service is running.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{
      minHeight: '100vh',
      backgroundColor: '#0c0f14',
      backgroundImage: 'radial-gradient(ellipse at 50% 10%, rgba(229, 9, 20, 0.15), transparent 70%)',
      color: '#e1e7ec',
      display: 'flex',
      flexDirection: 'column',
      fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif',
    }}>
      {/* ── TOP NAV ── */}
      <header className="navbar px-4 py-3 border-bottom border-secondary border-opacity-25" style={{ backgroundColor: 'rgba(15, 18, 24, 0.85)', backdropFilter: 'blur(10px)' }}>
        <div className="container-fluid d-flex justify-content-between align-items-center">
          <Link to="/" className="d-flex align-items-center gap-2 text-decoration-none">
            <span style={{ fontSize: '1.8rem' }}>🦁</span>
            <span className="fw-bold fs-4 text-warning tracking-wider">MEDIA JUNGLE</span>
            <span className="badge bg-danger text-uppercase ms-2 small">Streaming</span>
          </Link>
          <div className="d-flex align-items-center gap-2">
            <Link to="/" className="btn btn-outline-secondary btn-sm text-light">
              <i className="bi bi-house me-1"></i> Home
            </Link>
            <Link to="/admin" className="btn btn-outline-warning btn-sm">
              <i className="bi bi-shield-lock me-1"></i> Admin Portal
            </Link>
          </div>
        </div>
      </header>

      {/* ── LOGIN BOX ── */}
      <div className="container my-auto py-5" style={{ maxWidth: '440px' }}>
        <div className="card shadow-lg border border-secondary border-opacity-25" style={{ backgroundColor: '#161a23', borderRadius: '16px' }}>
          <div className="card-body p-4 p-sm-5">
            <div className="text-center mb-4">
              <div className="d-inline-flex p-3 rounded-circle bg-warning bg-opacity-10 text-warning mb-2">
                <i className="bi bi-person-fill fs-2"></i>
              </div>
              <h3 className="fw-bold text-light mb-1">User Sign In</h3>
              <p className="text-secondary small">Access the Media Jungle OTT Streaming Catalog</p>
            </div>

            {errorMessage && (
              <div className="alert alert-danger d-flex align-items-center py-2 px-3 small rounded-3 mb-4" role="alert">
                <i className="bi bi-exclamation-triangle-fill me-2 fs-5 flex-shrink-0"></i>
                <div>{errorMessage}</div>
              </div>
            )}

            <form onSubmit={handleSubmit}>
              <div className="mb-3">
                <label className="form-label small text-secondary fw-semibold">Email Address</label>
                <div className="input-group">
                  <span className="input-group-text bg-dark border-secondary border-opacity-25 text-secondary">
                    <i className="bi bi-envelope"></i>
                  </span>
                  <input
                    type="email"
                    name="email"
                    className="form-control bg-dark text-light border-secondary border-opacity-25 py-2"
                    placeholder="name@example.com"
                    value={credentials.email}
                    onChange={handleChange}
                    required
                  />
                </div>
              </div>

              <div className="mb-4">
                <label className="form-label small text-secondary fw-semibold">Password</label>
                <div className="input-group">
                  <span className="input-group-text bg-dark border-secondary border-opacity-25 text-secondary">
                    <i className="bi bi-lock"></i>
                  </span>
                  <input
                    type="password"
                    name="password"
                    className="form-control bg-dark text-light border-secondary border-opacity-25 py-2"
                    placeholder="Enter your password"
                    value={credentials.password}
                    onChange={handleChange}
                    required
                  />
                </div>
              </div>

              <button
                type="submit"
                className="btn btn-warning w-100 py-2 fw-semibold text-dark mb-3 shadow"
                disabled={loading}
              >
                {loading ? (
                  <>
                    <span className="spinner-border spinner-border-sm me-2" role="status"></span>
                    Signing In...
                  </>
                ) : (
                  <>
                    <i className="bi bi-box-arrow-in-right me-2"></i> Sign In to Stream
                  </>
                )}
              </button>

              {/* Quick Auto-fill Test Mail / User Credentials */}
              <div className="card border-0 bg-dark bg-opacity-75 p-3 mb-3 rounded-3 border border-secondary border-opacity-25 shadow-sm">
                <div className="text-secondary small fw-bold mb-2 d-flex align-items-center justify-content-between">
                  <span><i className="bi bi-envelope-check-fill text-warning me-1"></i> Quick Test Mail Login:</span>
                  <span className="badge bg-warning bg-opacity-25 text-warning border border-warning border-opacity-25">1-Click Auto Fill</span>
                </div>
                <div className="d-flex flex-column gap-2">
                  <button
                    type="button"
                    className="btn btn-sm btn-outline-warning text-start d-flex justify-content-between align-items-center py-2 px-3"
                    onClick={() => handleFillDemo('testuser@mediajungle.com', 'Password@123')}
                  >
                    <div>
                      <div className="fw-semibold text-light small">testuser@mediajungle.com</div>
                      <div className="text-secondary" style={{ fontSize: '0.75rem' }}>Pass: Password@123 (Consumer Streaming)</div>
                    </div>
                    <span className="badge bg-warning text-dark small">Use Mail</span>
                  </button>

                  <button
                    type="button"
                    className="btn btn-sm btn-outline-info text-start d-flex justify-content-between align-items-center py-2 px-3"
                    onClick={() => handleFillDemo('abhishek@gmail.com', 'Password@123')}
                  >
                    <div>
                      <div className="fw-semibold text-light small">abhishek@gmail.com</div>
                      <div className="text-secondary" style={{ fontSize: '0.75rem' }}>Pass: Password@123 (Admin User)</div>
                    </div>
                    <span className="badge bg-info text-dark small">Use Mail</span>
                  </button>
                </div>
              </div>
            </form>

            <div className="text-center pt-3 border-top border-secondary border-opacity-25">
              <p className="text-secondary small mb-2">
                Don't have an account?{' '}
                <Link to="/register" className="text-warning fw-semibold text-decoration-none">
                  Register with Email OTP <i className="bi bi-arrow-right-short"></i>
                </Link>
              </p>
              <p className="text-muted small mb-0">
                Are you an administrator?{' '}
                <Link to="/admin" className="text-info fw-semibold text-decoration-none">
                  Switch to Admin Login
                </Link>
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default UserLogin;
