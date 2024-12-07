import './Sidebar.css';
import LogoPlaceholder from '../LogoPlaceholder/LogoPlaceholder';
import NavigationOption from '../NavigationOption/NavigationOption';
import { useContext, useState } from 'react';
import { AuthContext } from '../../../contexts/AuthContext';
import authService from '../../../services/authService';

const Sidebar = ({options, onSelect}) => {

    const [highlightedOption, setHighlightedOption] = useState(0);
    
    const { user,logout } = useContext(AuthContext);

    const handleLogout = async () => {
        try {
          const response = await authService.logout(user.accessToken, user.refreshToken);
          console.log(response);
          logout();
        } catch (error) {
          console.error('Error during logout:', error);
        }
      };

    return(
        <div className="sidebar">
            <LogoPlaceholder />
            <div className="navigation-options">
            {options.map((option, index) => (
                        <NavigationOption
                            key={index}
                            title={option.title}
                            // iconSource={} it will have option.icon later on
                            isHighlighted={index === highlightedOption}
                            onClick={() => {
                                setHighlightedOption(index);
                                onSelect(option);
                            }}
                        />
                    ))}
                <NavigationOption 
                    title='Çıkış Yap'
                    onClick={handleLogout}
                />
            </div>
        </div>
    );
}
export default Sidebar