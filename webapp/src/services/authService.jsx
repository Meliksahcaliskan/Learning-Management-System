import axios from 'axios';

const API_URL = 'http://localhost:8080/api/v1/auth/login';

const login = async (credentials) => {
  // const response = await axios.post(API_URL, credentials);
  // return response.data;  // Contains id, username, email, role, and token
  return {
    id : "thisisID",
    username : "onurhanT",
    email : "dummy@email.com",
    role : "student",
    token : "thisisTOKEN"
  }
};

export default { login };