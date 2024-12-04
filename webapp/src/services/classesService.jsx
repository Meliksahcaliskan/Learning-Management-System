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
    console.log(response);
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
    console.log('fetched response :', response);
    return response.data;
}
