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
            requestsToMe: 0
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
        this.context.axios.get("/getInteractionsToMe").then((resp) =>{
            this.setState({
                requestsToMe: resp.data.length,
            })
        })
    }

    render() {
        let user = this.context.email
        return (
            <ul className="navbar-nav mr-auto">
                <li className="nav-item active">
                    <Link className="nav-link" to="/books">Книги</Link>
                </li>
                <li className="nav-item active">
                    <Link className="nav-link" to="/myBooks">Мои книги</Link>
                </li>
                <li className="nav-item active">
                    <Link className="nav-link" to="/addBook">Добавить книгу</Link>
                </li>
                <li className="nav-item active">
                    <Link className="nav-link" to="/interactions">Запросы
                        {this.state.requestsToMe ?
                            <span class="badge badge-success">{this.state.requestsToMe}</span> :
                            null}
                    </Link>
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