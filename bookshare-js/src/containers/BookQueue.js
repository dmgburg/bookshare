import React from 'react';
import { UserContext } from "../UserContext";

export default class BookQueue extends React.Component{
    list(queue){
      return queue.map((queueUser) => {
            if(queueUser === this.context.email){
                return <button className="list-group-item list-group-item-action" onClick={this.props.removeFromQueue}>{queueUser} <span class="badge float-right badge-danger">Выйти</span></button>
            } else{
                return <li className="list-group-item list-group-item-action">{queueUser} </li>
            }
        }
      )
    }

    render() {
        const queue = this.props.book.queue
        if(!queue || queue.length === 0){
            return <RequestButton
                      email={this.context.email}
                      book={this.props.book}
                      askForBook={this.props.askForBook}
                      handoverBook={this.props.handoverBook}/>
        }
        return (
        <div className="mt-1">
            <div className="row">
                <div className="col-sm-6 list-group">
                  <li className="list-group-item list-group-item-action active">Очередь</li>
                  {this.list(queue)}
                </div>
            </div>
             <RequestButton
                  email={this.context.email}
                  book={this.props.book}
                  askForBook={this.props.askForBook}
                  handoverBook={this.props.handoverBook} />
        </div>
        )
    }
}
BookQueue.contextType = UserContext;

class RequestButton extends React.Component{
    itsYourBook(props){
        return props.email === props.book.holder
                    && props.email === props.book.owner
                    && props.book.queue
                    && props.book.queue.length === 0
    }

    render() {
        if(!this.props.email){
        // not logged in
            return null
        }
        if(this.props.book.queue && this.props.book.queue.includes(this.props.email)){
        // already in queue
            return null
        }
        if (this.itsYourBook(this.props)){
            return (<div className="btn btn-success mt-2 disabled">Это ваша книга</div>)
        }
        if (this.props.email === this.props.book.holder){
            if(!this.props.book.queue || this.props.book.queue.length === 0){
                return (<div className="btn btn-success mt-2" onClick={this.props.handoverBook}>Вернуть книгу</div>)
            } else {
                return (<div className="btn btn-success mt-2" onClick={this.props.handoverBook}>Передать книгу {this.props.book.queue[0]}</div>)
            }
        }
        return (<div className="btn btn-success mt-2" onClick={this.props.askForBook}>Попросить книгу</div>)
    }
}