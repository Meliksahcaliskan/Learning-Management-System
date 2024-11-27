import NewAssignment from "../components/MainContents/Assignments/OfficerAssignments/NewAssignment/NewAssignment";
import ActiveHomeworksOfficer from "../components/MainContents/Assignments/OfficerAssignments/ActiveHomeworksOfficer/ActiveHomeworksOfficer";
import PastHomeworksOfficer from "../components/MainContents/Assignments/OfficerAssignments/PastHomeworksOfficer/PastHomeworksOfficer";


const assignmentOptions = {
    ROLE_OFFICER: [
        { title: 'Ödev Ekle', component: NewAssignment},
        { title: 'Aktif Ödevler', component: ActiveHomeworksOfficer },
        { title: 'Geçmiş Ödevler', component: PastHomeworksOfficer },
    ],
    ROLE_STUDENT: [
        { title: 'Aktif Ödevler', status : 'PENDING'},
        { title: 'Gönderilmiş', status : 'SUBMITTED'},
        { title: 'Geçmiş Ödevler', status : 'GRADED'},
    ]
}

export const getAssignmentOptions = (role) => {
    return assignmentOptions[role];
}