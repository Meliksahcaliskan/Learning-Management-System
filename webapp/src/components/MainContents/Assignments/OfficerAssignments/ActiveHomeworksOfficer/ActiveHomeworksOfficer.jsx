import './ActiveHomeworksOfficer.css';
import AssignmentSearch from '../../../../../utils/assignmentSearch/assignmentSearch';
import { useState } from 'react';
import UpdateAssignment from './UpdateAssignment/UpdateAssignment';

const ActiveHomeworksOfficer = () => {
    const [isSearched, setIsSearched] = useState(false);
    const [assignments, setAssignments] = useState([]);

    const handleSearchResults = (response) => {
        setAssignments([]);
        response = response.filter(assignment => new Date(assignment.dueDate) >= new Date());
        setIsSearched(true);
        setAssignments(response);
    };

    return (
        <>
            <AssignmentSearch onSearchResults={handleSearchResults} />
            {isSearched && (
                assignments.length > 0 ? (
                    assignments.map((assignment, index) => (
                        <UpdateAssignment
                            key={index}
                            assignment={assignment}
                        />
                    ))
                ) : (
                    <p>no assignments found</p>
                )
            )}
        </>
    );
};

export default ActiveHomeworksOfficer;
