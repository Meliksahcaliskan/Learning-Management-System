import axios from "axios";
const BASE_URL = import.meta.env.VITE_API_BASE_URL;

export const getAttendanceOfStudent = async (studentID, params, accessToken) => {
    const response = await axios.get(
        `${BASE_URL}/api/v1/attendance/${studentID}`,
        {
            headers : {
                Authorization : `Bearer ${accessToken}`,
                'Content-Type' : 'application/json'
            },
            params,
        }
    );
    return response.data;
}