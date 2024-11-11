import LogoPlaceholder from '../../components/common/LogoPlaceholder/LogoPlaceholder'
import InputField from '../../components/common/InputField/InputField'
import './Login.css'
import { useContext, useState } from 'react'
import authService from '../../services/authService'
import { AuthContext } from '../../contexts/AuthContext'

const Login = () => {

    const { login } = useContext(AuthContext);
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [rememberMe, setRememberMe] = useState(false);

    const [errorMessage, setErrorMessage] = useState('');


    const handleLogin = async (e) => {
        e.preventDefault();
        if(!username || !password) {
            loginError('Bu alanların doldurulması zorunludur');
            return;
        }

        try {
            const response = await authService.login({
                username : username,
                password : password
            });
            login(response);
        } catch(err) {
            loginError('Yanlış kullanıcı adı veya şifre');
        }
    }

    const loginError = (message) => {
        setErrorMessage(message);
        clearInputs();
    }

    const clearInputs = () => {
        setUsername('');
        setPassword('');
    }

    return(
        <div className="login-page">
            <img src="https://picsum.photos/140/900" alt="landspace photo" className='landing-img'/>
            <div className="login-container">
                <div className="login">
                    <LogoPlaceholder/>
                    <div className="login-form">
                        <div className="login-fields">
                            <h3 className="greeting">Hoş Geldiniz</h3>
                            <div className="inputs">
                                <div className="login-input-fields">
                                    {errorMessage && <div className="error-message">{errorMessage}</div>}
                                    <InputField 
                                        type={'text'}                          
                                        label={'Kullanıcı adı'}      
                                        placeholder={'Kullanıcı adınızı giriniz'}
                                        onChange={(e) => setUsername(e.target.value)}
                                        value={username}
                                        style={{borderColor : errorMessage ? 'red' : 'none'}}
                                        />
                                    <InputField
                                        type={'password'}
                                        label={'Şifre'}
                                        placeholder={'Şifrenizi giriniz'}
                                        onChange={(e) => setPassword(e.target.value)}
                                        value={password}
                                        style={{borderColor : errorMessage ? 'red' : 'none'}}
                                    />
                                </div>
                                <div className="login-options">
                                    <span className="remember-me">
                                        <label className="switch">
                                            <input  
                                                type="checkbox"
                                                checked={rememberMe}
                                                onChange={() => setRememberMe(!rememberMe)}
                                                />
                                            <span className="slider"></span>
                                        </label>
                                        <span>Beni hatırla</span>
                                    </span>
                                    <a href="#" className="forgot-password">Şifremi unuttum</a>
                                </div>
                            </div>
                        </div>
                        <button type="submit" className='login-btn btn' onClick={handleLogin}>Giriş yap</button>
                    </div>
                </div>
            </div>
        </div>
    );
}
export default Login