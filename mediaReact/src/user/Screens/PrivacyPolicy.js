// GDPR-TASK-25: real Privacy Policy content covering the data-subject rights required by GDPR
// Art. 12-22 (transparency, access, rectification, erasure, portability, objection) and the
// lawful bases for processing (Art. 6/7), replacing the previous placeholder page.
import React from 'react'
import Layout from '../Layout/Layout'

const section = { marginBottom: '28px' }
const heading = { color: '#FFC107', marginBottom: '8px' }

const PrivacyPolicy = () => {
  return (
    <Layout>
      <div
        className='mx-auto min-h-screen px-10 py-10 text-white'
        style={{
          background: 'linear-gradient(to bottom, #141335, #0c0d1a)',
          lineHeight: 1.6,
        }}
      >
        <h1 style={{ fontSize: '32px', marginBottom: '20px' }}>Privacy Policy</h1>
        <p style={{ marginBottom: '24px', opacity: 0.8 }}>
          Last updated: this notice explains how we collect, use, and protect your personal data
          in accordance with the General Data Protection Regulation (GDPR).
        </p>

        <div style={section}>
          <h2 style={heading}>1. What we collect</h2>
          <p>
            When you register, we collect your username, email address, mobile number, and an
            optional profile picture. When you use the service we also store your watch-later
            list, favourites, and subscription/payment status.
          </p>
        </div>

        <div style={section}>
          <h2 style={heading}>2. Why we process your data (lawful basis)</h2>
          <p>
            We process account data based on your consent, given at registration, and because it
            is necessary to perform our contract with you (providing the streaming service).
            Marketing emails are only sent if you separately opt in, and you can withdraw that
            consent at any time.
          </p>
        </div>

        <div style={section}>
          <h2 style={heading}>3. Your rights</h2>
          <ul style={{ listStyle: 'disc', paddingLeft: '24px' }}>
            <li><b>Right to access</b> - download a copy of the personal data we hold on you.</li>
            <li><b>Right to rectification</b> - correct inaccurate data from your profile page.</li>
            <li><b>Right to erasure</b> - request deletion ("right to be forgotten") of your account.</li>
            <li><b>Right to data portability</b> - receive your data in a machine-readable format.</li>
            <li><b>Right to withdraw consent</b> - opt out of marketing at any time.</li>
            <li><b>Right to object / restrict processing</b> - contact us using the details below.</li>
          </ul>
          <p style={{ marginTop: '10px' }}>
            You can exercise the access, erasure, and consent-withdrawal rights yourself from your{' '}
            <a href="/ViewProfile" style={{ color: '#FFC107', textDecoration: 'underline' }}>
              Profile
            </a>{' '}
            page under &ldquo;Privacy &amp; Your Data&rdquo;.
          </p>
        </div>

        <div style={section}>
          <h2 style={heading}>4. Cookies</h2>
          <p>
            We use essential cookies required for login and playback, and optional
            analytics/marketing cookies which are only set once you accept them in the cookie
            banner. You can change your choice at any time by clearing your browser's local
            storage for this site.
          </p>
        </div>

        <div style={section}>
          <h2 style={heading}>5. How long we keep your data</h2>
          <p>
            We keep account data for as long as your account is active. Abandoned registrations
            that never confirm consent are automatically anonymised after 30 days. Audit records
            of privacy-related actions are retained for up to 2 years for accountability.
          </p>
        </div>

        <div style={section}>
          <h2 style={heading}>6. Contact us</h2>
          <p>
            For any privacy questions or to exercise a right not covered by the self-service
            tools above, contact our Data Protection team at{' '}
            <a href="mailto:privacy@mediajungle.example" style={{ color: '#FFC107' }}>
              privacy@mediajungle.example
            </a>.
          </p>
        </div>
      </div>
    </Layout>
  )
}

export default PrivacyPolicy
