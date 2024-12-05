import { useContext, useState } from "react";
import arrowDown from '/icons/arrow-down.svg';
import arrowUp from '/icons/arrow-up.svg';
import deleteIcon from '/icons/delete.svg';

import './SingleAssignment.css';
import { submitAssignment, unsubmitStudentAssignment } from "../../../../../services/assignmentService";
import { AuthContext } from "../../../../../contexts/AuthContext";

const SingleAssigment = ({ assignment, refreshAssignments }) => {
    const { user } = useContext(AuthContext);

    const [isExpanded, setIsExpanded] = useState(false);

    const [uploadedFile, setuploadedFile] = useState(null);


    const handleFileUpload = (event) => {
        const file = event.target.files[0];
        setuploadedFile(file);
    }

    const handleAssignmentSubmit = async () => {

        const fileData = new FormData();
        fileData.append('document', uploadedFile);
        fileData.append('submissionComment', 'submit comment goes here');

        submitAssignment(assignment.id, fileData, user.accessToken)
            .then(response => {
                console.log(response);
                refreshAssignments();
            })
            .catch(error => {
                console.log(error);
            })
    }
    
    const handleAssignmentUnsubmit = async () => {
        console.log("handling assignment unsubmit");
        unsubmitStudentAssignment(assignment.id, user.accessToken)
            .then(response => {
                console.log(response);
                refreshAssignments();
            })
            .catch(error => {
                console.log(error);
            })

    }

    const handleDocumentRemoval = () => {
        console.log("removing uploaded file");
        setuploadedFile(null);
    }

    // TODO
    const handleDocumentDownload = () => {
        console.log("handling document download");
    }

    return(
        <div className="assignment-container">
            <div className="assignment-header" onClick={() => setIsExpanded((prev) => !prev)}>
                <div className="assignment-header-info">
                    <img src="https://placeholder.pics/svg/32x32" alt="icon" />
                    <span className="assignment-subject">{assignment.courseName}</span>
                    <span className="assignment-title">{assignment.title}</span>
                    <span className="assignment-dueDate">{(new Date(assignment.createdDate)).toLocaleDateString("en-GB")} - {(new Date(assignment.dueDate)).toLocaleDateString("en-GB")}</span>
                </div>
                <button className="expand-btn">
                    <img src={isExpanded ? arrowUp : arrowDown} alt="toggle assignment details" />
                </button>
            </div>
            {isExpanded && (
                <div className="assignment-body">
                    <div style={{border : '1px solid grey'}}></div>

                    <div className="assignment-body-section">
                        <label className="assignment-section-title">Açıklama</label>
                        {assignment.description ? (
                            <p className="assignment-section-text">{assignment.description}</p>
                        ) : (
                            <i className="assignment-section-text">Açıklama yok.</i>
                        )}
                    </div>

                    <div className="assignment-body-section">
                        <label className="assignment-section-title">Yardımcı materyaller</label>
                        {assignment.teacherDocuments ? (
                            <span className="assignment-document" onClick={handleDocumentDownload}>{assignment.teacherDocuments.fileName}</span>
                        ) : (
                            <i className="assignment-section-text">Döküman eklenmedi.</i>
                        )}
                    </div>
                    
                    {assignment.status === 'PENDING' && (
                        <>
                            <div className="assignment-body-section">
                                {uploadedFile ? (
                                    <>
                                        <label className="assignment-section-title">Yüklenen döküman</label>
                                        <div className="assignment-document-container">
                                            <span className="assignment-document">{uploadedFile.name}</span>
                                            <button type="submit" className="delete-btn" onClick={handleDocumentRemoval}><img src={deleteIcon} alt="remove file"/></button>
                                        </div>
                                    </>
                                ) : (
                                    <>
                                        <label className="assignment-section-title">Döküman ekle</label>
                                        <input type="file" onChange={handleFileUpload}/>
                                    </>
                                )}
                            </div>
                            <button className="btn" onClick={handleAssignmentSubmit}>Teslim Et</button>
                        </>
                    )}

                    {(assignment.status !== 'PENDING' && assignment.studentSubmissions) &&
                        <div className="assignment-body-section">
                            <label className="assignment-section-title">Eklenen dökümanlar</label>
                            <span className="assignment-document" onClick={handleDocumentDownload}>{assignment.studentSubmissions.fileName}</span>
                        </div>
                    }
                    {assignment.status === 'SUBMITTED' &&
                        <button className="btn" onClick={handleAssignmentUnsubmit}>Teslimi geri al</button>
                    }
                    {assignment.status === 'GRADED' &&
                        <div className="assignment-body-section">
                            <label className="assignment-section-title">Ödev sonucu</label>
                            {assignment.grade ? (
                                <span className="assignment-grade">{assignment.grade}/100</span>
                            ) : (
                                <i>Daha sonuçlandırılmadı</i>
                            )}
                        </div>
                    }
                </div>
            )}
        </div>
    );
}
export default SingleAssigment;