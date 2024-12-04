import { getAssignmentOptions } from "../../../../utils/assignmentOptions";
import Navigator from "../../../common/Navigator/Navigator";

import { useState } from "react";

const OfficerAssignments = ({ user }) => {

    const assignmentOptions = getAssignmentOptions('ROLE_OFFICER');
    const [selectedOption, setSelectedOption] = useState(assignmentOptions[0]);

    return(
        <>
            <Navigator
                options={assignmentOptions}
                onSelect={(option) => setSelectedOption(option)}
            />
            {selectedOption.component && <selectedOption.component user={user}/>}
        </>
    );
}
export default OfficerAssignments;