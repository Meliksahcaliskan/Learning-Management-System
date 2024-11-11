import './InputField.css';

const InputField = ({type, label, placeholder, value, onChange, style, name}) => {
    return(
        <div className="input-container">
            <label className='label'>{label}</label>
            <input  type={type}
                    placeholder={placeholder}
                    className="input"
                    value={value}
                    onChange={onChange}
                    style={style}
                    name={name}/>
        </div>
    );
}
export default InputField