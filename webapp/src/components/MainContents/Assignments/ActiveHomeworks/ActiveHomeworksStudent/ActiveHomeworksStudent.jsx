import { useEffect, useState } from 'react';
import './ActiveHomeworksStudent.css';

import { getAssignmentsForStudent } from '../../../../../services/assignmentService';

const ActiveHomeworksStudent = ({ user }) => {


    const [assignments, setAssignments] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(false);

    useEffect(() => {
        const fecthAssignments = async () => {
            setLoading(true);
            setError(null);
            
            try {
                const data = await getAssignmentsForStudent(user.id, user.token);
                setAssignments(data.filter((assignment) => assignment.status === 'PENDING'))
                // setAssignments(data);
            } catch (err) {
                setError(err.message);    
            } finally {
                setLoading(false);
            }
        }
        fecthAssignments();
    }, [user]);

    if (loading) return <p>Loading...</p>;
    if (error) return <p>Error: {error}</p>;

    return(
        <div>
      <h3>Assignments</h3>
      {assignments.length > 0 ? (
        <ul>
          {assignments.map((assignment) => (
            <li key={assignment.id}>{assignment.title}</li>
          ))}
        </ul>
      ) : (
        <p>No assignments found.</p>
      )}
    </div>
  
    );
}
export default ActiveHomeworksStudent;