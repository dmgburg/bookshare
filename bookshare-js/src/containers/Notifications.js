import React from 'react';
import { UserContext } from "../UserContext";
import { withRouter } from "react-router";

import { AgGridReact, AgGridColumn } from 'ag-grid-react';
import 'ag-grid-community/dist/styles/ag-grid.css';
import 'ag-grid-community/dist/styles/ag-theme-balham.css';

export default class Notifications extends React.Component {
  constructor(props, context) {
    super(props, context);

    this.state = {
      rowData: null
    };
  }

  async loadNotifications() {
    const axios = this.context.axios;
    const my = await axios.get("/api/book/notifications");
    this.setState({
        notifications: my.data,
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
    this.loadNotifications();
    params.api.sizeColumnsToFit();
  }

  static imageRenderer(params) {
      return `<span><img width="150" height="200" align="middle" style="margin:10px 0px" src=${ "/api/book/public/getCover/" + params.data.book.coverId}></span>`;
  }

  render() {
    return (
        <div className="container">
          <h1>Уведомления</h1>
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
                rowData={this.state.notifications}
                domLayout="autoHeight"
                enableColResize={true}
                context={{ componentParent: this }}
                frameworkComponents= {{
                    actionsRenderer: ActionsRenderer,
                }}
                onGridReady={this.onGridReady.bind(this)}>
                 <AgGridColumn
                    cellRenderer={Notifications.imageRenderer}
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
Notifications.contextType = UserContext;
export const NotificationsWithRouter = withRouter(Notifications);

class ActionsRenderer extends React.Component{
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
        if(this.props.data.type === "QUEUE_NOT_EMPTY"){
            return (
                <div>
                    <div>
                        <button className="btn btn-success btn-block">{this.props.data.fromUser} ждет эту книгу</button>
                    </div>
                </div>
            );
        }
        return null
    }
}