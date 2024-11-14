
// const userOptionsMap = {
//     ADMIN : [
//         'Ana Menü',
//         'Öğrenci Bul',
//         'Ödev Takibi',
//         'Yoklama',
//         'Sınav Sonuçları',
//         'Duyurular',
//         'Yeni Kullanıcı',
//     ],

//     TEACHER : ['Ana Menü',
//         'Öğrenci Bul',
//         'Ödev Takibi',
//         'Yoklama',
//         'Sınav Sonuçları',
//         'Duyurular',
//     ],

//     STUDENT : [
//         'Ana Menü',
//         'Ödevler',
//         'Devamsızlık',
//         'Geçmiş Sınavlar',
//         'Duyurular',
//     ]
// };
import NewUser from "../components/MainContents/NewUser/NewUser";
import OfficerAssignments from "../components/MainContents/Assignments/OfficerAssignments/OfficerAssignments";
import StudentAssignments from "../components/MainContents/Assignments/StudentAssignments/StudentsAssignments";

const userOptionsMap = {
    ADMIN: [
        { title: 'Ana Menü', component: null },
        { title: 'Öğrenci Bul', component: null },
        { title: 'Ödev Takibi', component: OfficerAssignments },
        { title: 'Yoklama', component: null },
        { title: 'Sınav Sonuçları', component: null },
        { title: 'Duyurular', component: null },
        { title: 'Yeni Kullanıcı', component: NewUser }
    ],
    TEACHER: [
        { title: 'Ana Menü', component: null },
        { title: 'Öğrenci Bul', component: null },
        { title: 'Ödev Takibi', component: null },
        { title: 'Yoklama', component: null },
        { title: 'Sınav Sonuçları', component: null },
        { title: 'Duyurular', component: null }
    ],
    STUDENT: [
        { title: 'Ana Menü', component: null },
        { title: 'Ödevler', component: StudentAssignments },
        { title: 'Devamsızlık', component: null },
        { title: 'Geçmiş Sınavlar', component: null },
        { title: 'Duyurular', component: null }
    ]
}


export const getSidebarOptions = (role) => {
    return userOptionsMap[role];
}


