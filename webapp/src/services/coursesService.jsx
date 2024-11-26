import axios from "axios";

export const fetchCourses = async () => {
  // try {
  //   const response = await axios.get('/api/v1/courses');
  //   return response.data;
  // } catch (error) {
  //   throw new Error(error.response?.data?.message || 'Failed to fetch courses');
  // }

  return [
    {
      "id": 1,
      "title": "Turkish"
    },
    {
      "id": 2,
      "title": "Mathematics"
    },
    {
      "id": 3,
      "title": "Physics"
    },
    {
      "id": 4,
      "title": "Chemistry"
    },
    {
      "id": 5,
      "title": "Biology"
    },
    {
      "id": 6,
      "title": "History"
    },
    {
      "id": 7,
      "title": "Geography"
    },
    {
      "id": 8,
      "title": "English"
    },
    {
      "id": 10,
      "title": "Music"
    }
  ]
};
