import React from 'react';
import { UserContext } from "../UserContext";
import { withRouter } from "react-router";

export default class AddBook extends React.Component {

    constructor(props) {
      super(props);

      this.state = {
        book: {},
      };

      this.handleChange = this.handleChange.bind(this);
      this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleChange(event) {
       const target = event.target;
       const value = target.value;
       const name = target.name;
       this.setState((state) => {
           const prevBook = state.book
           prevBook[name] = value
           return {
             [name]: value,
             book: prevBook
           }
       }
       );
    }

    async submitBook(event) {
       const book = this.state.book;
       try {
          const axios = this.context.axios;
          const formData = new FormData();
          formData.append("data", this.state.image)

          const coverId = await axios.post('/api/book/uploadCover', formData);
          book.coverId = coverId.data
          const bookId = await axios.post('/api/book/addBook', book );
          console.log(bookId);
          this.props.history.push("/bookDetails/" + bookId.data)
       } catch (err) {
          console.log(err);
       }
    }

    onImageChange = (event) => {
      if (event.target.files && event.target.files[0]) {
        this.setState({
          imageURL: URL.createObjectURL(event.target.files[0]),
          image: event.target.files[0]
        });
      }
    }

    handleSubmit = event => {
        event.preventDefault();
        this.submitBook();
    }

    //  TODO: move this logic to higher order
      componentDidMount(){
        const history = this.props.history
        if(!this.context.email) {
            history.push("/")
        }
      }

      componentDidUpdate(){
        const history = this.props.history
        if(!this.context.email) {
            history.push("/")
        }
      }

//    TODO: adjust inputs left and right
    render() {
        return (
            <div className="container mt-3">
                <form onSubmit={this.handleSubmit}>
                    <div className="row">
                        <div className="col-md-2">
                            <span>
                                <img className="img-fluid w-100" alt="" src={this.state.imageURL ? this.state.imageURL : "/defaultCover.png" }/>
                            </span>
                            <label className="btn btn-block btn-primary mt-2">
                                Загрузить <input onChange={this.onImageChange} type="file" style={{display: "none"}}/>
                            </label>
                        </div>
                        <div className="col-md-10">
                            <div className="form-group col-sm-12 row mx-0">
                              <input type="text"
                                     className="form-control"
                                     name="name"
                                     placeholder="Название"
                                     autoComplete="off"
                                     value={this.state.book.name}
                                     onChange={this.handleChange}/>
                            </div>
                            <div className="form-group col-sm-12 row mx-0">
                              <input type="text"
                                     className="form-control"
                                     name="author"
                                     placeholder="Автор"
                                     autoComplete="off"
                                     value={this.state.book.author}
                                     onChange={this.handleChange}/>
                            </div>
                            <div className="form-group col-sm-12 row mx-0">
                              <textarea type="text"
                                     className="form-control"
                                     name="description"
                                     placeholder="Описание"
                                     autoComplete="off"
                                     rows={7}
                                     ref={c => (this.textarea = c)}
                                     value={this.state.book.description}
                                     onChange={this.handleChange}/>
                            </div>
                        </div>
                    </div>
                    <div className="col-sm-12 row px-0 mx-0 my-3">
                        <input type="submit" className="btn btn-primary btn-block" value="Добавить"/>
                    </div>
                </form>
            </div>
          )
    }
}
AddBook.contextType = UserContext;
export const AddBookWithRouter = withRouter(AddBook);
