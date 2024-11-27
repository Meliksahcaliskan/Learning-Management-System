import axios from "axios";

export const getAllClasses = async (accessToken) => {
  const response = await axios.get(
    '/api/v1/classes',
    {
      headers : {
        Authorization : `Bearer ${accessToken}`,
      }
    },
  );
  return response.data;
};
