import axios from "axios";

export const getAllClasses = async (accessToken) => {
  try {
    const response = await axios.get(
      '/api/v1/classes',
      {
        headers : {
          Authorization : `Bearer ${accessToken}`
        }
      }
    );
    return response.data;
  }catch(error) {
    console.error("error fetching classes");
    throw error
  }
};

export const getTeacherClasses = async (accessToken) => {
    const response = await axios.get(
      '/api/v1/classes/teacher',
      {
        headers : {
          Authorization : `Bearer ${accessToken}`
        },
      }
    );
    return response.data;
}

export const getClasses = async (userRole, accessToken) => {
  if(userRole === 'ROLE_TEACHER') return await getTeacherClasses(accessToken);
  else if(userRole === 'ROLE_ADMIN' || userRole === 'ROLE_COORDINATOR') return await getAllClasses(accessToken);
}