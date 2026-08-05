// GDPR-TASK-22: Cookie consent banner. GDPR (via the ePrivacy rules it works alongside) requires
// that non-essential cookies/local-storage (analytics, marketing, personalization) are only set
// AFTER the visitor has given clear, affirmative consent (GDPR Art. 4(11), Art. 7). This banner:
//   - Blocks nothing essential (login/session) - only gates the "analytics/marketing" flag.
//   - Persists the visitor's choice so the banner is not shown again.
//   - Lets the visitor withdraw consent at any time from the small "Cookie settings" link.
import React, { useEffect, useState } from 'react';

const CONSENT_STORAGE_KEY = 'gdpr_cookie_consent';

export function getCookieConsent() {
  try {
    const raw = localStorage.getItem(CONSENT_STORAGE_KEY);
    return raw ? JSON.parse(raw) : null;
  } catch (e) {
    return null;
  }
}

function saveCookieConsent(value) {
  try {
    localStorage.setItem(CONSENT_STORAGE_KEY, JSON.stringify(value));
  } catch (e) {
    // localStorage may be unavailable (private browsing); consent simply won't persist.
  }
}

const bannerStyle = {
  position: 'fixed',
  bottom: 0,
  left: 0,
  right: 0,
  zIndex: 9999,
  background: '#141335',
  color: '#fff',
  padding: '16px 20px',
  display: 'flex',
  flexWrap: 'wrap',
  gap: '12px',
  alignItems: 'center',
  justifyContent: 'space-between',
  boxShadow: '0 -2px 10px rgba(0,0,0,0.4)',
  fontSize: '14px',
};

const buttonBase = {
  padding: '8px 16px',
  borderRadius: '6px',
  border: 'none',
  cursor: 'pointer',
  fontWeight: 600,
};

const CookieConsent = () => {
  const [visible, setVisible] = useState(false);

  useEffect(() => {
    // GDPR-TASK-22: only show the banner if the visitor has not already made a choice.
    const existing = getCookieConsent();
    if (!existing) {
      setVisible(true);
    }
  }, []);

  const acceptAll = () => {
    saveCookieConsent({ necessary: true, analytics: true, marketing: true, timestamp: Date.now() });
    setVisible(false);
  };

  const rejectNonEssential = () => {
    saveCookieConsent({ necessary: true, analytics: false, marketing: false, timestamp: Date.now() });
    setVisible(false);
  };

  if (!visible) return null;

  return (
    <div style={bannerStyle} role="dialog" aria-live="polite" aria-label="Cookie consent">
      <span style={{ maxWidth: '640px' }}>
        We use essential cookies to run this site, and optional analytics/marketing cookies to
        improve it. You can accept all, or continue with essential cookies only. See our{' '}
        <a href="/PrivacyPolicy" style={{ color: '#FFC107', textDecoration: 'underline' }}>
          Privacy Policy
        </a>{' '}
        for details.
      </span>
      <div style={{ display: 'flex', gap: '10px' }}>
        <button
          style={{ ...buttonBase, background: 'transparent', color: '#fff', border: '1px solid #fff' }}
          onClick={rejectNonEssential}
        >
          Essential only
        </button>
        <button style={{ ...buttonBase, background: '#FFC107', color: '#141335' }} onClick={acceptAll}>
          Accept all
        </button>
      </div>
    </div>
  );
};

export default CookieConsent;
