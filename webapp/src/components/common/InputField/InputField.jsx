import './InputField.css';

const InputField = ({type, label, placeholder, value, onChange, style}) => {

    const handleChange = (event) => {
        onChange(event.target.value);
    }

    return(
        <div className="input-container">
            <label className='label'>{label}</label>
            <input  type={type}
                    placeholder={placeholder}
                    className="input"
                    value={value}
                    onChange={handleChange}
                    style={style}/>
        </div>
    );
}
export default InputField