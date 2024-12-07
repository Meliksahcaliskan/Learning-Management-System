import './NewAssignment.css';
import deleteIcon from '/icons/delete.svg';

import { useContext, useEffect, useState } from 'react';
import { AuthContext } from '../../../../../contexts/AuthContext';

import { getAllClasses, getClasses, getTeacherClasses } from '../../../../../services/classesService';
import { getAllSubjectsOf } from '../../../../../services/coursesService';
import { createAssignment, uploadDocument } from '../../../../../services/assignmentService';

import { isDateInFuture } from '../../../../../utils/dateUtils';
import { calculateFileSize, isAllowedFileType } from '../../../../../utils/fileUtils';




// TODOs : if file upload failed after the assignment is created, delete the assignment and display proper error message

const NewAssignment = () => {
    const { user } = useContext(AuthContext);

    const [allClasses, setAllClasses] = useState([]);
    const [allSubjectsOfClass, setAllSubjectsOfClass] = useState([]);

    const [assignmentClass, setAssignmentClass] = useState({name : '', id : null});
    const [classError, setClassError] = useState('');

    const [assignmentSubject, setAssignmentSubject] = useState({name : '', id : null});
    const [subjectError, setSubjectError] = useState('');

    const [assignmentDueDate, setAssignmnentDueDate] = useState('');
    const [dateError, setDateError] = useState('');

    const [assignmentTitle, setAssignmentTitle] = useState('');
    const [titleError, setTitleError] = useState('');

    const [assignmentDescription, setAssignmentDescription] = useState('');

    const [assignmentDocument, setAssignmentDocument] = useState('');
    const [fileError, setFileError] = useState('');

    const [creationError, setCreationError] = useState(false);
    const [creationSuccess, setCreationSuccess] = useState(false);

    const [fetchError, setFecthError] = useState(false);
    const [uploadError, setUploadError] = useState(false);

    useEffect(() => {
        getClasses(user.role, user.accessToken)
            .then(response => {
                setAllClasses(response);
            })
            .catch(error => {
                console.log(error);
                setFecthError(true);
            })
    }, [user.accessToken]);

    const clearMessages = () => {
        setCreationError(false);
        setCreationSuccess(false);
    }

    const loadCourses = async (classID) => {
        getAllSubjectsOf(classID, user.accessToken)
            .then(data => {
                setAllSubjectsOfClass(data);
                setAssignmentSubject({name : '', id : null});
            })
            .catch(error => {
                console.error(error);
                setFecthError(true);
            });
    };

    const handleClassChange = (event) => {
        const selectedOption = event.target.options[event.target.selectedIndex];
        const newClassName = selectedOption.value;
        const newClassID = selectedOption.getAttribute('data-key');
        setAssignmentClass({name : newClassName, id : newClassID});
        loadCourses(newClassID);
        setClassError('');
        clearMessages();
    };

    const handleSubjectChange = (event) => {
        const selectedOption = event.target.options[event.target.selectedIndex]
        const newSubjectName = selectedOption.value;
        const newSubjectID = selectedOption.getAttribute('data-key');
        setAssignmentSubject({name : newSubjectName, id : newSubjectID});
        setSubjectError('');
        clearMessages();
    };

    const handleDueDateChange = (event) => {
        const dateInput = event.target.value;
        setDateError('');
        setAssignmnentDueDate(dateInput);
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

        if(file) {

            if(!isAllowedFileType(file.type)) {
                setFileError("Dosya tipi desteklenmiyor. Yalnızca PDF, Word veya metin dosyaları yükleyebilirsiniz.");
                setAssignmentDocument(null);
                return;
            }

            if(calculateFileSize(file) >= 5) {
                setFileError("Dosya boyutu 5 MB'den büyük olamaz.");
                setAssignmentDocument(null);
                return;
            }
            setAssignmentDocument(file);
            setFileError('');
        }
    };

    const handleFileRemove = () => {
        setAssignmentDocument(null);
    };

    const handleSubmit = async () => {
        let hasError = false;

        if (!assignmentClass.name) {
            setClassError('Sınıf seçimi yapınız.');
            hasError = true;
        }
        if (!assignmentSubject.name) {
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

        if(!assignmentDueDate || !isDateInFuture(assignmentDueDate)) {
            setDateError('Bitiş tarihi gelecekte olmalıdır.');
            hasError = true;
        }
        if (!hasError) {
            const payload = {
                teacherId: user.id,
                title: assignmentTitle,
                description: assignmentDescription,
                dueDate: assignmentDueDate,
                classId: assignmentClass.id,
                courseId: assignmentSubject.id,
                document: null,
            };

            createAssignment(payload, user.accessToken)
                .then(response => {
                    if(response.success) {
                        if(assignmentDocument) {
                            const assignmentID = response.data.id;
                            uploadDocument(assignmentID, assignmentDocument, user.accessToken)
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
        setAssignmentClass({name : '', id : null});
        setAssignmentSubject({name : '', id : null});
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
                    value={assignmentClass.name}
                    onChange={handleClassChange}
                >
                    <option value="" data-key={null} disabled>Sınıf Seçiniz</option>
                    {allClasses.map((singleClass) => (
                        <option value={singleClass.name} key={singleClass.id} data-key={singleClass.id}>
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
                    value={assignmentSubject.name}
                    onChange={handleSubjectChange}
                    disabled={assignmentClass.name === ''}
                >
                    <option value="" disabled>Ders Seçiniz</option>
                    {allSubjectsOfClass &&
                        allSubjectsOfClass.map((singleSubject) => (
                            <option value={singleSubject.name} key={singleSubject.id} data-key={singleSubject.id}>
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
                    <button type="submit" className="delete-icon" onClick={handleFileRemove}><img src={deleteIcon} alt="remove file" /></button>
                </div>
            ) : (
                <div className="input-container">
                    <label className="label">Döküman</label>
                    <input
                        className='input'
                        type='file'
                        accept=".pdf, .doc, .docx, .txt"
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