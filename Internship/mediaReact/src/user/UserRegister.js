import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import API_URL from '../Config';
import Swal from 'sweetalert2';

const UserRegister = () => {
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: '',
    mobnum: '',
  });

  const [otp, setOtp] = useState('');
  const [testOtpCode, setTestOtpCode] = useState('');
  const [otpSent, setOtpSent] = useState(false);
  const [otpVerified, setOtpVerified] = useState(false);
  const [sendingOtp, setSendingOtp] = useState(false);
  const [verifyingOtp, setVerifyingOtp] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');
  const [successMessage, setSuccessMessage] = useState('');
  const [timer, setTimer] = useState(0);

  const navigate = useNavigate();

  // Countdown timer for OTP expiry
  useEffect(() => {
    let interval = null;
    if (timer > 0) {
      interval = setInterval(() => {
        setTimer((prev) => prev - 1);
      }, 1000);
    }
    return () => clearInterval(interval);
  }, [timer]);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
    if (errorMessage) setErrorMessage('');
  };

  // Step 1: Request Email Verification OTP
  const handleSendOtp = async () => {
    const email = formData.email.trim();
    if (!email || !email.includes('@')) {
      setErrorMessage('Please enter a valid email address first.');
      return;
    }

    setSendingOtp(true);
    setErrorMessage('');
    setSuccessMessage('');

    try {
      const response = await fetch(`${API_URL}/api/v2/send-code?email=${encodeURIComponent(email)}`, {
        method: 'POST',
      });

      const data = await response.json().catch(() => ({}));

      if (response.ok || response.status === 201) {
        setOtpSent(true);
        setTimer(600); // 10 minutes
        if (data.testOtp) {
          setTestOtpCode(data.testOtp);
          setOtp(data.testOtp);
          setSuccessMessage(data.message || `Test OTP generated: ${data.testOtp}`);
        } else {
          setSuccessMessage(data.message || 'A 6-digit verification code has been dispatched to your email address.');
        }
      } else if (response.status === 409) {
        setErrorMessage(data.message || 'This email is already registered. Please log in.');
      } else {
        setErrorMessage(data.message || 'Unable to send verification OTP. Please try again.');
      }
    } catch (err) {
      setErrorMessage('Backend communication error. Please ensure the server is running.');
    } finally {
      setSendingOtp(false);
    }
  };

  // Step 2: Verify the 6-digit OTP
  const handleVerifyOtp = async () => {
    const cleanOtp = otp.trim();
    if (!cleanOtp || cleanOtp.length !== 6) {
      setErrorMessage('Please enter the 6-digit verification code.');
      return;
    }

    setVerifyingOtp(true);
    setErrorMessage('');
    setSuccessMessage('');

    try {
      const response = await fetch(
        `${API_URL}/api/v2/verify-code?email=${encodeURIComponent(formData.email.trim())}&code=${encodeURIComponent(cleanOtp)}`,
        { method: 'POST' }
      );

      const data = await response.json().catch(() => ({}));

      if (response.ok && (data.success || response.status === 200)) {
        setOtpVerified(true);
        setSuccessMessage('Email verified successfully! You can now complete your registration.');
        setErrorMessage('');
      } else {
        setErrorMessage(data.message || 'Invalid or expired verification code. Please check and try again.');
      }
    } catch (err) {
      setErrorMessage('Verification failed. Unable to communicate with the server.');
    } finally {
      setVerifyingOtp(false);
    }
  };

  // Step 3: Complete Registration
  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!otpVerified) {
      setErrorMessage('Please verify your email with the OTP before submitting registration.');
      return;
    }

    setSubmitting(true);
    setErrorMessage('');

    try {
      // Send as form-data parameter matching backend @RequestParam
      const params = new URLSearchParams();
      params.append('username', formData.username.trim());
      params.append('email', formData.email.trim());
      params.append('password', formData.password);
      params.append('mobnum', formData.mobnum.trim());

      const response = await fetch(`${API_URL}/api/v2/userregister`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: params.toString(),
      });

      const data = await response.json().catch(() => ({}));

      if (response.ok) {
        Swal.fire({
          icon: 'success',
          title: 'Account Created!',
          text: 'Your email has been verified and your Media Jungle account is ready.',
          confirmButtonText: 'Proceed to Sign In',
          confirmButtonColor: '#ffc107',
        }).then(() => {
          navigate('/login');
        });
      } else {
        setErrorMessage(data.message || 'Registration failed. Please check your details and try again.');
      }
    } catch (err) {
      setErrorMessage('Error submitting registration. Please verify the backend service is running.');
    } finally {
      setSubmitting(false);
    }
  };

  const formatTimer = (seconds) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs < 10 ? '0' : ''}${secs}`;
  };

  return (
    <div style={{
      minHeight: '100vh',
      backgroundColor: '#0c0f14',
      backgroundImage: 'radial-gradient(ellipse at 50% 10%, rgba(255, 193, 7, 0.12), transparent 70%)',
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
            <span className="badge bg-warning text-dark text-uppercase ms-2 small">New Account</span>
          </Link>
          <div className="d-flex align-items-center gap-2">
            <Link to="/" className="btn btn-outline-secondary btn-sm text-light">
              <i className="bi bi-house me-1"></i> Home
            </Link>
            <Link to="/login" className="btn btn-outline-warning btn-sm">
              <i className="bi bi-box-arrow-in-right me-1"></i> Sign In
            </Link>
          </div>
        </div>
      </header>

      {/* ── REGISTER FORM ── */}
      <div className="container my-auto py-5" style={{ maxWidth: '520px' }}>
        <div className="card shadow-lg border border-secondary border-opacity-25" style={{ backgroundColor: '#161a23', borderRadius: '16px' }}>
          <div className="card-body p-4 p-sm-5">
            <div className="text-center mb-4">
              <div className="d-inline-flex p-3 rounded-circle bg-warning bg-opacity-10 text-warning mb-2">
                <i className="bi bi-person-plus-fill fs-2"></i>
              </div>
              <h3 className="fw-bold text-light mb-1">Create Account</h3>
              <p className="text-secondary small">
                SOC 2 & ISO 27001 Protected: Email Verification Required
              </p>
            </div>

            {errorMessage && (
              <div className="alert alert-danger d-flex align-items-center py-2 px-3 small rounded-3 mb-3" role="alert">
                <i className="bi bi-exclamation-triangle-fill me-2 fs-5 flex-shrink-0"></i>
                <div>{errorMessage}</div>
              </div>
            )}

            {successMessage && (
              <div className="alert alert-success d-flex align-items-center py-2 px-3 small rounded-3 mb-3" role="alert">
                <i className="bi bi-check-circle-fill me-2 fs-5 flex-shrink-0"></i>
                <div>{successMessage}</div>
              </div>
            )}

            <div className="alert alert-dark border-secondary border-opacity-25 py-2 px-3 small rounded-3 mb-3 d-flex justify-content-between align-items-center">
              <span className="text-secondary small">
                <i className="bi bi-info-circle text-warning me-1"></i> Quick test login without registering?
              </span>
              <Link to="/login" className="btn btn-outline-warning btn-sm py-0 px-2 fw-semibold" style={{ fontSize: '0.75rem' }}>
                Use Test Mail
              </Link>
            </div>

            <form onSubmit={handleSubmit}>
              {/* Username */}
              <div className="mb-3">
                <label className="form-label small text-secondary fw-semibold">Username</label>
                <div className="input-group">
                  <span className="input-group-text bg-dark border-secondary border-opacity-25 text-secondary">
                    <i className="bi bi-person"></i>
                  </span>
                  <input
                    type="text"
                    name="username"
                    className="form-control bg-dark text-light border-secondary border-opacity-25 py-2"
                    placeholder="Enter your username"
                    value={formData.username}
                    onChange={handleChange}
                    required
                  />
                </div>
              </div>

              {/* Email + Send OTP Button */}
              <div className="mb-3">
                <div className="d-flex justify-content-between align-items-center mb-1">
                  <label className="form-label small text-secondary fw-semibold mb-0">Email Address</label>
                  {otpVerified && (
                    <span className="badge bg-success bg-opacity-25 text-success small">
                      <i className="bi bi-check-circle-fill me-1"></i> Verified
                    </span>
                  )}
                </div>
                <div className="input-group">
                  <span className="input-group-text bg-dark border-secondary border-opacity-25 text-secondary">
                    <i className="bi bi-envelope"></i>
                  </span>
                  <input
                    type="email"
                    name="email"
                    disabled={otpVerified}
                    className="form-control bg-dark text-light border-secondary border-opacity-25 py-2"
                    placeholder="name@example.com"
                    value={formData.email}
                    onChange={handleChange}
                    required
                  />
                  {!otpVerified && (
                    <button
                      type="button"
                      className="btn btn-outline-warning btn-sm px-3 fw-semibold"
                      disabled={sendingOtp || !formData.email || timer > 540}
                      onClick={handleSendOtp}
                    >
                      {sendingOtp ? (
                        <span className="spinner-border spinner-border-sm"></span>
                      ) : otpSent ? (
                        'Resend OTP'
                      ) : (
                        'Get OTP'
                      )}
                    </button>
                  )}
                </div>
              </div>

              {/* OTP Input Section (shown once OTP is sent and before verification) */}
              {otpSent && !otpVerified && (
                <div className="p-3 mb-3 rounded-3 border border-warning border-opacity-50" style={{ backgroundColor: 'rgba(255, 193, 7, 0.05)' }}>
                  <div className="d-flex justify-content-between align-items-center mb-2">
                    <label className="small text-warning fw-semibold mb-0">
                      <i className="bi bi-shield-lock me-1"></i> Enter 6-Digit Email OTP
                    </label>
                    {timer > 0 && (
                      <span className="badge bg-dark text-warning small">
                        Expires in: {formatTimer(timer)}
                      </span>
                    )}
                  </div>
                  <div className="input-group">
                    <input
                      type="text"
                      maxLength="6"
                      className="form-control bg-dark text-light border-warning border-opacity-50 text-center fw-bold fs-5 tracking-wider py-2"
                      placeholder="• • • • • •"
                      value={otp}
                      onChange={(e) => setOtp(e.target.value.replace(/\D/g, ''))}
                    />
                    <button
                      type="button"
                      className="btn btn-warning fw-semibold px-3"
                      disabled={verifyingOtp || otp.length !== 6}
                      onClick={handleVerifyOtp}
                    >
                      {verifyingOtp ? (
                        <span className="spinner-border spinner-border-sm"></span>
                      ) : (
                        <>
                          <i className="bi bi-check2-circle me-1"></i> Verify
                        </>
                      )}
                    </button>
                  </div>
                  {testOtpCode && (
                    <div className="mt-2 d-flex justify-content-between align-items-center bg-dark bg-opacity-75 p-2 rounded border border-warning border-opacity-25">
                      <span className="small text-warning" style={{ fontSize: '0.8rem' }}>
                        <i className="bi bi-key-fill me-1"></i> Test OTP: <strong>{testOtpCode}</strong>
                      </span>
                      <button
                        type="button"
                        className="btn btn-warning btn-sm py-0 px-2 fw-semibold"
                        style={{ fontSize: '0.75rem' }}
                        onClick={() => setOtp(testOtpCode)}
                      >
                        Auto-fill OTP
                      </button>
                    </div>
                  )}
                  <div className="form-text small text-secondary mt-1">
                    Check your email inbox or spam folder for your 6-digit confirmation code.
                  </div>
                </div>
              )}

              {/* Password */}
              <div className="mb-3">
                <label className="form-label small text-secondary fw-semibold">Password</label>
                <div className="input-group">
                  <span className="input-group-text bg-dark border-secondary border-opacity-25 text-secondary">
                    <i className="bi bi-lock"></i>
                  </span>
                  <input
                    type="password"
                    name="password"
                    className="form-control bg-dark text-light border-secondary border-opacity-25 py-2"
                    placeholder="Create a secure password"
                    value={formData.password}
                    onChange={handleChange}
                    required
                  />
                </div>
              </div>

              {/* Mobile Number */}
              <div className="mb-4">
                <label className="form-label small text-secondary fw-semibold">Mobile Number</label>
                <div className="input-group">
                  <span className="input-group-text bg-dark border-secondary border-opacity-25 text-secondary">
                    <i className="bi bi-phone"></i>
                  </span>
                  <input
                    type="tel"
                    name="mobnum"
                    className="form-control bg-dark text-light border-secondary border-opacity-25 py-2"
                    placeholder="e.g. 9876543210"
                    value={formData.mobnum}
                    onChange={handleChange}
                    required
                  />
                </div>
              </div>

              {/* Submit Button */}
              <button
                type="submit"
                className={`btn w-100 py-2 fw-semibold mb-3 shadow ${otpVerified ? 'btn-warning text-dark' : 'btn-secondary'}`}
                disabled={!otpVerified || submitting}
              >
                {submitting ? (
                  <>
                    <span className="spinner-border spinner-border-sm me-2" role="status"></span>
                    Creating Account...
                  </>
                ) : otpVerified ? (
                  <>
                    <i className="bi bi-person-check me-2"></i> Register Account
                  </>
                ) : (
                  <>
                    <i className="bi bi-shield-exclamation me-2"></i> Verify Email to Register
                  </>
                )}
              </button>
            </form>

            <div className="text-center pt-3 border-top border-secondary border-opacity-25">
              <p className="text-secondary small mb-2">
                Already registered?{' '}
                <Link to="/login" className="text-warning fw-semibold text-decoration-none">
                  Sign In <i className="bi bi-arrow-right-short"></i>
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

export default UserRegister;
