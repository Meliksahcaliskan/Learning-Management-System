import { getAssignmentOptions } from "../../../../utils/assignmentOptions";
import Navigator from "../../../common/Navigator/Navigator";

import { useContext, useEffect, useState } from "react";
import { getAllCourses } from "../../../../services/coursesService";
import { getAllClasses } from "../../../../services/classesService";




const OfficerAssignments = ({ user }) => {

    const assignmentOptions = getAssignmentOptions('ROLE_OFFICER');
    const [selectedOption, setSelectedOption] = useState(assignmentOptions[0]);

    // const [courseList, setCourseList] = useState([]);
    // const [classList, setClassList] = useState([]);

    // useEffect(() => {
    //     console.log("get classes and courses");
    //     // console.log(user.accessToken);
    //     const loadData = async () => {
    //         try {
    //             const classesData = await getAllClasses(user.accessToken);
    //             console.log("classes : ", classesData);
    //             const coursesData = await getAllCourses(user.accessToken);
    //             console.log("courses : ", coursesData);
    //             setClassList(classesData);
    //             setCourseList(coursesData);
    //         } catch(error) {
    //             console.log(error);
    //         }
    //     }
    //     loadData();
    // }, []);


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