import { useState } from 'react';
import './NewUser.css';
import NavigationOption from '../../common/NavigationOption/NavigationOption';
import InputField from '../../common/InputField/InputField';
import authService from '../../../services/authService';

const NewUser = () => {

    const [userType, setUserType] = useState('ROLE_STUDENT'); 
    
    const [studentData, setStudentData] = useState({
        firstName: '',
        lastName: '',
        idNumber: '',
        birthDate: '',
        email: '',
        class: '',
        guardianFirstName: '',
        guardianLastName: '',
        guardianPhoneNumber: ''
    });
    
    const [teacherData, setTeacherData] = useState({
        firstName: '',
        lastName: '',
        idNumber: '',
        subject: '',
        phoneNumber: '',
        email: ''
    });

    const [registerResponse, setRegisterResponse] = useState({
        message : '',
        color : ''
    });

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        clearResponse();
        if (userType === 'ROLE_STUDENT') {
            setStudentData((prevData) => ({ ...prevData, [name]: value }));
        } else {
            setTeacherData((prevData) => ({ ...prevData, [name]: value }));
        }
    };

    const handleFormSubmit = async (e) => {
        e.preventDefault();
        //validate all the inputs that are necessary are filled and correct.
        
        const formData = userType === 'ROLE_STUDENT' ? studentData : teacherData;
        try {
            const response = await authService.register({
                username : formData.firstName + formData.lastName,
                password : formData.idNumber.slice(0, 6),
                email : formData.email,
                role : userType
            });
            clearInputs();
            setRegisterResponse({
                message : 'Kayıt Başarıyla Tamamlandı!',
                color : '#00FF00'
            });

        }catch(error) {
            setRegisterResponse({
                message : 'Kayıt Yapılamadı!',
                color : '#FF0000'
            });
        }
    }

    const clearInputs = () => {
        userType === 'STUDENT' ? clearStudent() : clearTeacher();
    }

    const clearStudent = () => {
        setStudentData({
            firstName: '',
            lastName: '',
            idNumber: '',
            birthDate: '',
            email: '',
            class: '',
            guardianFirstName: '',
            guardianLastName: '',
            guardianPhoneNumber: ''
        });
    }
    const clearTeacher = () => {
        setTeacherData({
            firstName: '',
            lastName: '',
            idNumber: '',
            subject: '',
            phoneNumber: '',
            email: ''
        });
    }

    const clearResponse = () => {
        setRegisterResponse({
            message : '',
            color : ''
        });
    } 

    return(
        <div className="new-user">
            <div className="options">
                <NavigationOption 
                    title='Öğrenci'
                    isHighlighted={userType === 'ROLE_STUDENT'}
                    onClick={() => {
                        setUserType('ROLE_STUDENT');
                        clearResponse();
                    }}
                />
                <NavigationOption 
                    title='Öğretmen'
                    isHighlighted={userType === 'ROLE_TEACHER'}
                    onClick={() => {
                        setUserType('ROLE_TEACHER');
                        clearResponse();    
                    }}
                />
            </div>
            <div className="form">
                {userType === 'ROLE_STUDENT'
                    ? <StudentForm  data={studentData} onInputChange={handleInputChange}/>
                    : <TeacherForm data={teacherData} onInputChange={handleInputChange}/>
                }
                <button type="submit" className='save-btn btn' onClick={handleFormSubmit}>Kaydet</button>
            </div>
            <div className="register-response" style={{color : registerResponse.color}}>{registerResponse.message}</div>
        </div>
    );
}

const StudentForm = ({data, onInputChange}) => {
    return(
        <>
            <div className="form-title">Öğrenci</div>
                <div className="input-fields">
                    <InputField 
                        type = {'text'}
                        label={'Adı'}
                        value={data.firstName}
                        onChange={onInputChange}
                        name={'firstName'}
                        />
                    <InputField 
                        type = {'text'}
                        label={'Soyadı'}
                        value={data.lastName}
                        onChange={onInputChange}
                        name={'lastName'}
                        />
                    <InputField 
                        type = {'text'}
                        label={'TC Kimlik Numarası'}
                        value={data.idNumber}
                        onChange={onInputChange}
                        name={'idNumber'}
                        />
                    <InputField 
                        type = {'date'}
                        label={'Doğum Tarihi'}
                        value={data.birthDate}
                        onChange={onInputChange}
                        name={'birthDate'}
                        />
                    <InputField 
                        type = {'text'}
                        label={'E-posta adresi'}
                        value={data.email}
                        onChange={onInputChange}
                        name={'email'}
                        />
                    <InputField 
                        type = {'text'}
                        label={'Sınıfı'}
                        value={data.class}
                        onChange={onInputChange}
                        name={'class'}
                        />
                </div>
                <div className="form-title">Veli</div>
                <div className="input-fields">
                    <InputField 
                        type={'text'}
                        label={'Adı'}
                        value={data.guardianFirstName}
                        onChange={onInputChange}
                        name={'guardianFirstName'}
                        />
                    <InputField 
                        type={'text'}
                        label={'Soyadı'}
                        value={data.guardianLastName}
                        onChange={onInputChange}
                        name={'guardianLastName'}
                        />
                    <InputField 
                        type={'text'}
                        label={'Telefon Numarası'}
                        value={data.guardianPhoneNumber}
                        onChange={onInputChange}
                        name={'guardianPhoneNumber'}
                        />
                </div>
        </>
    );
}

const TeacherForm = ({data, onInputChange}) => {
    return(
        <>
            <div className="form-title">Öğretmen</div>
            <div className="input-fields">
                <InputField 
                    type={'text'}
                    label={'Adı'}
                    value={data.firstName}
                    onChange={onInputChange}
                    name={'firstName'}
                    />
                <InputField 
                    type={'text'}
                    label={'Soyadı'}
                    value={data.lastName}
                    onChange={onInputChange}
                    name={'lastName'}
                    />
                <InputField
                    type={'text'}
                    label={'TC Kimlik Numarası'}
                    value={data.idNumber}
                    onChange={onInputChange}
                    name={'idNumber'}
                    />
                <InputField 
                    type={'text'}
                    label={'Telefon Numarası'}
                    value={data.phoneNumber}
                    onChange={onInputChange}
                    name={'phoneNumber'}
                    />
                <InputField 
                    type={'text'}
                    label={'Konu'}
                    value={data.subject}
                    onChange={onInputChange}
                    name={'subject'}
                    />
                <InputField 
                    type = {'text'}
                    label={'E-posta adresi'}
                    value={data.email}
                    onChange={onInputChange}
                    name={'email'}
                    />
            </div>
        </>
    );
}

export default NewUser;