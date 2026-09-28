<<<<<<< HEAD
import axios from 'axios';
import React, { useEffect, useState } from 'react';
import {Outlet, useLocation, useNavigate } from 'react-router-dom';
=======
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
>>>>>>> internship/main
import API_URL from '../Config';
import Swal from 'sweetalert2';

const Login = () => {
  const [user, setUser] = useState({ username: '', password: '' });
<<<<<<< HEAD
  const [samp1, setsamp] = useState(1);
  const [users, setUsers] = useState([]);
  const [valid, setValid] = useState();
  let value = 1;
  const [isEmpty, setIsEmpty] = useState();
  // var isEmpty;
  const location = useLocation();
  const navigate = useNavigate();




  useEffect(() => {
    // console.log(`${API_URL}/api`);

    fetchUsers();

    if (location.pathname === '/admin') {
      setsamp(0);
    } else {
      setsamp(1);
    }
  }, [location.pathname]);



  const handleChange = (e) => {
    setUser({ ...user, [e.target.name]: e.target.value });
    // fetchUsers();




  };
  const hide = () => {
    // setsamp(1);
    setValid('');
  };

  const fetchUsers = async () => {
    try {
      const response = await axios.get(`${API_URL}/api/v2/GetAllUser`);
      setUsers(response.data.userList);
      // console.log(response.data.userList)
      setIsEmpty(response.data.empty);
      setValid(response.data.valid);
      // console.log("Is empty: " + isEmpty); // Corrected from empty to isEmpty
      // console.log("Valid: " + valid);
      // console.log("--------------------------------------------------------------------");
      // After setting the users state, check if the username exists
      // const userFound = response.data.find(userData => userData.username === user.username);
    } catch (error) {
      console.log('Error fetching users:', error);
      throw error;
    }

  }

//   const handleSubmit = async (e) => {
//     e.preventDefault();

//     try {
//         const sendData = {
//             username: user.username,
//             password: user.password
//         };

//         const response = await fetch(`${API_URL}/api/v2/login/admin`, {
//             method: "POST",
//             headers: {
//                 "Content-Type": "application/json",
//             },
//             body: JSON.stringify(sendData),
//         });

//         if (response.ok) {
//             sessionStorage.setItem('username', user.username);
//             sessionStorage.setItem('name', true);

//             // Navigate to the dashboard after successful login
//             navigate('/admin/Dashboard');
//         } else {
//             // Handle invalid username or password
//             Swal.fire({
//                 icon: 'error',
//                 title: 'Login Failed',
//                 text: 'Invalid username or password',
//             });
//         }
//     } catch (error) {
//         console.error('Error during login:', error);
//         // Show a generic error message using SweetAlert
//         Swal.fire({
//             icon: 'error',
//             title: 'Login Error',
//             text: 'An error occurred during login. Please try again later.',
//         });
//     }
// };


const handleSubmit = async (e) => {
  e.preventDefault();

  try {
    const sendData = {
      username: user.username,
      password: user.password
    };

    const response = await fetch(`${API_URL}/api/v2/login/admin`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(sendData),
    });

    if (response.ok) {
      try {
        const data = await response.json(); // Attempt to parse JSON response
        const jwtToken = data.Token; // Use the correct key based on your API response
        const name = data.UserName; // Use the correct key based on your API response
        const userId = data.AdminId; // Use the correct key based on your API response
        const message = data.message;
        const role = data.Role;

        // Store token in session storage
        sessionStorage.setItem("username", name);
        sessionStorage.setItem('tokenn', jwtToken);
        sessionStorage.setItem('adminId', userId);
        sessionStorage.setItem('name', true);
        sessionStorage.setItem('role', role);

        // console.log(name, jwtToken, userId,role);

        // Navigate to the dashboard after successful login
        navigate('/admin/Dashboard');
      } catch (jsonError) {
        console.error('Error parsing JSON:', jsonError);
        throw jsonError;
        // Swal.fire({
        //   icon: 'error',
        //   title: 'Login Error',
        //   text: 'An error occurred while processing the response. Please try again later.',
        // });
      }
    } else {
      // Handle invalid username or password based on status code
      const errorData = await response.json().catch(() => ({})); // Handle cases where response is not JSON
      const message = errorData.message || 'Invalid username or password';

      // Swal.fire({
      //   icon: 'error',
      //   title: 'Login Failed',
      //   text: message,
      // });
    }
  } catch (error) {
    console.error('Error during login:', error);
    throw error;
    // Swal.fire({
    //   icon: 'error',
    //   title: 'Login Error',
    //   text: 'An error occurred during login. Please try again later.',
    // });
  }
};



  const Submit = (e) => {
    e.preventDefault();
    fetchUsers();
    

    const userFound = users.find(userData => (
      user.username === userData.username && user.password === userData.password
    ));

    // && !isEmpty && valid
    if (userFound ) {
      hide();

      // handleLogin();
      sessionStorage.setItem('username', user.username);

      navigate('/admin/Dashboard');
      let ab = true;
      sessionStorage.setItem("name", ab);
    }
      
      // If needed, perform navigation or any other action here
    // } else if (isEmpty) {
    //   alert("Licence required")
    //   navigate('/licence');
    // } else if (!valid) {

    //   alert("Licence Expired")
    //   navigate('/licence');
    // }
    else {
      alert("Invalid username or password");
    }
    
    // if(
    //   // user.username==users.username
    //   //  && 
    //    user.password==users.password
    //    ){
    //   // navigate('Dashboard'); 
    //   navigate('/admin/Dashboard', { state: { username: user.username, password: user.password } });
    // }
    // else{
    //   alert("invalid");
    // }

  }
  return (
    <>
      {samp1 === 0 ? (
        <div className="contain">
          <div className="row justify-content-center">
            <div className="col-lg-5">
              <div className="card shadow-lg border-0 rounded-lg mt-5">
                <div className="bg-image" style={{ backgroundImage: "url('img/bg.jpg')" }}>
                  <div className="card-header">
                    <h3 className="text-center text-black font-weight-light my-4">Login </h3>
                  </div>
                  <div className="card-body">
                    <form onSubmit={handleSubmit}>
                      <div className="form-floating mb-3">
                        <input
                          className="form-control"
                          name="username"
                          type="text"
                          placeholder="Username"
                          value={user.username}
                          onChange={handleChange}
                          required
                        />
                        <label htmlFor="inputUsername">Username</label>
                      </div>
                      <div className="form-floating mb-3">
                        <input
                          className="form-control"
                          name="password"
                          type="password"
                          placeholder="Password"
                          value={user.password}
                          onChange={handleChange}
                          required
                        />
                        <label htmlFor="inputPassword">Password</label>
                      </div>
                      <div className="d-flex btn  align-items-center justify-content-between mt-4 mb-0">
                        {/* <a className="small colour" href="/AdminSignin">
                          Admin SignIn
                        </a> */}
                        <button className="btn btn-primary" type="submit" style={{ color: "black" }}>LOGIN</button>
                      </div>
                      <div className="small text-center">
                        {/* <Link to="Dashboard" onClick={hide}>Back to Home</Link> */}
                      </div>
                    </form>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      ) : null}
      <Outlet />
    </>
=======
  const [loading, setLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');
  const navigate = useNavigate();

  const handleChange = (e) => {
    setUser({ ...user, [e.target.name]: e.target.value });
    if (errorMessage) setErrorMessage('');
  };

  const handleFillCredentials = (username, password) => {
    setUser({ username, password });
    if (errorMessage) setErrorMessage('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrorMessage('');
    setLoading(true);

    try {
      const sendData = {
        username: user.username.trim(),
        password: user.password
      };

      const response = await fetch(`${API_URL}/api/v2/login/admin`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(sendData),
      });

      if (response.ok) {
        const data = await response.json();
        const jwtToken = data.Token;
        const name = data.UserName || user.username;
        const userId = data.AdminId || 1;
        const role = data.Role || 'ADMIN';

        sessionStorage.setItem("username", name);
        sessionStorage.setItem('tokenn', jwtToken);
        sessionStorage.setItem('adminId', userId);
        sessionStorage.setItem('name', 'true');
        sessionStorage.setItem('role', role);

        Swal.fire({
          icon: 'success',
          title: 'Login Successful',
          text: `Welcome back, ${name}!`,
          timer: 1500,
          showConfirmButton: false
        });

        setTimeout(() => {
          navigate('/admin/Dashboard');
        }, 600);
      } else {
        const errorData = await response.json().catch(() => ({}));
        const msg = errorData.message || (response.status === 401 ? 'Incorrect username or password' : 'Login failed. Please try again.');
        setErrorMessage(msg);
        Swal.fire({
          icon: 'error',
          title: 'Login Failed',
          text: msg,
        });
      }
    } catch (error) {
      console.error('Error during login:', error);
      const networkMsg = 'Unable to connect to the backend server at ' + API_URL + '. Please verify the backend is running.';
      setErrorMessage(networkMsg);
      Swal.fire({
        icon: 'error',
        title: 'Connection Error',
        text: networkMsg,
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container" style={{ minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
      <div className="col-12 col-sm-10 col-md-8 col-lg-5">
        <div className="card shadow-lg border-0 rounded-4 my-5" style={{ overflow: 'hidden' }}>
          <div className="card-header text-center py-4" style={{ backgroundColor: '#2b2f3a', color: '#fff' }}>
            <h3 className="fw-bold mb-1">Media Jungle</h3>
            <p className="small mb-0" style={{ color: '#adb5bd' }}>Security Administration Portal</p>
          </div>

          <div className="card-body p-4 p-sm-5 bg-light">
            <h4 className="text-center text-dark fw-semibold mb-4">Admin Login</h4>

            {errorMessage && (
              <div className="alert alert-danger text-center py-2 mb-4" role="alert">
                <strong>Error: </strong> {errorMessage}
              </div>
            )}

            <form onSubmit={handleSubmit}>
              <div className="mb-3">
                <label className="form-label fw-semibold text-secondary small" htmlFor="usernameInput">
                  Username
                </label>
                <input
                  id="usernameInput"
                  className="form-control form-control-lg"
                  name="username"
                  type="text"
                  placeholder="Enter username"
                  value={user.username}
                  onChange={handleChange}
                  required
                  autoFocus
                />
              </div>

              <div className="mb-4">
                <label className="form-label fw-semibold text-secondary small" htmlFor="passwordInput">
                  Password
                </label>
                <input
                  id="passwordInput"
                  className="form-control form-control-lg"
                  name="password"
                  type="password"
                  placeholder="Enter password"
                  value={user.password}
                  onChange={handleChange}
                  required
                />
              </div>

              <button
                className="btn btn-primary btn-lg w-100 fw-bold mb-3 shadow-sm"
                type="submit"
                disabled={loading}
                style={{
                  backgroundColor: '#0d6efd',
                  borderColor: '#0d6efd',
                  padding: '12px',
                  fontSize: '1rem',
                  letterSpacing: '0.5px'
                }}
              >
                {loading ? 'LOGGING IN...' : 'LOGIN'}
              </button>

              <div className="card border-0 bg-white shadow-sm p-3 mt-4 rounded-3">
                <div className="text-muted small fw-bold mb-2">Quick Auto-fill Credentials:</div>
                <div className="d-flex gap-2">
                  <button
                    type="button"
                    className="btn btn-sm btn-outline-primary flex-fill"
                    onClick={() => handleFillCredentials('admin', 'admin123')}
                  >
                    admin / admin123
                  </button>
                  <button
                    type="button"
                    className="btn btn-sm btn-outline-secondary flex-fill"
                    onClick={() => handleFillCredentials('Hari', 'Ackerman27')}
                  >
                    Hari / Ackerman27
                  </button>
                </div>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
>>>>>>> internship/main
  );
};

export default Login;
<<<<<<< HEAD









// import axios from 'axios';
// import React, { useEffect, useState } from 'react';
// import {Outlet, useLocation, useNavigate } from 'react-router-dom';
// import API_URL from '../Config';

// const Login = () => {
//   const [user, setUser] = useState({ username: '', password: '' });
//   const [samp1, setsamp] = useState(1);
//   const [users, setUsers] = useState([]);
//   const [valid, setValid] = useState();
//   let value = 1;
//   const [isEmpty, setIsEmpty] = useState();
//   // var isEmpty;
//   const location = useLocation();
//   const navigate = useNavigate();

  


//   useEffect(() => {
//     // console.log(`${API_URL}/api`);

//     fetchUsers();

//     if (location.pathname === '/admin') {
//       setsamp(0);
//     } else {
//       setsamp(1);
//     }
//   }, [location.pathname]);



//   const handleChange = (e) => {
//     setUser({ ...user, [e.target.name]: e.target.value });
//     // fetchUsers();




//   };
//   const hide = () => {
//     // setsamp(1);
//     setValid('');
//   };

//   const fetchUsers = async () => {
//     try {
//       const response = await axios.get(`${API_URL}/api/v2/GetAllUser`);
//       setUsers(response.data.userList);
//       console.log(response.data.userList)
//       setIsEmpty(response.data.empty); // Corrected from setisEmpty to setIsEmpty
//       setValid(response.data.valid);
//       // console.log("Is empty: " + isEmpty); // Corrected from empty to isEmpty
//       // console.log("Valid: " + valid);
//       // console.log("--------------------------------------------------------------------");
//       // After setting the users state, check if the username exists
//       // const userFound = response.data.find(userData => userData.username === user.username);
//     } catch (error) {
//       console.log('Error fetching users:', error);
//     }

//   }


//   const Submit = (e) => {
//     e.preventDefault();
//     fetchUsers();
    

//     const userFound = users.find(userData => (
//       user.username === userData.username && user.password === userData.password
//     ));

//     // && !isEmpty && valid
//     if (userFound ) {
//       hide();

//       // handleLogin();
//       sessionStorage.setItem('username', user.username);

//       navigate('/admin/Dashboard');
//       let ab = true;
//       sessionStorage.setItem("name", ab);
//     }
      
//       // If needed, perform navigation or any other action here
//     // } else if (isEmpty) {
//     //   alert("Licence required")
//     //   navigate('/licence');
//     // } else if (!valid) {

//     //   alert("Licence Expired")
//     //   navigate('/licence');
//     // }
//     else {
//       alert("Invalid username or password");
//     }
    
//     // if(
//     //   // user.username==users.username
//     //   //  && 
//     //    user.password==users.password
//     //    ){
//     //   // navigate('Dashboard'); 
//     //   navigate('/admin/Dashboard', { state: { username: user.username, password: user.password } });
//     // }
//     // else{
//     //   alert("invalid");
//     // }

//   }
//   return (
//     <>
//       {samp1 === 0 ? (
//         <div className="contain">
//           <div className="row justify-content-center">
//             <div className="col-lg-5">
//               <div className="card shadow-lg border-0 rounded-lg mt-5">
//                 <div className="bg-image" style={{ backgroundImage: "url('img/bg.jpg')" }}>
//                   <div className="card-header">
//                     <h3 className="text-center text-black font-weight-light my-4">Login </h3>
//                   </div>
//                   <div className="card-body">
//                     <form onSubmit={Submit}>
//                       <div className="form-floating mb-3">
//                         <input
//                           className="form-control"
//                           name="username"
//                           type="text"
//                           placeholder="Username"
//                           value={user.username}
//                           onChange={handleChange}
//                           required
//                         />
//                         <label htmlFor="inputUsername">Username</label>
//                       </div>
//                       <div className="form-floating mb-3">
//                         <input
//                           className="form-control"
//                           name="password"
//                           type="password"
//                           placeholder="Password"
//                           value={user.password}
//                           onChange={handleChange}
//                           required
//                         />
//                         <label htmlFor="inputPassword">Password</label>
//                       </div>
//                       <div className="d-flex btn  align-items-center justify-content-between mt-4 mb-0">
//                         <a className="small colour" href="/AdminSignin">
//                           Admin SignIn
//                         </a>
//                         <button className="btn btn-primary" type="submit" style={{ color: "black" }}>LOGIN</button>
//                       </div>
//                       <div className="small text-center">
//                         {/* <Link to="Dashboard" onClick={hide}>Back to Home</Link> */}
//                       </div>
//                     </form>
//                   </div>
//                 </div>
//               </div>
//             </div>
//           </div>
//         </div>
//       ) : null}
//       <Outlet />
//     </>
//   );
// };

// export default Login;




=======
>>>>>>> internship/main
