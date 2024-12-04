import './NewAssignment.css';
import deleteIcon from '/icons/delete.svg';

import { useContext, useEffect, useState } from 'react';
import { AuthContext } from '../../../../../contexts/AuthContext';

import { getAllClasses, getTeacherClasses } from '../../../../../services/classesService';
import { getAllSubjectsOf } from '../../../../../services/coursesService';
import { createAssignment, uploadDocument } from '../../../../../services/assignmentService';

import { isDateInFuture } from '../../../../../utils/dateUtils';
import { calculateFileSize } from '../../../../../utils/fileUtils';

const NewAssignment = () => {
    const { user } = useContext(AuthContext);

    const [allClasses, setAllClasses] = useState([]);
    const [allSubjectsOfClass, setAllSubjectsOfClass] = useState([]);

    const [assignmentClass, setAssignmentClass] = useState('');
    const [classError, setClassError] = useState('');

    const [assignmentSubject, setAssignmentSubject] = useState('');
    const [subjectError, setSubjectError] = useState('');

    const [assignmentDueDate, setAssignmnentDueDate] = useState('');
    const [dateError, setDateError] = useState('');

    const [assignmentTitle, setAssignmentTitle] = useState('');
    const [titleError, setTitleError] = useState('');

    const [assignmentDescription, setAssignmentDescription] = useState('');

    const [assignmentDocument, setAssignmentDocument] = useState(null);
    const [fileError, setFileError] = useState('');

    const [creationError, setCreationError] = useState(false);
    const [creationSuccess, setCreationSuccess] = useState(false);

    const [fetchError, setFecthError] = useState(false);
    const [uploadError, setUploadError] = useState(false);

    useEffect(() => {
        if(user.role === 'ROLE_TEACHER') {
            getTeacherClasses(user.accessToken)
                .then(data => {
                    setAllClasses(data)})
                .catch(error => {
                    console.log(error);
                    setFecthError(true)
                })
        }else {
            getAllClasses(user.accessToken)
                .then(data => setAllClasses(data))
                .catch(error => {
                    console.log(error);
                    setFecthError(true);
                })
        }
        console.log();
    }, [user.accessToken]);

    const clearMessages = () => {
        setCreationError(false);
        setCreationSuccess(false);
    }

    const loadCourses = async (className) => {
        const classID = allClasses.find(singleClass => singleClass.name === className)?.id;
        getAllSubjectsOf(classID, user.accessToken)
            .then(data => {
                setAllSubjectsOfClass(data);
                setAssignmentSubject('');
            })
            .catch(error => {
                console.error(error);
                setFecthError(true);
            });
    };

    const handleClassChange = (event) => {
        const newClassName = event.target.value;
        setAssignmentClass(newClassName);
        loadCourses(newClassName);
        setClassError('');
        clearMessages();
    };

    const handleSubjectChange = (event) => {
        setAssignmentSubject(event.target.value);
        setSubjectError('');
        clearMessages();
    };

    const handleDueDateChange = (event) => {
        const dateInput = event.target.value;
        if (isDateInFuture(dateInput)) {
            setAssignmnentDueDate(dateInput);
            setDateError('');
            clearMessages();
        } else {
            setDateError('Bitiş tarihi gelecekte olmalıdır.');
            setAssignmnentDueDate('');
        }
    };

    const handleTitleChange = (event) => {
        const newTitle = event.target.value;
        setAssignmentTitle(newTitle);
        setTitleError('');
        clearMessages();
    };

    const handleDescriptionChange = (event) => {
        const newDescription = event.target.value;
        setAssignmentDescription(newDescription);
    };

    const handleFileChange = (event) => {
        const file = event.target.files[0];
        if (file && calculateFileSize(file) < 10) {
            setAssignmentDocument(file);
            setFileError('');
        } else {
            setFileError("Dosya boyutu 10 MB'den büyük olamaz.");
            setAssignmentDocument(null);
        }
    };

    const handleFileRemove = () => {
        setAssignmentDocument(null);
    };

    const handleSubmit = async () => {
        let hasError = false;

        if (!assignmentClass) {
            setClassError('Sınıf seçimi yapınız.');
            hasError = true;
        }
        if (!assignmentSubject) {
            setSubjectError('Ders seçimi yapınız');
            hasError = true;
        }
        if (!assignmentDueDate) {
            setDateError('Bitiş tarihi seçiniz.');
            hasError = true;
        }
        if (!assignmentTitle) {
            setTitleError('Ödev Başlığı giriniz.');
            hasError = true;
        }
        if (assignmentTitle.trim().length < 3) {
            setTitleError('Ödev başlığı 3 karakterden fazla olmalıdır.');
            hasError = true;
        }
        if (!hasError) {
            const payload = {
                teacherId: user.id,
                title: assignmentTitle,
                description: assignmentDescription,
                dueDate: assignmentDueDate,
                className: assignmentClass, // TODO: it will change to classID later on
                courseName: assignmentSubject,
                document: null,
            };

            createAssignment(payload, user.accessToken)
                .then(response => {
                    console.log('assignment creation response ',response);
                    if(response.success) {
                        if(assignmentDocument) {
                            const assignmentID = response.data.id;
                            uploadDocument(assignmentID, assignmentDocument, true, user.accessToken)
                                .then(response => {
                                    console.log('upload document to assignment response', response);
                                })
                                .catch(error => {
                                    setUploadError(true);
                                });
                        }
                        resetForm();
                        setCreationSuccess(true);
                    }
                })
                .catch(error => {
                    setCreationError(true);
                })
        }
    };

    const resetForm = () => {
        setAssignmentClass('');
        setAssignmentSubject('');
        setAssignmnentDueDate('');
        setAssignmentTitle('');
        setAssignmentDescription('');
        setAssignmentDocument(null);
    }

    return (
        <>
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
                {classError && <p className='error-message'>{classError}</p>}
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
                    {allSubjectsOfClass &&
                        allSubjectsOfClass.map((singleSubject) => (
                            <option value={singleSubject.name} key={singleSubject.id}>
                                {singleSubject.name}
                            </option>
                    ))}
                </select>
                {subjectError && <p className='error-message'>{subjectError}</p>}
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

            {assignmentDocument ? (
                <div style={{ display: 'flex' }}>
                    <span className="assignment-document">{assignmentDocument.name}</span>
                    <button type="submit" className="delete-btn" onClick={handleFileRemove}><img src={deleteIcon} alt="remove file" /></button>
                </div>
            ) : (
                <div className="input-container">
                    <label className="label">Döküman</label>
                    <input
                        className='input'
                        type='file'
                        onChange={handleFileChange}
                        value=''
                    />
                    {fileError && <p className='error-message'>{fileError}</p>}
                </div>
            )}

            <button
                type='submit'
                className="btn"
                onClick={handleSubmit}
            >
                Oluştur
            </button>
            {creationError && 
                <p className='error-message' style={{ whiteSpace : 'pre-line'}}>
                    Ödev oluşturulukren hata! <br />
                    Aynı başlığa sahip bir ödev olabilir. <br />
                </p>
            }
            {creationSuccess && <p className='success-message'>Ödev başarıyla oluşturuldu.</p>}
        </div>
        {fetchError && 
            <p className='error-message'>
                Sayfa yüklenirken hata oluştu! <br/>
                Lütfen sayfayı yenileyiniz. <br />
            </p>
        }
        {uploadError &&
            <p className='error-message'>
                Döküman eklenemedi!
            </p>
        }
        </>
    );
};
export default NewAssignment;