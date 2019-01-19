import axios from 'axios';
import React, { Component } from 'react';
import './App.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import LoginWithRouter from "./containers/Login";
import RegisterWithRouter from "./containers/Register";
import Header from "./containers/Header";
import AlertArea from "./containers/AlertArea";
import Books from "./containers/Books";
import BookDetailsWithRouter from "./containers/BookDetails";
import NotificationsWithRouter from "./containers/Notifications";
import AddBookWithRouter from "./containers/AddBook";
import { UserContext } from "./UserContext";
import { BrowserRouter as Router, Route } from "react-router-dom";

class App extends Component {
  constructor(props) {
    super(props);

    this.setEmail = (email) => {
      this.setState({
        email: email
      });
    };

    const instance = axios.create({
      baseURL: 'http://localhost:8080/',
      withCredentials: true,
    })

    instance.get("/api/user/public/currentUser").then((responce) => {
        this.setState( {
          init: true,
          alert: null,
          email: responce.data,
          axios: instance,
          setEmail: this.setEmail,
          setAlert: this.setAlert,
        });
    })

    this.state = {
      init: false,
      email: "",
      alert: null,
      axios: instance,
      setEmail: this.setEmail,
    };

    this.loadAllBooks = this.loadAllBooks.bind(this);
    this.loadMyBooks = this.loadMyBooks.bind(this);
    this.setAlert = this.setAlert.bind(this);
  }

  async loadAllBooks() {
      const response = await this.state.axios.get("/api/book/public/allBooks")
      console.log("Got all books: " + JSON.stringify(response.data))
      return response.data
  }
  async loadMyBooks() {
      const response = await this.state.axios.get("/api/book/myBooks")
      console.log("Got all books: " + JSON.stringify(response.data))
      return response.data
  }

  setAlert(err){
    let alert = "Что-то пошло не так"
    if(!err){
        alert = null
    }
    if(err && err.response && err.response.data && err.response.data.message){
        alert = err.response.data.message
    }
    this.setState({
        alert: alert
    })
  }

  render() {
    if(!this.state.init){
        return (<div>loading</div>)
    }
    return (
        <Router>
           <div>
           <UserContext.Provider value={this.state}>
            <Header />
            <AlertArea alert={this.state.alert} setAlert={this.setAlert}/>
                <div className="container mt-2">
                   <Route path="/books"   render={(props) => <Books {...props} loadData={this.loadAllBooks} actionsHidden={true}/>} />
                   <Route path="/myBooks"   render={(props) => <Books {...props} loadData={this.loadMyBooks} />} />
                   <Route path="/addBook" component={AddBookWithRouter} />
                   <Route path="/signin" component={LoginWithRouter} />
                   <Route path="/signup" component={RegisterWithRouter} />
                   <Route path="/notifications" component={NotificationsWithRouter} />
                   <Route path="/bookDetails/:id" component={BookDetailsWithRouter} />
                </div>
            </UserContext.Provider>
           </div>
         </Router>

    );
  }
}

export default App;
