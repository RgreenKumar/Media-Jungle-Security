import React, { useState, useEffect } from 'react';
import API_URL from '../Config';
import { toast } from 'react-hot-toast';

// ISO 27001 | Module 1: Access Management | Tasks 1-10 Admin Security Control Panel
// Description: Admin UI dashboard providing dedicated interfaces for all 10 Access Management ISO 27001 tasks.
const AccessManagementDashboard = () => {
  const [activeTab, setActiveTab] = useState('roles');
  
  // State variables for all 10 tasks
  const [users, setUsers] = useState([]);
  const [selectedUser, setSelectedUser] = useState(null);
  const [newRole, setNewRole] = useState('SECURITY_ADMIN');
  
  const [approvalRequests, setApprovalRequests] = useState([]);
  const [requestRole, setRequestRole] = useState('SECURITY_ADMIN');
  const [requestReason, setRequestReason] = useState('');
  const [requesterEmail, setRequesterEmail] = useState('');

  const [mfaEmail, setMfaEmail] = useState('');
  const [mfaSecretData, setMfaSecretData] = useState(null);
  const [mfaCode, setMfaCode] = useState('');

  const [pamEmail, setPamEmail] = useState('');
  const [pamRole, setPamRole] = useState('SYSTEM_ADMIN');
  const [pamJustification, setPamJustification] = useState('');
  const [pamSessions, setPamSessions] = useState([]);

  const [activeSessions, setActiveSessions] = useState([]);

  const [provUsername, setProvUsername] = useState('');
  const [provEmail, setProvEmail] = useState('');
  const [provPassword, setProvPassword] = useState('');
  const [provMobnum, setProvMobnum] = useState('');
  const [provRole, setProvRole] = useState('USER');

  const [reviewTargetEmail, setReviewTargetEmail] = useState('');
  const [reviewRole, setReviewRole] = useState('USER');
  const [reviewDecision, setReviewDecision] = useState('KEPT');
  const [reviewComments, setReviewComments] = useState('');
  const [accessReviews, setAccessReviews] = useState([]);

  const [loginStats, setLoginStats] = useState({ totalSuccessful: 0, totalFailedPassword: 0, totalFailedMfa: 0, recentActivities: [] });

  const [auditLogs, setAuditLogs] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    fetchUsers();
    fetchApprovalRequests();
    fetchActiveSessions();
    fetchPamSessions();
    fetchAccessReviews();
    fetchLoginDashboard();
    fetchAuditLogs();
  }, []);

  const fetchUsers = async () => {
    try {
      const res = await fetch(`${API_URL}/api/v2/getalluser`);
      if (res.ok) {
        const data = await res.json();
        setUsers(data);
      }
    } catch (e) {
      console.error('Error fetching users:', e);
    }
  };

  // ISO 27001 | Task 1: User Role Management
  const handleUpdateRole = async (userId) => {
    try {
      const res = await fetch(`${API_URL}/api/v2/access-management/users/${userId}/role`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ role: newRole, adminEmail: 'admin@mediajungle.com' })
      });
      if (res.ok) {
        toast.success('User security role updated successfully!');
        fetchUsers();
        fetchAuditLogs();
      } else {
        toast.error('Failed to update user role');
      }
    } catch (e) {
      toast.error('Error updating role: ' + e.message);
    }
  };

  // ISO 27001 | Task 2: Access Approval Workflow
  const fetchApprovalRequests = async () => {
    try {
      const res = await fetch(`${API_URL}/api/v2/access-management/approval-requests`);
      if (res.ok) {
        const data = await res.json();
        setApprovalRequests(data);
      }
    } catch (e) {
      console.error(e);
    }
  };

  const handleCreateApprovalRequest = async (e) => {
    e.preventDefault();
    try {
      const res = await fetch(`${API_URL}/api/v2/access-management/approval-requests/submit`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ requesterEmail, requestedRole: requestRole, reason: requestReason })
      });
      if (res.ok) {
        toast.success('Access approval request submitted successfully!');
        setRequestReason('');
        fetchApprovalRequests();
        fetchAuditLogs();
      }
    } catch (e) {
      toast.error('Error submitting request: ' + e.message);
    }
  };

  const handleReviewRequest = async (id, status) => {
    try {
      const res = await fetch(`${API_URL}/api/v2/access-management/approval-requests/${id}/review`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ status, reviewerEmail: 'admin@mediajungle.com' })
      });
      if (res.ok) {
        toast.success(`Request ${status.toLowerCase()} successfully!`);
        fetchApprovalRequests();
        fetchUsers();
        fetchAuditLogs();
      }
    } catch (e) {
      toast.error('Error reviewing request');
    }
  };

  // ISO 27001 | Task 3: Multi-Factor Authentication
  const handleSetupMfa = async () => {
    if (!mfaEmail) return toast.error('Enter user email for MFA setup');
    try {
      const res = await fetch(`${API_URL}/api/v2/access-management/mfa/setup`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email: mfaEmail })
      });
      if (res.ok) {
        const data = await res.json();
        setMfaSecretData(data);
        toast.success('MFA secret generated!');
      } else {
        toast.error('Failed to generate MFA secret');
      }
    } catch (e) {
      toast.error(e.message);
    }
  };

  const handleVerifyMfa = async () => {
    try {
      const res = await fetch(`${API_URL}/api/v2/access-management/mfa/verify`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email: mfaEmail, code: mfaCode })
      });
      if (res.ok) {
        toast.success('MFA code verified and enabled!');
        setMfaCode('');
        fetchAuditLogs();
      } else {
        toast.error('Invalid MFA verification code');
      }
    } catch (e) {
      toast.error(e.message);
    }
  };

  // ISO 27001 | Task 4: Privileged Access Management
  const fetchPamSessions = async () => {
    try {
      const res = await fetch(`${API_URL}/api/v2/access-management/pam/active-sessions`);
      if (res.ok) {
        const data = await res.json();
        setPamSessions(data);
      }
    } catch (e) { console.error(e); }
  };

  const handleStartPamSession = async (e) => {
    e.preventDefault();
    try {
      const res = await fetch(`${API_URL}/api/v2/access-management/pam/elevate`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ adminEmail: pamEmail, role: pamRole, justification: pamJustification })
      });
      if (res.ok) {
        toast.success('PAM elevated session started!');
        setPamJustification('');
        fetchPamSessions();
        fetchAuditLogs();
      }
    } catch (e) { toast.error(e.message); }
  };

  // ISO 27001 | Task 5: Active Session Monitoring
  const fetchActiveSessions = async () => {
    try {
      const res = await fetch(`${API_URL}/api/v2/access-management/sessions/active`);
      if (res.ok) {
        const data = await res.json();
        setActiveSessions(data);
      }
    } catch (e) { console.error(e); }
  };

  const handleTerminateSession = async (id) => {
    try {
      const res = await fetch(`${API_URL}/api/v2/access-management/sessions/terminate/${id}`, {
        method: 'DELETE'
      });
      if (res.ok) {
        toast.success('Session terminated remotely');
        fetchActiveSessions();
        fetchAuditLogs();
      }
    } catch (e) { toast.error(e.message); }
  };

  // ISO 27001 | Task 6: User Provisioning
  const handleProvisionUser = async (e) => {
    e.preventDefault();
    try {
      const res = await fetch(`${API_URL}/api/v2/access-management/provision`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          username: provUsername,
          email: provEmail,
          password: provPassword,
          mobnum: provMobnum,
          role: provRole,
          adminEmail: 'admin@mediajungle.com'
        })
      });
      if (res.ok) {
        toast.success('User provisioned with security role!');
        setProvUsername(''); setProvEmail(''); setProvPassword(''); setProvMobnum('');
        fetchUsers();
        fetchAuditLogs();
      } else {
        toast.error('User provisioning failed');
      }
    } catch (e) { toast.error(e.message); }
  };

  // ISO 27001 | Task 7: User Deprovisioning
  const handleDeprovisionUser = async (userId) => {
    if (!window.confirm('Are you sure you want to deprovision this user? This will instantly revoke access.')) return;
    try {
      const res = await fetch(`${API_URL}/api/v2/access-management/deprovision/${userId}`, {
        method: 'POST'
      });
      if (res.ok) {
        toast.success('User account deprovisioned and active sessions revoked!');
        fetchUsers();
        fetchActiveSessions();
        fetchAuditLogs();
      }
    } catch (e) { toast.error(e.message); }
  };

  // ISO 27001 | Task 8: Access Reviews
  const fetchAccessReviews = async () => {
    try {
      const res = await fetch(`${API_URL}/api/v2/access-management/access-reviews`);
      if (res.ok) {
        const data = await res.json();
        setAccessReviews(data);
      }
    } catch (e) { console.error(e); }
  };

  const handleSubmitAccessReview = async (e) => {
    e.preventDefault();
    try {
      const res = await fetch(`${API_URL}/api/v2/access-management/access-reviews/submit`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          targetEmail: reviewTargetEmail,
          reviewedRole: reviewRole,
          reviewerEmail: 'auditor@mediajungle.com',
          decision: reviewDecision,
          comments: reviewComments
        })
      });
      if (res.ok) {
        toast.success('Access review submitted!');
        setReviewComments('');
        fetchAccessReviews();
        fetchUsers();
        fetchAuditLogs();
      }
    } catch (e) { toast.error(e.message); }
  };

  // ISO 27001 | Task 9: Login Activity Dashboard
  const fetchLoginDashboard = async () => {
    try {
      const res = await fetch(`${API_URL}/api/v2/access-management/login-activity/dashboard`);
      if (res.ok) {
        const data = await res.json();
        setLoginStats(data);
      }
    } catch (e) { console.error(e); }
  };

  // ISO 27001 | Task 10: Access Audit Module
  const fetchAuditLogs = async () => {
    try {
      const res = await fetch(`${API_URL}/api/v2/access-management/audit-logs`);
      if (res.ok) {
        const data = await res.json();
        setAuditLogs(data);
      }
    } catch (e) { console.error(e); }
  };

  return (
    <div className="container-fluid p-4" style={{ backgroundColor: '#0f172a', color: '#f8fafc', minHeight: '100vh' }}>
      <div className="d-flex justify-content-between align-items-center mb-4 pb-3 border-bottom border-secondary">
        <div>
          <h2 className="text-primary font-weight-bold mb-1">
            <i className="fas fa-shield-alt mr-2"></i>ISO 27001 — Access Management Dashboard
          </h2>
          <p className="text-muted mb-0">Module 1 Implementation: Controls 1 to 10 for Media Jungle OTT Security</p>
        </div>
        <span className="badge badge-success p-2">ISO 27001 Compliant</span>
      </div>

      {/* Tabs Header */}
      <ul className="nav nav-pills mb-4 bg-dark p-2 rounded">
        {[
          { id: 'roles', label: '1. User Role Management', icon: 'fa-user-tag' },
          { id: 'approvals', label: '2. Access Approval Workflow', icon: 'fa-tasks' },
          { id: 'mfa', label: '3. Multi-Factor Auth', icon: 'fa-key' },
          { id: 'pam', label: '4. Privileged Access', icon: 'fa-user-shield' },
          { id: 'sessions', label: '5. Session Monitoring', icon: 'fa-desktop' },
          { id: 'provisioning', label: '6. User Provisioning', icon: 'fa-user-plus' },
          { id: 'deprovisioning', label: '7. User Deprovisioning', icon: 'fa-user-minus' },
          { id: 'reviews', label: '8. Access Reviews', icon: 'fa-clipboard-check' },
          { id: 'logins', label: '9. Login Activity', icon: 'fa-chart-line' },
          { id: 'audit', label: '10. Access Audit Logs', icon: 'fa-history' }
        ].map((tab) => (
          <li className="nav-item m-1" key={tab.id}>
            <button
              className={`btn btn-sm ${activeTab === tab.id ? 'btn-primary' : 'btn-outline-secondary text-light'}`}
              onClick={() => setActiveTab(tab.id)}
            >
              <i className={`fas ${tab.icon} mr-1`}></i> {tab.label}
            </button>
          </li>
        ))}
      </ul>

      {/* Tab 1: User Role Management */}
      {activeTab === 'roles' && (
        <div className="card bg-dark text-white p-4 shadow">
          <h4><i className="fas fa-user-tag text-info mr-2"></i>Task 1: User Role Management (RBAC)</h4>
          <p className="text-muted">Enforce security-specific roles (SECURITY_ADMIN, SYSTEM_ADMIN, AUDITOR, USER, ADMIN).</p>
          <div className="table-responsive mt-3">
            <table className="table table-dark table-hover">
              <thead>
                <tr>
                  <th>ID</th><th>Username</th><th>Email</th><th>Current Role</th><th>Status</th><th>Action</th>
                </tr>
              </thead>
              <tbody>
                {users.map((u) => (
                  <tr key={u.id}>
                    <td>{u.id}</td>
                    <td>{u.username}</td>
                    <td>{u.email}</td>
                    <td><span className="badge badge-info">{u.role || 'USER'}</span></td>
                    <td><span className={`badge ${u.status === 'DEPROVISIONED' ? 'badge-danger' : 'badge-success'}`}>{u.status || 'ACTIVE'}</span></td>
                    <td>
                      <select className="form-control form-control-sm d-inline-block w-auto mr-2" onChange={(e) => setNewRole(e.target.value)}>
                        <option value="USER">USER</option>
                        <option value="SECURITY_ADMIN">SECURITY_ADMIN</option>
                        <option value="SYSTEM_ADMIN">SYSTEM_ADMIN</option>
                        <option value="AUDITOR">AUDITOR</option>
                        <option value="ADMIN">ADMIN</option>
                      </select>
                      <button className="btn btn-sm btn-primary" onClick={() => handleUpdateRole(u.id)}>Update Role</button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Tab 2: Access Approval Workflow */}
      {activeTab === 'approvals' && (
        <div className="card bg-dark text-white p-4 shadow">
          <h4><i className="fas fa-tasks text-warning mr-2"></i>Task 2: Access Approval Workflow</h4>
          <form onSubmit={handleCreateApprovalRequest} className="row g-3 mb-4 bg-secondary p-3 rounded">
            <div className="col-md-4">
              <label>Requester Email</label>
              <input type="email" className="form-control" value={requesterEmail} onChange={(e) => setRequesterEmail(e.target.value)} required />
            </div>
            <div className="col-md-3">
              <label>Requested Role</label>
              <select className="form-control" value={requestRole} onChange={(e) => setRequestRole(e.target.value)}>
                <option value="SECURITY_ADMIN">SECURITY_ADMIN</option>
                <option value="SYSTEM_ADMIN">SYSTEM_ADMIN</option>
                <option value="AUDITOR">AUDITOR</option>
              </select>
            </div>
            <div className="col-md-3">
              <label>Business Reason</label>
              <input type="text" className="form-control" value={requestReason} onChange={(e) => setRequestReason(e.target.value)} required />
            </div>
            <div className="col-md-2 d-flex align-items-end">
              <button type="submit" className="btn btn-warning w-100">Submit Request</button>
            </div>
          </form>

          <h5>Pending & Historical Access Requests</h5>
          <table className="table table-dark">
            <thead>
              <tr><th>ID</th><th>Requester</th><th>Requested Role</th><th>Reason</th><th>Status</th><th>Action</th></tr>
            </thead>
            <tbody>
              {approvalRequests.map((r) => (
                <tr key={r.id}>
                  <td>{r.id}</td><td>{r.requesterEmail}</td><td>{r.requestedRole}</td><td>{r.reason}</td>
                  <td><span className={`badge ${r.status === 'APPROVED' ? 'badge-success' : r.status === 'REJECTED' ? 'badge-danger' : 'badge-warning'}`}>{r.status}</span></td>
                  <td>
                    {r.status === 'PENDING' && (
                      <>
                        <button className="btn btn-sm btn-success mr-2" onClick={() => handleReviewRequest(r.id, 'APPROVED')}>Approve</button>
                        <button className="btn btn-sm btn-danger" onClick={() => handleReviewRequest(r.id, 'REJECTED')}>Reject</button>
                      </>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Tab 3: Multi-Factor Authentication */}
      {activeTab === 'mfa' && (
        <div className="card bg-dark text-white p-4 shadow">
          <h4><i className="fas fa-key text-success mr-2"></i>Task 3: Multi-Factor Authentication (OTP / TOTP)</h4>
          <div className="row">
            <div className="col-md-6 border-right border-secondary">
              <h5>1. Generate MFA Secret</h5>
              <div className="form-group">
                <label>User Email</label>
                <input type="email" className="form-control" value={mfaEmail} onChange={(e) => setMfaEmail(e.target.value)} placeholder="user@example.com" />
              </div>
              <button className="btn btn-info" onClick={handleSetupMfa}>Generate Secret</button>

              {mfaSecretData && (
                <div className="mt-3 p-3 bg-secondary rounded">
                  <p><strong>Generated Secret:</strong> <code>{mfaSecretData.secret}</code></p>
                  <p><strong>OTP Auth URL:</strong> <small>{mfaSecretData.otpauthUrl}</small></p>
                </div>
              )}
            </div>

            <div className="col-md-6">
              <h5>2. Verify & Enable MFA</h5>
              <div className="form-group">
                <label>Enter 6-Digit OTP Code</label>
                <input type="text" className="form-control" value={mfaCode} onChange={(e) => setMfaCode(e.target.value)} placeholder="123456" maxLength={6} />
              </div>
              <button className="btn btn-success" onClick={handleVerifyMfa}>Verify & Enable MFA</button>
            </div>
          </div>
        </div>
      )}

      {/* Tab 4: Privileged Access Management */}
      {activeTab === 'pam' && (
        <div className="card bg-dark text-white p-4 shadow">
          <h4><i className="fas fa-user-shield text-danger mr-2"></i>Task 4: Privileged Access Management (PAM)</h4>
          <form onSubmit={handleStartPamSession} className="row g-3 mb-4 bg-secondary p-3 rounded">
            <div className="col-md-4">
              <label>Admin Email</label>
              <input type="email" className="form-control" value={pamEmail} onChange={(e) => setPamEmail(e.target.value)} required />
            </div>
            <div className="col-md-3">
              <label>Elevated Role</label>
              <select className="form-control" value={pamRole} onChange={(e) => setPamRole(e.target.value)}>
                <option value="SYSTEM_ADMIN">SYSTEM_ADMIN</option>
                <option value="SECURITY_ADMIN">SECURITY_ADMIN</option>
              </select>
            </div>
            <div className="col-md-3">
              <label>Elevation Justification</label>
              <input type="text" className="form-control" value={pamJustification} onChange={(e) => setPamJustification(e.target.value)} required />
            </div>
            <div className="col-md-2 d-flex align-items-end">
              <button type="submit" className="btn btn-danger w-100">Elevate Session</button>
            </div>
          </form>

          <h5>Active Privileged Sessions</h5>
          <table className="table table-dark">
            <thead>
              <tr><th>ID</th><th>Admin Email</th><th>Role</th><th>IP Address</th><th>Justification</th><th>Start Time</th></tr>
            </thead>
            <tbody>
              {pamSessions.map((s) => (
                <tr key={s.id}>
                  <td>{s.id}</td><td>{s.adminEmail}</td><td><span className="badge badge-danger">{s.role}</span></td>
                  <td>{s.ipAddress}</td><td>{s.elevationJustification}</td><td>{new Date(s.startTime).toLocaleString()}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Tab 5: Session Monitoring */}
      {activeTab === 'sessions' && (
        <div className="card bg-dark text-white p-4 shadow">
          <h4><i className="fas fa-desktop text-primary mr-2"></i>Task 5: Session Monitoring & Termination</h4>
          <div className="d-flex justify-content-between mb-3">
            <span>Active Live Sessions: <strong>{activeSessions.length}</strong></span>
            <button className="btn btn-sm btn-outline-info" onClick={fetchActiveSessions}>Refresh Sessions</button>
          </div>
          <table className="table table-dark">
            <thead>
              <tr><th>Session ID</th><th>User Email</th><th>IP Address</th><th>User Agent</th><th>Login Time</th><th>Action</th></tr>
            </thead>
            <tbody>
              {activeSessions.map((s) => (
                <tr key={s.id}>
                  <td>{s.id}</td><td>{s.userEmail}</td><td>{s.ipAddress}</td><td><small>{s.userAgent}</small></td>
                  <td>{new Date(s.loginTime).toLocaleString()}</td>
                  <td>
                    <button className="btn btn-sm btn-danger" onClick={() => handleTerminateSession(s.id)}>Force Logout</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Tab 6: User Provisioning */}
      {activeTab === 'provisioning' && (
        <div className="card bg-dark text-white p-4 shadow">
          <h4><i className="fas fa-user-plus text-success mr-2"></i>Task 6: Automated User Provisioning</h4>
          <form onSubmit={handleProvisionUser} className="row g-3">
            <div className="col-md-6">
              <label>Username</label>
              <input type="text" className="form-control" value={provUsername} onChange={(e) => setProvUsername(e.target.value)} required />
            </div>
            <div className="col-md-6">
              <label>Email Address</label>
              <input type="email" className="form-control" value={provEmail} onChange={(e) => setProvEmail(e.target.value)} required />
            </div>
            <div className="col-md-6">
              <label>Password</label>
              <input type="password" className="form-control" value={provPassword} onChange={(e) => setProvPassword(e.target.value)} required />
            </div>
            <div className="col-md-3">
              <label>Mobile Number</label>
              <input type="text" className="form-control" value={provMobnum} onChange={(e) => setProvMobnum(e.target.value)} />
            </div>
            <div className="col-md-3">
              <label>Assigned Role</label>
              <select className="form-control" value={provRole} onChange={(e) => setProvRole(e.target.value)}>
                <option value="USER">USER</option>
                <option value="SECURITY_ADMIN">SECURITY_ADMIN</option>
                <option value="SYSTEM_ADMIN">SYSTEM_ADMIN</option>
                <option value="AUDITOR">AUDITOR</option>
              </select>
            </div>
            <div className="col-12 mt-3">
              <button type="submit" className="btn btn-success">Provision Account</button>
            </div>
          </form>
        </div>
      )}

      {/* Tab 7: User Deprovisioning */}
      {activeTab === 'deprovisioning' && (
        <div className="card bg-dark text-white p-4 shadow">
          <h4><i className="fas fa-user-minus text-danger mr-2"></i>Task 7: User Deprovisioning & Revocation</h4>
          <p className="text-muted">Instantly lock accounts, set status to DEPROVISIONED, and revoke active sessions.</p>
          <table className="table table-dark">
            <thead>
              <tr><th>ID</th><th>Username</th><th>Email</th><th>Current Status</th><th>Action</th></tr>
            </thead>
            <tbody>
              {users.map((u) => (
                <tr key={u.id}>
                  <td>{u.id}</td><td>{u.username}</td><td>{u.email}</td>
                  <td><span className={`badge ${u.status === 'DEPROVISIONED' ? 'badge-danger' : 'badge-success'}`}>{u.status || 'ACTIVE'}</span></td>
                  <td>
                    {u.status !== 'DEPROVISIONED' ? (
                      <button className="btn btn-sm btn-danger" onClick={() => handleDeprovisionUser(u.id)}>Deprovision Account</button>
                    ) : (
                      <span className="text-muted">Access Revoked</span>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Tab 8: Access Reviews */}
      {activeTab === 'reviews' && (
        <div className="card bg-dark text-white p-4 shadow">
          <h4><i className="fas fa-clipboard-check text-info mr-2"></i>Task 8: Periodic Access Reviews</h4>
          <form onSubmit={handleSubmitAccessReview} className="row g-3 mb-4 bg-secondary p-3 rounded">
            <div className="col-md-4">
              <label>Target User Email</label>
              <input type="email" className="form-control" value={reviewTargetEmail} onChange={(e) => setReviewTargetEmail(e.target.value)} required />
            </div>
            <div className="col-md-3">
              <label>Reviewed Role</label>
              <input type="text" className="form-control" value={reviewRole} onChange={(e) => setReviewRole(e.target.value)} required />
            </div>
            <div className="col-md-2">
              <label>Decision</label>
              <select className="form-control" value={reviewDecision} onChange={(e) => setReviewDecision(e.target.value)}>
                <option value="KEPT">KEPT</option>
                <option value="REVOKED">REVOKED</option>
                <option value="MODIFIED">MODIFIED</option>
              </select>
            </div>
            <div className="col-md-3">
              <label>Comments</label>
              <input type="text" className="form-control" value={reviewComments} onChange={(e) => setReviewComments(e.target.value)} />
            </div>
            <div className="col-12 mt-2">
              <button type="submit" className="btn btn-info">Record Review</button>
            </div>
          </form>

          <h5>Past Access Review Certifications</h5>
          <table className="table table-dark">
            <thead>
              <tr><th>Target User</th><th>Role</th><th>Reviewer</th><th>Decision</th><th>Review Date</th><th>Next Due</th></tr>
            </thead>
            <tbody>
              {accessReviews.map((r) => (
                <tr key={r.id}>
                  <td>{r.targetEmail}</td><td>{r.reviewedRole}</td><td>{r.reviewerEmail}</td>
                  <td><span className={`badge ${r.decision === 'KEPT' ? 'badge-success' : 'badge-danger'}`}>{r.decision}</span></td>
                  <td>{new Date(r.reviewDate).toLocaleDateString()}</td>
                  <td>{new Date(r.nextReviewDue).toLocaleDateString()}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Tab 9: Login Activity Dashboard */}
      {activeTab === 'logins' && (
        <div className="card bg-dark text-white p-4 shadow">
          <h4><i className="fas fa-chart-line text-warning mr-2"></i>Task 9: Login Activity Dashboard</h4>
          <div className="row mb-4">
            <div className="col-md-4">
              <div className="card bg-success text-white p-3">
                <h5>Successful Logins</h5>
                <h2>{loginStats.totalSuccessful}</h2>
              </div>
            </div>
            <div className="col-md-4">
              <div className="card bg-danger text-white p-3">
                <h5>Failed (Password)</h5>
                <h2>{loginStats.totalFailedPassword}</h2>
              </div>
            </div>
            <div className="col-md-4">
              <div className="card bg-warning text-dark p-3">
                <h5>Failed (MFA Code)</h5>
                <h2>{loginStats.totalFailedMfa}</h2>
              </div>
            </div>
          </div>

          <h5>Recent Login Attempts</h5>
          <table className="table table-dark">
            <thead>
              <tr><th>Timestamp</th><th>Email</th><th>Status</th><th>IP Address</th><th>Failure Reason</th></tr>
            </thead>
            <tbody>
              {(loginStats.recentActivities || []).map((l) => (
                <tr key={l.id}>
                  <td>{new Date(l.timestamp).toLocaleString()}</td><td>{l.email}</td>
                  <td><span className={`badge ${l.status === 'SUCCESS' ? 'badge-success' : 'badge-danger'}`}>{l.status}</span></td>
                  <td>{l.ipAddress}</td><td>{l.failureReason || 'N/A'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Tab 10: Access Audit Module */}
      {activeTab === 'audit' && (
        <div className="card bg-dark text-white p-4 shadow">
          <h4><i className="fas fa-history text-secondary mr-2"></i>Task 10: Access Audit Module Repository</h4>
          <div className="table-responsive">
            <table className="table table-dark table-hover">
              <thead>
                <tr><th>ID</th><th>Timestamp</th><th>Username</th><th>Role</th><th>Action</th><th>Resource</th><th>Details</th><th>IP</th><th>Status</th></tr>
              </thead>
              <tbody>
                {auditLogs.map((a) => (
                  <tr key={a.id}>
                    <td>{a.id}</td><td>{new Date(a.timestamp).toLocaleString()}</td><td>{a.username}</td>
                    <td><span className="badge badge-info">{a.userRole || 'N/A'}</span></td>
                    <td><span className="badge badge-warning">{a.action}</span></td>
                    <td>{a.resource}</td><td><small>{a.details}</small></td><td>{a.ipAddress}</td>
                    <td><span className={`badge ${a.status === 'SUCCESS' ? 'badge-success' : 'badge-danger'}`}>{a.status}</span></td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
};

export default AccessManagementDashboard;
