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

//FIXME move to async
  componentDidMount(){
     const data = this.props.loadData()

    data.then((books) =>{
        this.setState({
            rowData: books
        })
    })

  }

  onGridReady(params) {
    this.gridApi = params.api;
    this.gridColumnApi = params.columnApi;

    params.api.sizeColumnsToFit();
  }

 static imageRenderer(params) {
   return `<span><img width="150" height="200" align="middle" style="margin:10px 0px" src=${ "http://localhost:8080/api/book/public/getCover/" + params.value}></span>`;
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
                onRowSelected={this.onRowSelected.bind(this)}
                onGridReady={this.onGridReady.bind(this)}
              >
                 <AgGridColumn headerName="Обложка" width={200}
                     field="coverId"
                     suppressSizeToFit
                     cellRenderer={Books.imageRenderer}
                     autoHeight />
                 <AgGridColumn
                     headerName="Название"
                     field="name" />
                 <AgGridColumn headerName="Автор" field="author" />
                 <AgGridColumn cellClass="cell-wrap-text" headerName="Описание" field="description" />
              </AgGridReact>
            </div>
          </div>
        </div>
    );
  }
}
Books.contextType = UserContext;
