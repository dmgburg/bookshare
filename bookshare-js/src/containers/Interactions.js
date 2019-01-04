import React from 'react';
import { UserContext } from "../UserContext";

import { AgGridReact, AgGridColumn } from 'ag-grid-react';
import 'ag-grid-community/dist/styles/ag-grid.css';
import 'ag-grid-community/dist/styles/ag-theme-balham.css';

export default class Interactions extends React.Component {
  constructor(props, context) {
    super(props, context);

    this.state = {
      rowData: null
    };
  }

  async loadInteractions() {
    const axios = this.context.axios;
    const my = await axios.get("/getMyInteractions");
    const toMe = await axios.get("/getInteractionsToMe");
    this.setState({
        myRequests: my.data,
        requestsToMe: toMe.data,
        frameworkComponents: {
          actionsRenderer: ActionsRenderer
        }
    })
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

  onGridReady(params) {
    this.gridApi = params.api;
    this.gridColumnApi = params.columnApi;
    this.loadInteractions();
    params.api.sizeColumnsToFit();
  }

  static imageRenderer(params) {
      return `<span><img width="150" height="200" align="middle" style="margin:10px 0px" src=${ "http://localhost:8080/getCover/" + params.data.book.coverId}></span>`;
  }

  static cancel(params){
    console.log(params)
  }

  async cancel(interationId) {
    const axios = this.context.axios
    const response = axios.post("/cancelInteraction/" + interationId)
    console.log()
  }

  render() {
    return (
        <div className="interactions container">
          <h1>Мои запросы</h1>
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
                rowData={this.state.myRequests}
                domLayout="autoHeight"
                enableColResize={true}
                frameworkComponents={this.state.frameworkComponents}
                onGridReady={this.onGridReady.bind(this)}>
                 <AgGridColumn
                     headerName="От"
                     field="fromUser" />
                 <AgGridColumn headerName="К" field="toUser" />
                 <AgGridColumn
                    cellRenderer={Interactions.imageRenderer}
                    headerName="Книга"
                    width={110}
                    autoHeight
                    field="bookName" />
                 <AgGridColumn
                    cellRenderer="actionsRenderer"
                    headerName=""
                    field="bookName" />
              </AgGridReact>
            </div>
          </div>

        </div>
    );
  }
}
Interactions.contextType = UserContext;

class ActionsRenderer extends React.Component{
    render() {
        return (<span><button class="btn btn-danger btn-block">Отменить</button></span>);
    }
}

// <h1>Запросы мне</h1>
//          <div id="center">
//            <div
//              id="myGrid"
//              style={{
//                boxSizing: "border-box",
//                height: "100%",
//                width: "100%"
//              }}
//              className="ag-theme-balham">
//              <AgGridReact
//                rowData={this.state.requestsToMe}
//                domLayout="autoHeight"
//                enableColResize={true}
//                onGridReady={this.onGridReady.bind(this)}>
//                 <AgGridColumn
//                     headerName="От"
//                     field="fromUser" />
//                 <AgGridColumn headerName="К" field="toUser" />
//                 <AgGridColumn
//                    cellRenderer={Interactions.imageRenderer}
//                    headerName="Книга"
//                    width={110}
//                    autoHeight
//                    field="bookName" />
//                 <AgGridColumn
//                    cellRenderer={"actionsRenderer"}
//                    headerName=""
//                    field="bookName" />
//              </AgGridReact>
//            </div>
//          </div>