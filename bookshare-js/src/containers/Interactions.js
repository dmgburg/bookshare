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
    const my = await axios.get("/api/book/getMyInteractions");
    const toMe = await axios.get("/api/book/getInteractionsToMe");
    this.setState({
        myRequests: my.data,
        requestsToMe: toMe.data,
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
      return `<span><img width="150" height="200" align="middle" style="margin:10px 0px" src=${ "/api/book/public/getCover/" + params.data.book.coverId}></span>`;
  }

  static methodFromParent(params){
    console.log(params)
  }

  async cancelInteraction(interationId) {
    const axios = this.context.axios
    var params = new URLSearchParams();
    params.append('id', interationId);
    await axios.post("/api/book/cancelInteraction", params)
    this.loadInteractions()
  }

  async successInteraction(interationId) {
    const axios = this.context.axios
    var params = new URLSearchParams();
    params.append('id', interationId);
    await axios.post("/api/book/successInteraction", params)
    this.loadInteractions()
  }

  async rejectInteraction(interationId) {
    const axios = this.context.axios
    var params = new URLSearchParams();
    params.append('id', interationId);
    await axios.post("/api/book/rejectInteraction", params)
    this.loadInteractions()
  }

  async closeInteraction(interationId) {
    const axios = this.context.axios
    var params = new URLSearchParams();
    params.append('id', interationId);
    await axios.post("/api/book/closeInteraction", params)
    this.loadInteractions()
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
                context={{ componentParent: this }}
                frameworkComponents= {{
                    myRequestsRenderer: MyRequestsRenderer,
                }}
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
                    cellRenderer="myRequestsRenderer"
                    headerName=""
                    field="bookName" />
              </AgGridReact>
            </div>
          </div>
          <h1>Запросы мне</h1>
          <div id="center">
            <div
              id="myGrid"
              style={{
                boxSizing: "border-box",
                height: "100%",
                width: "100%"
              }}
              className="ag-theme-balham">
              <AgGridReact
                rowData={this.state.requestsToMe}
                domLayout="autoHeight"
                enableColResize={true}
                context={{ componentParent: this }}
                frameworkComponents= {{
                    requestsToMeRenderer: RequestsToMeRenderer
                }}
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
                    cellRenderer="requestsToMeRenderer"
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

class RequestsToMeRenderer extends React.Component{
    constructor(props) {
        super(props);
        this.successInteraction = this.successInteraction.bind(this);
        this.rejectInteraction = this.rejectInteraction.bind(this);
    }

    successInteraction() {
        this.props.context.componentParent.successInteraction(this.props.data.id)
    }

    rejectInteraction() {
        this.props.context.componentParent.rejectInteraction(this.props.data.id)
    }

    render() {
        return (
            <div className="container">
                <div className="row">
                    <button className="btn btn-success btn-block" onClick={this.successInteraction}>Можно забирать</button>
                </div>
                <div className="row">
                    <button className="btn btn-danger btn-block" onClick={this.rejectInteraction}>Отказать</button>
                </div>
            </div>
        );
    }
}

class MyRequestsRenderer extends React.Component{
    constructor(props) {
        super(props);
        this.cancelInteraction = this.cancelInteraction.bind(this);
        this.closeInteraction = this.closeInteraction.bind(this);
    }

    cancelInteraction() {
        this.props.context.componentParent.cancelInteraction(this.props.data.id)
    }

    closeInteraction() {
        this.props.context.componentParent.closeInteraction(this.props.data.id)
    }

    render() {
        if(this.props.data.state === "NEW"){
            return (
                <div>
                    <div>
                        <button className="btn btn-danger btn-block" onClick={this.cancelInteraction}>Отменить</button>
                    </div>
                </div>
            );
        } else if(this.props.data.state === "REJECTED"){
            return (<div>
                <div>
                    Держатель не сможет принести книгу
                </div>
                <div>
                    <button className="btn btn-success btn-block" onClick={this.closeInteraction}>OK</button>
                </div>
            </div>)
        } else if (this.props.data.state === "REJECTED") {
            return (<div>
                <div>
                    Книгу можно забирать
                </div>
                <div>
                    <button className="btn btn-success btn-block" onClick={this.closeInteraction}>Забрал</button>
                </div>
            </div>)
        }
    }
}