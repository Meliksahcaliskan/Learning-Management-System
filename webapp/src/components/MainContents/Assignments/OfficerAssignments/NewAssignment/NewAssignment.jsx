import './NewAssignment.css';
import { getAllClasses } from '../../../../../services/classesService';
import { useContext, useEffect, useState } from 'react';
import { AuthContext } from '../../../../../contexts/AuthContext';
import { getAllSubjectsOf } from '../../../../../services/coursesService';
import { isDateInFuture } from '../../../../../utils/dateUtils';
import { calculateFileSize } from '../../../../../utils/fileUtils';
import deleteIcon from '/icons/delete.svg';
import { createAssignment } from '../../../../../services/assignmentService';


const NewAssignment = () => {
    const { user } = useContext(AuthContext);

    const [allClasses, setAllClasses] = useState([]);
    const [allSubjectsOfClass, setAllSubjectsOfClass] = useState([]);

    const [assignmentClass, setAssignmentClass] = useState('');
    const [assignmentSubject, setAssignmentSubject] = useState('');
    
    const [assignmentDueDate, setAssignmnentDueDate] = useState('');
    const [dateError, setDateError] = useState('');

    const [assignmentTitle, setAssignmentTitle] = useState('');
    const [titleError, setTitleError] = useState('');

    const [assignmentDescription, setAssignmentDescription] = useState('');

    const [assignmentDocument, setAssignmentDocument] = useState(null);
    const [fileError, setFileError] = useState('');

    useEffect(() => {
        getAllClasses(user.accessToken)
            .then(data => setAllClasses(data))
            .catch(error => console.error(error));
    }, [user.accessToken]);

    const loadCourses = async (className) => {
        const classID = allClasses.find(singleClass => singleClass.name === className)?.id;
        if (classID) {
            getAllSubjectsOf(user.accessToken, classID)
                .then(data => {
                    setAllSubjectsOfClass(data);
                    setAssignmentSubject(''); 
                })
                .catch(error => console.error(error));
        }
    };

    const handleClassChange = (event) => {
        const newClassName = event.target.value;
        setAssignmentClass(newClassName);
        loadCourses(newClassName); 
    };

    const handleSubjectChange = (event) => {
        setAssignmentSubject(event.target.value);
    };

    const handleDueDateChange = (event) => {
        const dateInput = event.target.value;
        if (isDateInFuture(dateInput)) {
            setAssignmnentDueDate(dateInput); 
            setDateError('');    
        }else {
            setDateError('Bitiş tarihi gelecekte olmalıdır.');
            setAssignmnentDueDate('');
        }
    };

    const handleTitleChange = (event) => {
        const newTitle = event.target.value;
        setAssignmentTitle(newTitle);
        setTitleError('');
    }

    const handleDescriptionChange = (event) => {
        const newDescription = event.target.value;
        setAssignmentDescription(newDescription);
    }

    const handleFileChange = (event) => {
        const file = event.target.files[0];
        if(file && calculateFileSize(file) < 10) {
            setAssignmentDocument(file);
            setFileError('');
        }else {
            setFileError("Dosya boyutu 10 MB'den büyük olamaz.");
        }
    }

    const handleFileRemove = () => {
        setAssignmentDocument(null);
    }

    const handleSubmit = async () => {
        if(assignmentTitle.trim().length >= 3) {
            console.log("make the api call")
            const response = await createAssignment({
                teacherId : user.id,
                title : assignmentTitle,
                description : assignmentDescription,
                dueDate : assignmentDueDate,
                className : assignmentClass,
                courseName : assignmentSubject,
                date : new Date().toISOString().split("T")[0],
                //document???
            }, user.accessToken);
            console.log(response);
        }else {
            setTitleError('Başlık 3 karakterden fazla olmalıdır.');
            setAssignmentTitle('');
        }
    }

    return (
        <div className="newAssignmentForm">
            <div className="input-container">
                <label className="label">Sınıf Adı</label>
                <select
                    className="input"
                    value={assignmentClass}
                    onChange={handleClassChange}
                >
                    <option value="" disabled>Sınıf Seçiniz</option>
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
                    className='input'
                    value={assignmentSubject}
                    onChange={handleSubjectChange}
                    disabled={assignmentClass === ''}
                >
                    <option value="" disabled>Ders Seçiniz</option>
                    {allSubjectsOfClass.map((singleSubject) => (
                        <option value={singleSubject.name} key={singleSubject.id}>
                            {singleSubject.name}
                        </option>
                    ))}
                </select>
            </div>

            <div className="input-container">
                <label className="label">Bitiş Tarihi</label>
                <input
                    className='input'
                    type='date'
                    value={assignmentDueDate}
                    onChange={handleDueDateChange}
                    />
                {dateError && <p className='error-message'>{dateError}</p>}
            </div>

            <div className="input-container">
                <label className="label">Başlık</label>
                <input
                    className='input'
                    type='text'
                    value={assignmentTitle}
                    onChange={handleTitleChange}
                />
                {titleError && <p className='error-message'>{titleError}</p>}
            </div>

            <div className="input-container">
                <label className="label">Açıklama</label>
                <input
                    className='input'
                    type='text'
                    value={assignmentDescription}
                    onChange={handleDescriptionChange}
                />
            </div>

            {/* <div className="input-container">
                <label className="label">Döküman</label>
                <div  style={{display : 'flex'}}>
                    <input
                        className='input'
                        type='file'
                        onChange={handleFileChange}
                    />
                    {assignmentDocument && 
                        <button
                            className='delete-btn'
                            type='submit'
                            onClick={handleFileRemove}
                        >
                            delete
                        </button>}
                </div>
                {fileError && <p className='error-message'>{fileError}</p>}
            </div> */}
            {assignmentDocument ? (
                <div style={{display : 'flex'}}>
                    <span className="assignment-document">{assignmentDocument.name}</span>
                    <button type="submit" className="delete-btn" onClick={handleFileRemove}><img src={deleteIcon} alt="remove file"/></button>
                </div>
            ) : (
                <div className="input-container">
                    <label className="label">Döküman</label>
                    <input
                        className='input'
                        type='file'
                        onChange={handleFileChange}
                    />
                </div>
            )}

            <button
                type='submit'
                className="btn"
                onClick={handleSubmit}
            >
                Oluştur
            </button>
        </div>
    );
};

