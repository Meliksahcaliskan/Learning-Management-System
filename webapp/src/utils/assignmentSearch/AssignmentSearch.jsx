import { AuthContext } from '../../contexts/AuthContext';
import { getAssignmentsForTeacher } from '../../services/assignmentService';
import { getAllClasses, getTeacherClasses } from '../../services/classesService';
import { getAllSubjectsOf } from '../../services/coursesService';
import { isDateInFuture } from '../dateUtils';
import './AssignmentSearch.css';
import { useContext, useEffect, useState } from 'react';


const AssignmentSearch = ({onSearchResults}) => {
    const { user } = useContext(AuthContext);

    const [allClasses, setAllClasses] = useState([]);
    const [allSubjectsOfClass, setAllSubjectsOfClass] = useState([]);

    const [searchClass, setSearchClass] = useState('');
    const [searchSubject, setSearchSubject] = useState('');

    const [searchDueDate, setSearchDueDate] = useState('');
    const [dateError, setDateError] = useState('');

    useEffect(() => {
        if(user.role === 'ROLE_TEACHER') {
            getTeacherClasses(user.accessToken)
                .then(data => {
                    setAllClasses(data)})
                .catch(error => {
                    console.log(error);
                })
        }else {
            getAllClasses(user.accessToken)
                .then(data => setAllClasses(data))
                .catch(error => {
                    console.log(error);
                })
        }
    }, [user.accessToken]);

    const handleClassChange = (event) => {
        const newClassName = event.target.value;
        if(newClassName === '') {
            setAllSubjectsOfClass([]);
            setSearchSubject('');
        }else {
            loadCourses(newClassName);  
        }
        setSearchClass(newClassName);
    }

    const loadCourses = async (className) => {
        const classID = allClasses.find(singleClass => singleClass.name === className)?.id;
        getAllSubjectsOf(classID, user.accessToken)
            .then(data => {
                setAllSubjectsOfClass(data);
            })
            .catch(error => {
                console.error(error);
            });
    };

    const handleSubjectChange = (event) => {
        setSearchSubject(event.target.value);
    }

    const handleDueDateChange = (event) => {
        const dateInput = event.target.value;
        if(isDateInFuture(dateInput)) {
            setSearchDueDate(dateInput);
            setDateError('');
        } else {
            setDateError('Bitiş tarihi gelecekte olmalıdır.');
            setSearchDueDate('');
        }
    }


    const handleSearch = () => {
        console.log('searching');
        
        // if the user is a teacher get all assignments created by the teacher
        //if the user is an admin or a coordinator get all assignments in the system
        //based on the search inputs filter the retrieved assignments
        //return the filtered list
        if(user.role === 'ROLE_TEACHER') {
            getAssignmentsForTeacher(user.id, user.accessToken)
                .then(data => {
                    console.log("retrieved successfully");
                })
                .catch(error => {
                    console.log(error);
                })
        }

        
        // onSearchResults("ewfgewfeefewfw");
    }

    useEffect(() => {


    }, []);


    return(
        <div className="homework-search">
            <div className="search-options">
                <div className="input-container">
                    <label className='label'>Sınıf Adı</label>
                    <select 
                        className='input'
                        name='className'
                        onChange={handleClassChange}
                        value={searchClass}
                    >
                        <option value="">Sınıf seçiniz</option>
                        {allClasses.map((singleClass) => (
                            <option value={singleClass.name} key={singleClass.id}>
                                {singleClass.name}
                            </option>
                        ))}
                    </select>
                </div>

                <div className="input-container">
                    <label className="label">Ders Adı</label>
                    <select 
                        className="input"
                        name='subjectName'
                        onChange={handleSubjectChange}
                        value={searchSubject}
                        disabled={searchClass === ''}
                    >
                        <option value="">Ders seçiniz</option>
                        {allSubjectsOfClass &&
                            allSubjectsOfClass.map((singleSubject) => (
                                <option value={singleSubject.name} key={singleSubject.id}>
                                    {singleSubject.name}
                                </option>
                        ))}
                    </select>
                </div>
                <div className="input-container">
                    <label className='label'>Bitiş Tarihi</label>
                    <input
                        className="input"
                        type="date"
                        name='endDate'
                        onChange={handleDueDateChange}    
                        value={searchDueDate}
                    />
                    {dateError && <p className='error-message'>{dateError}</p>}
                </div>
            </div>
            <button className="save-btn btn" onClick={handleSearch}>Ara</button>
        </div>
    );

}
export default AssignmentSearch;