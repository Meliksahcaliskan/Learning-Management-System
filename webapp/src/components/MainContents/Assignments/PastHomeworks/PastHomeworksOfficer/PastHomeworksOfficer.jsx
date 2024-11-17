import { useState } from 'react';
import './PastHomeworksOfficer.css';
import AssignmentSearch from '../../../../../utils/assignmentSearch/assignmentSearch';





const PastHomeworksOfficer = () => {


    const [assignments, setAssignments] = useState([]);

    const handleSearchResults = (response) => {
        setAssignments(response);
    }

    return(
        <>
            <AssignmentSearch onSearch={handleSearchResults}/>
        </>
    );
}
export default PastHomeworksOfficer;