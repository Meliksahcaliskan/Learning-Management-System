import { getAssignmentOptions } from "../../../../utils/assignmentOptions";
import Navigator from "../../../common/Navigator/Navigator";

import { useEffect, useState } from "react";
import { fetchCourses } from "../../../../services/coursesService";
import { fetchClasses } from "../../../../services/classesService";




const OfficerAssignments = ({ user }) => {

    const assignmentOptions = getAssignmentOptions('ROLE_OFFICER');
    const [selectedOption, setSelectedOption] = useState(assignmentOptions[0]);

    //get class list
    //get course list
    const [courseList, setCourseList] = useState([]);
    const [classList, setClassList] = useState([]);

    useEffect(() => {
        const loadLists = async () => {
            try {
                const [coursesData, classesData] = await Promise.all([fetchCourses(), fetchClasses()]);
                setCourseList(coursesData);
                setClassList(classesData);
            }catch (error) {

            }
        }
        loadLists();

    }, []);


    return(
        <>
            <Navigator
                options={assignmentOptions}
                onSelect={(option) => setSelectedOption(option)}
            />

            {selectedOption.component && <selectedOption.component user={user} classes={classList} courses={courseList}/>}
        </>
    );
}
export default OfficerAssignments;