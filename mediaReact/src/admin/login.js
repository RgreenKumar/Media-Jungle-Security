import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import API_URL from '../Config';
import Swal from 'sweetalert2';

const Login = () => {
  const [user, setUser] = useState({ username: '', password: '' });
  const [loading, setLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');
  const navigate = useNavigate();

  const handleChange = (e) => {
    setUser({ ...user, [e.target.name]: e.target.value });
    if (errorMessage) setErrorMessage('');
  };

  const handleFillCredentials = (username, password) => {
    setUser({ username, password });
    if (errorMessage) setErrorMessage('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrorMessage('');
    setLoading(true);

    try {
      const sendData = {
        username: user.username.trim(),
        password: user.password
      };

      const response = await fetch(`${API_URL}/api/v2/login/admin`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(sendData),
      });

      if (response.ok) {
        const data = await response.json();
        const jwtToken = data.Token;
        const name = data.UserName || user.username;
        const userId = data.AdminId || 1;
        const role = data.Role || 'ADMIN';

        sessionStorage.setItem("username", name);
        sessionStorage.setItem('tokenn', jwtToken);
        sessionStorage.setItem('adminId', userId);
        sessionStorage.setItem('name', 'true');
        sessionStorage.setItem('role', role);

        Swal.fire({
          icon: 'success',
          title: 'Login Successful',
          text: `Welcome back, ${name}!`,
          timer: 1500,
          showConfirmButton: false
        });

        setTimeout(() => {
          navigate('/admin/Dashboard');
        }, 600);
      } else {
        const errorData = await response.json().catch(() => ({}));
        const msg = errorData.message || (response.status === 401 ? 'Incorrect username or password' : 'Login failed. Please try again.');
        setErrorMessage(msg);
        Swal.fire({
          icon: 'error',
          title: 'Login Failed',
          text: msg,
        });
      }
    } catch (error) {
      console.error('Error during login:', error);
      const networkMsg = 'Unable to connect to the backend server at ' + API_URL + '. Please verify the backend is running.';
      setErrorMessage(networkMsg);
      Swal.fire({
        icon: 'error',
        title: 'Connection Error',
        text: networkMsg,
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container" style={{ minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
      <div className="col-12 col-sm-10 col-md-8 col-lg-5">
        <div className="card shadow-lg border-0 rounded-4 my-5" style={{ overflow: 'hidden' }}>
          <div className="card-header text-center py-4" style={{ backgroundColor: '#2b2f3a', color: '#fff' }}>
            <h3 className="fw-bold mb-1">Media Jungle</h3>
            <p className="small mb-0" style={{ color: '#adb5bd' }}>Security Administration Portal</p>
          </div>

          <div className="card-body p-4 p-sm-5 bg-light">
            <h4 className="text-center text-dark fw-semibold mb-4">Admin Login</h4>

            {errorMessage && (
              <div className="alert alert-danger text-center py-2 mb-4" role="alert">
                <strong>Error: </strong> {errorMessage}
              </div>
            )}

            <form onSubmit={handleSubmit}>
              <div className="mb-3">
                <label className="form-label fw-semibold text-secondary small" htmlFor="usernameInput">
                  Username
                </label>
                <input
                  id="usernameInput"
                  className="form-control form-control-lg"
                  name="username"
                  type="text"
                  placeholder="Enter username"
                  value={user.username}
                  onChange={handleChange}
                  required
                  autoFocus
                />
              </div>

              <div className="mb-4">
                <label className="form-label fw-semibold text-secondary small" htmlFor="passwordInput">
                  Password
                </label>
                <input
                  id="passwordInput"
                  className="form-control form-control-lg"
                  name="password"
                  type="password"
                  placeholder="Enter password"
                  value={user.password}
                  onChange={handleChange}
                  required
                />
              </div>

              <button
                className="btn btn-primary btn-lg w-100 fw-bold mb-3 shadow-sm"
                type="submit"
                disabled={loading}
                style={{
                  backgroundColor: '#0d6efd',
                  borderColor: '#0d6efd',
                  padding: '12px',
                  fontSize: '1rem',
                  letterSpacing: '0.5px'
                }}
              >
                {loading ? 'LOGGING IN...' : 'LOGIN'}
              </button>

              <div className="card border-0 bg-white shadow-sm p-3 mt-4 rounded-3">
                <div className="text-muted small fw-bold mb-2">Quick Auto-fill Credentials:</div>
                <div className="d-flex gap-2">
                  <button
                    type="button"
                    className="btn btn-sm btn-outline-primary flex-fill"
                    onClick={() => handleFillCredentials('admin', 'admin123')}
                  >
                    admin / admin123
                  </button>
                  <button
                    type="button"
                    className="btn btn-sm btn-outline-secondary flex-fill"
                    onClick={() => handleFillCredentials('Hari', 'Ackerman27')}
                  >
                    Hari / Ackerman27
                  </button>
                </div>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Login;
