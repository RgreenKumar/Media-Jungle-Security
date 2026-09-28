<<<<<<< HEAD
import React from 'react'
import { Link} from 'react-router-dom';

function usersidebar() {
  return (
    <div class="header__logo_user">
      <img src="img/logo.png" alt="" />
        <div className='side_value_user'>
        <h5 className="text-center text-white font-weight-light my-4 Wording_user">
        <Link className="Link" to="/login1">
        <span> Profile</span>
      </Link>
          
          </h5>
        <h5 className="text-center text-white font-weight-light my-4 Wording_user">
         
        <Link className="Link" to="/">
        <span> Home</span>
      </Link> 
          </h5>
        <h5 className="text-center text-white font-weight-light my-4 Wording_user">
          
        <Link className="Link" to="/search">
        <span> Search</span>
      </Link>
          </h5>
        <h5 className="text-center text-white font-weight-light my-4 Wording_user">
          
        <Link className="Link" to="/VideoHomescreen">
        <span> Videos</span>
      </Link>
          </h5> 
        </div>

    </div>
  )
}

export default usersidebar
=======
import React from 'react';
import { Link } from 'react-router-dom';

const Usersidebar = () => {
  return (
    <nav className="navbar navbar-expand-lg navbar-dark bg-dark mb-3 px-3">
      <Link className="navbar-brand text-warning" to="/">Media Jungle</Link>
      <div className="navbar-nav">
        <Link className="nav-link" to="/">Dashboard</Link>
        <Link className="nav-link" to="/admin">Admin</Link>
      </div>
    </nav>
  );
};

export default Usersidebar;
>>>>>>> internship/main
