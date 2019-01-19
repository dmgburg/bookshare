import React from 'react';
import crypto from 'crypto';
import { UserContext } from "../UserContext";
import { withRouter } from "react-router";

export default class Register extends React.Component {

 constructor(props) {
    super(props);

    this.state = {
      email: "",
      password: "",
      password2: "",
      passwordValidationError: "",
    };

    this.handleChange = this.handleChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
  }

  handleChange(event) {
     const target = event.target;
     const value = target.value;
     const name = target.name;

    let passwordValidationError = ""
    let passwords = {
        password: this.state.password,
        password2: this.state.password2,
    }
    passwords[name] = value
    if (passwords.password !== passwords.password2) {
      passwordValidationError = "Пароли отличаются"
    }
    if(passwords.password.length < 5){
      passwordValidationError = "Слишком короткий пароль"
    }
     this.setState({
       [name]: value,
       passwordValidationError: passwordValidationError
     });
  }

    async onSignup() {
        const { email, password } = this.state;
        try {
           var salt = crypto.randomBytes(Math.ceil(128/2))
                       .toString('hex') /** convert to hexadecimal format */
                       .slice(0, 128);   /** return required number of characters */
           var hash = crypto.createHmac('sha512', salt); /** Hashing algorithm sha512 */
           hash.update(password);
           var value = hash.digest('hex');

           const response = await this.context.axios.post('/api/user/public/createUser', { email: email, passwordHash: value, passwordSalt: salt });
           this.context.setAlert("Успешно, пожалуйста залогиньтесь")
           const history = this.props.history.push("/signin")
        } catch (err) {
           this.context.setAlert(err)
        }
    }

  handleSubmit = event => {
    event.preventDefault();
    this.onSignup();
  }

//  TODO: move this logic to higher order
  componentDidMount(){
    const history = this.props.history
    if(this.context.email) {
        history.push("/")
    }
  }

  componentDidUpdate(){
    const history = this.props.history
    if(this.context.email) {
        history.push("/")
    }
  }

  render() {
    return (
    <div>
      <div className="col-md-6 offset-md-3 bg-light mt-5 py-5 rounded">
        <div className="login-form">
          <form onSubmit={this.handleSubmit}>
            <div className="form-group">
              <label htmlFor="email" className="col-sm-2 col-form-label row font-weight-bold mx-0">E-mail</label>
              <div className="col-sm-12 row mx-0">
                <input type="text"
                       className="form-control"
                       name="email"
                       placeholder="E-mail"
                       value={this.state.email}
                       onChange={this.handleChange}/>
              </div>
            </div>
            <div className="form-group">
              <label htmlFor="password" className="col-sm-12 col-form-label row font-weight-bold mx-0">Пароль</label>
              <div className="col-sm-12 row mx-0">
                <input type="password"
                        className={this.state.passwordValidationError ? "form-control is-invalid" : "form-control"}
                        name="password"
                        placeholder="Пароль"
                        onChange={this.handleChange}/>
              </div>
            </div>
            <div className="form-group">
              <label htmlFor="password" className="col-sm-12 col-form-label row font-weight-bold mx-0">Пароль еще раз</label>
              <div className="col-sm-12 row mx-0">
                <input type="password"
                        className={this.state.passwordValidationError ? "form-control is-invalid" : "form-control"}
                        name="password2"
                        placeholder="Пароль еще раз"
                        onChange={this.handleChange}/>
                <div className="invalid-tooltip">
                  {this.state.passwordValidationError}
                </div>
              </div>
            </div>
            <div className="col-sm-12 row mx-0">
              <input type="submit" disabled={this.state.passwordValidationError} className="btn btn-primary btn-block" value="Зарегистрироваться"/>
            </div>
           </form>
        </div>
      </div>
      <div>
        <div className="col-md-6 offset-md-3 bg-light mt-1 py-3 rounded">
          <span>Уже зарегистрированы? <a className="nav-link d-inline-block px-0" href="/signin">Войдите</a></span>
        </div>
      </div>
    </div>
    );
  }
}
Register.contextType = UserContext;

export const RegisterWithRouter = withRouter(Register);