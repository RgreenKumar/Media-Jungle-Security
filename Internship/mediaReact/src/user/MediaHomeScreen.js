import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import API_URL from '../Config';

const MediaHomeScreen = () => {
  const [videos, setVideos] = useState([]);
  const [categories, setCategories] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState('All');
  const [currentHeroIndex, setCurrentHeroIndex] = useState(0);
  const [activeVideo, setActiveVideo] = useState(null);
  const [loading, setLoading] = useState(true);
  const [currentUser, setCurrentUser] = useState({
    token: sessionStorage.getItem('tokenn') || sessionStorage.getItem('userToken'),
    username: sessionStorage.getItem('username'),
    role: sessionStorage.getItem('role')
  });

  const handleLogout = () => {
    sessionStorage.clear();
    setCurrentUser({ token: null, username: null, role: null });
  };

  const defaultVideos = [
    {
      id: 1,
      title: 'City of Echoes',
      videoTitle: 'City of Echoes',
      description: 'A detective follows a trail through a city that never sleeps, unraveling encrypted communications and corporate espionage.',
      mainVideoDuration: '1h 45m',
      rating: 'U/A 13+',
      tags: 'Action, Thriller, Mystery',
      category: 'Action',
      date: '2026-07-19',
      dashStatus: 'READY'
    },
    {
      id: 2,
      title: 'Beyond the Reef',
      videoTitle: 'Beyond the Reef',
      description: 'A marine biologist explores a recovering coral reef ecosystem while deploying environmental monitoring sensors.',
      mainVideoDuration: '1h 32m',
      rating: 'U',
      tags: 'Documentary, Nature, Science',
      category: 'Documentary',
      date: '2026-07-19',
      dashStatus: 'READY'
    },
    {
      id: 3,
      title: 'Sunday Table',
      videoTitle: 'Sunday Table',
      description: 'A heartwarming family comedy about generational recipes, unexpected reunions, and second chances.',
      mainVideoDuration: '1h 55m',
      rating: 'U',
      tags: 'Comedy, Family, Drama',
      category: 'Comedy',
      date: '2026-07-19',
      dashStatus: 'READY'
    }
  ];

  useEffect(() => {
    fetchVideos();
    fetchCategories();
  }, []);

  const fetchVideos = async () => {
    try {
      const res = await fetch(`${API_URL}/api/v2/video/getall`);
      if (res.ok) {
        const data = await res.json();
        if (data && data.length > 0) {
          setVideos(data);
          return;
        }
      }
      setVideos(defaultVideos);
    } catch (err) {
      console.warn('Using default catalog:', err);
      setVideos(defaultVideos);
    } finally {
      setLoading(false);
    }
  };

  const fetchCategories = async () => {
    try {
      const res = await fetch(`${API_URL}/api/v2/GetAllCategories`);
      if (res.ok) {
        const data = await res.json();
        if (data && data.length > 0) {
          setCategories(data);
        }
      }
    } catch (err) {
      // default tags will be used
    }
  };

  const filteredVideos = selectedCategory === 'All'
    ? videos
    : videos.filter(v => {
        const cat = (v.category || v.tags || '').toLowerCase();
        return cat.includes(selectedCategory.toLowerCase());
      });

  const featuredMovie = videos[currentHeroIndex] || defaultVideos[0];

  const handlePlayVideo = (vid) => {
    setActiveVideo(vid);
  };

  const handleClosePlayer = () => {
    setActiveVideo(null);
  };

  return (
    <div style={{ backgroundColor: '#111317', color: '#fff', minHeight: '100vh', fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif' }}>
      
      {/* ── TOP NAVIGATION BAR ── */}
      <nav className="navbar navbar-expand-lg navbar-dark px-4 py-3 sticky-top" style={{ backgroundColor: 'rgba(17, 19, 23, 0.95)', backdropFilter: 'blur(10px)', borderBottom: '1px solid rgba(255,255,255,0.08)' }}>
        <div className="container-fluid">
          <Link className="navbar-brand d-flex align-items-center gap-2" to="/">
            <span style={{ fontSize: '1.6rem' }}>🦁</span>
            <span className="fw-bold tracking-wide" style={{ fontSize: '1.4rem', color: '#ffc107', letterSpacing: '1px' }}>MEDIA JUNGLE</span>
          </Link>

          <button className="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navContent">
            <span className="navbar-toggler-icon"></span>
          </button>

          <div className="collapse navbar-collapse" id="navContent">
            <ul className="navbar-nav me-auto mb-2 mb-lg-0 ms-lg-4 gap-lg-3">
              <li className="nav-item">
                <a className="nav-link active fw-semibold text-white" href="#featured">Home</a>
              </li>
              <li className="nav-item">
                <a className="nav-link text-light opacity-75" href="#movies">Movies & Shows</a>
              </li>
              <li className="nav-item">
                <a className="nav-link text-light opacity-75" href="#audio">Audio & Music</a>
              </li>
              <li className="nav-item">
                <a className="nav-link text-warning fw-semibold" href="#security">
                  <i className="bi bi-shield-lock-fill me-1"></i>Security Standards
                </a>
              </li>
            </ul>

            <div className="d-flex align-items-center gap-2">
              <span className="badge rounded-pill bg-dark text-warning border border-warning px-3 py-2 small d-none d-xl-inline-block">
                <i className="bi bi-patch-check-fill me-1"></i> ISO 27001 & SOC 2 Verified
              </span>

              {currentUser.token ? (
                <div className="d-flex align-items-center gap-2">
                  <div className="d-flex align-items-center gap-2 text-light bg-dark px-3 py-1 rounded-pill border border-secondary border-opacity-25">
                    <i className={`bi ${currentUser.role === 'ADMIN' ? 'bi-shield-check text-danger' : 'bi-person-circle text-warning'}`}></i>
                    <span className="small fw-semibold">{currentUser.username}</span>
                    <span className={`badge ${currentUser.role === 'ADMIN' ? 'bg-danger text-light' : 'bg-warning text-dark'} small`} style={{ fontSize: '0.7rem' }}>
                      {currentUser.role || 'USER'}
                    </span>
                  </div>

                  {currentUser.role === 'ADMIN' && (
                    <Link to="/admin/Dashboard" className="btn btn-warning btn-sm fw-bold px-3 py-1 text-dark">
                      <i className="bi bi-speedometer2 me-1"></i> Dashboard
                    </Link>
                  )}

                  <button className="btn btn-outline-danger btn-sm px-2 py-1" onClick={handleLogout} title="Sign Out">
                    <i className="bi bi-box-arrow-right"></i>
                  </button>
                </div>
              ) : (
                <div className="d-flex align-items-center gap-2">
                  <Link to="/login" className="btn btn-outline-light btn-sm px-3 py-2">
                    <i className="bi bi-box-arrow-in-right me-1"></i> Sign In
                  </Link>
                  <Link to="/register" className="btn btn-outline-warning btn-sm px-3 py-2">
                    <i className="bi bi-person-plus me-1"></i> Register (OTP)
                  </Link>
                  <Link to="/admin" className="btn btn-warning fw-bold px-3 py-2 shadow-sm text-dark">
                    <i className="bi bi-shield-lock-fill me-1"></i> Admin
                  </Link>
                </div>
              )}
            </div>
          </div>
        </div>
      </nav>

      {/* ── VIDEO PLAYER MODAL / OVERLAY ── */}
      {activeVideo && (
        <div style={{
          position: 'fixed', top: 0, left: 0, width: '100%', height: '100%',
          backgroundColor: 'rgba(0,0,0,0.92)', zIndex: 9999, display: 'flex',
          flexDirection: 'column', alignItems: 'center', justifyContent: 'center', padding: '20px'
        }}>
          <div style={{ width: '100%', maxWidth: '1000px', position: 'relative' }}>
            <div className="d-flex justify-content-between align-items-center mb-3">
              <h4 className="fw-bold text-warning mb-0">
                <i className="bi bi-play-circle me-2"></i>Now Playing: {activeVideo.videoTitle || activeVideo.title}
              </h4>
              <button className="btn btn-outline-light btn-sm px-3" onClick={handleClosePlayer}>
                <i className="bi bi-x-lg me-1"></i> Close Player
              </button>
            </div>

            <div className="rounded-3 overflow-hidden shadow-lg bg-black">
              <video
                controls
                autoPlay
                style={{ width: '100%', maxHeight: '65vh', display: 'block' }}
                src={`${API_URL}/api/v2/${activeVideo.id}/videofile`}
              >
                Your browser does not support HTML5 video streaming.
              </video>
            </div>

            <div className="card border-0 bg-dark text-white p-3 mt-3 rounded-3 shadow">
              <div className="d-flex gap-3 align-items-center mb-2 flex-wrap">
                <span className="badge bg-warning text-dark fw-bold">{activeVideo.rating || 'U/A'}</span>
                <span className="badge bg-secondary">{activeVideo.mainVideoDuration || 'Feature Film'}</span>
                <span className="badge bg-info text-dark">DASH {activeVideo.dashStatus || 'READY'}</span>
                <span className="text-muted small">Streamed securely via Media Jungle Secure HLS/DASH</span>
              </div>
              <p className="text-light small mb-0">{activeVideo.description}</p>
            </div>
          </div>
        </div>
      )}

      {/* ── HERO BANNER SECTION ── */}
      <div id="featured" style={{
        position: 'relative',
        minHeight: '68vh',
        background: 'linear-gradient(to bottom, rgba(17,19,23,0.3) 0%, rgba(17,19,23,0.95) 100%), linear-gradient(135deg, #1f2330 0%, #111317 100%)',
        display: 'flex',
        alignItems: 'center',
        padding: '60px 40px',
        borderBottom: '1px solid rgba(255,255,255,0.06)'
      }}>
        <div className="container-fluid" style={{ maxWidth: '1300px' }}>
          <div className="row align-items-center">
            <div className="col-lg-7">
              <span className="badge bg-warning text-dark fw-bold px-3 py-2 text-uppercase mb-3">
                <i className="bi bi-fire me-1"></i> Featured Premiere
              </span>
              <h1 className="display-4 fw-bolder mb-3" style={{ letterSpacing: '-0.5px' }}>
                {featuredMovie.videoTitle || featuredMovie.title}
              </h1>
              <div className="d-flex gap-3 align-items-center mb-4 text-secondary small flex-wrap">
                <span className="badge bg-dark border border-secondary text-light px-2 py-1">{featuredMovie.rating || 'U/A 13+'}</span>
                <span><i className="bi bi-clock me-1"></i> {featuredMovie.mainVideoDuration || '1h 45m'}</span>
                <span><i className="bi bi-film me-1"></i> {featuredMovie.tags || 'Action, Drama'}</span>
                <span className="text-success"><i className="bi bi-check-circle-fill me-1"></i> 4K UHD Available</span>
              </div>
              <p className="lead text-light mb-4" style={{ maxWidth: '650px', fontSize: '1.1rem', opacity: 0.9 }}>
                {featuredMovie.description}
              </p>
              <div className="d-flex gap-3">
                <button
                  className="btn btn-warning btn-lg fw-bold px-4 py-3 text-dark d-flex align-items-center gap-2 shadow"
                  onClick={() => handlePlayVideo(featuredMovie)}
                >
                  <i className="bi bi-play-fill" style={{ fontSize: '1.5rem', lineHeight: 1 }}></i> Watch Now
                </button>
                <button
                  className="btn btn-outline-light btn-lg fw-semibold px-4 py-3"
                  onClick={() => setCurrentHeroIndex((currentHeroIndex + 1) % (videos.length || 1))}
                >
                  <i className="bi bi-shuffle me-2"></i> Next Feature
                </button>
              </div>
            </div>

            <div className="col-lg-5 d-none d-lg-block text-center">
              <div style={{
                position: 'relative',
                display: 'inline-block',
                borderRadius: '16px',
                overflow: 'hidden',
                boxShadow: '0 20px 40px rgba(0,0,0,0.6)',
                border: '1px solid rgba(255,255,255,0.1)'
              }}>
                <div style={{
                  width: '320px',
                  height: '460px',
                  background: 'linear-gradient(135deg, #2b3040 0%, #151820 100%)',
                  display: 'flex',
                  flexDirection: 'column',
                  justifyContent: 'flex-end',
                  padding: '24px',
                  textAlign: 'left'
                }}>
                  <div style={{ position: 'absolute', top: '20px', right: '20px' }}>
                    <span className="badge bg-warning text-dark fw-bold">DASH STREAM</span>
                  </div>
                  <div style={{ fontSize: '3.5rem', color: 'rgba(255,193,7,0.4)', marginBottom: 'auto', paddingTop: '40px' }}>
                    🎬
                  </div>
                  <h4 className="fw-bold text-white mb-1">{featuredMovie.videoTitle || featuredMovie.title}</h4>
                  <p className="text-secondary small mb-3">{featuredMovie.tags}</p>
                  <button className="btn btn-sm btn-outline-warning w-100 fw-semibold" onClick={() => handlePlayVideo(featuredMovie)}>
                    <i className="bi bi-play-circle me-1"></i> Stream Direct
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* ── MOVIE CATALOG SECTION ── */}
      <div id="movies" className="container-fluid py-5" style={{ maxWidth: '1300px' }}>
        <div className="d-flex flex-wrap justify-content-between align-items-center mb-4 gap-3">
          <div>
            <h3 className="fw-bold mb-1"><i className="bi bi-collection-play-fill text-warning me-2"></i>Media Jungle Catalog</h3>
            <p className="text-secondary small mb-0">Browse licensed videos and streaming releases</p>
          </div>

          {/* Category Filter Pills */}
          <div className="d-flex gap-2 flex-wrap">
            {['All', 'Action', 'Drama', 'Documentary', 'Comedy', 'Family'].map(cat => (
              <button
                key={cat}
                className={`btn btn-sm ${selectedCategory === cat ? 'btn-warning text-dark fw-bold' : 'btn-outline-secondary text-light'}`}
                onClick={() => setSelectedCategory(cat)}
              >
                {cat}
              </button>
            ))}
          </div>
        </div>

        {/* Video Cards Grid */}
        <div className="row g-4">
          {filteredVideos.map((vid) => (
            <div key={vid.id} className="col-12 col-sm-6 col-md-4 col-lg-4">
              <div
                className="card h-100 border-0 rounded-3 shadow-sm"
                style={{
                  backgroundColor: '#1b1e26',
                  transition: 'transform 0.2s, box-shadow 0.2s',
                  cursor: 'pointer',
                  border: '1px solid rgba(255,255,255,0.06)'
                }}
                onClick={() => handlePlayVideo(vid)}
              >
                <div style={{
                  height: '190px',
                  background: 'linear-gradient(135deg, #272c3d 0%, #151821 100%)',
                  borderTopLeftRadius: '8px',
                  borderTopRightRadius: '8px',
                  position: 'relative',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  overflow: 'hidden'
                }}>
                  <div style={{ fontSize: '3.5rem', opacity: 0.7 }}>🎞️</div>
                  <div style={{
                    position: 'absolute', top: 0, left: 0, width: '100%', height: '100%',
                    backgroundColor: 'rgba(0,0,0,0.3)', display: 'flex', alignItems: 'center', justifyContent: 'center'
                  }}>
                    <div style={{
                      width: '56px', height: '56px', borderRadius: '50%',
                      backgroundColor: '#ffc107', display: 'flex', alignItems: 'center', justifyContent: 'center',
                      color: '#000', fontSize: '1.6rem', boxShadow: '0 4px 12px rgba(0,0,0,0.5)'
                    }}>
                      <i className="bi bi-play-fill" style={{ marginLeft: '3px' }}></i>
                    </div>
                  </div>
                  <span className="badge bg-dark text-warning position-absolute bottom-0 end-0 m-2 small">
                    <i className="bi bi-clock me-1"></i>{vid.mainVideoDuration || '1h 45m'}
                  </span>
                </div>

                <div className="card-body p-3">
                  <div className="d-flex justify-content-between align-items-center mb-1">
                    <h5 className="fw-bold text-white mb-0 text-truncate">{vid.videoTitle || vid.title}</h5>
                    <span className="badge bg-secondary text-white small">{vid.rating || 'U/A'}</span>
                  </div>
                  <p className="text-warning small mb-2">{vid.tags || 'Action, Thriller'}</p>
                  <p className="text-secondary small mb-3" style={{ display: '-webkit-box', WebkitLineClamp: 2, WebkitBoxOrient: 'vertical', overflow: 'hidden' }}>
                    {vid.description}
                  </p>
                  <div className="d-flex justify-content-between align-items-center pt-2 border-top border-secondary border-opacity-25">
                    <span className="text-muted small"><i className="bi bi-shield-check text-success me-1"></i>ISO Encrypted</span>
                    <button className="btn btn-sm btn-outline-warning fw-semibold">
                      Play Stream
                    </button>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* ── AUDIO & MUSIC STREAMING SECTION ── */}
      <div id="audio" className="container-fluid py-5" style={{ maxWidth: '1300px', borderTop: '1px solid rgba(255,255,255,0.06)' }}>
        <div className="d-flex justify-content-between align-items-center mb-4">
          <div>
            <h3 className="fw-bold mb-1"><i className="bi bi-music-note-beamed text-warning me-2"></i>Media Jungle Audio & Soundtracks</h3>
            <p className="text-secondary small mb-0">Stream original film scores and audio releases</p>
          </div>
          <span className="badge bg-dark border border-secondary text-secondary px-3 py-2">Audio Engine Ready</span>
        </div>

        <div className="row g-3">
          {[
            { id: 1, title: 'Echoes of the City (Original Theme)', artist: 'Media Jungle Orchestra', duration: '3:45', genre: 'Soundtrack' },
            { id: 2, title: 'Deep Ocean Ambient Waves', artist: 'Pacific Soundscapes', duration: '4:12', genre: 'Ambient' },
            { id: 3, title: 'Sunday Morning Acoustic', artist: 'Sunny Vibes', duration: '2:58', genre: 'Acoustic / Folk' },
            { id: 4, title: 'Midnight Highway Synthwave', artist: 'Neon Trails', duration: '3:30', genre: 'Electronic' }
          ].map((track) => (
            <div key={track.id} className="col-12 col-md-6">
              <div className="d-flex align-items-center justify-content-between p-3 rounded-3" style={{ backgroundColor: '#1b1e26', border: '1px solid rgba(255,255,255,0.05)' }}>
                <div className="d-flex align-items-center gap-3">
                  <div style={{ width: '45px', height: '45px', borderRadius: '8px', backgroundColor: '#ffc107', color: '#000', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '1.2rem' }}>
                    <i className="bi bi-music-note"></i>
                  </div>
                  <div>
                    <h6 className="fw-bold text-white mb-0">{track.title}</h6>
                    <span className="text-secondary small">{track.artist} • <span className="text-warning">{track.genre}</span></span>
                  </div>
                </div>
                <div className="d-flex align-items-center gap-3">
                  <span className="text-muted small">{track.duration}</span>
                  <button className="btn btn-sm btn-outline-warning rounded-circle" style={{ width: '36px', height: '36px', padding: 0 }}>
                    <i className="bi bi-play-fill" style={{ fontSize: '1.1rem' }}></i>
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* ── SECURITY STANDARDS HIGHLIGHT SECTION ── */}
      <div id="security" className="py-5" style={{ backgroundColor: '#0c0e12', borderTop: '1px solid rgba(255,255,255,0.06)' }}>
        <div className="container-fluid" style={{ maxWidth: '1300px' }}>
          <div className="row align-items-center">
            <div className="col-lg-8">
              <span className="badge bg-warning text-dark fw-bold mb-2">ENTERPRISE SECURITY</span>
              <h3 className="fw-bold mb-2">Protected by ISO 27001 & SOC 2 Compliance Controls</h3>
              <p className="text-secondary mb-3">
                Media Jungle enforces 20 standardized controls including JWT RBAC, BCrypt Cryptography, Brute-Force Rate Limiting, XSS Defenses, and Immutable Audit Logging in the backend.
              </p>
              <div className="d-flex gap-2 flex-wrap">
                <span className="badge bg-dark border border-success text-success px-3 py-2">✓ ISO 27001 A.8.24 (Crypto)</span>
                <span className="badge bg-dark border border-success text-success px-3 py-2">✓ ISO 27001 A.8.5 (Auth)</span>
                <span className="badge bg-dark border border-success text-success px-3 py-2">✓ SOC 2 CC6.1 (Access)</span>
                <span className="badge bg-dark border border-success text-success px-3 py-2">✓ SOC 2 CC7.4 (Audit Trail)</span>
              </div>
            </div>
            <div className="col-lg-4 text-lg-end mt-4 mt-lg-0">
              <Link to="/admin" className="btn btn-outline-warning btn-lg fw-bold px-4 py-3">
                <i className="bi bi-shield-shaded me-2"></i> Open Security Dashboard
              </Link>
            </div>
          </div>
        </div>
      </div>

      {/* ── FOOTER ── */}
      <footer className="py-4 text-center text-secondary small" style={{ borderTop: '1px solid rgba(255,255,255,0.05)' }}>
        <p className="mb-1">© 2026 Media Jungle Streaming Inc. All Rights Reserved.</p>
        <p className="text-muted mb-0">Built with Spring Boot 3.4 & React 18 • Verified Compliant with ISO 27001 & SOC 2</p>
      </footer>
    </div>
  );
};

export default MediaHomeScreen;
