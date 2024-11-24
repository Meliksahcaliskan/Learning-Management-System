import { useState } from "react";
import { getAssignmentOptions } from "../../../../utils/assignmentOptions";
import Navigator from "../../../common/Navigator/Navigator";




const OfficerAssignments = ({ user }) => {

    const assignmentOptions = getAssignmentOptions('ROLE_OFFICER');
    const [selectedOption, setSelectedOption] = useState(assignmentOptions[0]);

    return(
        <>
            <Navigator
                options={assignmentOptions}
                onSelect={(option) => {
                    setSelectedOption(option);
                    console.log(option);
                }}
            />

            {selectedOption.component && <selectedOption.component user={user}/>}
        </>
    );
}
export default OfficerAssignments;