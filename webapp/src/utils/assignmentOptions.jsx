import NewAssignment from "../components/MainContents/Assignments/NewAssignment/NewAssignment";

import ActiveHomeworksOfficer from "../components/MainContents/Assignments/ActiveHomeworksOfficer/ActiveHomeworksOfficer";
// import ActiveHomeworksStudent from "../components/MainContents/Assignments/ActiveHomeworks/ActiveHomeworksStudent/ActiveHomeworksStudent";

import PastHomeworksOfficer from "../components/MainContents/Assignments/PastHomeworksOfficer/PastHomeworksOfficer";
// import PastHomeworksStudent from "../components/MainContents/Assignments/PastHomeworks/PastHomeworksStudent/PastHomeworksStudent";


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