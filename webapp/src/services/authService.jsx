import axios from 'axios';

const login = async (credentials) => {
  const response = await axios.post('/api/v1/auth/login', credentials);
  return response.data;
};

const register = async (credentials) => {
  const response = await axios.post('api/v1/auth/register', credentials);
  return response.data;
}

const logout = async (accessToken, refreshToken = null) => {
  const response = await axios.post(
    '/api/v1/auth/logout',
    {},
    {
      headers : {
        Authorization : `Bearer ${accessToken}`,
        ...(refreshToken && { 'Refresh-Token' : refreshToken})
      }
    }
  );
  console.log(response);
  return response.data;
}
export default { login, logout, register };