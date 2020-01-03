import React from 'react';
import { UserContext } from "../UserContext";
import { ActionsRenderer } from "./ActionsRenderer";

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
        console.log("BookQueue render props:" + JSON.stringify(this.props))
        console.log("BookQueue render context:" + JSON.stringify(this.context))
        const queue = this.props.book.queue
        let aContext = Object.assign({}, this.context)
        aContext.componentParent = this
        let extra = (<div></div>)
        if(queue && queue.length > 0){
            extra = (<div className="row">
                                 <div className="col-sm-6 list-group">
                                   <li className="list-group-item list-group-item-action active">Очередь</li>
                                   {this.list(queue)}
                                 </div>
                             </div>)
        }
        return (
        <div className="mt-1">
             {extra}
             <ActionsRenderer
                  context={{
                    email: this.context.email,
                    askForBook: this.props.askForBook,
                    confirmHandover:this.props.confirmHandover,
                    handoverBook:this.props.handoverBook
                  }}
                  data={this.props.book}/>
        </div>
        )
    }
}
BookQueue.contextType = UserContext;