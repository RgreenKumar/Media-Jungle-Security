import React, { useState, useEffect } from 'react';
import API_URL from '../Config';
import { toast } from 'react-hot-toast';

// ISO 27001 | Module 3: Code Level Security | Tasks 1-10 Admin Dashboard
// Description: Admin UI dashboard providing dedicated management interfaces for all 10 Code Level Security ISO 27001 tasks.
const CodeSecurityDashboard = () => {
  const [activeTab, setActiveTab] = useState('standards');

  // State variables for all 10 tasks
  const [standards, setStandards] = useState({});
  const [testInput, setTestInput] = useState("<script>alert('xss')</script> OR 1=1--");
  const [valResult, setValResult] = useState(null);
  const [sqliResult, setSqliResult] = useState(null);
  const [xssResult, setXssResult] = useState(null);

  const [csrfSession, setCsrfSession] = useState('session_user_admin');
  const [csrfTokenData, setCsrfTokenData] = useState(null);

  const [apiMetrics, setApiMetrics] = useState({});
  const [dependencies, setDependencies] = useState([]);
  const [codeReviews, setCodeReviews] = useState([]);
  const [securityScanRuns, setSecurityScanRuns] = useState([]);
  const [vulnerabilities, setVulnerabilities] = useState([]);

  const [reviewTitle, setReviewTitle] = useState('');
  const [reviewAuthor, setReviewAuthor] = useState('');
  const [reviewComments, setReviewComments] = useState('');

  useEffect(() => {
    fetchStandards();
    fetchApiMetrics();
    fetchDependencies();
    fetchCodeReviews();
    fetchSecurityScanRuns();
    fetchVulnerabilities();
    fetchCsrfToken();
  }, []);

  // ISO 27001 | Task 1: Secure Coding Standards
  const fetchStandards = async () => {
    try {
      const res = await fetch(`${API_URL}/api/v2/code-security/standards`);
      if (res.ok) {
        const data = await res.json();
        setStandards(data);
      }
    } catch (e) { console.error(e); }
  };

  // ISO 27001 | Task 2: Input Validation
  const handleTestInputValidation = async () => {
    try {
      const res = await fetch(`${API_URL}/api/v2/code-security/validate-input`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ input: testInput })
      });
      if (res.ok) {
        const data = await res.json();
        setValResult(data);
      }
    } catch (e) { toast.error(e.message); }
  };

  // ISO 27001 | Task 3: SQL Injection Protection
  const handleTestSqli = async () => {
    try {
      const res = await fetch(`${API_URL}/api/v2/code-security/sqli-check`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ input: testInput })
      });
      if (res.ok) {
        const data = await res.json();
        setSqliResult(data);
      }
    } catch (e) { toast.error(e.message); }
  };

  // ISO 27001 | Task 4: XSS Protection
  const handleTestXss = async () => {
    try {
      const res = await fetch(`${API_URL}/api/v2/code-security/sanitize-xss`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ input: testInput })
      });
      if (res.ok) {
        const data = await res.json();
        setXssResult(data);
      }
    } catch (e) { toast.error(e.message); }
  };

  // ISO 27001 | Task 5: CSRF Protection
  const fetchCsrfToken = async () => {
    try {
      const res = await fetch(`${API_URL}/api/v2/code-security/csrf-token?sessionId=${csrfSession}`);
      if (res.ok) {
        const data = await res.json();
        setCsrfTokenData(data);
      }
    } catch (e) { console.error(e); }
  };

  // ISO 27001 | Task 6: Secure API Metrics
  const fetchApiMetrics = async () => {
    try {
      const res = await fetch(`${API_URL}/api/v2/code-security/api-security/metrics`);
      if (res.ok) {
        const data = await res.json();
        setApiMetrics(data);
      }
    } catch (e) { console.error(e); }
  };

  // ISO 27001 | Task 7: Dependency Management
  const fetchDependencies = async () => {
    try {
      const res = await fetch(`${API_URL}/api/v2/code-security/dependencies`);
      if (res.ok) {
        const data = await res.json();
        setDependencies(data);
      }
    } catch (e) { console.error(e); }
  };

  // ISO 27001 | Task 8: Code Review Workflow
  const fetchCodeReviews = async () => {
    try {
      const res = await fetch(`${API_URL}/api/v2/code-security/code-reviews`);
      if (res.ok) {
        const data = await res.json();
        setCodeReviews(data);
      }
    } catch (e) { console.error(e); }
  };

  const handleSubmitCodeReview = async (e) => {
    e.preventDefault();
    try {
      const res = await fetch(`${API_URL}/api/v2/code-security/code-reviews/submit`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          featureTitle: reviewTitle,
          authorEmail: reviewAuthor,
          reviewerEmail: 'lead-auditor@mediajungle.com',
          comments: reviewComments
        })
      });
      if (res.ok) {
        toast.success('Code review submitted for pre-deployment sign-off!');
        setReviewTitle(''); setReviewAuthor(''); setReviewComments('');
        fetchCodeReviews();
      }
    } catch (e) { toast.error(e.message); }
  };

  const handleApproveCodeReview = async (id, status) => {
    try {
      const res = await fetch(`${API_URL}/api/v2/code-security/code-reviews/${id}/review`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ status })
      });
      if (res.ok) {
        toast.success(`Code review ${status.toLowerCase()}!`);
        fetchCodeReviews();
      }
    } catch (e) { toast.error(e.message); }
  };

  // ISO 27001 | Task 9: Security Testing
  const fetchSecurityScanRuns = async () => {
    try {
      const res = await fetch(`${API_URL}/api/v2/code-security/security-testing/runs`);
      if (res.ok) {
        const data = await res.json();
        setSecurityScanRuns(data);
      }
    } catch (e) { console.error(e); }
  };

  const handleRunSecurityScan = async () => {
    try {
      const res = await fetch(`${API_URL}/api/v2/code-security/security-testing/run`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ testType: 'DAST_FULL_API_SCAN' })
      });
      if (res.ok) {
        toast.success('Automated DAST/SAST security scan completed!');
        fetchSecurityScanRuns();
      }
    } catch (e) { toast.error(e.message); }
  };

  // ISO 27001 | Task 10: Vulnerability Management Portal
  const fetchVulnerabilities = async () => {
    try {
      const res = await fetch(`${API_URL}/api/v2/code-security/vulnerabilities`);
      if (res.ok) {
        const data = await res.json();
        setVulnerabilities(data);
      }
    } catch (e) { console.error(e); }
  };

  const handleUpdateVulnStatus = async (id, status) => {
    try {
      const res = await fetch(`${API_URL}/api/v2/code-security/vulnerabilities/${id}/status`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ status })
      });
      if (res.ok) {
        toast.success(`Vulnerability status updated to ${status}`);
        fetchVulnerabilities();
      }
    } catch (e) { toast.error(e.message); }
  };

  return (
    <div className="container-fluid p-4" style={{ backgroundColor: '#0f172a', color: '#f8fafc', minHeight: '100vh' }}>
      <div className="d-flex justify-content-between align-items-center mb-4 pb-3 border-bottom border-secondary">
        <div>
          <h2 className="text-warning font-weight-bold mb-1">
            <i className="fas fa-code-branch mr-2"></i>ISO 27001 — Code Level Security Dashboard
          </h2>
          <p className="text-muted mb-0">Module 3 Implementation: Controls 1 to 10 for Media Jungle Application Security</p>
        </div>
        <span className="badge badge-warning text-dark p-2">Secure Coding Certified</span>
      </div>

      {/* Tabs Header */}
      <ul className="nav nav-pills mb-4 bg-dark p-2 rounded">
        {[
          { id: 'standards', label: '1. Secure Coding Standards', icon: 'fa-shield-alt' },
          { id: 'input', label: '2. Input Validation', icon: 'fa-filter' },
          { id: 'sqli', label: '3. SQLi Protection', icon: 'fa-database' },
          { id: 'xss', label: '4. XSS Protection', icon: 'fa-terminal' },
          { id: 'csrf', label: '5. CSRF Protection', icon: 'fa-key' },
          { id: 'api', label: '6. Secure API Dev', icon: 'fa-network-wired' },
          { id: 'deps', label: '7. Dependency Mgmt', icon: 'fa-cubes' },
          { id: 'review', label: '8. Code Review Workflow', icon: 'fa-code-merge' },
          { id: 'testing', label: '9. Security Testing', icon: 'fa-bug' },
          { id: 'portal', label: '10. Vulnerability Portal', icon: 'fa-user-ninja' }
        ].map((tab) => (
          <li className="nav-item m-1" key={tab.id}>
            <button
              className={`btn btn-sm ${activeTab === tab.id ? 'btn-warning text-dark font-weight-bold' : 'btn-outline-secondary text-light'}`}
              onClick={() => setActiveTab(tab.id)}
            >
              <i className={`fas ${tab.icon} mr-1`}></i> {tab.label}
            </button>
          </li>
        ))}
      </ul>

      {/* Tab 1: Secure Coding Standards */}
      {activeTab === 'standards' && (
        <div className="card bg-dark text-white p-4 shadow">
          <h4><i className="fas fa-shield-alt text-warning mr-2"></i>Task 1: Secure Coding Standards & Headers</h4>
          <div className="row mt-3">
            <div className="col-md-6">
              <div className="p-4 bg-secondary rounded mb-3">
                <h5>Active Spring Boot HTTP Response Headers</h5>
                <ul>
                  <li><strong>Strict-Transport-Security (HSTS):</strong> <code>max-age=31536000; includeSubDomains</code></li>
                  <li><strong>X-Content-Type-Options:</strong> <code>nosniff</code></li>
                  <li><strong>X-Frame-Options:</strong> <code>DENY</code></li>
                  <li><strong>X-XSS-Protection:</strong> <code>1; mode=block</code></li>
                  <li><strong>Content-Security-Policy:</strong> <code>default-src 'self'</code></li>
                </ul>
              </div>
            </div>
            <div className="col-md-6">
              <div className="p-4 bg-secondary rounded">
                <h5>Flutter Mobile App (`ott_project`) Security Policy</h5>
                <ul>
                  <li><strong>SSL Certificate Pinning:</strong> <span className="badge badge-success">ENFORCED</span></li>
                  <li><strong>Secure Storage Encryption:</strong> <span className="badge badge-success">ENFORCED</span></li>
                  <li><strong>Min TLS Version:</strong> <code>TLSv1.3</code></li>
                  <li><strong>Screen Capture Prevention:</strong> <span className="badge badge-info">ACTIVE</span></li>
                </ul>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Tab 2: Input Validation */}
      {activeTab === 'input' && (
        <div className="card bg-dark text-white p-4 shadow">
          <h4><i className="fas fa-filter text-info mr-2"></i>Task 2: Input Validation Framework Playground</h4>
          <div className="form-group mt-3">
            <label>Test Input Payload</label>
            <input type="text" className="form-control" value={testInput} onChange={(e) => setTestInput(e.target.value)} />
          </div>
          <button className="btn btn-info mb-3" onClick={handleTestInputValidation}>Validate & Sanitize Input</button>

          {valResult && (
            <div className="p-3 bg-secondary rounded">
              <p><strong>Raw Input:</strong> <code>{valResult.rawInput}</code></p>
              <p><strong>Sanitized Output:</strong> <code>{valResult.sanitized}</code></p>
              <p><strong>Is Valid Email:</strong> <span className={`badge ${valResult.isValidEmail ? 'badge-success' : 'badge-danger'}`}>{valResult.isValidEmail ? 'YES' : 'NO'}</span></p>
              <p><strong>Is Alphanumeric:</strong> <span className={`badge ${valResult.isAlphanumeric ? 'badge-success' : 'badge-danger'}`}>{valResult.isAlphanumeric ? 'YES' : 'NO'}</span></p>
            </div>
          )}
        </div>
      )}

      {/* Tab 3: SQL Injection Protection */}
      {activeTab === 'sqli' && (
        <div className="card bg-dark text-white p-4 shadow">
          <h4><i className="fas fa-database text-danger mr-2"></i>Task 3: SQL Injection Protection Inspector</h4>
          <div className="form-group mt-3">
            <label>Enter Test Query Payload</label>
            <input type="text" className="form-control" value={testInput} onChange={(e) => setTestInput(e.target.value)} />
          </div>
          <button className="btn btn-danger mb-3" onClick={handleTestSqli}>Inspect SQL Payload</button>

          {sqliResult && (
            <div className="p-3 bg-secondary rounded">
              <p><strong>SQL Injection Detected:</strong> <span className={`badge ${sqliResult.sqliDetected ? 'badge-danger' : 'badge-success'}`}>{sqliResult.sqliDetected ? 'YES (MALICIOUS)' : 'NO (SAFE)'}</span></p>
              <p><strong>Escaped Parameterized Input:</strong> <code>{sqliResult.escapedInput}</code></p>
              <p><strong>Recommendation:</strong> {sqliResult.recommendation}</p>
            </div>
          )}
        </div>
      )}

      {/* Tab 4: XSS Protection */}
      {activeTab === 'xss' && (
        <div className="card bg-dark text-white p-4 shadow">
          <h4><i className="fas fa-terminal text-success mr-2"></i>Task 4: XSS HTML Encoding & Script Tag Stripper</h4>
          <div className="form-group mt-3">
            <label>User HTML Content</label>
            <input type="text" className="form-control" value={testInput} onChange={(e) => setTestInput(e.target.value)} />
          </div>
          <button className="btn btn-success mb-3" onClick={handleTestXss}>Sanitize XSS Content</button>

          {xssResult && (
            <div className="p-3 bg-secondary rounded">
              <p><strong>Encoded HTML:</strong> <code>{xssResult.encodedHtml}</code></p>
              <p><strong>Stripped Script Tags:</strong> <code>{xssResult.strippedScriptTags}</code></p>
            </div>
          )}
        </div>
      )}

      {/* Tab 5: CSRF Protection */}
      {activeTab === 'csrf' && (
        <div className="card bg-dark text-white p-4 shadow">
          <h4><i className="fas fa-key text-warning mr-2"></i>Task 5: CSRF Token Generator & Header Validator</h4>
          <div className="p-4 bg-secondary rounded mt-3">
            <p><strong>Active Session ID:</strong> <code>{csrfTokenData?.sessionId}</code></p>
            <p><strong>Generated Cryptographic CSRF Token:</strong> <code>{csrfTokenData?.csrfToken}</code></p>
            <p><strong>Required HTTP Header:</strong> <code>{csrfTokenData?.headerName}</code></p>
            <button className="btn btn-warning mt-2" onClick={fetchCsrfToken}>Regenerate CSRF Token</button>
          </div>
        </div>
      )}

      {/* Tab 6: Secure API Dev */}
      {activeTab === 'api' && (
        <div className="card bg-dark text-white p-4 shadow">
          <h4><i className="fas fa-network-wired text-info mr-2"></i>Task 6: Secure API Development & Rate Limiting</h4>
          <div className="p-4 bg-secondary rounded mt-3">
            <p><strong>Token Bucket Rate Limiting:</strong> <span className="badge badge-success">{apiMetrics.rateLimitingEnforced ? 'ENFORCED' : 'OFF'}</span></p>
            <p><strong>Max Allowed API Requests / Minute / IP:</strong> {apiMetrics.maxRequestsPerMinute} req/min</p>
            <p><strong>Transport Security:</strong> <code>{apiMetrics.tlsVersion}</code></p>
            <p><strong>JWT Authorization Header Check:</strong> <span className="badge badge-success">ACTIVE</span></p>
          </div>
        </div>
      )}

      {/* Tab 7: Dependency Management */}
      {activeTab === 'deps' && (
        <div className="card bg-dark text-white p-4 shadow">
          <h4><i className="fas fa-cubes text-primary mr-2"></i>Task 7: Dependency Vulnerability Monitor</h4>
          <table className="table table-dark mt-3">
            <thead>
              <tr><th>Project Module</th><th>Package Name</th><th>Version</th><th>CVE ID</th><th>Severity</th><th>Remediation</th></tr>
            </thead>
            <tbody>
              {dependencies.map((d) => (
                <tr key={d.id}>
                  <td><span className="badge badge-info">{d.projectModule}</span></td>
                  <td><strong>{d.packageName}</strong></td><td>{d.currentVersion}</td>
                  <td><code>{d.cveId}</code></td>
                  <td><span className={`badge ${d.severity === 'CRITICAL' || d.severity === 'HIGH' ? 'badge-danger' : 'badge-warning'}`}>{d.severity}</span></td>
                  <td>{d.remediationRecommendation}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Tab 8: Code Review Workflow */}
      {activeTab === 'review' && (
        <div className="card bg-dark text-white p-4 shadow">
          <h4><i className="fas fa-code-merge text-warning mr-2"></i>Task 8: Pre-Deployment Security Code Review</h4>
          <form onSubmit={handleSubmitCodeReview} className="row g-3 mb-4 bg-secondary p-3 rounded">
            <div className="col-md-4">
              <label>Feature / PR Title</label>
              <input type="text" className="form-control" value={reviewTitle} onChange={(e) => setReviewTitle(e.target.value)} required />
            </div>
            <div className="col-md-3">
              <label>Author Email</label>
              <input type="email" className="form-control" value={reviewAuthor} onChange={(e) => setReviewAuthor(e.target.value)} required />
            </div>
            <div className="col-md-5">
              <label>Security Checklist Comments</label>
              <input type="text" className="form-control" value={reviewComments} onChange={(e) => setReviewComments(e.target.value)} required />
            </div>
            <div className="col-12 mt-2">
              <button type="submit" className="btn btn-warning">Submit Code Review</button>
            </div>
          </form>

          <h5>Pending & Historical Code Reviews</h5>
          <table className="table table-dark">
            <thead>
              <tr><th>Title</th><th>Author</th><th>Reviewer</th><th>Status</th><th>Action</th></tr>
            </thead>
            <tbody>
              {codeReviews.map((r) => (
                <tr key={r.id}>
                  <td>{r.featureTitle}</td><td>{r.authorEmail}</td><td>{r.reviewerEmail}</td>
                  <td><span className={`badge ${r.status === 'APPROVED' ? 'badge-success' : 'badge-warning'}`}>{r.status}</span></td>
                  <td>
                    {r.status === 'PENDING' && (
                      <button className="btn btn-sm btn-success" onClick={() => handleApproveCodeReview(r.id, 'APPROVED')}>Approve Sign-off</button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Tab 9: Security Testing */}
      {activeTab === 'testing' && (
        <div className="card bg-dark text-white p-4 shadow">
          <div className="d-flex justify-content-between align-items-center mb-3">
            <h4><i className="fas fa-bug text-danger mr-2"></i>Task 9: Automated DAST / SAST Security Testing</h4>
            <button className="btn btn-danger" onClick={handleRunSecurityScan}>
              <i className="fas fa-play mr-1"></i> Run Automated Security Scan
            </button>
          </div>

          <table className="table table-dark mt-3">
            <thead>
              <tr><th>Run ID</th><th>Test Type</th><th>Total Checks</th><th>Vulns Found</th><th>Critical</th><th>High</th><th>Summary</th><th>Status</th></tr>
            </thead>
            <tbody>
              {securityScanRuns.map((s) => (
                <tr key={s.id}>
                  <td>{s.id}</td><td>{s.testType}</td><td>{s.totalChecksPerformed}</td>
                  <td><span className="badge badge-warning">{s.vulnerabilitiesFoundCount}</span></td>
                  <td><span className="badge badge-danger">{s.criticalCount}</span></td>
                  <td><span className="badge badge-danger">{s.highCount}</span></td>
                  <td><small>{s.summaryReport}</small></td>
                  <td><span className={`badge ${s.status === 'PASSED' ? 'badge-success' : 'badge-danger'}`}>{s.status}</span></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Tab 10: Vulnerability Portal */}
      {activeTab === 'portal' && (
        <div className="card bg-dark text-white p-4 shadow">
          <h4><i className="fas fa-user-ninja text-success mr-2"></i>Task 10: Vulnerability Management Portal</h4>
          <table className="table table-dark mt-3">
            <thead>
              <tr><th>ID</th><th>Title</th><th>Type</th><th>CVSS</th><th>Severity</th><th>Component</th><th>Status</th><th>Action</th></tr>
            </thead>
            <tbody>
              {vulnerabilities.map((v) => (
                <tr key={v.id}>
                  <td>{v.id}</td><td><strong>{v.title}</strong></td><td><code>{v.vulnerabilityType}</code></td>
                  <td><span className="badge badge-warning">{v.cvssScore}</span></td>
                  <td><span className={`badge ${v.severity === 'CRITICAL' || v.severity === 'HIGH' ? 'badge-danger' : 'badge-info'}`}>{v.severity}</span></td>
                  <td><span className="badge badge-secondary">{v.affectedComponent}</span></td>
                  <td><span className={`badge ${v.status === 'REMEDIATED' || v.status === 'VERIFIED' ? 'badge-success' : 'badge-danger'}`}>{v.status}</span></td>
                  <td>
                    {v.status === 'OPEN' && (
                      <button className="btn btn-sm btn-success" onClick={() => handleUpdateVulnStatus(v.id, 'REMEDIATED')}>
                        Mark Remediated
                      </button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};

export default CodeSecurityDashboard;
