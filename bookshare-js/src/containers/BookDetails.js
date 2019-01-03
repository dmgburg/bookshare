import React from 'react';
import { UserContext } from "../UserContext";

export default class BookDetails extends React.Component {
    constructor(props){
        super(props)
        this.state = {
            book: {}
        }
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
                        <div className="form-group col-sm-12 row mx-0">
                          <div>{this.state.book.name}</div>
                        </div>
                        <div className="form-group col-sm-12 row mx-0">
                          <div>{this.state.book.description}</div>
                        </div>
                    </div>
                </div>
            </div>
          )
    }
}
BookDetails.contextType = UserContext;