import './MainContent.css'
import React from 'react';

const MainContent = ({ content = null }) => {
    return (
        <div className='main-content'>
            {content ? React.createElement(content) : <div>The content for this option is not built yet</div>}
        </div>
    );
};
export default MainContent;