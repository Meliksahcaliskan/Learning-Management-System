import axios from "axios";



export const createAssignment = async (credentials) => {
    const response = await axios.post('/api/assignments/createAssignment', credentials);
    return response;
}

export const getAssignmentsForStudent = async (studentID, token) => {
    const response = await axios.get(`/api/assignments/displayAssignments/${studentID}`, {
        headers : {
            Authorization : `Bearer ${token}`
        },
    });
    return response.data;
}



export default { createAssignment, getAssignmentsForStudent };