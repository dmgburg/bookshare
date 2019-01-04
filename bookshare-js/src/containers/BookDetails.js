import React from 'react';
import { UserContext } from "../UserContext";
import { withRouter } from "react-router";

export default class BookDetails extends React.Component {
    constructor(props){
        super(props)
        this.state = {
            book: {}
        }
        this.askForBook = this.askForBook.bind(this)
    }

    async askForBook(){
        const axios = this.context.axios;
        const response = await axios.get("/askForBook/" + this.state.book.id)
        console.log(response.data)
        this.props.history.push("/interactions")
    }

    async returnBook(){
        const axios = this.context.axios;
        const response = await axios.get("/returnBook/" + this.state.book.id)
        console.log(response.data)
        this.props.history.push("/interactions")
    }

    componentWillMount() {
        this.fetchState()
    }

    async fetchState(){
        try{
        const bookResp = await this.context.axios.get('/getBook/' + this.props.match.params.id);
        console.log("Got book data:" + bookResp);
        this.setState({
            book: bookResp.data
        })
        } catch (e){
            console.log(e)
        }
    }

    render() {
        return (
            <div className="container mt-3">
                <div className="row">
                    <div className="col-md-4">
                        <span>
                            <img className="img-fluid w-100" alt="" src={this.state.book.coverId ? "http://localhost:8080/getCover/" + this.state.book.coverId : "/defaultCover.png" }/>
                        </span>
                    </div>
                    <div className="col-md-8">
                          <h1 className="col-sm-12 ">{this.state.book.name}</h1>
                        <div className="row">
                          <div className="col-sm-3 font-weight-bold">Автор</div>
                          <div className="col-sm-9">{this.state.book.author}</div>
                        </div>
                        <div className="row">
                          <div className="col-sm-3 font-weight-bold">Владелец</div>
                          <div className="col-sm-9">{this.state.book.owner}</div>
                        </div>
                        <div className="row">
                          <div className="col-sm-3 font-weight-bold">Сейчас книга у</div>
                          <div className="col-sm-9">{this.state.book.holder}</div>
                        </div>
                        <RequestButton
                            email={this.context.email}
                            book={this.state.book}
                            askForBook={this.askForBook} />
                    </div>
                </div>
                <h3 className="row mx-0">О книге</h3>
                <div className="row mx-0">
                  <div className="col-sm-12">{this.state.book.description}</div>
                </div>
            </div>
          )
    }
}
BookDetails.contextType = UserContext;
export const BookDetailsWithRouter = withRouter(BookDetails);

class RequestButton extends React.Component{
    render() {
        if(!this.props.email){
            return null
        }
        if (this.props.email === this.props.holder){
            return (<div className="btn btn-success mt-2" onClick={this.props.askForBook}>Вернуть книгу</div>)
        }
        return (<div className="btn btn-success mt-2" onClick={this.props.askForBook}>Попросить книгу</div>)
    }
}