import React from 'react';
import { UserContext } from "../UserContext";
import { withRouter } from "react-router";
import Cookies from 'universal-cookie';

export default class AddBook extends React.Component {

    constructor(props) {
      super(props);

      const cookies = new Cookies();
      const state = cookies.get('addBookDraft') ? cookies.get('addBookDraft') : {book: {}} ;

      this.state = state;

      this.handleChange = this.handleChange.bind(this);
      this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleChange(event) {
       const target = event.target;
       const value = target.value;
       const name = target.name;

       this.setState(
           (state) => {
               const prevBook = state.book || {}
               prevBook[name] = value
               return {
                 [name]: value,
                 book: prevBook
               }
           }, function() {
                const cookies = new Cookies();
                cookies.set('addBookDraft', JSON.stringify(this.state));
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
          const cookies = new Cookies();
          cookies.set('addBookDraft', null);
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
        }, function() {
             const cookies = new Cookies();
             cookies.set('addBookDraft', JSON.stringify(this.state));
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
    let book = this.state.book || {}
        return (
            <div className="container mt-3">
                <form onSubmit={this.handleSubmit}>
                    <div className="row">
                        <div className="col-md-2">
                            <span>
                                <img className="img-fluid w-100" alt="" src={this.state.imageURL ? this.state.imageURL : "/defaultCover.png" }/>
                            </span>
                            <label className="btn btn-block btn-primary mt-2">
                                Загрузить <input onChange={this.onImageChange} className="add-book-image" type="file" style={{display: "none"}}/>
                            </label>
                        </div>
                        <div className="col-md-10">
                            <div className="form-group col-sm-12 row mx-0">
                              <input type="text"
                                     className="form-control add-book-name"
                                     name="name"
                                     placeholder="Название"
                                     autoComplete="off"
                                     value={book.name}
                                     onChange={this.handleChange}/>
                            </div>
                            <div className="form-group col-sm-12 row mx-0">
                              <input type="text"
                                     className="form-control add-book-author"
                                     name="author"
                                     placeholder="Автор"
                                     autoComplete="off"
                                     value={book.author}
                                     onChange={this.handleChange}/>
                            </div>
                            <div className="form-group col-sm-12 row mx-0">
                              <textarea type="text"
                                     className="form-control add-book-description"
                                     name="description"
                                     placeholder="Описание"
                                     autoComplete="off"
                                     rows={7}
                                     ref={c => (this.textarea = c)}
                                     value={book.description}
                                     onChange={this.handleChange}/>
                            </div>
                        </div>
                    </div>
                    <div className="col-sm-12 row px-0 mx-0 my-3 add-book-submit">
                        <input type="submit" className="btn btn-primary btn-block" value="Добавить"/>
                    </div>
                </form>
            </div>
          )
    }
}
AddBook.contextType = UserContext;
export const AddBookWithRouter = withRouter(AddBook);
