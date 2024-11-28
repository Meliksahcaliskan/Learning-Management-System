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
