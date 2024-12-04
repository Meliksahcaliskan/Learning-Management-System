import './ActiveHomeworksOfficer.css';
import AssignmentSearch from '../../../../../utils/assignmentSearch/assignmentSearch';
import { useState } from 'react';

const ActiveHomeworksOfficer = () => {

    const [assignments, setAssignments] = useState('');

    const handleSearchResults = (response) => {
        setAssignments(response);
        console.log("search results : ", response);
    }


    return(
        <>
            <AssignmentSearch onSearchResults={handleSearchResults}></AssignmentSearch>
        </>
    );
}
export default ActiveHomeworksOfficer;