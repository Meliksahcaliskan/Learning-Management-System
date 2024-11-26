import axios from "axios";

export const fetchClasses = async () => {
//   try {
//     const response = await axios.get('/api/v1/classes');
//     return response.data; // Adjust based on API response structure
//   } catch (error) {
//     throw new Error(error.response?.data?.message || 'Failed to fetch classes');
//   }
    return [
        {
          "id": 1,
          "name": "9A"
        },
        {
          "id": 2,
          "name": "9B"
        },
        {
          "id": 3,
          "name": "9C"
        },
        {
          "id": 4,
          "name": "10A"
        },
        {
          "id": 5,
          "name": "10B"
        },
        {
          "id": 6,
          "name": "10C"
        },
        {
          "id": 7,
          "name": "11A"
        },
        {
          "id": 8,
          "name": "11B"
        },
        {
          "id": 9,
          "name": "11C"
        },
        {
          "id": 10,
          "name": "12A"
        }
    ]
};
