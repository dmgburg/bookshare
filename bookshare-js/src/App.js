import axios from 'axios';
import React, { Component } from 'react';
import './App.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import LoginWithRouter from "./containers/Login";
import RegisterWithRouter from "./containers/Register";
import Header from "./containers/Header";
import Books from "./containers/Books";
import BookDetailsWithRouter from "./containers/BookDetails";
import InteractionsWithRouter from "./containers/Interactions";
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

    instance.get("/currentUser").then((responce) => {
        this.setState( {
          email: responce.data,
          axios: instance,
          setEmail: this.setEmail,
        });
    })

    this.state = {
      email: "",
      axios: instance,
      setEmail: this.setEmail,
    };

    this.loadAllBooks = this.loadAllBooks.bind(this);
    this.loadMyBooks = this.loadMyBooks.bind(this);
  }

  async loadAllBooks() {
      const response = await this.state.axios.get("/allBooks")
      console.log("Got all books: " + JSON.stringify(response.data))
      return response.data
  }
  async loadMyBooks() {
      const response = await this.state.axios.get("/myBooks")
      console.log("Got all books: " + JSON.stringify(response.data))
      return response.data
  }

  render() {
    return (
        <Router>
           <div>
            <div className="container">
              <UserContext.Provider value={this.state}>
                 <Header />
                 <Route path="/books"   render={(props) => <Books {...props} loadData={this.loadAllBooks} />} />
                 <Route path="/myBooks"   render={(props) => <Books {...props} loadData={this.loadMyBooks} />} />
                 <Route path="/addBook" component={AddBookWithRouter} />
                 <Route path="/signin" component={LoginWithRouter} />
                 <Route path="/signup" component={RegisterWithRouter} />
                 <Route path="/interactions" component={InteractionsWithRouter} />
                 <Route path="/bookDetails/:id" component={BookDetailsWithRouter} />
              </UserContext.Provider>
            </div>
           </div>
         </Router>

    );
  }
}

export default App;
