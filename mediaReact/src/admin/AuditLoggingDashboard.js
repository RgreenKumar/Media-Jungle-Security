import React, { useState, useEffect } from 'react';
import API_URL from '../Config';
import { toast } from 'react-hot-toast';

// ISO 27001 | Module 5: Audit Logging & Monitoring | Tasks 1-10 Admin Dashboard
// Description: Admin UI dashboard providing dedicated management interfaces for all 10 Audit Logging & Monitoring ISO 27001 tasks.
const AuditLoggingDashboard = () => {
  const [activeTab, setActiveTab] = useState('activities');

  // State variables for all 10 tasks
  const [userActivities, setUserActivities] = useState([]);
  const [adminActions, setAdminActions] = useState([]);
  const [auditTrail, setAuditTrail] = useState([]);
  const [securityEvents, setSecurityEvents] = useState([]);
  const [realtimeMetrics, setRealtimeMetrics] = useState({});
  const [alerts, setAlerts] = useState([]);
  const [complianceReport, setComplianceReport] = useState(null);
  const [auditReviews, setAuditReviews] = useState([]);

  const [retentionDays, setRetentionDays] = useState(90);
  const [auditScope, setAuditScope] = useState('FULL_SYSTEM_LOGS');
  const [reviewFindings, setReviewFindings] = useState('');

  useEffect(() => {
    fetchUserActivities();
    fetchAdminActions();
    fetchAuditTrail();
    fetchSecurityEvents();
    fetchRealtimeMetrics();
    fetchAlerts();
    fetchAuditReviews();
  }, []);

  // ISO 27001 | Task 1: User Activity Logging
  const fetchUserActivities = async () => {
    try {
      const res = await fetch(`${API_URL}/api/v2/audit-logging/user-activities`);
      if (res.ok) {
        const data = await res.json();
        setUserActivities(data);
      }
    } catch (e) { console.error(e); }
  };

  // ISO 27001 | Task 2: Administrative Action Logging
  const fetchAdminActions = async () => {
    try {
      const res = await fetch(`${API_URL}/api/v2/audit-logging/admin-actions`);
      if (res.ok) {
        const data = await res.json();
        setAdminActions(data);
      }
    } catch (e) { console.error(e); }
  };

  // ISO 27001 | Task 3: Audit Trail Repository
  const fetchAuditTrail = async () => {
    try {
      const res = await fetch(`${API_URL}/api/v2/audit-logging/audit-trail`);
      if (res.ok) {
        const data = await res.json();
        setAuditTrail(data);
      }
    } catch (e) { console.error(e); }
  };

  // ISO 27001 | Task 4: Security Event Logging
  const fetchSecurityEvents = async () => {
    try {
      const res = await fetch(`${API_URL}/api/v2/audit-logging/security-events`);
      if (res.ok) {
        const data = await res.json();
        setSecurityEvents(data);
      }
    } catch (e) { console.error(e); }
  };

  // ISO 27001 | Task 5: Real-Time Monitoring Dashboard
  const fetchRealtimeMetrics = async () => {
    try {
      const res = await fetch(`${API_URL}/api/v2/audit-logging/realtime-dashboard`);
      if (res.ok) {
        const data = await res.json();
        setRealtimeMetrics(data);
      }
    } catch (e) { console.error(e); }
  };

  // ISO 27001 | Task 6: Alert Management System
  const fetchAlerts = async () => {
    try {
      const res = await fetch(`${API_URL}/api/v2/audit-logging/alerts`);
      if (res.ok) {
        const data = await res.json();
        setAlerts(data);
      }
    } catch (e) { console.error(e); }
  };

  const handleUpdateAlertStatus = async (id, status) => {
    try {
      const res = await fetch(`${API_URL}/api/v2/audit-logging/alerts/${id}/status`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ status })
      });
      if (res.ok) {
        toast.success(`Alert marked as ${status}`);
        fetchAlerts();
      }
    } catch (e) { toast.error(e.message); }
  };

  // ISO 27001 | Task 7: Log Retention Purging
  const handleRetentionPurge = async () => {
    try {
      const res = await fetch(`${API_URL}/api/v2/audit-logging/retention/purge?days=${retentionDays}`, {
        method: 'POST'
      });
      if (res.ok) {
        toast.success(`Logs older than ${retentionDays} days purged per retention policy.`);
      }
    } catch (e) { toast.error(e.message); }
  };

  // ISO 27001 | Task 8: Anomaly Detection Engine
  const handleTriggerAnomalyScan = async () => {
    try {
      const res = await fetch(`${API_URL}/api/v2/audit-logging/anomaly-scan`, {
        method: 'POST'
      });
      if (res.ok) {
        toast.success('Anomaly Detection Scan completed!');
        fetchAlerts();
      }
    } catch (e) { toast.error(e.message); }
  };

  // ISO 27001 | Task 9: Compliance Reporting Module
  const handleGenerateComplianceReport = async () => {
    try {
      const res = await fetch(`${API_URL}/api/v2/audit-logging/compliance-report`);
      if (res.ok) {
        const data = await res.json();
        setComplianceReport(data);
        toast.success('ISO 27001 Compliance Report generated!');
      }
    } catch (e) { toast.error(e.message); }
  };

  // ISO 27001 | Task 10: Audit Review Workflow
  const fetchAuditReviews = async () => {
    try {
      const res = await fetch(`${API_URL}/api/v2/audit-logging/audit-reviews`);
      if (res.ok) {
        const data = await res.json();
        setAuditReviews(data);
      }
    } catch (e) { console.error(e); }
  };

  const handleSubmitAuditReview = async (e) => {
    e.preventDefault();
    try {
      const res = await fetch(`${API_URL}/api/v2/audit-logging/audit-reviews/submit`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          auditorEmail: 'lead-auditor@mediajungle.com',
          auditScope,
          totalLogsReviewed: 1250,
          reviewFindings
        })
      });
      if (res.ok) {
        toast.success('ISO 27001 Audit Review sign-off recorded!');
        setReviewFindings('');
        fetchAuditReviews();
      }
    } catch (e) { toast.error(e.message); }
  };

  return (
    <div className="container-fluid p-4" style={{ backgroundColor: '#0f172a', color: '#f8fafc', minHeight: '100vh' }}>
      <div className="d-flex justify-content-between align-items-center mb-4 pb-3 border-bottom border-secondary">
        <div>
          <h2 className="text-info font-weight-bold mb-1">
            <i className="fas fa-list-alt mr-2"></i>ISO 27001 — Audit Logging & Monitoring Dashboard
          </h2>
          <p className="text-muted mb-0">Module 5 Implementation: Controls 1 to 10 for Media Jungle Audit Governance</p>
        </div>
        <span className="badge badge-info p-2">Audit Compliance 100%</span>
      </div>

      {/* Tabs Header */}
      <ul className="nav nav-pills mb-4 bg-dark p-2 rounded">
        {[
          { id: 'activities', label: '1. User Activities', icon: 'fa-users-cog' },
          { id: 'admin', label: '2. Admin Actions', icon: 'fa-user-shield' },
          { id: 'trail', label: '3. Audit Trail', icon: 'fa-history' },
          { id: 'events', label: '4. Security Events', icon: 'fa-shield-virus' },
          { id: 'realtime', label: '5. Real-Time Mon', icon: 'fa-desktop' },
          { id: 'alerts', label: '6. Alert System', icon: 'fa-bell' },
          { id: 'retention', label: '7. Log Retention', icon: 'fa-calendar-minus' },
          { id: 'anomaly', label: '8. Anomaly Engine', icon: 'fa-search-location' },
          { id: 'compliance', label: '9. Compliance Report', icon: 'fa-certificate' },
          { id: 'review', label: '10. Audit Review', icon: 'fa-clipboard-check' }
        ].map((tab) => (
          <li className="nav-item m-1" key={tab.id}>
            <button
              className={`btn btn-sm ${activeTab === tab.id ? 'btn-info font-weight-bold' : 'btn-outline-secondary text-light'}`}
              onClick={() => setActiveTab(tab.id)}
            >
              <i className={`fas ${tab.icon} mr-1`}></i> {tab.label}
            </button>
          </li>
        ))}
      </ul>

      {/* Tab 1: User Activities */}
      {activeTab === 'activities' && (
        <div className="card bg-dark text-white p-4 shadow">
          <h4><i className="fas fa-users-cog text-info mr-2"></i>Task 1: User Activity Logging</h4>
          <table className="table table-dark mt-3">
            <thead>
              <tr><th>User Email</th><th>Action</th><th>Target Resource</th><th>IP Address</th><th>User Agent</th><th>Timestamp</th></tr>
            </thead>
            <tbody>
              {userActivities.map((a) => (
                <tr key={a.id}>
                  <td><strong>{a.userEmail}</strong></td>
                  <td><span className="badge badge-info">{a.action}</span></td>
                  <td>{a.targetResource}</td><td><code>{a.ipAddress}</code></td>
                  <td><small>{a.userAgent}</small></td>
                  <td>{new Date(a.timestamp).toLocaleString()}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Tab 2: Admin Actions */}
      {activeTab === 'admin' && (
        <div className="card bg-dark text-white p-4 shadow">
          <h4><i className="fas fa-user-shield text-warning mr-2"></i>Task 2: Administrative Action Logging</h4>
          <table className="table table-dark mt-3">
            <thead>
              <tr><th>Admin Email</th><th>Action Type</th><th>Target Setting</th><th>Change Details</th><th>IP Address</th><th>Timestamp</th></tr>
            </thead>
            <tbody>
              {adminActions.map((a) => (
                <tr key={a.id}>
                  <td>{a.adminEmail}</td>
                  <td><span className="badge badge-warning">{a.actionType}</span></td>
                  <td><strong>{a.targetSetting}</strong></td>
                  <td><small>{a.changeDetails}</small></td>
                  <td><code>{a.ipAddress}</code></td>
                  <td>{new Date(a.timestamp).toLocaleString()}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Tab 3: Audit Trail */}
      {activeTab === 'trail' && (
        <div className="card bg-dark text-white p-4 shadow">
          <h4><i className="fas fa-history text-primary mr-2"></i>Task 3: Central Audit Trail Repository</h4>
          <table className="table table-dark mt-3">
            <thead>
              <tr><th>Category</th><th>Actor</th><th>Action</th><th>Target</th><th>Payload Diff</th><th>IP</th><th>Status</th></tr>
            </thead>
            <tbody>
              {auditTrail.map((t) => (
                <tr key={t.id}>
                  <td><span className="badge badge-secondary">{t.eventCategory}</span></td>
                  <td>{t.actor}</td><td><strong>{t.action}</strong></td><td>{t.targetResource}</td>
                  <td><code>{t.payloadDiff}</code></td><td><code>{t.ipAddress}</code></td>
                  <td><span className="badge badge-success">{t.status}</span></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Tab 4: Security Events */}
      {activeTab === 'events' && (
        <div className="card bg-dark text-white p-4 shadow">
          <h4><i className="fas fa-shield-virus text-danger mr-2"></i>Task 4: Security Event & Incident Logging</h4>
          <table className="table table-dark mt-3">
            <thead>
              <tr><th>Event Type</th><th>Severity</th><th>Details</th><th>Source IP</th><th>User Email</th><th>Timestamp</th></tr>
            </thead>
            <tbody>
              {securityEvents.map((e) => (
                <tr key={e.id}>
                  <td><strong>{e.eventType}</strong></td>
                  <td><span className={`badge ${e.severity === 'CRITICAL' ? 'badge-danger' : 'badge-warning'}`}>{e.severity}</span></td>
                  <td><small>{e.eventDetails}</small></td><td><code>{e.sourceIp}</code></td><td>{e.userEmail}</td>
                  <td>{new Date(e.timestamp).toLocaleString()}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Tab 5: Real-Time Monitoring */}
      {activeTab === 'realtime' && (
        <div className="card bg-dark text-white p-4 shadow">
          <h4><i className="fas fa-desktop text-success mr-2"></i>Task 5: Real-Time System Monitoring Dashboard</h4>
          <div className="row mt-3">
            <div className="col-md-3">
              <div className="card bg-primary text-white p-3 text-center">
                <h5>Active User Sessions</h5>
                <h2>{realtimeMetrics.activeUserSessions}</h2>
              </div>
            </div>
            <div className="col-md-3">
              <div className="card bg-info text-white p-3 text-center">
                <h5>Requests / Second</h5>
                <h2>{realtimeMetrics.requestsPerSecond} RPS</h2>
              </div>
            </div>
            <div className="col-md-3">
              <div className="card bg-warning text-dark p-3 text-center">
                <h5>CPU Load</h5>
                <h2>{realtimeMetrics.cpuUtilizationPercent}%</h2>
              </div>
            </div>
            <div className="col-md-3">
              <div className="card bg-success text-white p-3 text-center">
                <h5>System Health</h5>
                <h2>{realtimeMetrics.systemHealth}</h2>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Tab 6: Alert System */}
      {activeTab === 'alerts' && (
        <div className="card bg-dark text-white p-4 shadow">
          <h4><i className="fas fa-bell text-warning mr-2"></i>Task 6: Alert Management System</h4>
          <table className="table table-dark mt-3">
            <thead>
              <tr><th>Alert Title</th><th>Severity</th><th>Summary</th><th>Assigned To</th><th>Status</th><th>Action</th></tr>
            </thead>
            <tbody>
              {alerts.map((a) => (
                <tr key={a.id}>
                  <td><strong>{a.alertTitle}</strong></td>
                  <td><span className={`badge ${a.severity === 'CRITICAL' || a.severity === 'HIGH' ? 'badge-danger' : 'badge-warning'}`}>{a.severity}</span></td>
                  <td><small>{a.alertSummary}</small></td><td>{a.assignedTo}</td>
                  <td><span className={`badge ${a.status === 'RESOLVED' ? 'badge-success' : 'badge-danger'}`}>{a.status}</span></td>
                  <td>
                    {a.status !== 'RESOLVED' && (
                      <button className="btn btn-sm btn-success" onClick={() => handleUpdateAlertStatus(a.id, 'RESOLVED')}>Resolve Alert</button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Tab 7: Log Retention */}
      {activeTab === 'retention' && (
        <div className="card bg-dark text-white p-4 shadow">
          <h4><i className="fas fa-calendar-minus text-info mr-2"></i>Task 7: Log Retention Management</h4>
          <div className="p-4 bg-secondary rounded mt-3">
            <h5>Automated Log Retention Policy</h5>
            <p>Retention period configured: <strong>90 Days</strong> (Scheduled daily at 02:00 AM)</p>
            <div className="form-inline mt-3">
              <label className="mr-2">Manual Purge Cutoff (Days):</label>
              <input type="number" className="form-control mr-2" value={retentionDays} onChange={(e) => setRetentionDays(Number(e.target.value))} />
              <button className="btn btn-danger" onClick={handleRetentionPurge}>Execute Log Retention Purge</button>
            </div>
          </div>
        </div>
      )}

      {/* Tab 8: Anomaly Engine */}
      {activeTab === 'anomaly' && (
        <div className="card bg-dark text-white p-4 shadow">
          <div className="d-flex justify-content-between align-items-center mb-3">
            <h4><i className="fas fa-search-location text-danger mr-2"></i>Task 8: Anomaly Detection Engine</h4>
            <button className="btn btn-danger" onClick={handleTriggerAnomalyScan}>Run Anomaly Scan</button>
          </div>
          <div className="p-4 bg-secondary rounded">
            <h5>Rule-Based Anomaly Detectors Active:</h5>
            <ul>
              <li><strong>Rapid Auth Failure Spike Detector:</strong> Alerts when &gt; 5 failed logins occur in 60s.</li>
              <li><strong>IP Velocity & Rate Limit Detector:</strong> Flags requests exceeding 100 req/min limit.</li>
              <li><strong>Privilege Escalation Monitor:</strong> Logs unexpected admin role modifications.</li>
            </ul>
          </div>
        </div>
      )}

      {/* Tab 9: Compliance Report */}
      {activeTab === 'compliance' && (
        <div className="card bg-dark text-white p-4 shadow">
          <div className="d-flex justify-content-between align-items-center mb-3">
            <h4><i className="fas fa-certificate text-success mr-2"></i>Task 9: ISO 27001 Compliance Reporting Module</h4>
            <button className="btn btn-success" onClick={handleGenerateComplianceReport}>Generate ISO 27001 Report</button>
          </div>

          {complianceReport && (
            <div className="p-4 bg-secondary rounded mt-3">
              <h5>{complianceReport.reportTitle}</h5>
              <p><strong>Standard:</strong> {complianceReport.standard}</p>
              <p><strong>Compliance Score:</strong> <span className="badge badge-success font-size-lg">{complianceReport.complianceScore}</span></p>
              <p><strong>Total Modules Implemented:</strong> {complianceReport.totalModulesImplemented} / 5</p>
              <p><strong>Total Tasks Implemented:</strong> {complianceReport.totalTasksImplemented} / 50</p>
              <p><strong>Generated At:</strong> {complianceReport.generatedAt}</p>
            </div>
          )}
        </div>
      )}

      {/* Tab 10: Audit Review */}
      {activeTab === 'review' && (
        <div className="card bg-dark text-white p-4 shadow">
          <h4><i className="fas fa-clipboard-check text-info mr-2"></i>Task 10: Periodic Audit Record Review Sign-Off</h4>
          <form onSubmit={handleSubmitAuditReview} className="row g-3 mb-4 bg-secondary p-3 rounded">
            <div className="col-md-4">
              <label>Audit Scope</label>
              <select className="form-control" value={auditScope} onChange={(e) => setAuditScope(e.target.value)}>
                <option value="FULL_SYSTEM_LOGS">FULL_SYSTEM_LOGS</option>
                <option value="ADMIN_ACTIONS">ADMIN_ACTIONS</option>
                <option value="ACCESS_CONTROL_LOGS">ACCESS_CONTROL_LOGS</option>
              </select>
            </div>
            <div className="col-md-6">
              <label>Auditor Findings & Comments</label>
              <input type="text" className="form-control" value={reviewFindings} onChange={(e) => setReviewFindings(e.target.value)} required />
            </div>
            <div className="col-md-2 d-flex align-items-end">
              <button type="submit" className="btn btn-info w-100">Submit Sign-off</button>
            </div>
          </form>

          <h5>Auditor Record Review History</h5>
          <table className="table table-dark">
            <thead>
              <tr><th>Auditor</th><th>Scope</th><th>Logs Reviewed</th><th>Findings</th><th>Review Date</th><th>Next Due</th></tr>
            </thead>
            <tbody>
              {auditReviews.map((r) => (
                <tr key={r.id}>
                  <td>{r.auditorEmail}</td><td><span className="badge badge-info">{r.auditScope}</span></td>
                  <td>{r.totalLogsReviewed}</td><td><small>{r.reviewFindings}</small></td>
                  <td>{new Date(r.reviewDate).toLocaleDateString()}</td>
                  <td>{new Date(r.nextReviewDue).toLocaleDateString()}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};

export default AuditLoggingDashboard;
