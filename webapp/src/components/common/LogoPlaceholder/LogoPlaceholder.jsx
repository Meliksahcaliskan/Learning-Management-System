import './LogoPlaceholder.css'

const LogoPlaceholder = () => {
    return(
        <div className='placeholder-container'>
            <img src="https://placeholder.pics/svg/50x50" alt="Logo image" className='logo'/>
            <span className='title'>Name</span>
        </div>
    );
}
export default LogoPlaceholder