export default NewAssignment;



// const NewAssignment = () => {

//     const { user } = useContext(AuthContext);
//     const [classes, setClasses] = useState([]);
//     const [courses, setCourses] = useState([]);


//     const [assignmentData, setAssignmentData] = useState({
//         className : '',
//         subjectName : '',
//         dueDate : '',
//         title : '',
//         description : '',
//         document : null
//     });

//     useEffect(() => {
//         const loadClasses = async () => {
//             try {
//                 const response = await getAllClasses(user.accessToken);
//                 setClasses(response); 
//                 console.log(response);
//             } catch(error){
//                 console.log(error);
//             }
//         }
//         loadClasses();
//     }, []);


//     const handleInputChange = (e) => {
//         const { name, value } = e.target;
//         setAssignmentData({
//           ...assignmentData,
//           [name]: value,
//         });
//         if(name === "className") {
            
//         }
//       };


//       const handleFileUpload = (e) => {
//         console.log(e.target.files[0]);
//         setAssignmentData({
//             ...assignmentData,
//             document : e.target.files[0],
//         });
//       };

//     const handleSubmit = async () => {
//         // check the validity of the inputs
//         // if it is not give error
//         // highlight the invalid inputs
//         //else make the api call
//         //if it is not successful give proper error message
//         console.log("handling submit");
//         checkAssignmentData();
//     }

//     const checkAssignmentData = () => {
//         //no need to check class name cause it will be a list to be seelcted
//         //same for subjectName
//         //check if the end date is in the future
//         //check if the title is not empty
//         //document is optional
//         console.log("checking assignment data");
//         console.log(assignmentData.className ? "valid c" : "non valid c")
//         console.log(assignmentData.subjectName ? "valid s" : "non valid s")

        
//     }

//     return(
//         <div className="newAssignmentForm">
//                 <div className="input-container">
//                     <label className="label">Sınıf Adı</label>
//                     <select
//                         className="input"
//                         value={assignmentData.className}
//                         onChange={handleInputChange}
//                         name='className'
//                     >
//                     <option value="" disabled>Sınıf Seçiniz</option>
//                     {classes.map((singleClass) => (
//                         <option value={singleClass.name} key={singleClass.id}>
//                             {singleClass.name}
//                         </option>
//                     ))}
//                     </select>
//                 </div>

//                 <div className="input-container">
//                     <label className="label">Ders Adı</label>
//                     <select 
//                         className="input"
//                         value={assignmentData.subjectName}
//                         onChange={handleInputChange}
//                         name='subjectName'
//                         disabled={assignmentData.className === ''}
//                     >
//                         <option value="" disabled>Ders Seçiniz</option>
//                         {courses.map((singleCourse) => (
//                             <option value={singleCourse.name} key={singleCourse.id}>
//                                 {singleCourse.name}
//                             </option>
//                         ))}
//                     </select>
//                 </div>
//                 <div className="input-container">
//                     <label className="label">Bitiş Tarihi</label>
//                     <input
//                         className="input"
//                         type='date'
//                         placeholder=''
//                         onChange={handleInputChange}
//                         value={assignmentData.dueDate}
//                         name='dueDate'
//                     />
//                 </div>
//                 <div className="input-container">
//                     <label className="label">Başlık</label>
//                     <input 
//                         className="input"
//                         type='text'
//                         placeholder='Ödev başlığını giriniz'
//                         onChange={handleInputChange}
//                         value={assignmentData.title}
//                         name='title'
//                     />
//                 </div>
//                 <div className="input-container">
//                     <label className='label'>Açıklama</label>
//                     <input 
//                         className="input"
//                         type='text'
//                         placeholder='Ödev açıklamasını giriniz'
//                         onChange={handleInputChange}
//                         value={assignmentData.description}
//                         name='description'
//                     />
//                 </div>
//                 <div className="input-container">
//                     <label className="label">Döküman Ekle</label>
//                     <input
//                         className="input"
//                         type="file"
//                         onChange={handleFileUpload}
//                         name='document'
//                     />
//                 </div>
//                 <button type="submit" className="save-btn btn" onClick={handleSubmit}>Kaydet</button>
//         </div>
//     );
// }
// export default NewAssignment;
