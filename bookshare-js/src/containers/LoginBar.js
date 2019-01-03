import React from 'react';
import { Link } from "react-router-dom";
import { UserContext } from "../UserContext";

export class LoginBar extends React.Component {

    render() {
        let user = this.context.email
        if(user){
            return (<UserLoginBar/>)
        }
        return (<GuestLoginBar/>)
    }
}
LoginBar.contextType = UserContext;

class UserLoginBar extends React.Component {
    constructor (props){
        super(props)

        this.handleClick = this.handleClick.bind(this)
    }

    async onLogout() {
          try {
             await this.context.axios.get('/logout');
          } catch (err) {
             console.log(err);
          }
      }

    handleClick(event){
        event.preventDefault();
        this.onLogout();
        this.context.setEmail(null);

    }

    render() {
        let user = this.context.email
        return (
        <div>
            <ul className="navbar-nav mr-auto my-0">
                <li className="active text-center">
                    <p className="nav-link my-0">{user}</p>
                </li>
                <li className="nav-item active">
                    <button className="nav-link btn btn-outline-primary" onClick={this.handleClick}>Выйти</button>
                </li>
            </ul>
        </div>
        )
    }
}
UserLoginBar.contextType = UserContext;

class GuestLoginBar extends React.Component {
  render() {
    return (
            <div className="navbar-nav">
                <div className="nav-item active">
                    <Link className="nav-link btn btn-outline-primary" to="/signin">Войти</Link>
                </div>
                <div className="nav-item active">
                  <Link className="nav-link btn btn-success" to="/signup">Регистрация</Link>
                </div>
            </div>
    )
  }
}