import './AssignmentSearch.css';
import { useState } from 'react';


const AssignmentSearch = ({onSearchResults}) => {






    const [searchData, setSearchData] = useState({
        className : '',
        subjectName : '',
        endDate : ''
    });


    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setSearchData({
          ...searchData,
          [name]: value,
        });
    }

    const handleSearch = () => {
        console.log(searchData);

        // check validity of the search



        // onSearchResults(reponse of the api call);
    }


    return(
        <div className="homework-search">
            <div className="search-options">
                <div className="input-container">
                    <label className='label'>Sınıf Adı</label>
                    <select 
                        className='input'
                        name='className'
                        onChange={handleInputChange}
                        value={searchData.className}
                    >
                        {/* options */}
                        <option value="">Sınıf seçiniz</option>
                        <option value="classIDA">12-A</option>
                        <option value="classIDB">12-B</option>
                        <option value="classIDC">12-C</option>
                        <option value="classIDD">12-D</option>
                        <option value="classIDE">12-E</option>
                    </select>
                </div>

                <div className="input-container">
                    <label className="label">Ders Adı</label>
                    <select 
                        className="input"
                        name='subjectName'
                        onChange={handleInputChange}
                        value={searchData.subjectName}
                    >
                        {/* options */}
                        <option value="">Ders seçiniz</option>
                        <option value="subjectID">Türkçe</option>
                        <option value="subjectID">Matematik</option>
                        <option value="subjectID">Tarih</option>
                        <option value="subjectID">Geometri</option>
                        <option value="subjectID">Felsefe</option>
                    </select>
                </div>
                <div className="input-container">
                    <label className='label'>Bitiş Tarihi</label>
                    <input
                        className="input"
                        type="date"
                        name='endDate'
                        onChange={handleInputChange}    
                        value={searchData.endDate}
                    />
                </div>
            </div>
            <button className="save-btn btn" onClick={handleSearch}>Ara</button>
        </div>
    );

}
export default AssignmentSearch;