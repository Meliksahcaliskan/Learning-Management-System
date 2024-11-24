import { useState } from "react";
import arrowDown from '/icons/arrow-down.svg';
import arrowUp from '/icons/arrow-up.svg';
import deleteIcon from '/icons/delete.svg';

import './SingleAssignment.css';

const SingleAssigment = ({ assignment }) => {

    const [isExpanded, setIsExpanded] = useState(false);

    const [uploadedFile, setuploadedFile] = useState(null);


    const handleFileUpload = (event) => {
        console.log("handling file upload");
        const file = event.target.files[0];
        setuploadedFile(file);
    }

    const handleAssignmentSubmit = () => {
        console.log("handling assignment submit");
    }
    
    const handleAssignmentUnsubmit = () => {
        console.log("handling assignment unsubmit");
    }

    const handleDocumentRemoval = () => {
        console.log("removing uploaded file");
        setuploadedFile(null);
    }

    return(
        <div className="assignment-container">
            <div className="assignment-header">
                <div className="assignment-header-info">
                    <img src="https://placeholder.pics/svg/32x32" alt="icon" />
                    <span className="assignment-subject">{assignment.subject}</span>
                    <span className="assignment-title">{assignment.title}</span>
                    <span className="assignment-dueDate">{(new Date(assignment.dueDate)).toLocaleDateString("en-GB")}</span>
                </div>
                <button
                    className="expand-btn"
                    onClick={() => setIsExpanded((prev) => !prev)}   
                >
                    <img src={isExpanded ? arrowUp : arrowDown} alt="toggle assignment details" />
                </button>
            </div>
            {isExpanded && (
                <div className="assignment-body">
                    <div style={{border : '1px solid grey'}}></div>
                    <div className="assignment-body-section">
                        <label className="assignment-section-title">Açıklama</label>
                        <p className="assignment-section-text">{assignment.description}</p>
                    </div>
                    {assignment.document &&
                        <div className="assignment-body-section">
                            <label className="assignment-section-title">Yardımcı materyaller</label>
                            <span className="assignment-document">{assignment.document}</span>
                        </div>    
                    }
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
                                        <input type="file" onChange={handleFileUpload} />
                                    </>
                                )}
                            </div>
                            <button className="btn" onClick={handleAssignmentSubmit}>Teslim Et</button>
                        </>
                    )}

                    {(assignment.status !== 'PENDING' && assignment.uploadedDocument) &&
                        <div className="assignment-body-section">
                            <label className="assignment-section-title">Eklenen dökümanlar</label>
                            <span className="assignment-document">{assignment.uploadedDocument}</span>
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