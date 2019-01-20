import React from 'react';
import { Link } from "react-router-dom";
import { UserContext } from "../UserContext";

export class BooksBar  extends React.Component {

    render() {
        let user = this.context.email
        if(user){
            return (<UserBooksBar />)
        }
        return (<GuestBooksBar />)
    }
}
BooksBar.contextType = UserContext;

class UserBooksBar  extends React.Component {
    constructor (props){
        super(props)
        this.handleClick = this.handleClick.bind(this)

        this.state = {
            notifications: 0
        }
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

    componentDidMount(){
        this.context.axios.get("/api/book/notifications").then((resp) =>{
            this.setState({
                notifications: resp.data.length,
            })
        })
    }

    render() {
        return (
            <ul className="navbar-nav mr-auto">
                <li className="nav-item active">
                    <Link className="nav-link" to="/books">Книги</Link>
                </li>
                <li className="nav-item active">
                    <Link className="nav-link" to="/myBooks">Мои книги
                        {this.state.notifications ? <span className="badge badge-success ml-1">{this.state.notifications}</span> : null}
                    </Link>
                </li>
                <li className="nav-item active">
                    <Link className="nav-link" to="/addBook">Добавить книгу</Link>
                </li>
                <li className="nav-item active">
                    <Link className="nav-link" to="/search">Поиск</Link>
                </li>
            </ul>
        )
    }
}
UserBooksBar.contextType = UserContext;

class GuestBooksBar  extends React.Component {
  render() {
    return (
        <ul className="navbar-nav mr-auto">
            <li className="nav-item active">
                <Link className="nav-link" to="/books">Книги</Link>
            </li>
        </ul>
    )
  }
}