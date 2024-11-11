import './Header.css';

const Header = ({title = "Ana Menü", user = {name : 'Name', imgSource : 'https://placeholder.pics/svg/60x60'}}) => {

    return(
        <div className="header">
            <span className="header-title">{title}</span>
            <div className="profile-container">
                <div className="profile">
                    <img src={user.imgSource} alt="" className="user-image" />
                    <span className="username">{user.name}</span>
                </div>
                <div className="dropdown-icon">
                    <img src="https://placeholder.pics/svg/24x24" alt="" />
                </div>
            </div>
        </div>
    );
}
export default Header;