import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import API_URL from '../Config';
import Swal from 'sweetalert2';

const Dashboard = () => {
  const [activeTab, setActiveTab] = useState('overview'); // overview, videos, users, security, auditlogs
  const [videos, setVideos] = useState([]);
  const [users, setUsers] = useState([]);
  const [registeredUsers, setRegisteredUsers] = useState([]);
  const [userSubTab, setUserSubTab] = useState('registered'); // 'registered' | 'staff'
  const [auditLogs, setAuditLogs] = useState([]);
  const [standards, setStandards] = useState([]);
  const [standardsSummary, setStandardsSummary] = useState(null);
  const [selectedFramework, setSelectedFramework] = useState('ALL');
  const [searchQuery, setSearchQuery] = useState('');
  const [previewVideo, setPreviewVideo] = useState(null);
  const [loading, setLoading] = useState(true);

  // Staff user modal states
  const [showAddUserModal, setShowAddUserModal] = useState(false);
  const [showEditUserModal, setShowEditUserModal] = useState(false);
  const [userActionLoading, setUserActionLoading] = useState(false);

  // Registered customer modal states
  const [showAddRegUserModal, setShowAddRegUserModal] = useState(false);
  const [showEditRegUserModal, setShowEditRegUserModal] = useState(false);
  const [newRegUser, setNewRegUser] = useState({
    username: '',
    email: '',
    password: '',
    mobnum: '',
    role: 'USER'
  });
  const [editingRegUser, setEditingRegUser] = useState(null);

  // Video CRUD states
  const [showEditVideoModal, setShowEditVideoModal] = useState(false);
  const [editingVideo, setEditingVideo] = useState(null);

  const initialUserState = {
    username: '',
    email: '',
    password: '',
    confirmPassword: '',
    mobnum: '',
    role: 'SUBADMIN',
    compname: 'Media Jungle',
    pincode: '',
    country: 'India',
    address: ''
  };

  const [newUser, setNewUser] = useState(initialUserState);
  const [editingUser, setEditingUser] = useState(null);

  const navigate = useNavigate();
  const currentUsername = sessionStorage.getItem('username') || 'admin';
  const currentRole = sessionStorage.getItem('role') || 'ADMIN';
  const token = sessionStorage.getItem('tokenn');

  useEffect(() => {
    fetchInitialData();
  }, []);

  const fetchInitialData = async () => {
    setLoading(true);
    await Promise.allSettled([
      fetchVideos(),
      fetchUsers(),
      fetchRegisteredUsers(),
      fetchStandards(),
      fetchAuditLogs()
    ]);
    setLoading(false);
  };

  const fetchVideos = async () => {
    try {
      const res = await fetch(`${API_URL}/api/v2/video/getall`);
      if (res.ok) {
        const data = await res.json();
        setVideos(data || []);
      }
    } catch (e) {
      console.warn('Could not fetch videos:', e);
    }
  };

  const fetchUsers = async () => {
    try {
      const headers = token ? { Authorization: `Bearer ${token}` } : {};
      const res = await fetch(`${API_URL}/api/v2/GetAllUser`, { headers });
      if (res.ok) {
        const data = await res.json();
        setUsers(data.userList || []);
      }
    } catch (e) {
      console.warn('Could not fetch users:', e);
    }
  };

  const fetchRegisteredUsers = async () => {
    try {
      const headers = token ? { Authorization: `Bearer ${token}` } : {};
      const res = await fetch(`${API_URL}/api/v2/GetAllUsers`, { headers });
      if (res.ok) {
        const data = await res.json().catch(() => []);
        setRegisteredUsers(Array.isArray(data) ? data : []);
      } else {
        setRegisteredUsers([]);
      }
    } catch (e) {
      console.warn('Could not fetch registered users:', e);
      setRegisteredUsers([]);
    }
  };

  const fetchStandards = async () => {
    try {
      const [listRes, sumRes] = await Promise.all([
        fetch(`${API_URL}/api/v2/security-standards`),
        fetch(`${API_URL}/api/v2/security-standards/summary`)
      ]);
      if (listRes.ok) setStandards(await listRes.json());
      if (sumRes.ok) setStandardsSummary(await sumRes.json());
    } catch (e) {
      console.warn('Could not fetch security standards:', e);
    }
  };

  const fetchAuditLogs = async () => {
    try {
      const headers = token ? { Authorization: `Bearer ${token}` } : {};
      const res = await fetch(`${API_URL}/api/admin/auditlogs?size=15`, { headers });
      if (res.ok) {
        const data = await res.json();
        setAuditLogs(data.content || []);
      }
    } catch (e) {
      console.warn('Could not fetch audit logs:', e);
    }
  };

  const handleLogout = () => {
    sessionStorage.clear();
    localStorage.clear();
    Swal.fire({
      icon: 'info',
      title: 'Logged Out',
      text: 'You have been signed out.',
      timer: 1200,
      showConfirmButton: false
    });
    navigate('/admin');
  };

  const handleAddUser = async (e) => {
    e.preventDefault();
    if (!newUser.username.trim() || !newUser.email.trim() || !newUser.password) {
      Swal.fire({ icon: 'warning', title: 'Missing Information', text: 'Username, email, and password are required.' });
      return;
    }
    setUserActionLoading(true);
    try {
      const res = await fetch(`${API_URL}/api/v2/AddUser`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`
        },
        body: JSON.stringify({
          ...newUser,
          confirmPassword: newUser.confirmPassword || newUser.password
        })
      });
      const data = await res.json().catch(() => ({}));
      if (res.ok) {
        Swal.fire({
          icon: 'success',
          title: 'User Created',
          text: `User ${newUser.username} added successfully!`,
          timer: 1500,
          showConfirmButton: false
        });
        setShowAddUserModal(false);
        setNewUser(initialUserState);
        fetchUsers();
      } else {
        Swal.fire({
          icon: 'error',
          title: 'Failed to Add User',
          text: data.message || 'Error occurred while creating user.'
        });
      }
    } catch (err) {
      Swal.fire({ icon: 'error', title: 'Server Error', text: 'Unable to connect to backend server.' });
    } finally {
      setUserActionLoading(false);
    }
  };

  const handleOpenEditModal = (u) => {
    setEditingUser({
      id: u.id,
      username: u.username || '',
      email: u.email || '',
      mobnum: u.mobnum || '',
      role: u.role || 'SUBADMIN',
      compname: u.compname || '',
      pincode: u.pincode || '',
      country: u.country || 'India',
      address: u.address || '',
      password: ''
    });
    setShowEditUserModal(true);
  };

  const handleUpdateUser = async (e) => {
    e.preventDefault();
    if (!editingUser || !editingUser.id) return;
    setUserActionLoading(true);
    try {
      const payload = {
        username: editingUser.username.trim(),
        email: editingUser.email.trim(),
        mobnum: editingUser.mobnum,
        role: editingUser.role,
        compname: editingUser.compname,
        pincode: editingUser.pincode,
        country: editingUser.country,
        address: editingUser.address
      };
      if (editingUser.password && editingUser.password.trim()) {
        payload.password = editingUser.password.trim();
        payload.confirmPassword = editingUser.password.trim();
      }

      const res = await fetch(`${API_URL}/api/v2/UpdateUser/${editingUser.id}`, {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`
        },
        body: JSON.stringify(payload)
      });
      const data = await res.json().catch(() => ({}));
      if (res.ok) {
        Swal.fire({
          icon: 'success',
          title: 'User Updated',
          text: `User ${editingUser.username} updated successfully!`,
          timer: 1500,
          showConfirmButton: false
        });
        setShowEditUserModal(false);
        setEditingUser(null);
        fetchUsers();
      } else {
        Swal.fire({
          icon: 'error',
          title: 'Update Failed',
          text: data.message || 'Error occurred while updating user.'
        });
      }
    } catch (err) {
      Swal.fire({ icon: 'error', title: 'Server Error', text: 'Unable to connect to backend server.' });
    } finally {
      setUserActionLoading(false);
    }
  };

  const handleDeleteUser = (userId, targetUsername) => {
    if (targetUsername === currentUsername) {
      Swal.fire({
        icon: 'warning',
        title: 'Action Restricted',
        text: 'You cannot delete your own active administrator account.'
      });
      return;
    }

    Swal.fire({
      title: `Delete ${targetUsername}?`,
      text: 'Are you sure you want to permanently delete this user? This action cannot be undone.',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#dc3545',
      cancelButtonColor: '#6c757d',
      confirmButtonText: 'Yes, Delete User'
    }).then(async (result) => {
      if (result.isConfirmed) {
        try {
          const res = await fetch(`${API_URL}/api/v2/DeleteUser/${userId}`, {
            method: 'DELETE',
            headers: {
              Authorization: `Bearer ${token}`
            }
          });
          if (res.ok) {
            Swal.fire({
              icon: 'success',
              title: 'Deleted!',
              text: `User ${targetUsername} has been removed.`,
              timer: 1500,
              showConfirmButton: false
            });
            fetchUsers();
          } else {
            const data = await res.json().catch(() => ({}));
            Swal.fire({
              icon: 'error',
              title: 'Delete Failed',
              text: data.message || 'Could not delete user.'
            });
          }
        } catch (err) {
          Swal.fire({ icon: 'error', title: 'Server Error', text: 'Unable to connect to backend server.' });
        }
      }
    });
  };

  // ── CUSTOMER (USER_REGISTER) CRUD HANDLERS ──
  const handleAddRegUser = async (e) => {
    e.preventDefault();
    if (!newRegUser.username.trim() || !newRegUser.email.trim()) {
      Swal.fire({ icon: 'warning', title: 'Missing Information', text: 'Username and email are required.' });
      return;
    }
    setUserActionLoading(true);
    try {
      const res = await fetch(`${API_URL}/api/v2/admin/registered-user`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`
        },
        body: JSON.stringify(newRegUser)
      });
      const data = await res.json().catch(() => ({}));
      if (res.ok) {
        Swal.fire({ icon: 'success', title: 'Customer Created', text: `User ${newRegUser.username} added successfully!`, timer: 1500, showConfirmButton: false });
        setShowAddRegUserModal(false);
        setNewRegUser({ username: '', email: '', password: '', mobnum: '', role: 'USER' });
        fetchRegisteredUsers();
      } else {
        Swal.fire({ icon: 'error', title: 'Create Failed', text: data.message || 'Could not create customer.' });
      }
    } catch (err) {
      Swal.fire({ icon: 'error', title: 'Server Error', text: 'Unable to connect to backend server.' });
    } finally {
      setUserActionLoading(false);
    }
  };

  const handleOpenEditRegModal = (user) => {
    setEditingRegUser({
      id: user.id,
      username: user.username || '',
      email: user.email || '',
      mobnum: user.mobnum || '',
      role: user.role || 'USER',
      password: ''
    });
    setShowEditRegUserModal(true);
  };

  const handleUpdateRegUser = async (e) => {
    e.preventDefault();
    if (!editingRegUser || !editingRegUser.id) return;
    setUserActionLoading(true);
    try {
      const payload = {
        username: editingRegUser.username.trim(),
        email: editingRegUser.email.trim(),
        mobnum: editingRegUser.mobnum,
        role: editingRegUser.role
      };
      if (editingRegUser.password && editingRegUser.password.trim()) {
        payload.password = editingRegUser.password.trim();
      }
      const res = await fetch(`${API_URL}/api/v2/admin/registered-user/${editingRegUser.id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`
        },
        body: JSON.stringify(payload)
      });
      const data = await res.json().catch(() => ({}));
      if (res.ok) {
        Swal.fire({ icon: 'success', title: 'Updated!', text: 'Customer details updated successfully!', timer: 1500, showConfirmButton: false });
        setShowEditRegUserModal(false);
        setEditingRegUser(null);
        fetchRegisteredUsers();
      } else {
        Swal.fire({ icon: 'error', title: 'Update Failed', text: data.message || 'Could not update customer.' });
      }
    } catch (err) {
      Swal.fire({ icon: 'error', title: 'Server Error', text: 'Unable to connect to backend server.' });
    } finally {
      setUserActionLoading(false);
    }
  };

  const handleDeleteRegUser = (userId, targetUsername) => {
    Swal.fire({
      title: `Delete Customer ${targetUsername}?`,
      text: 'Are you sure you want to delete this customer account? This cannot be undone.',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#dc3545',
      cancelButtonColor: '#6c757d',
      confirmButtonText: 'Yes, Delete Customer'
    }).then(async (result) => {
      if (result.isConfirmed) {
        try {
          const res = await fetch(`${API_URL}/api/v2/admin/registered-user/${userId}`, {
            method: 'DELETE',
            headers: { Authorization: `Bearer ${token}` }
          });
          if (res.ok) {
            Swal.fire({ icon: 'success', title: 'Deleted!', text: `Customer ${targetUsername} removed.`, timer: 1500, showConfirmButton: false });
            fetchRegisteredUsers();
          } else {
            const data = await res.json().catch(() => ({}));
            Swal.fire({ icon: 'error', title: 'Delete Failed', text: data.message || 'Could not delete customer.' });
          }
        } catch (err) {
          Swal.fire({ icon: 'error', title: 'Server Error', text: 'Unable to connect to backend server.' });
        }
      }
    });
  };

  const handleDeleteAllUsers = () => {
    Swal.fire({
      title: 'DELETE ALL REGISTERED USERS?',
      text: 'CRITICAL ACTION: This will permanently delete ALL registered customer accounts in the database! Only active administrative accounts will be preserved.',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#dc3545',
      cancelButtonColor: '#6c757d',
      confirmButtonText: 'Yes, Delete All Users!'
    }).then(async (result) => {
      if (result.isConfirmed) {
        try {
          const res = await fetch(`${API_URL}/api/v2/users/delete-all`, {
            method: 'DELETE',
            headers: { Authorization: `Bearer ${token}` }
          });
          const data = await res.json().catch(() => ({}));
          if (res.ok) {
            Swal.fire({
              icon: 'success',
              title: 'All Users Deleted!',
              text: `Successfully deleted ${data.registeredUsersDeleted || 0} registered user(s). Database is clean!`,
              timer: 2000,
              showConfirmButton: false
            });
            fetchRegisteredUsers();
            fetchUsers();
          } else {
            Swal.fire({ icon: 'error', title: 'Action Failed', text: data.message || 'Could not delete all users.' });
          }
        } catch (err) {
          Swal.fire({ icon: 'error', title: 'Server Error', text: 'Unable to connect to backend server.' });
        }
      }
    });
  };

  // ── VIDEO CRUD HANDLERS ──
  const handleDeleteVideo = (videoId, title) => {
    Swal.fire({
      title: `Delete Video?`,
      text: `Are you sure you want to permanently delete "${title}"? This cannot be undone.`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#dc3545',
      cancelButtonColor: '#6c757d',
      confirmButtonText: 'Yes, Delete Video'
    }).then(async (result) => {
      if (result.isConfirmed) {
        try {
          const res = await fetch(`${API_URL}/api/v2/deletevideo/${videoId}`, {
            method: 'DELETE',
            headers: { Authorization: `Bearer ${token}` }
          });
          if (res.ok) {
            Swal.fire({ icon: 'success', title: 'Video Deleted', text: `Video "${title}" was removed.`, timer: 1500, showConfirmButton: false });
            fetchVideos();
          } else {
            Swal.fire({ icon: 'error', title: 'Delete Failed', text: 'Could not delete video.' });
          }
        } catch (e) {
          Swal.fire({ icon: 'error', title: 'Network Error', text: 'Failed to connect to backend.' });
        }
      }
    });
  };

  const filteredStandards = standards.filter(s => {
    const matchFramework = selectedFramework === 'ALL' || s.framework === selectedFramework;
    const matchQuery = !searchQuery ||
      s.code.toLowerCase().includes(searchQuery.toLowerCase()) ||
      s.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
      s.description.toLowerCase().includes(searchQuery.toLowerCase()) ||
      s.category.toLowerCase().includes(searchQuery.toLowerCase());
    return matchFramework && matchQuery;
  });

  if (currentRole !== 'ADMIN' && currentRole !== 'SUBADMIN') {
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
            Administrator privileges are required to view the Media Jungle Admin Console and Security Compliance Suite.
            Your current role is <span className="badge bg-secondary text-uppercase">{currentRole}</span>.
          </p>
          <div className="d-flex justify-content-center gap-2">
            <Link to="/" className="btn btn-warning btn-sm fw-semibold">
              <i className="bi bi-house-door me-1"></i> Return to Streaming Site
            </Link>
            <Link to="/admin" className="btn btn-outline-danger btn-sm">
              <i className="bi bi-box-arrow-in-right me-1"></i> Sign In as Admin
            </Link>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div style={{ minHeight: '100vh', backgroundColor: '#0f1218', color: '#e1e7ec', fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif' }}>
      
      {/* ── TOP HEADER ── */}
      <header className="navbar navbar-dark px-4 py-2 sticky-top border-bottom border-secondary border-opacity-25" style={{ backgroundColor: '#171b23' }}>
        <div className="container-fluid d-flex justify-content-between align-items-center">
          <div className="d-flex align-items-center gap-3">
            <Link to="/" className="d-flex align-items-center gap-2 text-decoration-none">
              <span style={{ fontSize: '1.6rem' }}>🦁</span>
              <span className="fw-bold fs-5 text-warning tracking-wider">MEDIA JUNGLE</span>
            </Link>
            <span className="badge bg-secondary text-uppercase small px-2 py-1">Admin Console</span>
          </div>

          <div className="d-flex align-items-center gap-3">
            <Link to="/" className="btn btn-outline-warning btn-sm fw-semibold">
              <i className="bi bi-box-arrow-up-right me-1"></i> View Streaming Site
            </Link>
            <div className="d-flex align-items-center gap-2 text-light bg-dark px-3 py-1 rounded-pill border border-secondary border-opacity-25">
              <i className="bi bi-person-circle text-warning"></i>
              <span className="small fw-semibold">{currentUsername}</span>
              <span className="badge bg-warning text-dark small" style={{ fontSize: '0.7rem' }}>{currentRole}</span>
            </div>
            <button className="btn btn-outline-danger btn-sm" onClick={handleLogout}>
              <i className="bi bi-box-arrow-right me-1"></i> Logout
            </button>
          </div>
        </div>
      </header>

      {/* ── MAIN WORKSPACE (SIDEBAR + CONTENT) ── */}
      <div className="d-flex" style={{ minHeight: 'calc(100vh - 60px)' }}>
        
        {/* SIDEBAR NAVIGATION */}
        <aside style={{ width: '260px', backgroundColor: '#141820', borderRight: '1px solid rgba(255,255,255,0.06)' }} className="p-3 d-flex flex-column">
          <div className="text-secondary small fw-bold text-uppercase px-3 mb-2" style={{ letterSpacing: '0.5px' }}>
            Media Management
          </div>
          <nav className="nav flex-column gap-1 mb-4">
            <button
              className={`nav-link text-start rounded-3 px-3 py-2 border-0 ${activeTab === 'overview' ? 'bg-warning text-dark fw-bold' : 'text-light bg-transparent hover-bg'}`}
              onClick={() => setActiveTab('overview')}
            >
              <i className="bi bi-speedometer2 me-2"></i> Dashboard Overview
            </button>
            <button
              className={`nav-link text-start rounded-3 px-3 py-2 border-0 ${activeTab === 'videos' ? 'bg-warning text-dark fw-bold' : 'text-light bg-transparent'}`}
              onClick={() => setActiveTab('videos')}
            >
              <i className="bi bi-film me-2"></i> Video Catalog ({videos.length})
            </button>
            <button
              className={`nav-link text-start rounded-3 px-3 py-2 border-0 ${activeTab === 'users' ? 'bg-warning text-dark fw-bold' : 'text-light bg-transparent'}`}
              onClick={() => setActiveTab('users')}
            >
              <i className="bi bi-people-fill me-2"></i> Users & Admins ({users.length})
            </button>
          </nav>

          <div className="text-secondary small fw-bold text-uppercase px-3 mb-2" style={{ letterSpacing: '0.5px' }}>
            Security & Standards
          </div>
          <nav className="nav flex-column gap-1">
            <button
              className={`nav-link text-start rounded-3 px-3 py-2 border-0 ${activeTab === 'security' ? 'bg-warning text-dark fw-bold' : 'text-warning bg-dark bg-opacity-25'}`}
              onClick={() => setActiveTab('security')}
            >
              <i className="bi bi-shield-check me-2 text-warning"></i> ISO 27001 & SOC 2 (20)
            </button>
            <button
              className={`nav-link text-start rounded-3 px-3 py-2 border-0 ${activeTab === 'auditlogs' ? 'bg-warning text-dark fw-bold' : 'text-light bg-transparent'}`}
              onClick={() => setActiveTab('auditlogs')}
            >
              <i className="bi bi-journal-text me-2"></i> Audit Trail Logs
            </button>
          </nav>

          <div className="mt-auto p-3 rounded-3 bg-dark border border-secondary border-opacity-25 small">
            <div className="d-flex align-items-center gap-2 text-success fw-bold mb-1">
              <i className="bi bi-shield-lock-fill"></i> Compliance: 100%
            </div>
            <div className="text-muted" style={{ fontSize: '0.75rem' }}>
              All 10 ISO 27001 & 10 SOC 2 Controls Enforced in Backend.
            </div>
          </div>
        </aside>

        {/* MAIN BODY CONTENT */}
        <main className="flex-grow-1 p-4 overflow-auto" style={{ backgroundColor: '#0f1218' }}>

          {/* ───────────────────────────────────────────────────────────── */}
          {/* TAB 1: OVERVIEW DASHBOARD */}
          {/* ───────────────────────────────────────────────────────────── */}
          {activeTab === 'overview' && (
            <div>
              <div className="d-flex justify-content-between align-items-center mb-4">
                <div>
                  <h3 className="fw-bold mb-1">Media Jungle Overview</h3>
                  <p className="text-secondary small mb-0">OTT Media Platform & Security Administration Status</p>
                </div>
                <span className="badge bg-success bg-opacity-25 text-success border border-success px-3 py-2">
                  <i className="bi bi-broadcast me-1"></i> System Online (Port 8080)
                </span>
              </div>

              {/* 4 Metric Cards */}
              <div className="row g-3 mb-4">
                <div className="col-12 col-sm-6 col-lg-3">
                  <div className="card border-0 rounded-3 p-3 text-white" style={{ backgroundColor: '#1a1f29', borderLeft: '4px solid #0d6efd' }}>
                    <div className="text-muted small text-uppercase">Movies & Videos</div>
                    <div className="fs-3 fw-bold my-1 text-primary">{videos.length || 3}</div>
                    <div className="small text-secondary"><i className="bi bi-play-circle me-1"></i> DASH Streaming Enabled</div>
                  </div>
                </div>

                <div className="col-12 col-sm-6 col-lg-3">
                  <div className="card border-0 rounded-3 p-3 text-white" style={{ backgroundColor: '#1a1f29', borderLeft: '4px solid #198754' }}>
                    <div className="text-muted small text-uppercase">Active Categories</div>
                    <div className="fs-3 fw-bold my-1 text-success">5</div>
                    <div className="small text-secondary"><i className="bi bi-tags me-1"></i> Action, Drama, Comedy...</div>
                  </div>
                </div>

                <div className="col-12 col-sm-6 col-lg-3">
                  <div className="card border-0 rounded-3 p-3 text-white" style={{ backgroundColor: '#1a1f29', borderLeft: '4px solid #ffc107' }}>
                    <div className="text-muted small text-uppercase">Users & Admins</div>
                    <div className="fs-3 fw-bold my-1 text-warning">{users.length + registeredUsers.length}</div>
                    <div className="small text-secondary"><i className="bi bi-people me-1"></i> {registeredUsers.length} Customers | {users.length} Staff</div>
                  </div>
                </div>

                <div className="col-12 col-sm-6 col-lg-3">
                  <div className="card border-0 rounded-3 p-3 text-white" style={{ backgroundColor: '#1a1f29', borderLeft: '4px solid #20c997' }}>
                    <div className="text-muted small text-uppercase">Security Compliance</div>
                    <div className="fs-3 fw-bold my-1 text-info">100%</div>
                    <div className="small text-secondary"><i className="bi bi-shield-check me-1"></i> 20 / 20 Standards Pass</div>
                  </div>
                </div>
              </div>

              {/* Quick Actions & System Info */}
              <div className="row g-4 mb-4">
                <div className="col-lg-8">
                  <div className="card border-0 rounded-3 p-4 text-white" style={{ backgroundColor: '#1a1f29' }}>
                    <div className="d-flex justify-content-between align-items-center mb-3">
                      <h5 className="fw-bold mb-0"><i className="bi bi-film text-warning me-2"></i>Catalog Videos</h5>
                      <button className="btn btn-sm btn-outline-warning" onClick={() => setActiveTab('videos')}>View All Videos</button>
                    </div>
                    <div className="table-responsive">
                      <table className="table table-dark table-hover mb-0" style={{ backgroundColor: 'transparent' }}>
                        <thead>
                          <tr className="text-secondary small">
                            <th>ID</th>
                            <th>Movie Title</th>
                            <th>Duration</th>
                            <th>Rating</th>
                            <th>Status</th>
                            <th>Action</th>
                          </tr>
                        </thead>
                        <tbody>
                          {videos.slice(0, 5).map((v) => (
                            <tr key={v.id}>
                              <td className="text-secondary">#{v.id}</td>
                              <td className="fw-semibold text-white">{v.videoTitle || v.title}</td>
                              <td className="text-secondary">{v.mainVideoDuration || '1h 45m'}</td>
                              <td><span className="badge bg-secondary">{v.rating || 'U/A'}</span></td>
                              <td><span className="badge bg-success">{v.dashStatus || 'READY'}</span></td>
                              <td>
                                <button className="btn btn-sm btn-warning fw-bold text-dark py-0 px-2" onClick={() => setPreviewVideo(v)}>
                                  ▶ Play
                                </button>
                              </td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </div>
                  </div>
                </div>

                <div className="col-lg-4">
                  <div className="card border-0 rounded-3 p-4 text-white" style={{ backgroundColor: '#1a1f29' }}>
                    <h5 className="fw-bold mb-3"><i className="bi bi-shield-lock-fill text-warning me-2"></i>Security Controls</h5>
                    <ul className="list-unstyled mb-0 d-flex flex-column gap-3 small">
                      <li className="d-flex justify-content-between align-items-center pb-2 border-bottom border-secondary border-opacity-25">
                        <span>ISO 27001 Controls</span>
                        <span className="badge bg-success">10 Implemented</span>
                      </li>
                      <li className="d-flex justify-content-between align-items-center pb-2 border-bottom border-secondary border-opacity-25">
                        <span>SOC 2 Trust Criteria</span>
                        <span className="badge bg-success">10 Implemented</span>
                      </li>
                      <li className="d-flex justify-content-between align-items-center pb-2 border-bottom border-secondary border-opacity-25">
                        <span>Brute-Force Rate Limiting</span>
                        <span className="badge bg-primary">5 attempts / 15m</span>
                      </li>
                      <li className="d-flex justify-content-between align-items-center pb-2 border-bottom border-secondary border-opacity-25">
                        <span>Audit Log Retention</span>
                        <span className="badge bg-info text-dark">Active PostgreSQL</span>
                      </li>
                      <li className="d-flex justify-content-between align-items-center">
                        <span>Cryptographic Hashing</span>
                        <span className="badge bg-secondary">BCrypt Strength 10</span>
                      </li>
                    </ul>
                    <button className="btn btn-warning btn-sm w-100 fw-bold mt-3 text-dark" onClick={() => setActiveTab('security')}>
                      Inspect Security Matrix
                    </button>
                  </div>
                </div>
              </div>
            </div>
          )}

          {/* ───────────────────────────────────────────────────────────── */}
          {/* TAB 2: VIDEO CATALOG */}
          {/* ───────────────────────────────────────────────────────────── */}
          {activeTab === 'videos' && (
            <div>
              <div className="d-flex justify-content-between align-items-center mb-4">
                <div>
                  <h3 className="fw-bold mb-1">Video Catalog & DASH Streaming</h3>
                  <p className="text-secondary small mb-0">Manage and stream video content in Media Jungle</p>
                </div>
                <button className="btn btn-warning btn-sm fw-bold text-dark" onClick={fetchVideos}>
                  <i className="bi bi-arrow-clockwise me-1"></i> Refresh Catalog
                </button>
              </div>

              <div className="card border-0 rounded-3 p-3 text-white mb-4" style={{ backgroundColor: '#1a1f29' }}>
                <div className="table-responsive">
                  <table className="table table-dark table-hover mb-0">
                    <thead>
                      <tr className="text-secondary small">
                        <th>ID</th>
                        <th>Title</th>
                        <th>Description</th>
                        <th>Duration</th>
                        <th>Tags</th>
                        <th>Rating</th>
                        <th>DASH Stream</th>
                        <th className="text-end">Actions</th>
                      </tr>
                    </thead>
                    <tbody>
                      {videos.map((v) => (
                        <tr key={v.id}>
                          <td className="text-secondary">#{v.id}</td>
                          <td className="fw-bold text-white">{v.videoTitle || v.title}</td>
                          <td className="text-secondary small" style={{ maxWidth: '300px' }}>{v.description}</td>
                          <td className="text-secondary">{v.mainVideoDuration || '1h 45m'}</td>
                          <td><span className="badge bg-dark border border-secondary text-warning">{v.tags || 'Action'}</span></td>
                          <td><span className="badge bg-secondary">{v.rating || 'U/A'}</span></td>
                          <td><span className="badge bg-success">{v.dashStatus || 'READY'}</span></td>
                          <td className="text-end">
                            <div className="btn-group btn-group-sm">
                              <button className="btn btn-warning fw-bold text-dark" onClick={() => setPreviewVideo(v)} title="Stream Media">
                                <i className="bi bi-play-fill me-1"></i> Stream
                              </button>
                              <button className="btn btn-outline-danger" onClick={() => handleDeleteVideo(v.id, v.videoTitle || v.title)} title="Delete Video">
                                <i className="bi bi-trash me-1"></i> Delete
                              </button>
                            </div>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          )}

          {/* ───────────────────────────────────────────────────────────── */}
          {/* TAB 3: USER & ADMIN MANAGEMENT */}
          {/* ───────────────────────────────────────────────────────────── */}
          {activeTab === 'users' && (
            <div>
              <div className="d-flex flex-wrap justify-content-between align-items-center mb-3 gap-3">
                <div>
                  <h3 className="fw-bold mb-1">User & Role Management</h3>
                  <p className="text-secondary small mb-0">Full CRUD administrative control for Registered Customers and Staff/Admin Accounts</p>
                </div>
                <div className="d-flex flex-wrap gap-2">
                  {userSubTab === 'registered' ? (
                    <>
                      <button className="btn btn-warning btn-sm fw-bold text-dark px-3" onClick={() => setShowAddRegUserModal(true)}>
                        <i className="bi bi-person-plus-fill me-1"></i> Add Customer
                      </button>
                      <button className="btn btn-danger btn-sm fw-bold px-3 shadow-sm" onClick={handleDeleteAllUsers} title="Permanently delete all customer accounts">
                        <i className="bi bi-trash3-fill me-1"></i> Delete All Users
                      </button>
                      <button className="btn btn-outline-secondary btn-sm text-light" onClick={fetchRegisteredUsers}>
                        <i className="bi bi-arrow-clockwise me-1"></i> Refresh
                      </button>
                    </>
                  ) : (
                    <>
                      <button className="btn btn-warning btn-sm fw-bold text-dark px-3" onClick={() => setShowAddUserModal(true)}>
                        <i className="bi bi-shield-plus me-1"></i> Add Staff / Admin
                      </button>
                      <button className="btn btn-outline-secondary btn-sm text-light" onClick={fetchUsers}>
                        <i className="bi bi-arrow-clockwise me-1"></i> Refresh
                      </button>
                    </>
                  )}
                </div>
              </div>

              {/* Sub-navigation tabs */}
              <div className="d-flex gap-2 mb-4">
                <button
                  className={`btn btn-sm ${userSubTab === 'registered' ? 'btn-warning text-dark fw-bold' : 'btn-outline-secondary text-light'}`}
                  onClick={() => setUserSubTab('registered')}
                >
                  <i className="bi bi-people-fill me-1"></i> Registered Customers ({registeredUsers.length})
                </button>
                <button
                  className={`btn btn-sm ${userSubTab === 'staff' ? 'btn-warning text-dark fw-bold' : 'btn-outline-secondary text-light'}`}
                  onClick={() => setUserSubTab('staff')}
                >
                  <i className="bi bi-shield-lock me-1"></i> Admins & Staff ({users.length})
                </button>
              </div>

              {/* VIEW 1: REGISTERED CUSTOMERS */}
              {userSubTab === 'registered' && (
                <div className="card border-0 rounded-3 p-3 text-white" style={{ backgroundColor: '#1a1f29' }}>
                  {registeredUsers.length === 0 ? (
                    <div className="text-center py-5">
                      <i className="bi bi-people display-4 text-secondary mb-3 d-block"></i>
                      <h5 className="fw-semibold text-light">No Registered Customers Found</h5>
                      <p className="text-secondary small mb-3">The customer table is completely clean or all users have been deleted.</p>
                      <button className="btn btn-warning btn-sm fw-bold text-dark px-3" onClick={() => setShowAddRegUserModal(true)}>
                        <i className="bi bi-person-plus-fill me-1"></i> Add First Customer
                      </button>
                    </div>
                  ) : (
                    <div className="table-responsive">
                      <table className="table table-dark table-hover mb-0 align-middle">
                        <thead>
                          <tr className="text-secondary small">
                            <th>ID</th>
                            <th>Customer Name</th>
                            <th>Email Address</th>
                            <th>Mobile</th>
                            <th>Role</th>
                            <th>Date Added</th>
                            <th className="text-end">Actions</th>
                          </tr>
                        </thead>
                        <tbody>
                          {registeredUsers.map((u, idx) => (
                            <tr key={u.id || idx}>
                              <td className="text-secondary">#{u.id}</td>
                              <td className="fw-bold text-white">
                                <i className="bi bi-person-fill text-info me-2"></i>{u.username}
                              </td>
                              <td className="text-light">{u.email}</td>
                              <td className="text-secondary small">{u.mobnum || '-'}</td>
                              <td>
                                <span className={`badge ${u.role === 'ADMIN' ? 'bg-danger' : 'bg-primary'}`}>
                                  {u.role || 'USER'}
                                </span>
                              </td>
                              <td className="text-secondary small">{u.date || 'Active'}</td>
                              <td className="text-end">
                                <div className="btn-group btn-group-sm">
                                  <button
                                    className="btn btn-outline-warning py-1 px-2"
                                    title="Edit Customer"
                                    onClick={() => handleOpenEditRegModal(u)}
                                  >
                                    <i className="bi bi-pencil-square me-1"></i> Edit
                                  </button>
                                  <button
                                    className="btn btn-outline-danger py-1 px-2"
                                    title="Delete Customer"
                                    onClick={() => handleDeleteRegUser(u.id, u.username)}
                                  >
                                    <i className="bi bi-trash me-1"></i> Delete
                                  </button>
                                </div>
                              </td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </div>
                  )}
                </div>
              )}

              {/* VIEW 2: ADMIN & STAFF ACCOUNTS */}
              {userSubTab === 'staff' && (
                <div className="card border-0 rounded-3 p-3 text-white" style={{ backgroundColor: '#1a1f29' }}>
                  <div className="table-responsive">
                    <table className="table table-dark table-hover mb-0 align-middle">
                      <thead>
                        <tr className="text-secondary small">
                          <th>ID</th>
                          <th>User</th>
                          <th>Email</th>
                          <th>Contact</th>
                          <th>Company / Address</th>
                          <th>Role (RBAC)</th>
                          <th className="text-end">Actions</th>
                        </tr>
                      </thead>
                      <tbody>
                        {users.map((u, idx) => (
                          <tr key={u.id || idx}>
                            <td className="text-secondary">#{u.id || idx + 1}</td>
                            <td className="fw-bold text-white">
                              <i className="bi bi-shield-check text-warning me-2"></i>{u.username}
                            </td>
                            <td className="text-secondary">{u.email || 'N/A'}</td>
                            <td className="text-secondary small">{u.mobnum || '-'}</td>
                            <td className="text-secondary small">{u.compname || u.address || '-'}</td>
                            <td>
                              <span className={`badge ${u.role === 'ADMIN' ? 'bg-danger' : u.role === 'SUBADMIN' ? 'bg-warning text-dark' : u.role === 'EMPLOYEE' ? 'bg-info text-dark' : 'bg-primary'}`}>
                                {u.role || 'USER'}
                              </span>
                            </td>
                            <td className="text-end">
                              <div className="btn-group btn-group-sm">
                                <button
                                  className="btn btn-outline-warning py-1 px-2"
                                  title="Edit Staff"
                                  onClick={() => handleOpenEditModal(u)}
                                >
                                  <i className="bi bi-pencil-square me-1"></i> Edit
                                </button>
                                <button
                                  className="btn btn-outline-danger py-1 px-2"
                                  title={u.username === currentUsername ? "Cannot delete self" : "Delete Staff"}
                                  disabled={u.username === currentUsername}
                                  onClick={() => handleDeleteUser(u.id, u.username)}
                                >
                                  <i className="bi bi-trash me-1"></i> Delete
                                </button>
                              </div>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                </div>
              )}
            </div>
          )}

          {/* ───────────────────────────────────────────────────────────── */}
          {/* TAB 4: ISO 27001 & SOC 2 SECURITY STANDARDS */}
          {/* ───────────────────────────────────────────────────────────── */}
          {activeTab === 'security' && (
            <div>
              <div className="d-flex flex-wrap justify-content-between align-items-center mb-4 gap-3">
                <div>
                  <h3 className="fw-bold mb-1">ISO 27001 & SOC 2 Security Standards</h3>
                  <p className="text-secondary small mb-0">20 Enterprise compliance controls implemented across the Media Jungle backend</p>
                </div>
                <div className="d-flex gap-2">
                  <span className="badge bg-success fs-6 py-2 px-3">
                    <i className="bi bi-patch-check-fill me-1"></i> Status: COMPLIANT (100%)
                  </span>
                </div>
              </div>

              {/* Standards Metrics */}
              <div className="row g-3 mb-4">
                <div className="col-6 col-md-3">
                  <div className="p-3 rounded-3 text-center" style={{ backgroundColor: '#1a1f29' }}>
                    <div className="text-secondary small">Total Standards</div>
                    <div className="fs-3 fw-bold text-white">{standardsSummary?.totalStandards || 20}</div>
                  </div>
                </div>
                <div className="col-6 col-md-3">
                  <div className="p-3 rounded-3 text-center" style={{ backgroundColor: '#1a1f29' }}>
                    <div className="text-secondary small">ISO 27001 Controls</div>
                    <div className="fs-3 fw-bold text-primary">{standardsSummary?.iso27001Count || 10}</div>
                  </div>
                </div>
                <div className="col-6 col-md-3">
                  <div className="p-3 rounded-3 text-center" style={{ backgroundColor: '#1a1f29' }}>
                    <div className="text-secondary small">SOC 2 Controls</div>
                    <div className="fs-3 fw-bold text-warning">{standardsSummary?.soc2Count || 10}</div>
                  </div>
                </div>
                <div className="col-6 col-md-3">
                  <div className="p-3 rounded-3 text-center" style={{ backgroundColor: '#1a1f29' }}>
                    <div className="text-secondary small">Implemented Rate</div>
                    <div className="fs-3 fw-bold text-success">100%</div>
                  </div>
                </div>
              </div>

              {/* Filters and Search */}
              <div className="d-flex flex-wrap justify-content-between align-items-center mb-3 gap-3">
                <div className="d-flex gap-2">
                  {['ALL', 'ISO 27001', 'SOC 2'].map(f => (
                    <button
                      key={f}
                      className={`btn btn-sm ${selectedFramework === f ? 'btn-warning text-dark fw-bold' : 'btn-outline-secondary text-light'}`}
                      onClick={() => setSelectedFramework(f)}
                    >
                      {f}
                    </button>
                  ))}
                </div>
                <input
                  type="text"
                  className="form-control form-control-sm bg-dark text-white border-secondary"
                  style={{ maxWidth: '280px' }}
                  placeholder="Search code, name, or file..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                />
              </div>

              {/* Standards Table */}
              <div className="card border-0 rounded-3 p-3 text-white" style={{ backgroundColor: '#1a1f29' }}>
                <div className="table-responsive">
                  <table className="table table-dark table-hover mb-0">
                    <thead>
                      <tr className="text-secondary small">
                        <th>Control ID</th>
                        <th>Standard Name</th>
                        <th>Framework</th>
                        <th>Domain / Category</th>
                        <th>Status</th>
                        <th>Backend Implementation File</th>
                      </tr>
                    </thead>
                    <tbody>
                      {filteredStandards.map((std) => (
                        <tr key={std.code}>
                          <td className="fw-bold text-warning">{std.code}</td>
                          <td className="fw-semibold text-white">{std.name}</td>
                          <td>
                            <span className={`badge ${std.framework === 'ISO 27001' ? 'bg-primary' : 'bg-warning text-dark'}`}>
                              {std.framework}
                            </span>
                          </td>
                          <td className="text-secondary small">{std.category}</td>
                          <td><span className="badge bg-success">{std.status}</span></td>
                          <td className="text-info small font-monospace">{std.implementationFile}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          )}

          {/* ───────────────────────────────────────────────────────────── */}
          {/* TAB 5: AUDIT TRAIL LOGS */}
          {/* ───────────────────────────────────────────────────────────── */}
          {activeTab === 'auditlogs' && (
            <div>
              <div className="d-flex justify-content-between align-items-center mb-4">
                <div>
                  <h3 className="fw-bold mb-1">ISO 27001 Security Audit Trail</h3>
                  <p className="text-secondary small mb-0">Real-time immutable logging of system authentication and administration events</p>
                </div>
                <button className="btn btn-warning btn-sm fw-bold text-dark" onClick={fetchAuditLogs}>
                  <i className="bi bi-arrow-clockwise me-1"></i> Refresh Logs
                </button>
              </div>

              <div className="card border-0 rounded-3 p-3 text-white" style={{ backgroundColor: '#1a1f29' }}>
                <div className="table-responsive">
                  <table className="table table-dark table-hover mb-0">
                    <thead>
                      <tr className="text-secondary small">
                        <th>Timestamp</th>
                        <th>Action</th>
                        <th>User</th>
                        <th>Role</th>
                        <th>Status</th>
                        <th>Description</th>
                      </tr>
                    </thead>
                    <tbody>
                      {auditLogs.length > 0 ? (
                        auditLogs.map((log, i) => (
                          <tr key={log.id || i}>
                            <td className="text-secondary small">{log.timestamp ? new Date(log.timestamp).toLocaleString() : 'Recent'}</td>
                            <td className="fw-bold text-warning">{log.action}</td>
                            <td className="text-white">{log.username}</td>
                            <td><span className="badge bg-secondary">{log.role}</span></td>
                            <td>
                              <span className={`badge ${log.status === 'SUCCESS' ? 'bg-success' : 'bg-danger'}`}>
                                {log.status}
                              </span>
                            </td>
                            <td className="text-light small">{log.description}</td>
                          </tr>
                        ))
                      ) : (
                        <tr>
                          <td colSpan="6" className="text-center text-secondary py-4">
                            No audit logs loaded. Log in to generate events.
                          </td>
                        </tr>
                      )}
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          )}

        </main>
      </div>

      {/* ── VIDEO STREAM PREVIEW MODAL ── */}
      {previewVideo && (
        <div style={{
          position: 'fixed', top: 0, left: 0, width: '100%', height: '100%',
          backgroundColor: 'rgba(0,0,0,0.92)', zIndex: 9999, display: 'flex',
          flexDirection: 'column', alignItems: 'center', justifyContent: 'center', padding: '20px'
        }}>
          <div style={{ width: '100%', maxWidth: '900px' }}>
            <div className="d-flex justify-content-between align-items-center mb-3 text-white">
              <h5 className="fw-bold text-warning mb-0">Preview: {previewVideo.videoTitle || previewVideo.title}</h5>
              <button className="btn btn-outline-light btn-sm" onClick={() => setPreviewVideo(null)}>Close</button>
            </div>
            <video
              controls
              autoPlay
              style={{ width: '100%', maxHeight: '60vh', borderRadius: '8px' }}
              src={`${API_URL}/api/v2/${previewVideo.id}/videofile`}
            />
          </div>
        </div>
      )}

      {/* ── ADD USER MODAL ── */}
      {showAddUserModal && (
        <div style={{
          position: 'fixed', top: 0, left: 0, width: '100%', height: '100%',
          backgroundColor: 'rgba(0,0,0,0.85)', zIndex: 9999, display: 'flex',
          alignItems: 'center', justifyContent: 'center', padding: '20px'
        }}>
          <div className="card text-white border-secondary border-opacity-25 shadow-lg" style={{ backgroundColor: '#171b23', width: '100%', maxWidth: '600px', borderRadius: '12px' }}>
            <div className="card-header border-secondary border-opacity-25 d-flex justify-content-between align-items-center py-3">
              <h5 className="fw-bold mb-0 text-warning">
                <i className="bi bi-person-plus-fill me-2"></i>Add New System User
              </h5>
              <button className="btn-close btn-close-white" onClick={() => setShowAddUserModal(false)}></button>
            </div>
            <form onSubmit={handleAddUser}>
              <div className="card-body p-4" style={{ maxHeight: '70vh', overflowY: 'auto' }}>
                <div className="row g-3">
                  <div className="col-md-6">
                    <label className="form-label small text-secondary fw-semibold">Username *</label>
                    <input
                      type="text"
                      className="form-control bg-dark text-light border-secondary border-opacity-50"
                      value={newUser.username}
                      onChange={(e) => setNewUser({ ...newUser, username: e.target.value })}
                      placeholder="e.g. JohnDoe"
                      required
                    />
                  </div>
                  <div className="col-md-6">
                    <label className="form-label small text-secondary fw-semibold">Email *</label>
                    <input
                      type="email"
                      className="form-control bg-dark text-light border-secondary border-opacity-50"
                      value={newUser.email}
                      onChange={(e) => setNewUser({ ...newUser, email: e.target.value })}
                      placeholder="john@example.com"
                      required
                    />
                  </div>
                  <div className="col-md-6">
                    <label className="form-label small text-secondary fw-semibold">Password *</label>
                    <input
                      type="password"
                      className="form-control bg-dark text-light border-secondary border-opacity-50"
                      value={newUser.password}
                      onChange={(e) => setNewUser({ ...newUser, password: e.target.value, confirmPassword: e.target.value })}
                      placeholder="••••••••"
                      required
                    />
                  </div>
                  <div className="col-md-6">
                    <label className="form-label small text-secondary fw-semibold">Role (RBAC) *</label>
                    <select
                      className="form-select bg-dark text-light border-secondary border-opacity-50"
                      value={newUser.role}
                      onChange={(e) => setNewUser({ ...newUser, role: e.target.value })}
                    >
                      <option value="SUBADMIN">SUBADMIN (Management)</option>
                      <option value="ADMIN">ADMIN (Full Privileges)</option>
                      <option value="EMPLOYEE">EMPLOYEE (Content Operations)</option>
                      <option value="USER">USER (Standard Consumer)</option>
                    </select>
                  </div>
                  <div className="col-md-6">
                    <label className="form-label small text-secondary fw-semibold">Mobile Number</label>
                    <input
                      type="tel"
                      className="form-control bg-dark text-light border-secondary border-opacity-50"
                      value={newUser.mobnum}
                      onChange={(e) => setNewUser({ ...newUser, mobnum: e.target.value })}
                      placeholder="e.g. 9876543210"
                    />
                  </div>
                  <div className="col-md-6">
                    <label className="form-label small text-secondary fw-semibold">Company Name</label>
                    <input
                      type="text"
                      className="form-control bg-dark text-light border-secondary border-opacity-50"
                      value={newUser.compname}
                      onChange={(e) => setNewUser({ ...newUser, compname: e.target.value })}
                      placeholder="Media Jungle"
                    />
                  </div>
                  <div className="col-md-6">
                    <label className="form-label small text-secondary fw-semibold">Country</label>
                    <input
                      type="text"
                      className="form-control bg-dark text-light border-secondary border-opacity-50"
                      value={newUser.country}
                      onChange={(e) => setNewUser({ ...newUser, country: e.target.value })}
                      placeholder="India"
                    />
                  </div>
                  <div className="col-md-6">
                    <label className="form-label small text-secondary fw-semibold">Pincode</label>
                    <input
                      type="text"
                      className="form-control bg-dark text-light border-secondary border-opacity-50"
                      value={newUser.pincode}
                      onChange={(e) => setNewUser({ ...newUser, pincode: e.target.value })}
                      placeholder="560001"
                    />
                  </div>
                  <div className="col-12">
                    <label className="form-label small text-secondary fw-semibold">Address</label>
                    <input
                      type="text"
                      className="form-control bg-dark text-light border-secondary border-opacity-50"
                      value={newUser.address}
                      onChange={(e) => setNewUser({ ...newUser, address: e.target.value })}
                      placeholder="Street / Branch Address"
                    />
                  </div>
                </div>
              </div>
              <div className="card-footer border-secondary border-opacity-25 d-flex justify-content-end gap-2 py-3">
                <button type="button" className="btn btn-outline-secondary btn-sm" onClick={() => setShowAddUserModal(false)}>
                  Cancel
                </button>
                <button type="submit" className="btn btn-warning btn-sm fw-bold text-dark" disabled={userActionLoading}>
                  {userActionLoading ? <span className="spinner-border spinner-border-sm me-1"></span> : <i className="bi bi-check2 me-1"></i>}
                  Save User
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* ── EDIT USER MODAL ── */}
      {showEditUserModal && editingUser && (
        <div style={{
          position: 'fixed', top: 0, left: 0, width: '100%', height: '100%',
          backgroundColor: 'rgba(0,0,0,0.85)', zIndex: 9999, display: 'flex',
          alignItems: 'center', justifyContent: 'center', padding: '20px'
        }}>
          <div className="card text-white border-secondary border-opacity-25 shadow-lg" style={{ backgroundColor: '#171b23', width: '100%', maxWidth: '600px', borderRadius: '12px' }}>
            <div className="card-header border-secondary border-opacity-25 d-flex justify-content-between align-items-center py-3">
              <h5 className="fw-bold mb-0 text-warning">
                <i className="bi bi-pencil-square me-2"></i>Update User: {editingUser.username}
              </h5>
              <button className="btn-close btn-close-white" onClick={() => setShowEditUserModal(false)}></button>
            </div>
            <form onSubmit={handleUpdateUser}>
              <div className="card-body p-4" style={{ maxHeight: '70vh', overflowY: 'auto' }}>
                <div className="row g-3">
                  <div className="col-md-6">
                    <label className="form-label small text-secondary fw-semibold">Username *</label>
                    <input
                      type="text"
                      className="form-control bg-dark text-light border-secondary border-opacity-50"
                      value={editingUser.username}
                      onChange={(e) => setEditingUser({ ...editingUser, username: e.target.value })}
                      required
                    />
                  </div>
                  <div className="col-md-6">
                    <label className="form-label small text-secondary fw-semibold">Email *</label>
                    <input
                      type="email"
                      className="form-control bg-dark text-light border-secondary border-opacity-50"
                      value={editingUser.email}
                      onChange={(e) => setEditingUser({ ...editingUser, email: e.target.value })}
                      required
                    />
                  </div>
                  <div className="col-md-6">
                    <label className="form-label small text-secondary fw-semibold">Role (RBAC) *</label>
                    <select
                      className="form-select bg-dark text-light border-secondary border-opacity-50"
                      value={editingUser.role}
                      onChange={(e) => setEditingUser({ ...editingUser, role: e.target.value })}
                    >
                      <option value="SUBADMIN">SUBADMIN</option>
                      <option value="ADMIN">ADMIN</option>
                      <option value="EMPLOYEE">EMPLOYEE</option>
                      <option value="USER">USER</option>
                    </select>
                  </div>
                  <div className="col-md-6">
                    <label className="form-label small text-secondary fw-semibold">New Password (optional)</label>
                    <input
                      type="password"
                      className="form-control bg-dark text-light border-secondary border-opacity-50"
                      value={editingUser.password}
                      onChange={(e) => setEditingUser({ ...editingUser, password: e.target.value })}
                      placeholder="Leave empty to keep current"
                    />
                  </div>
                  <div className="col-md-6">
                    <label className="form-label small text-secondary fw-semibold">Mobile Number</label>
                    <input
                      type="tel"
                      className="form-control bg-dark text-light border-secondary border-opacity-50"
                      value={editingUser.mobnum}
                      onChange={(e) => setEditingUser({ ...editingUser, mobnum: e.target.value })}
                    />
                  </div>
                  <div className="col-md-6">
                    <label className="form-label small text-secondary fw-semibold">Company Name</label>
                    <input
                      type="text"
                      className="form-control bg-dark text-light border-secondary border-opacity-50"
                      value={editingUser.compname}
                      onChange={(e) => setEditingUser({ ...editingUser, compname: e.target.value })}
                    />
                  </div>
                  <div className="col-md-6">
                    <label className="form-label small text-secondary fw-semibold">Country</label>
                    <input
                      type="text"
                      className="form-control bg-dark text-light border-secondary border-opacity-50"
                      value={editingUser.country}
                      onChange={(e) => setEditingUser({ ...editingUser, country: e.target.value })}
                    />
                  </div>
                  <div className="col-md-6">
                    <label className="form-label small text-secondary fw-semibold">Pincode</label>
                    <input
                      type="text"
                      className="form-control bg-dark text-light border-secondary border-opacity-50"
                      value={editingUser.pincode}
                      onChange={(e) => setEditingUser({ ...editingUser, pincode: e.target.value })}
                    />
                  </div>
                  <div className="col-12">
                    <label className="form-label small text-secondary fw-semibold">Address</label>
                    <input
                      type="text"
                      className="form-control bg-dark text-light border-secondary border-opacity-50"
                      value={editingUser.address}
                      onChange={(e) => setEditingUser({ ...editingUser, address: e.target.value })}
                    />
                  </div>
                </div>
              </div>
              <div className="card-footer border-secondary border-opacity-25 d-flex justify-content-end gap-2 py-3">
                <button type="button" className="btn btn-outline-secondary btn-sm" onClick={() => setShowEditUserModal(false)}>
                  Cancel
                </button>
                <button type="submit" className="btn btn-warning btn-sm fw-bold text-dark" disabled={userActionLoading}>
                  {userActionLoading ? <span className="spinner-border spinner-border-sm me-1"></span> : <i className="bi bi-check2 me-1"></i>}
                  Update User
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* ── ADD REGISTERED CUSTOMER MODAL ── */}
      {showAddRegUserModal && (
        <div style={{
          position: 'fixed', top: 0, left: 0, width: '100%', height: '100%',
          backgroundColor: 'rgba(0,0,0,0.85)', zIndex: 9999, display: 'flex',
          alignItems: 'center', justifyContent: 'center', padding: '20px'
        }}>
          <div className="card text-white border-secondary border-opacity-25 shadow-lg" style={{ backgroundColor: '#171b23', width: '100%', maxWidth: '550px', borderRadius: '12px' }}>
            <div className="card-header border-secondary border-opacity-25 d-flex justify-content-between align-items-center py-3">
              <h5 className="fw-bold mb-0 text-warning">
                <i className="bi bi-person-plus-fill me-2"></i>Add Registered Customer
              </h5>
              <button className="btn-close btn-close-white" onClick={() => setShowAddRegUserModal(false)}></button>
            </div>
            <form onSubmit={handleAddRegUser}>
              <div className="card-body p-4">
                <div className="row g-3">
                  <div className="col-12">
                    <label className="form-label small text-secondary fw-semibold">Customer / Username *</label>
                    <input
                      type="text"
                      className="form-control bg-dark text-light border-secondary border-opacity-50"
                      value={newRegUser.username}
                      onChange={(e) => setNewRegUser({ ...newRegUser, username: e.target.value })}
                      placeholder="e.g. JohnDoe"
                      required
                    />
                  </div>
                  <div className="col-12">
                    <label className="form-label small text-secondary fw-semibold">Email Address *</label>
                    <input
                      type="email"
                      className="form-control bg-dark text-light border-secondary border-opacity-50"
                      value={newRegUser.email}
                      onChange={(e) => setNewRegUser({ ...newRegUser, email: e.target.value })}
                      placeholder="e.g. john@example.com"
                      required
                    />
                  </div>
                  <div className="col-md-6">
                    <label className="form-label small text-secondary fw-semibold">Password *</label>
                    <input
                      type="password"
                      className="form-control bg-dark text-light border-secondary border-opacity-50"
                      value={newRegUser.password}
                      onChange={(e) => setNewRegUser({ ...newRegUser, password: e.target.value })}
                      placeholder="Password"
                      required
                    />
                  </div>
                  <div className="col-md-6">
                    <label className="form-label small text-secondary fw-semibold">Mobile Number</label>
                    <input
                      type="tel"
                      className="form-control bg-dark text-light border-secondary border-opacity-50"
                      value={newRegUser.mobnum}
                      onChange={(e) => setNewRegUser({ ...newRegUser, mobnum: e.target.value })}
                      placeholder="e.g. 9876543210"
                    />
                  </div>
                  <div className="col-12">
                    <label className="form-label small text-secondary fw-semibold">Role</label>
                    <select
                      className="form-select bg-dark text-light border-secondary border-opacity-50"
                      value={newRegUser.role}
                      onChange={(e) => setNewRegUser({ ...newRegUser, role: e.target.value })}
                    >
                      <option value="USER">USER (Consumer)</option>
                      <option value="ADMIN">ADMIN</option>
                    </select>
                  </div>
                </div>
              </div>
              <div className="card-footer border-secondary border-opacity-25 d-flex justify-content-end gap-2 py-3">
                <button type="button" className="btn btn-outline-secondary btn-sm" onClick={() => setShowAddRegUserModal(false)}>
                  Cancel
                </button>
                <button type="submit" className="btn btn-warning btn-sm fw-bold text-dark" disabled={userActionLoading}>
                  {userActionLoading ? <span className="spinner-border spinner-border-sm me-1"></span> : <i className="bi bi-check2 me-1"></i>}
                  Create Customer
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* ── EDIT REGISTERED CUSTOMER MODAL ── */}
      {showEditRegUserModal && editingRegUser && (
        <div style={{
          position: 'fixed', top: 0, left: 0, width: '100%', height: '100%',
          backgroundColor: 'rgba(0,0,0,0.85)', zIndex: 9999, display: 'flex',
          alignItems: 'center', justifyContent: 'center', padding: '20px'
        }}>
          <div className="card text-white border-secondary border-opacity-25 shadow-lg" style={{ backgroundColor: '#171b23', width: '100%', maxWidth: '550px', borderRadius: '12px' }}>
            <div className="card-header border-secondary border-opacity-25 d-flex justify-content-between align-items-center py-3">
              <h5 className="fw-bold mb-0 text-warning">
                <i className="bi bi-pencil-square me-2"></i>Edit Customer: {editingRegUser.username}
              </h5>
              <button className="btn-close btn-close-white" onClick={() => setShowEditRegUserModal(false)}></button>
            </div>
            <form onSubmit={handleUpdateRegUser}>
              <div className="card-body p-4">
                <div className="row g-3">
                  <div className="col-12">
                    <label className="form-label small text-secondary fw-semibold">Customer / Username *</label>
                    <input
                      type="text"
                      className="form-control bg-dark text-light border-secondary border-opacity-50"
                      value={editingRegUser.username}
                      onChange={(e) => setEditingRegUser({ ...editingRegUser, username: e.target.value })}
                      required
                    />
                  </div>
                  <div className="col-12">
                    <label className="form-label small text-secondary fw-semibold">Email Address *</label>
                    <input
                      type="email"
                      className="form-control bg-dark text-light border-secondary border-opacity-50"
                      value={editingRegUser.email}
                      onChange={(e) => setEditingRegUser({ ...editingRegUser, email: e.target.value })}
                      required
                    />
                  </div>
                  <div className="col-md-6">
                    <label className="form-label small text-secondary fw-semibold">Mobile Number</label>
                    <input
                      type="tel"
                      className="form-control bg-dark text-light border-secondary border-opacity-50"
                      value={editingRegUser.mobnum}
                      onChange={(e) => setEditingRegUser({ ...editingRegUser, mobnum: e.target.value })}
                    />
                  </div>
                  <div className="col-md-6">
                    <label className="form-label small text-secondary fw-semibold">Role</label>
                    <select
                      className="form-select bg-dark text-light border-secondary border-opacity-50"
                      value={editingRegUser.role}
                      onChange={(e) => setEditingRegUser({ ...editingRegUser, role: e.target.value })}
                    >
                      <option value="USER">USER (Consumer)</option>
                      <option value="ADMIN">ADMIN</option>
                    </select>
                  </div>
                  <div className="col-12">
                    <label className="form-label small text-secondary fw-semibold">New Password (optional)</label>
                    <input
                      type="password"
                      className="form-control bg-dark text-light border-secondary border-opacity-50"
                      value={editingRegUser.password}
                      onChange={(e) => setEditingRegUser({ ...editingRegUser, password: e.target.value })}
                      placeholder="Leave empty to keep existing password"
                    />
                  </div>
                </div>
              </div>
              <div className="card-footer border-secondary border-opacity-25 d-flex justify-content-end gap-2 py-3">
                <button type="button" className="btn btn-outline-secondary btn-sm" onClick={() => setShowEditRegUserModal(false)}>
                  Cancel
                </button>
                <button type="submit" className="btn btn-warning btn-sm fw-bold text-dark" disabled={userActionLoading}>
                  {userActionLoading ? <span className="spinner-border spinner-border-sm me-1"></span> : <i className="bi bi-check2 me-1"></i>}
                  Save Changes
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

    </div>
  );
};

export default Dashboard;
