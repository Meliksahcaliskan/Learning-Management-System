import './StudentAssignments.css';
import { useState, useEffect } from 'react';
import { getAssignmentsForStudent } from '../../../../services/assignmentService';
import { getAssignmentOptions } from '../../../../utils/assignmentOptions';

import Navigator from '../../../common/Navigator/Navigator';


import SingleAssigment from './SingleAssignment/SingleAssignment';

const StudentAssignments = ({ user }) => {
  const assignmentOptions = getAssignmentOptions(user.role);
  const [selectedOption, setSelectedOption] = useState(assignmentOptions[0]);

  const [assignments, setAssignments] = useState([]);
  const [selectedAssignments, setSelectedAssignments] = useState([]);

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);


  useEffect(() => {
    const fetchAssignments = async () => {
      setLoading(true);
      setError(null);
      try {
        const response = await getAssignmentsForStudent(user.id, user.accessToken);
        console.log(response.data);
        setAssignments(response.data);
        setSelectedAssignments(response.data.filter((assignment) => assignment.status === selectedOption.status));
      } catch (err) {
        console.log(err);
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };
    fetchAssignments();
  }, [user]);

  const handleOptionChange = (option) => {
    setSelectedOption(option);
    setSelectedAssignments(assignments.filter((assignment) => assignment.status === option.status));
  };

  if (loading) return <p>Loading...</p>;
  if (error) return <p>Error: {error}</p>;

  return (
    <>
      <Navigator
        options={assignmentOptions}
        onSelect={(option) => handleOptionChange(option)}
      />
      {selectedAssignments.length > 0 ? (
        <ul className='search-result-list'>
          {selectedAssignments.map((selectedAssignment) => (
            <li key={selectedAssignment.id}>
              <SingleAssigment assignment={selectedAssignment} />
            </li>
          ))}
        </ul>
      ) : (
        <p>No assignments found</p>
      )}
    </>
  );
};

export default StudentAssignments;
