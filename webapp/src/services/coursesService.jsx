import axios from "axios";

export const getAllCourses = async (accessToken) => {
  const response = await axios.get(
    '/api/v1/courses',
    {
      headers: {
        Authorization: `Bearer ${accessToken}`,
      },
    }
  );
    return response.data;
};
