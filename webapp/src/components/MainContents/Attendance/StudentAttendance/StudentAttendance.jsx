import { useContext, useState } from "react";
import { getAttendanceOfStudent } from "../../../../services/attendanceService";
import { AuthContext } from "../../../../contexts/AuthContext";
import DateInput from "../../../common/DateInput/DateInput";



const StudentAttendance = () => {
    const { user } = useContext(AuthContext);

    const [startDate, setStartDate] = useState('');
    const [endDate, setEndDate] = useState('');
    const [searchError, setSearchError] = useState('');
    const [attendanceHistory, setAttenddanceHistory] = useState([]);
    const [isSearched, setIsSearched] = useState(false);

    const handleSearch = async () => {
        setSearchError('');
        if((new Date(startDate)) > (new Date(endDate))){
            setSearchError('Başlangıç tarihi bitiş tarihinden önce olmalıdır.');
            return;
        }
        setIsSearched(true);
        console.log('fetching attendance records');
        console.log(startDate, endDate);
        const params = {};
        if(startDate) params.startDate = startDate
        if(endDate) params.endDate = endDate
        
        try {
            const response = await getAttendanceOfStudent(user.id, params, user.accessToken);
            console.log(response);
            setAttenddanceHistory(response.data);
        }catch(error) {
            console.log(error);
        }
    }
   

    return(
        <>
            <div className="search">
                <div className="search-options">
                <DateInput 
                    title='Başlangıç Tarihi'
                    onInput={(date) => {setStartDate(date), setSearchError('')}}
                    />
                <DateInput 
                    title='Bitiş Tarihi'
                    onInput={(date) => {setEndDate(date), setSearchError('')}}
                />
                <button className="btn" onClick={handleSearch}>Ara</button>
                </div>
                {searchError && <p className='error-message'>{searchError}</p>}
            </div>
            {/* {attendanceHistory.length > 0 ? (
                attendanceHistory.map((attendanceEntry) => (
                    <div>
                        {attendanceEntry.status}
                    </div>
                ))
            ) : (
                <p>no attendance found</p>
            )} */}
            {isSearched && (
                attendanceHistory.length > 0 ? (
                    attendanceHistory.map((attendanceEntry) => (
                        <div>
                            <p>
                                {attendanceEntry.attendanceId}
                            </p>
                            <p>
                                {attendanceEntry.status}
                            </p>
                            <p>
                                {attendanceEntry.comment}
                            </p>
                        </div>
                    ))
                ) : (
                    <p>no attendanceHistory found</p>
                )
            )}
            
        </>
    );
}
export default StudentAttendance;