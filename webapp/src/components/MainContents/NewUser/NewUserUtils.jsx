export const clearInputs = () => {
    userType === 'STUDENT' ? clearStudent() : clearTeacher();
}

export const clearStudent = () => {
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
export const clearTeacher = () => {
    setTeacherData({
        firstName: '',
        lastName: '',
        idNumber: '',
        subject: '',
        phoneNumber: '',
        email: ''
    });
}

export const clearResponse = () => {
    setRegisterResponse({
        message : '',
        color : ''
    });
} 