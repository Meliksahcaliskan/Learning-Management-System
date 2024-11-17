import './StudentAssignments.css';
import { useState, useEffect } from 'react';
import { getAssignmentsForStudent } from '../../../../services/assignmentService';
import { getAssignmentOptions } from '../../../../utils/assignmentOptions';
import Navigator from '../../../common/Navigator/Navigator';

const StudentAssignments = ({ user }) => {

    const assignmentOptions = getAssignmentOptions(user.role);
    const [selectedOption, setSelectedOption] = useState(assignmentOptions[0]);
    
    const [assignments, setAssignments] = useState([]);
    const [selectedAssignments, setSelectedAssignments] = useState([]);

    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(false);

    useEffect(() => {
        const fecthAssignments = async () => {
            setLoading(true);
            setError(null);
            try {
                const data = await getAssignmentsForStudent(user.id, user.token);
                setAssignments(data);
                setSelectedAssignments(data.filter((assignment) => assignment.status === selectedOption.status));
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
        <>
            <Navigator
                options={assignmentOptions}
                onSelect={(option) => {
                    setSelectedOption(option);
                    setSelectedAssignments(assignments.filter((assignment) => assignment.status === option.status));
                }}
            />
            <h3>assigments</h3>
            {selectedAssignments.length > 0 ? (
                <ul>
                    {selectedAssignments.map((selectedAssignment) => (
                    <li key={selectedAssignment.id}>{selectedAssignment.title}</li>
                    ))}
                </ul>
            ) : (
                <p>No assignments found.</p>
            )}
        </>
    );
}
export default StudentAssignments;