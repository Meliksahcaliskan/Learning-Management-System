import { useState } from 'react';
import NavigationOption from '../NavigationOption/NavigationOption';
import './Navigator.css';

const Navigator = ({ options, onSelect }) => {

    const [highlightedOption, setHighlightedOption] = useState(0);

    return(
        <div className="navigator">
            {options.map((option, index) => (
                <NavigationOption
                    key={index}
                    title={option.title}
                    isHighlighted={index === highlightedOption}
                    onClick={() => {
                        setHighlightedOption(index);
                        onSelect(option);
                    }}
                />
            ))}
        </div>
    );
}
export default Navigator;