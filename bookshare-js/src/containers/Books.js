import React from 'react';
import { UserContext } from "../UserContext";

import { AgGridReact, AgGridColumn } from 'ag-grid-react';
import 'ag-grid-community/dist/styles/ag-grid.css';
import 'ag-grid-community/dist/styles/ag-theme-balham.css';

export default class Books extends React.Component {
  constructor(props, context) {
    super(props, context);

    this.state = {
      rowData: null
    };
  }

  fetchState(){
   const data = this.props.loadData()

      this.loadNotifications()
      data.then((books) =>{
          this.setState({
              rowData: books
          })
      })
   }

//FIXME move to async
  componentDidMount(){
    this.fetchState()
  }

  onGridReady(params) {
    this.gridApi = params.api;
    this.gridColumnApi = params.columnApi;

    params.api.sizeColumnsToFit();
  }

  async loadNotifications() {
    const axios = this.context.axios;
    const my = await axios.get("/api/book/notifications");
    this.setState({
        notifications: my.data,
    })
  }

  async handoverBook(id){
      const axios = this.context.axios;
      const response = await axios.get("/api/book/handoverBook/" + id)
      console.log(response.data)
      this.fetchState()
  }

  async confirmHandover(id){
      const axios = this.context.axios;
      const response = await axios.get("/api/book/confirmHandover/" + id)
      console.log(response.data)
      this.fetchState()
  }

  async askForBook(id){
      const axios = this.context.axios;
      const response = await axios.get("/api/book/addToQueue/" + id)
      console.log(response.data)
      this.fetchState()
  }

 static imageRenderer(params) {
   return `<span><img width="150" height="200" align="middle" style="margin:10px 0px" src=${ "/api/book/public/getCover/" + params.value}></span>`;
 }

  onRowSelected(event) {
    this.props.history.push("/bookDetails/" + event.node.data.id)
  }

  render() {
    return (
        <div className="books container">
          <div id="center">
            <div
              id="myGrid"
              style={{
                boxSizing: "border-box",
                height: "100%",
                width: "100%"
              }}
              className="ag-theme-balham"
            >
              <AgGridReact
                rowData={this.state.rowData}
                domLayout="autoHeight"
                enableColResize={true}
                rowSelection={"single"}
                context={{
                    componentParent: this,
                    email: this.context.email
                }}
                frameworkComponents= {{
                    detailsRenderer: DetailsRenderer,
                    actionsRenderer: ActionsRenderer,
                }}
                onGridReady={this.onGridReady.bind(this)}>
                 <AgGridColumn headerName="Обложка" width={200}
                     field="coverId"
                     suppressSizeToFit
                     cellRenderer={Books.imageRenderer}
                     onCellClicked={this.onRowSelected.bind(this)}
                     autoHeight />
                 <AgGridColumn
                    cellClass="cell-wrap-text"
                    headerName="Название"
                    field="name"
                    onCellClicked={this.onRowSelected.bind(this)}
                    cellRenderer="detailsRenderer"/>
                 <AgGridColumn
                    cellClass="cell-wrap-text"
                    headerName="Описание"
                    onCellClicked={this.onRowSelected.bind(this)}
                    field="description" />
                 <AgGridColumn
                     cellClass="cell-wrap-text"
                     cellRenderer="actionsRenderer"
                     headerName="Действия"
                     hide={this.props.actionsHidden}
                     field="description" />
              </AgGridReact>
            </div>
          </div>
        </div>
    );
  }
}
Books.contextType = UserContext;

class ActionsRenderer extends React.Component{
    constructor(props) {
        super(props);
        this.handoverBook = this.handoverBook.bind(this);
        this.confirmHandover = this.confirmHandover.bind(this);
        this.askForBook = this.askForBook.bind(this);
    }

    handoverBook() {
        this.props.context.componentParent.handoverBook(this.props.data.id)
    }

    confirmHandover() {
        this.props.context.componentParent.confirmHandover(this.props.data.id)
    }

    askForBook() {
        this.props.context.componentParent.askForBook(this.props.data.id)
    }

    render() {
        if(this.props.data.notification && this.props.data.notification.type === "OWNER_WANTS_THE_BOOK" && this.props.data.notification.fromUser === this.props.context.email){
            return(
                <div>
                    <div>
                        <span>Вы попросили {this.props.data.holder} вернуть книгу прямо сейчас</span>
                        <button className="btn btn-success btn-block" onClick={this.confirmHandover}>Забрал</button>
                    </div>
                </div>
            )
        } else if(!this.props.data.notification && this.props.data.owner === this.props.context.email && this.props.data.owner === this.props.data.holder){
            return (
                <div>
                    <div>
                        <span>Это Ваша книга</span>
                    </div>
                </div>
            );
        } else if(this.props.data.notification && this.props.data.notification.type === "QUEUE_NOT_EMPTY"){
            return (
                <div>
                    <div>
                        <button className="btn btn-success btn-block" onClick={this.handoverBook}> Передать книгу {this.props.data.notification.fromUser}</button>
                    </div>
                </div>
            );
        } else if(this.props.data.notification && this.props.data.notification.type === "BOOK_IS_WAITING" && this.props.data.notification.fromUser === this.props.context.email){
            return(
                <div>
                    <div>
                        <span>Вы пообещали книгу {this.props.data.notification.toUser}, если вы отдали книгу, напомните ему подтвердить</span>
                    </div>
                </div>
            )
        } else if(this.props.data.notification && this.props.data.notification.type === "BOOK_IS_WAITING" && this.props.data.notification.fromUser !== this.props.context.email){
            return(
                <div>
                    <div>
                        <span>{this.props.data.notification.fromUser} готов отдать книгу</span>
                        <button className="btn btn-success btn-block" onClick={this.confirmHandover}>Забрал</button>
                    </div>
                </div>
            )
        } else if(this.props.data.notification && this.props.data.notification.type === "OWNER_WANTS_THE_BOOK" && this.props.data.notification.fromUser !== this.props.context.email){
            return(
                <div>
                    <div>
                        <span>Владелец книги {this.props.data.notification.fromUser} просит отдать книгу сейчас</span>
                    </div>
                </div>
            )
        } else if(this.props.data.owner === this.props.context.email && this.props.data.owner !== this.props.data.holder){
            return (
                <div>
                    <div>
                        <span>Это Ваша книга. Сейчас она у {this.props.data.holder}</span>
                        <button className="btn btn-success btn-block" onClick={this.askForBook}>Попросить вернуть книгу сейчac</button>
                    </div>
                </div>
            );
        }
        return null
    }
}

class DetailsRenderer extends React.Component{
    render() {
        return (<span>Автор: {this.props.data.author}<br/>{this.props.data.name}<br/>Сейчас книга у {this.props.data.holder}</span>);
    }
}