import React from 'react';
import { UserContext } from "../UserContext";

export class ActionsRenderer extends React.Component{
    constructor(props) {
        super(props);
        this.handoverBook = this.handoverBook.bind(this);
        this.confirmHandover = this.confirmHandover.bind(this);
        this.askForBook = this.askForBook.bind(this);
    }

    handoverBook() {
        this.props.context.handoverBook(this.props.data.id)
    }

    confirmHandover() {
        this.props.context.confirmHandover(this.props.data.id)
    }

    askForBook() {
        this.props.context.askForBook(this.props.data.id)
    }

    render() {
        console.log("ActionRenderer data:", JSON.stringify(this.props.data, null, 2))
        if(this.props.data.notification) {
            let notification = this.props.data.notification
            if(this.props.data.notification.type === "OWNER_WANTS_THE_BOOK"){
                if(this.props.data.owner === this.props.context.email){
                    return(
                         <div>
                             <div>
                                 <span className="action-requested-now">Вы попросили {this.props.data.holder} вернуть книгу прямо сейчас</span>
                                 <button className="btn btn-success btn-block action-confirm-handover" onClick={this.confirmHandover}>Забрал</button>
                             </div>
                         </div>
                    )
                } else {
                    return(
                        <div>
                            <div>
                                <span className="action-owner-requested">Владелец книги {this.props.data.notification.fromUser} просит отдать книгу сейчас</span>
                            </div>
                        </div>
                    )
                }
            } else if (this.props.data.notification.type === "BOOK_IS_WAITING") {
                if(this.props.data.notification.fromUser === this.props.context.email){
                    return(
                        <div>
                            <div>
                                <span className="action-promised">Вы пообещали книгу {this.props.data.notification.toUser}, если вы отдали книгу, напомните ему подтвердить</span>
                            </div>
                        </div>
                    )
                 } else {
                    return(
                        <div>
                            <div>
                                <span>{this.props.data.notification.fromUser} готов отдать книгу</span>
                                <button className="btn btn-success btn-block action-confirm-handover" onClick={this.confirmHandover}>Забрал</button>
                            </div>
                        </div>
                    )
                 }
            }
        } else {
            if(this.props.data.owner === this.props.context.email){
                if (this.props.data.owner === this.props.data.holder) {
                    return (
                        <div>
                            <div>
                                <span className="action-your-book">Это Ваша книга</span>
                            </div>
                        </div>
                    );
                } else {
                    return (
                        <div>
                            <div>
                                <span>Это Ваша книга. Сейчас она у {this.props.data.holder}</span>
                                <button className="btn btn-success btn-block action-request-now" onClick={this.askForBook}>Попросить вернуть книгу сейчac</button>
                            </div>
                        </div>
                    );
                }
            }
        }
        if(this.props.data.queue && this.props.data.queue.length > 0){
            if (this.props.data.queue.includes(this.props.context.email)){
                // already in queue
                return (<span className="action-already-in-queue">Вы в очереди на книгу</span>)
            } else if (this.props.data.holder === this.props.context.email){
                return (<button className="btn btn-success btn-block action-handover" onClick={this.handoverBook}>Передать книгу</button>)
            }
        }
        if (this.props.data.holder === this.props.context.email) {
            return (<span className="action-you-have-the-book">Книга сейчас у Вас</span>)
        }
        return (<div className="btn btn-success mt-2 action-ask-book" onClick={this.askForBook}>Попросить книгу</div>)
    }
}
ActionsRenderer.contextType = UserContext;