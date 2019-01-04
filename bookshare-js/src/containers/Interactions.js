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

  async loadIntaractions() {
    const resp = this.context.axios.get("/getInteractions")
    this.setState({
        rowData: resp.data
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

    params.api.sizeColumnsToFit();
  }

  render() {
    return (
        <div className="interactions container">
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
                onGridReady={this.onGridReady.bind(this)}
              >
                 <AgGridColumn
                     headerName=""
                     field="fromUser" />
                 <AgGridColumn headerName="" field="toUser" />
                 <AgGridColumn cellClass="cell-wrap-text" headerName="Книга" field="bookName" />
              </AgGridReact>
            </div>
          </div>
        </div>
    );
  }
}
Books.contextType = UserContext;

