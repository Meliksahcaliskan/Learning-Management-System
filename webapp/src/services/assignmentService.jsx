import axios from "axios";



const createAssignment = async (credentials) => {
    const response = await axios.post('/api/assignments/createAssignment', credentials);
    return response;
}

export default { createAssignment };