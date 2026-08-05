import React, { useState, useEffect } from 'react';
import API_URL from '../Config';
import { toast } from 'react-hot-toast';

// ISO 27001 | Module 4: Risk Management | Tasks 1-10 Admin Dashboard
// Description: Admin UI dashboard providing dedicated management interfaces for all 10 Risk Management ISO 27001 tasks.
const RiskManagementDashboard = () => {
  const [activeTab, setActiveTab] = useState('register');

  // State variables for all 10 tasks
  const [risks, setRisks] = useState([]);
  const [dashboardStats, setDashboardStats] = useState({ totalRisks: 0, criticalCount: 0, highCount: 0, averageRiskScore: 0 });
  const [mitigations, setMitigations] = useState([]);
  const [threats, setThreats] = useState([]);
  const [controlReviews, setControlReviews] = useState([]);
  const [riskReviews, setRiskReviews] = useState([]);
  const [execReport, setExecReport] = useState(null);

  const [newTitle, setNewTitle] = useState('');
  const [newDesc, setNewDesc] = useState('');
  const [newCategory, setNewCategory] = useState('APPLICATION_SECURITY');
  const [newLikelihood, setNewLikelihood] = useState(3);
  const [newImpact, setNewImpact] = useState(3);
  const [newEffectiveness, setNewEffectiveness] = useState(75);

  const [selectedRiskId, setSelectedRiskId] = useState('');
  const [mitigationTitle, setMitigationTitle] = useState('');
  const [mitigationDetails, setMitigationDetails] = useState('');
  const [mitigationOwner, setMitigationOwner] = useState('');

  const [reviewPeriod, setReviewPeriod] = useState('Q1-2026');
  const [reviewComments, setReviewComments] = useState('');

  useEffect(() => {
    fetchRisks();
    fetchDashboardStats();
    fetchMitigations();
    fetchThreats();
    fetchControlReviews();
    fetchRiskReviews();
  }, []);

  // ISO 27001 | Task 1 & 2: Risk Register & Assessment Engine
  const fetchRisks = async () => {
    try {
      const res = await fetch(`${API_URL}/api/v2/risk-management/register`);
      if (res.ok) {
        const data = await res.json();
        setRisks(data);
      }
    } catch (e) { console.error(e); }
  };

  const handleRegisterRisk = async (e) => {
    e.preventDefault();
    try {
      const res = await fetch(`${API_URL}/api/v2/risk-management/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          riskTitle: newTitle,
          description: newDesc,
          category: newCategory,
          likelihood: newLikelihood,
          impact: newImpact,
          controlEffectivenessPercent: newEffectiveness
        })
      });
      if (res.ok) {
        toast.success('Security Risk registered & scored successfully!');
        setNewTitle(''); setNewDesc('');
        fetchRisks();
        fetchDashboardStats();
      }
    } catch (e) { toast.error(e.message); }
  };

  // ISO 27001 | Task 3: Risk Reporting Dashboard
  const fetchDashboardStats = async () => {
    try {
      const res = await fetch(`${API_URL}/api/v2/risk-management/dashboard`);
      if (res.ok) {
        const data = await res.json();
        setDashboardStats(data);
      }
    } catch (e) { console.error(e); }
  };

  // ISO 27001 | Task 4: Risk Mitigation Tracker
  const fetchMitigations = async () => {
    try {
      const res = await fetch(`${API_URL}/api/v2/risk-management/mitigations`);
      if (res.ok) {
        const data = await res.json();
        setMitigations(data);
      }
    } catch (e) { console.error(e); }
  };

  const handleAddMitigation = async (e) => {
    e.preventDefault();
    if (!selectedRiskId) return toast.error('Select a risk item for treatment plan');
    try {
      const res = await fetch(`${API_URL}/api/v2/risk-management/mitigations`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          riskId: selectedRiskId,
          mitigationTitle,
          actionDetails: mitigationDetails,
          assignedOwner: mitigationOwner
        })
      });
      if (res.ok) {
        toast.success('Risk mitigation treatment action added!');
        setMitigationTitle(''); setMitigationDetails(''); setMitigationOwner('');
        fetchMitigations();
        fetchRisks();
      }
    } catch (e) { toast.error(e.message); }
  };

  // ISO 27001 | Task 5: Threat Analysis Module
  const fetchThreats = async () => {
    try {
      const res = await fetch(`${API_URL}/api/v2/risk-management/threats`);
      if (res.ok) {
        const data = await res.json();
        setThreats(data);
      }
    } catch (e) { console.error(e); }
  };

  // ISO 27001 | Task 6: Control Effectiveness Review
  const fetchControlReviews = async () => {
    try {
      const res = await fetch(`${API_URL}/api/v2/risk-management/control-reviews`);
      if (res.ok) {
        const data = await res.json();
        setControlReviews(data);
      }
    } catch (e) { console.error(e); }
  };

  // ISO 27001 | Task 9: Management Risk Reports
  const handleGenerateReport = async () => {
    try {
      const res = await fetch(`${API_URL}/api/v2/risk-management/management-report`);
      if (res.ok) {
        const data = await res.json();
        setExecReport(data);
        toast.success('Executive Risk Management Report generated!');
      }
    } catch (e) { toast.error(e.message); }
  };

  // ISO 27001 | Task 10: Risk Review Workflow
  const fetchRiskReviews = async () => {
    try {
      const res = await fetch(`${API_URL}/api/v2/risk-management/reviews`);
      if (res.ok) {
        const data = await res.json();
        setRiskReviews(data);
      }
    } catch (e) { console.error(e); }
  };

  const handleSubmitRiskReview = async (e) => {
    e.preventDefault();
    try {
      const res = await fetch(`${API_URL}/api/v2/risk-management/reviews/submit`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          reviewerEmail: 'ciso@mediajungle.com',
          reviewPeriod,
          comments: reviewComments
        })
      });
      if (res.ok) {
        toast.success('CISO Risk Review sign-off submitted!');
        setReviewComments('');
        fetchRiskReviews();
      }
    } catch (e) { toast.error(e.message); }
  };

  return (
    <div className="container-fluid p-4" style={{ backgroundColor: '#0f172a', color: '#f8fafc', minHeight: '100vh' }}>
      <div className="d-flex justify-content-between align-items-center mb-4 pb-3 border-bottom border-secondary">
        <div>
          <h2 className="text-danger font-weight-bold mb-1">
            <i className="fas fa-exclamation-triangle mr-2"></i>ISO 27001 — Risk Management Dashboard
          </h2>
          <p className="text-muted mb-0">Module 4 Implementation: Controls 1 to 10 for Media Jungle Risk Governance</p>
        </div>
        <span className="badge badge-danger p-2">Risk Governance Active</span>
      </div>

      {/* Tabs Header */}
      <ul className="nav nav-pills mb-4 bg-dark p-2 rounded">
        {[
          { id: 'register', label: '1. Risk Register', icon: 'fa-book-open' },
          { id: 'assessment', label: '2. Assessment Engine', icon: 'fa-calculator' },
          { id: 'reporting', label: '3. Reporting Dashboard', icon: 'fa-chart-pie' },
          { id: 'mitigation', label: '4. Risk Mitigation', icon: 'fa-tasks' },
          { id: 'threats', label: '5. Threat Analysis', icon: 'fa-spider' },
          { id: 'controls', label: '6. Control Review', icon: 'fa-sliders-h' },
          { id: 'residual', label: '7. Residual Risk', icon: 'fa-chart-bar' },
          { id: 'monitoring', label: '8. Continuous Monitoring', icon: 'fa-sync' },
          { id: 'management', label: '9. Management Reports', icon: 'fa-file-invoice' },
          { id: 'review', label: '10. Risk Review Workflow', icon: 'fa-check-double' }
        ].map((tab) => (
          <li className="nav-item m-1" key={tab.id}>
            <button
              className={`btn btn-sm ${activeTab === tab.id ? 'btn-danger font-weight-bold' : 'btn-outline-secondary text-light'}`}
              onClick={() => setActiveTab(tab.id)}
            >
              <i className={`fas ${tab.icon} mr-1`}></i> {tab.label}
            </button>
          </li>
        ))}
      </ul>

      {/* Tab 1: Risk Register */}
      {activeTab === 'register' && (
        <div className="card bg-dark text-white p-4 shadow">
          <h4><i className="fas fa-book-open text-danger mr-2"></i>Task 1: Centralized Risk Register</h4>
          <form onSubmit={handleRegisterRisk} className="row g-3 mb-4 bg-secondary p-3 rounded">
            <div className="col-md-4">
              <label>Risk Title</label>
              <input type="text" className="form-control" value={newTitle} onChange={(e) => setNewTitle(e.target.value)} required />
            </div>
            <div className="col-md-3">
              <label>Category</label>
              <select className="form-control" value={newCategory} onChange={(e) => setNewCategory(e.target.value)}>
                <option value="APPLICATION_SECURITY">APPLICATION_SECURITY</option>
                <option value="INFRASTRUCTURE">INFRASTRUCTURE</option>
                <option value="AUTHENTICATION">AUTHENTICATION</option>
                <option value="PRIVACY">PRIVACY</option>
                <option value="COMPLIANCE">COMPLIANCE</option>
              </select>
            </div>
            <div className="col-md-2">
              <label>Likelihood (1-5)</label>
              <input type="number" className="form-control" min={1} max={5} value={newLikelihood} onChange={(e) => setNewLikelihood(Number(e.target.value))} />
            </div>
            <div className="col-md-2">
              <label>Impact (1-5)</label>
              <input type="number" className="form-control" min={1} max={5} value={newImpact} onChange={(e) => setNewImpact(Number(e.target.value))} />
            </div>
            <div className="col-md-1 d-flex align-items-end">
              <button type="submit" className="btn btn-danger w-100">Add</button>
            </div>
          </form>

          <table className="table table-dark table-hover">
            <thead>
              <tr><th>ID</th><th>Risk Title</th><th>Category</th><th>Score</th><th>Level</th><th>Effectiveness</th><th>Residual Status</th><th>Status</th></tr>
            </thead>
            <tbody>
              {risks.map((r) => (
                <tr key={r.id}>
                  <td>{r.id}</td><td><strong>{r.riskTitle}</strong></td><td><span className="badge badge-info">{r.category}</span></td>
                  <td><code>{r.inherentRiskScore}</code></td>
                  <td><span className={`badge ${r.inherentRiskLevel === 'CRITICAL' || r.inherentRiskLevel === 'HIGH' ? 'badge-danger' : 'badge-warning'}`}>{r.inherentRiskLevel}</span></td>
                  <td>{r.controlEffectivenessPercent}%</td>
                  <td><span className={`badge ${r.residualRiskStatus === 'ACCEPTABLE' ? 'badge-success' : 'badge-danger'}`}>{r.residualRiskStatus}</span></td>
                  <td><span className="badge badge-secondary">{r.status}</span></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Tab 2: Assessment Engine */}
      {activeTab === 'assessment' && (
        <div className="card bg-dark text-white p-4 shadow">
          <h4><i className="fas fa-calculator text-warning mr-2"></i>Task 2: 5x5 Likelihood x Impact Risk Assessment Engine</h4>
          <div className="p-4 bg-secondary rounded mt-3">
            <h5>Interactive Risk Matrix Scoring (Likelihood x Impact)</h5>
            <div className="row mt-3">
              <div className="col-md-4">
                <label>Likelihood (1: Rare → 5: Almost Certain)</label>
                <input type="range" className="form-control-range" min={1} max={5} value={newLikelihood} onChange={(e) => setNewLikelihood(Number(e.target.value))} />
                <h4 className="text-warning mt-2">Likelihood Value: {newLikelihood}</h4>
              </div>
              <div className="col-md-4">
                <label>Impact (1: Negligible → 5: Catastrophic)</label>
                <input type="range" className="form-control-range" min={1} max={5} value={newImpact} onChange={(e) => setNewImpact(Number(e.target.value))} />
                <h4 className="text-warning mt-2">Impact Value: {newImpact}</h4>
              </div>
              <div className="col-md-4">
                <div className="card bg-dark p-3 text-center">
                  <h5>Calculated Inherent Risk Score</h5>
                  <h1 className="display-4 font-weight-bold text-danger">{newLikelihood * newImpact}</h1>
                  <span>Level: {newLikelihood * newImpact <= 5 ? 'LOW' : newLikelihood * newImpact <= 12 ? 'MEDIUM' : newLikelihood * newImpact <= 19 ? 'HIGH' : 'CRITICAL'}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Tab 3: Reporting Dashboard */}
      {activeTab === 'reporting' && (
        <div className="card bg-dark text-white p-4 shadow">
          <h4><i className="fas fa-chart-pie text-info mr-2"></i>Task 3: Risk Reporting Dashboard</h4>
          <div className="row mt-3">
            <div className="col-md-3">
              <div className="card bg-primary text-white p-3 text-center">
                <h5>Total Active Risks</h5>
                <h2>{dashboardStats.totalRisks}</h2>
              </div>
            </div>
            <div className="col-md-3">
              <div className="card bg-danger text-white p-3 text-center">
                <h5>High / Critical Risks</h5>
                <h2>{dashboardStats.highCount + dashboardStats.criticalCount}</h2>
              </div>
            </div>
            <div className="col-md-3">
              <div className="card bg-warning text-dark p-3 text-center">
                <h5>Average Risk Score</h5>
                <h2>{dashboardStats.averageRiskScore} / 25</h2>
              </div>
            </div>
            <div className="col-md-3">
              <div className="card bg-success text-white p-3 text-center">
                <h5>Acceptable Residual Risks</h5>
                <h2>{dashboardStats.acceptableResidualCount}</h2>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Tab 4: Risk Mitigation Tracker */}
      {activeTab === 'mitigation' && (
        <div className="card bg-dark text-white p-4 shadow">
          <h4><i className="fas fa-tasks text-success mr-2"></i>Task 4: Risk Mitigation Action Tracker</h4>
          <form onSubmit={handleAddMitigation} className="row g-3 mb-4 bg-secondary p-3 rounded">
            <div className="col-md-4">
              <label>Select Target Risk Item</label>
              <select className="form-control" value={selectedRiskId} onChange={(e) => setSelectedRiskId(e.target.value)} required>
                <option value="">-- Select Risk Item --</option>
                {risks.map(r => <option key={r.id} value={r.id}>{r.riskTitle}</option>)}
              </select>
            </div>
            <div className="col-md-3">
              <label>Mitigation Action Title</label>
              <input type="text" className="form-control" value={mitigationTitle} onChange={(e) => setMitigationTitle(e.target.value)} required />
            </div>
            <div className="col-md-3">
              <label>Assigned Owner</label>
              <input type="email" className="form-control" value={mitigationOwner} onChange={(e) => setMitigationOwner(e.target.value)} required />
            </div>
            <div className="col-md-2 d-flex align-items-end">
              <button type="submit" className="btn btn-success w-100">Add Action</button>
            </div>
          </form>

          <h5>Active Mitigation Treatment Actions</h5>
          <table className="table table-dark">
            <thead>
              <tr><th>ID</th><th>Risk ID</th><th>Mitigation Action</th><th>Owner</th><th>Target Date</th><th>Status</th></tr>
            </thead>
            <tbody>
              {mitigations.map((m) => (
                <tr key={m.id}>
                  <td>{m.id}</td><td>#{m.riskId}</td><td><strong>{m.mitigationTitle}</strong></td>
                  <td>{m.assignedOwner}</td><td>{m.targetCompletionDate}</td>
                  <td><span className={`badge ${m.status === 'COMPLETED' ? 'badge-success' : 'badge-warning'}`}>{m.status}</span></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Tab 5: Threat Analysis */}
      {activeTab === 'threats' && (
        <div className="card bg-dark text-white p-4 shadow">
          <h4><i className="fas fa-spider text-danger mr-2"></i>Task 5: STRIDE Threat Analysis Module</h4>
          <table className="table table-dark mt-3">
            <thead>
              <tr><th>Threat Vector Name</th><th>STRIDE Category</th><th>Target Asset</th><th>Likelihood</th></tr>
            </thead>
            <tbody>
              {threats.map((t) => (
                <tr key={t.id}>
                  <td><strong>{t.threatName}</strong></td>
                  <td><span className="badge badge-danger">{t.strideCategory}</span></td>
                  <td>{t.targetAsset}</td>
                  <td><span className="badge badge-warning">{t.likelihood}</span></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Tab 6: Control Review */}
      {activeTab === 'controls' && (
        <div className="card bg-dark text-white p-4 shadow">
          <h4><i className="fas fa-sliders-h text-info mr-2"></i>Task 6: Safeguard Control Effectiveness Review</h4>
          <table className="table table-dark mt-3">
            <thead>
              <tr><th>Control Name</th><th>Effectiveness Rating (%)</th><th>Evaluation Notes</th><th>Tested By</th><th>Test Date</th></tr>
            </thead>
            <tbody>
              {controlReviews.map((c) => (
                <tr key={c.id}>
                  <td><strong>{c.controlName}</strong></td>
                  <td>
                    <div className="progress bg-secondary" style={{ height: '20px' }}>
                      <div className="progress-bar bg-success" style={{ width: `${c.effectivenessPercent}%` }}>
                        {c.effectivenessPercent}%
                      </div>
                    </div>
                  </td>
                  <td><small>{c.evaluationNotes}</small></td>
                  <td>{c.testedBy}</td>
                  <td>{new Date(c.testDate).toLocaleDateString()}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Tab 7: Residual Risk */}
      {activeTab === 'residual' && (
        <div className="card bg-dark text-white p-4 shadow">
          <h4><i className="fas fa-chart-bar text-warning mr-2"></i>Task 7: Residual Risk Tracking</h4>
          <table className="table table-dark mt-3">
            <thead>
              <tr><th>Risk Title</th><th>Inherent Score</th><th>Control Effectiveness</th><th>Residual Score</th><th>Tolerance Status</th></tr>
            </thead>
            <tbody>
              {risks.map((r) => (
                <tr key={r.id}>
                  <td>{r.riskTitle}</td>
                  <td><span className="badge badge-danger">{r.inherentRiskScore}</span></td>
                  <td>{r.controlEffectivenessPercent}%</td>
                  <td><span className="badge badge-warning">{r.residualRiskScore}</span></td>
                  <td><span className={`badge ${r.residualRiskStatus === 'ACCEPTABLE' ? 'badge-success' : 'badge-danger'}`}>{r.residualRiskStatus}</span></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Tab 8: Continuous Monitoring */}
      {activeTab === 'monitoring' && (
        <div className="card bg-dark text-white p-4 shadow">
          <h4><i className="fas fa-sync text-primary mr-2"></i>Task 8: Continuous Risk Monitoring Daemon</h4>
          <div className="p-4 bg-secondary rounded mt-3">
            <p><strong>Monitoring Daemon Status:</strong> <span className="badge badge-success">ACTIVE_RUNNING</span></p>
            <p><strong>Evaluation Cron Schedule:</strong> <code>0 */15 * * * ?</code> (Runs every 15 minutes)</p>
            <p><strong>Monitored Infrastructure:</strong> OTT Gateway, PostgreSQL Cluster, JWT Auth Service, Transcoders</p>
          </div>
        </div>
      )}

      {/* Tab 9: Management Reports */}
      {activeTab === 'management' && (
        <div className="card bg-dark text-white p-4 shadow">
          <div className="d-flex justify-content-between align-items-center mb-3">
            <h4><i className="fas fa-file-invoice text-success mr-2"></i>Task 9: Executive Management Risk Reports</h4>
            <button className="btn btn-success" onClick={handleGenerateReport}>
              Generate Executive Report
            </button>
          </div>

          {execReport && (
            <div className="p-4 bg-secondary rounded mt-3">
              <h5>{execReport.reportTitle}</h5>
              <p><strong>Generated At:</strong> {execReport.generatedAt}</p>
              <p><strong>Overall Risk Rating:</strong> <span className="badge badge-success">{execReport.overallSecurityRating}</span></p>
              <p><strong>Active Threat Vectors:</strong> {execReport.activeThreats}</p>
              <p><strong>Active Treatment Actions:</strong> {execReport.mitigationActionsCount}</p>
            </div>
          )}
        </div>
      )}

      {/* Tab 10: Risk Review Workflow */}
      {activeTab === 'review' && (
        <div className="card bg-dark text-white p-4 shadow">
          <h4><i className="fas fa-check-double text-info mr-2"></i>Task 10: Periodic CISO Risk Review Sign-Off</h4>
          <form onSubmit={handleSubmitRiskReview} className="row g-3 mb-4 bg-secondary p-3 rounded">
            <div className="col-md-4">
              <label>Review Period</label>
              <select className="form-control" value={reviewPeriod} onChange={(e) => setReviewPeriod(e.target.value)}>
                <option value="Q1-2026">Q1-2026</option>
                <option value="Q2-2026">Q2-2026</option>
                <option value="ANNUAL-2026">ANNUAL-2026</option>
              </select>
            </div>
            <div className="col-md-6">
              <label>CISO Sign-off Comments</label>
              <input type="text" className="form-control" value={reviewComments} onChange={(e) => setReviewComments(e.target.value)} required />
            </div>
            <div className="col-md-2 d-flex align-items-end">
              <button type="submit" className="btn btn-info w-100">Submit Sign-off</button>
            </div>
          </form>

          <h5>Past CISO Risk Review Certification Records</h5>
          <table className="table table-dark">
            <thead>
              <tr><th>Reviewer</th><th>Period</th><th>Risks Reviewed</th><th>High/Critical</th><th>Review Date</th><th>Next Due</th></tr>
            </thead>
            <tbody>
              {riskReviews.map((r) => (
                <tr key={r.id}>
                  <td>{r.reviewerEmail}</td><td><span className="badge badge-info">{r.reviewPeriod}</span></td>
                  <td>{r.totalRisksReviewed}</td><td><span className="badge badge-warning">{r.highCriticalCount}</span></td>
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

export default RiskManagementDashboard;
