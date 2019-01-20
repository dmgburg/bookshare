import React from 'react';
import { Link } from "react-router-dom";
import { UserContext } from "../UserContext";
import Books from "./Books";

export class Search extends React.Component {

    constructor(props){
        super(props)
        this.search = this.search.bind(this)
        this.foundBooks = this.foundBooks.bind(this)
        this.state = {
            books : null,
            lastSearch : null
        }
    }

    async search(searchString) {
      var params = new URLSearchParams();
      params.append('searchString', searchString);
      const axios = this.context.axios;
      const my = await axios.post("/api/search/author", params);
      this.setState({
          books: my.data,
          lastSearch: searchString
      })
    }

    foundBooks() {
        return Promise.resolve(this.state.books);
    }

    render() {
        if(this.state.books && this.state.books.length > 0){
            return (
                <div>
                    <SearchBar search={this.search}/>
                    <Books loadData={this.foundBooks}/>
                </div>
            )
        }
        if(this.state.books && this.state.lastSearch){
            return (
                <div>
                    <SearchBar search={this.search}/>
                    <div>По Вашему запросу "{this.state.lastSearch}" ничего не найдено</div>
                </div>
            )
        }
        return (<SearchBar search={this.search}/>)
    }
}

Search.contextType = UserContext;


class SearchBar extends React.Component {

    constructor(props){
        super(props)
        this.handleChange = this.handleChange.bind(this)
        this.handleSubmit = this.handleSubmit.bind(this)
        this.state = {
            searchString: null
        }
    }

  handleChange(event) {
     const target = event.target;
     const value = target.value;
     const name = target.name;

     this.setState({
       [name]: value
     });
  }

  handleSubmit(event) {
    event.preventDefault();
    this.props.search(this.state.searchString)
  }

  render() {
      let user = this.context.email
      return (
          <form onSubmit={this.handleSubmit}>
              <div className="card-body row no-gutters align-items-center">
                  <div className="col-auto">
                      <button className="btn btn-lg btn-light">По автору</button>
                  </div>
                  <div className="col-auto">
                      <i className="fas fa-search h4 text-body"></i>
                  </div>
                  <div className="col">
                      <input className="form-control form-control-lg form-control-borderless"
                          type="text"
                          name="searchString"
                          value={this.state.searchString}
                          onChange={this.handleChange}
                          placeholder="Search topics or keywords"/>
                  </div>
                  <div className="col-auto">
                      <button className="btn btn-lg btn-success" type="submit">Search</button>
                  </div>
              </div>
          </form>
      )
  }
}