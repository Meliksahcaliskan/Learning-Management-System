import axios from 'axios';


const login = async (credentials) => {
  console.log('making the api call');
  const response = await axios.post('/api/auth/login', credentials);
  return response.data;  // Contains id, username, email, role, and token

  // return {
  //   id : "thisisID",
  //   username : "onurhanT",
  //   email : "dummy@email.com",
  //   role : "student",
  //   token : "thisisTOKEN"
  // }

  // throw new Error('Yanlış kullanıcı adı veya şifre');
};

export default { login };