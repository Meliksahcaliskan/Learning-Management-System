import './NewAssignment.css';
import { AuthContext } from '../../../../../contexts/AuthContext';
import assignmentService from '../../../../../services/assignmentService';
import { useState } from 'react';

const NewAssignment = ({user, classes, courses}) => {


    const [AssignmentData, setAssignmentData] = useState({
        className : '',
        subjectName : '',
        endDate : '',
        title : '',
        description : '',
        document : null
    });


    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setAssignmentData({
          ...AssignmentData,
          [name]: value,
        });
      };


      const handleFileUpload = (e) => {
        console.log(e.target.files[0]);
        setAssignmentData({
            ...AssignmentData,
            document : e.target.files[0],
        });
      };

    const handleSubmit = async () => {
        // check the validity of the inputs
        // if it is not give error
        // highlight the invalid inputs
        //else make the api call
        //if it is not successful give proper error message
        console.log(AssignmentData);
    }

    const checkAssignmentData = () => {
        //no need to check class name cause it will be a list to be seelcted
        //same for subjectName
        //check if the end date is in the future
        //check if the title is not empty
        //document is optional
    }

    return(
        <div className="newAssignmentForm">
                <div className="input-container">
                  <label className="label">Sınıf Adı</label>
                  <select
                    className="input"
                    value={AssignmentData.className}
                    onChange={handleInputChange}
                    name='className'
                  >
                    {/* get the list of classes of the school */}
                    {classes.map((singleClass) => (
                        <option value={singleClass.id} key={singleClass.id}>
                            {singleClass.name}
                        </option>
                    ))}

                  </select>
                </div>
                <div className="input-container">
                    <label className="label">Ders Adı</label>
                    <select 
                        className="input"
                        value={AssignmentData.subjectName}
                        onChange={handleInputChange}
                        name='subjectName'
                    >
                        {/* get the list of subjects */}
                        {courses.map((singleCourse) => (
                            <option value={singleCourse.id} key={singleCourse.id}>
                                {singleCourse.title}
                            </option>
                        ))}

                    </select>
                </div>
                <div className="input-container">
                    <label className="label">Bitiş Tarihi</label>
                    <input
                        className="input"
                        type='date'
                        placeholder=''
                        onChange={handleInputChange}
                        value={AssignmentData.endDate}
                        name='endDate'
                    />
                </div>
                <div className="input-container">
                    <label className="label">Başlık</label>
                    <input 
                        className="input"
                        type='text'
                        placeholder='Ödev başlığını giriniz'
                        onChange={handleInputChange}
                        value={AssignmentData.title}
                        name='title'
                    />
                </div>
                <div className="input-container">
                    <label className='label'>Açıklama</label>
                    <input 
                        className="input"
                        type='text'
                        placeholder='Ödev açıklamasını giriniz'
                        onChange={handleInputChange}
                        value={AssignmentData.description}
                        name='description'
                    />
                </div>
                <div className="input-container">
                    <label className="label">Döküman Ekle</label>
                    <input
                        className="input"
                        type="file"
                        onChange={handleFileUpload}
                        value={AssignmentData.document || ''}
                        name='document'
                    />
                </div>
                <button type="submit" className="save-btn btn" onClick={handleSubmit}>Kaydet</button>
        </div>
    );
}
export default NewAssignment;
