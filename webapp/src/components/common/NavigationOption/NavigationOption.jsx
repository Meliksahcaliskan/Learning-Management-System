import './NavigationOption.css'

function NavigationOption({title = 'Navigation item', iconSource = 'https://placeholder.pics/svg/32x32', isHighlighted, onClick}) {
    return(
        <div className={`navigation-option ${isHighlighted ? 'highlighted' : 'default'}`} onClick={onClick}>
            <img src={iconSource} alt="icon" className='icon'/>
            <p className="navigation-title">{title}</p>
        </div>
    );
}
export default NavigationOption