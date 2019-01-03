import React from 'react';
import crypto from 'crypto';
import { UserContext } from "../UserContext";
import { withRouter } from "react-router";

export default class Login extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      email: "",
      password: ""
    };

    this.handleChange = this.handleChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
  }

  handleChange(event) {
     const target = event.target;
     const value = target.value;
     const name = target.name;

     this.setState({
       [name]: value
     });
  }

    async onLogin() {
        const { email, password } = this.state;
        const axios = this.context.axios;
        try {
           const salt = await axios.post('/userSalt', { email: email });
           var hash = crypto.createHmac('sha512', salt.data); /** Hashing algorithm sha512 */
           hash.update(password);
           var value = hash.digest('hex');

           var params = new URLSearchParams();
           params.append('username', email);
           params.append('password', value);
           const response = await axios.post('/login', params);
           console.log(JSON.stringify(response));
           this.context.setEmail(response.data)
        } catch (err) {
           console.log(err);
        }
    }


  handleSubmit = event => {
    event.preventDefault();
    this.onLogin();
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
        <div>
          <div className="col-md-6 offset-md-3 bg-light mt-5 py-5 rounded">
            <div className="login-form">
              <form onSubmit={this.handleSubmit}>
                <div className="form-group">
                  <label htmlFor="email" className="col-sm-2 col-form-label row font-weight-bold mx-0">E-mail</label>
                  <div className="form-group col-sm-12 row mx-0">
                    <input type="text"
                           className="form-control"
                           name="email"
                           placeholder="E-mail"
                           value={this.state.email}
                           onChange={this.handleChange}/>
                  </div>
                </div>
                <div className="form-group form-group">
                  <label htmlFor="password" className="col-sm-2 col-form-label row font-weight-bold mx-0">Пароль</label>
                  <div className="col-sm-12 row mx-0">
                    <input type="password"
                            className="form-control"
                            name="password"
                            placeholder="Пароль"
                            onChange={this.handleChange}/>
                  </div>
                </div>
                <div className="col-sm-12 row mx-0">
                    <input type="submit" className="btn btn-primary btn-block" value="Войти"/>
                </div>
              </form>
            </div>
          </div>
        </div>
          <div>
            <div className="col-md-6 offset-md-3 bg-light mt-1 py-3 rounded">
              <span>Ещё нет аккаунта? <a className="nav-link d-inline-block px-0" href="/signup">Зарегистрируйтесь</a></span>
            </div>
          </div>
      </div>
    );
  }
}
Login.contextType = UserContext;

export const LoginWithRouter = withRouter(Login);
