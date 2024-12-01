
import NewUser from "../components/MainContents/NewUser/NewUser";
import Assignments from '../components/MainContents/Assignments/Assignments';

const userOptionsMap = {
    ROLE_ADMIN: [
        { title: 'Ana Menü', component: null },
        { title: 'Öğrenci Bul', component: null },
        { title: 'Ödev Takibi', component: Assignments },
        { title: 'Yoklama', component: null },
        { title: 'Sınav Sonuçları', component: null },
        { title: 'Duyurular', component: null },
        { title: 'Yeni Kullanıcı', component: NewUser }
    ],
    ROLE_TEACHER: [
        { title: 'Ana Menü', component: null },
        { title: 'Öğrenci Bul', component: null },
        { title: 'Ödev Takibi', component: Assignments },
        { title: 'Yoklama', component: null },
        { title: 'Sınav Sonuçları', component: null },
        { title: 'Duyurular', component: null }
    ],
    ROLE_COORDINATOR: [
        { title: 'Ana Menü', component: null },
        { title: 'Öğrenci Bul', component: null },
        { title: 'Ödev Takibi', component: Assignments },
        { title: 'Yoklama', component: null },
        { title: 'Sınav Sonuçları', component: null },
        { title: 'Duyurular', component: null }
    ],
    ROLE_STUDENT: [
        { title: 'Ana Menü', component: null },
        { title: 'Ödevler', component: Assignments },
        { title: 'Devamsızlık', component: null },
        { title: 'Geçmiş Sınavlar', component: null },
        { title: 'Duyurular', component: null }
    ]
}

export const getSidebarOptions = (role) => {
    return userOptionsMap[role];
}