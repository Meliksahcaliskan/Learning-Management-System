import { AuthContext } from '../../contexts/AuthContext';
import { getAssignments, getAssignmentsForTeacher } from '../../services/assignmentService';
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
            console.log(dateInput)
        } else {
            setDateError('Bitiş tarihi gelecekte olmalıdır.');
            setSearchDueDate('');
        }
    }

    const handleSearch = async () => {
        getAssignments(user.role, user.id, user.accessToken)
            .then(response => {
                onSearchResults(filterAssignments(response.data))
            })
            .catch(error => {
                console.log(error);
            })
    }

    const filterAssignments = (assignments) => {
        if(searchClass) {
            assignments = assignments.filter(assignment => assignment.className === searchClass);
        }
        if(searchSubject) {
            assignments = assignments.filter(assignment => assignment.courseName === searchSubject);
        }
        if(searchDueDate) {
            const targetDate = new Date(searchDueDate);
            assignments = assignments.filter(assignment => new Date(assignment.dueDate) <= targetDate);
        }
        assignments = assignments.filter(assignment => new Date(assignment.dueDate) > new Date());
        return assignments;
    }

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