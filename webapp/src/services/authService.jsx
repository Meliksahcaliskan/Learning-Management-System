import axios from 'axios';

const login = async (credentials) => {
  const response = await axios.post('/api/auth/login', credentials);
  return response.data;  // Contains id, username, email, role, and token
};

const register = async (credentials) => {
  const response = await axios.post('api/auth/register', credentials);
  return response.data;
}
export default { login, register };