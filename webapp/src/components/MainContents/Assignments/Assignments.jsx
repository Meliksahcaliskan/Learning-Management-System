import { useContext, useState } from 'react';
import './Assignments.css';
import { AuthContext } from '../../../contexts/AuthContext';
import { getAssignmentOptions } from '../../../utils/assignmentOptions';
import Navigator from '../../common/Navigator/Navigator';
import StudentAssignments from './StudentAssignments/StudentAssignments';



const Assignments = () => {

    const { user } = useContext(AuthContext);
    const assignmentOptions = getAssignmentOptions(user.role);
    const [selectedOption, setSelectedOption] = useState(assignmentOptions[0]);
    

    return(
        <div className="assignments">
            {/* <Navigator
                options={assignmentOptions}
                onSelect={(option) => setSelectedOption(option)}
                />
            use selectedOption's component here
            {selectedOption.component && <selectedOption.component user={user}/>} */}
            <StudentAssignments user={user}/>
        </div>
    );
}
export default Assignments;