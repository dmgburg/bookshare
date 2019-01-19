import React from 'react';
import { UserContext } from "../UserContext";
import BookQueue from "./BookQueue";
import { withRouter } from "react-router";

export default class BookDetails extends React.Component {
    constructor(props){
        super(props)
        this.state = {
            book: {}
        }
        this.askForBook = this.askForBook.bind(this)
        this.handoverBook = this.handoverBook.bind(this)
        this.removeFromQueue = this.removeFromQueue.bind(this)
    }

    async askForBook(){
        const axios = this.context.axios;
        const response = await axios.get("/api/book/addToQueue/" + this.state.book.id)
        console.log(response.data)
        this.fetchState()
    }

    async handoverBook(){
        const axios = this.context.axios;
        const response = await axios.get("/api/book/handoverBook/" + this.state.book.id)
        console.log(response.data)
        this.fetchState()
    }

    async removeFromQueue(){
        const axios = this.context.axios;
        const response = await axios.get("/api/book/removeFromQueue/" + this.state.book.id)
        console.log(response.data)
        this.fetchState()
    }

    componentWillMount() {
        this.fetchState()
    }

    async fetchState(){
        try{
        const bookResp = await this.context.axios.get('/api/book/public/getBook/' + this.props.match.params.id);
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
                            <img className="img-fluid w-100" alt="" src={this.state.book.coverId ? "/api/book/public/getCover/" + this.state.book.coverId : "/defaultCover.png" }/>
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
                        <BookQueue book={this.state.book}
                            askForBook={this.askForBook}
                            handoverBook={this.handoverBook}
                            removeFromQueue={this.removeFromQueue}/>
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