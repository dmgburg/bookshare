import React from 'react';
import { UserContext } from "../UserContext";
import { ActionsRenderer } from "./ActionsRenderer";

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
      const response = await axios.post("/api/book/confirmHandover/" + id)
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
                pagination={true}
                paginationPageSize={3}
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

class DetailsRenderer extends React.Component{
    render() {
        return (<span>Автор: {this.props.data.author}<br/>{this.props.data.name}<br/>Сейчас книга у {this.props.data.holder}</span>);
    }
}