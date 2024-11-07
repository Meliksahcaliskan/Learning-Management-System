import LogoPlaceholder from '../../components/common/LogoPlaceholder/LogoPlaceholder'
import InputField from '../../components/common/InputField/InputField'
import './Login.css'
import { useState } from 'react'

const Login = () => {


    const [rememberMe, setRememberMe] = useState(true);












    return(
        <div className="login-page">
            <img src="https://picsum.photos/140/900" alt="landspace photo" className='landing-img'/>
            <div className="login-container">
                <LogoPlaceholder/>
                <div className="login-form">
                    <div className="login-fields">
                        <h3 className="greeting">Hoş Geldiniz</h3>
                        <div className="inputs">
                            <div className="input-fields">
                                <InputField 
                                    type={'text'}                          
                                    label={'Kullanıcı adı'}      
                                    placeholder={'Kullanıcı adınızı giriniz'}
                                />
                                <InputField
                                    type={'password'}
                                    label={'Şifre'}
                                    placeholder={'Şifrenizi giriniz'}
                                />
                            </div>
                            <div className="options">
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
                </div>
            </div>
        </div>
    );






}
export default Login