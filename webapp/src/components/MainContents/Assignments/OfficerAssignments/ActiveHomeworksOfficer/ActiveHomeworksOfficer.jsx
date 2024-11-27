import './ActiveHomeworksOfficer.css';
import AssignmentSearch from '../../../../../utils/assignmentSearch/assignmentSearch';
import { useState } from 'react';

const ActiveHomeworksOfficer = () => {

    const [assignments, setAssignments] = useState([]);

    const handleSearchResults = (response) => {
        setAssignments(response);
    }


    return(
        <>
            <AssignmentSearch onSearch={handleSearchResults}></AssignmentSearch>
        </>
    );
}
export default ActiveHomeworksOfficer;