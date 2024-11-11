import PropTypes from 'prop-types';
import './MainContent.css'

const MainContent = ({ content = null }) => {
    return (
        <div className='main-content'>
            {content ? (
                typeof content === 'function' ? content() : content
            ) : (
                <div>The content for this option is not built yet</div>
            )}
        </div>
    );
};

MainContent.propTypes = {
    content: PropTypes.oneOfType([
        PropTypes.node,     // for JSX elements
        PropTypes.func      // for function-based components
    ])
};
export default MainContent;
