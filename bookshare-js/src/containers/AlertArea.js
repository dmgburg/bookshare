import React from 'react';
import { UserContext } from "../UserContext";
import { withRouter } from "react-router";

export default class AlertArea extends React.Component {
    constructor(props){
        super(props)

        this.clearAlert = this.clearAlert.bind(this)
    }

    clearAlert() {
        this.props.setAlert(null)
    }

    render() {
        if(this.props.alert){
            return (
                <div class="alert alert-warning alert-dismissible fade show" role="alert">
                  <button type="button" class="close" data-dismiss="alert" aria-label="Close" onClick={this.clearAlert}>
                    <span aria-hidden="true">&times;</span>
                  </button>
                  {this.props.alert}
                </div>
              )
        } else {
            return null
        }
    }
}
