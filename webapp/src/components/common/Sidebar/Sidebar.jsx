import './Sidebar.css';
import LogoPlaceholder from '../LogoPlaceholder/LogoPlaceholder';
import NavigationOption from '../NavigationOption/NavigationOption';
import { useState } from 'react';

const Sidebar = ({options, onSelect}) => {

    const [highlightedOption, setHighlightedOption] = useState(0);
    
    const handleLogout = () => {
        console.log('log out the user');
    }

    return(
        <div className="sidebar">
            <LogoPlaceholder />
            <div className="navigation-options">
            {options.map((option, index) => (
                        <NavigationOption
                            key={index}
                            title={option.title} // it will change to option.title later on
                            // iconSource={} it will have option.icon later on
                            isHighlighted={index === highlightedOption} //deciding classname based on its index
                            onClick={() => {
                                setHighlightedOption(index);    //set as the highlighted option
                                onSelect(option);               //let parent component know that the content will be displayed should change to option // it will be option.title later on 
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