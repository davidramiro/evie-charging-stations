import React, { Component } from "react";
import LocationCard from "./LocationCard";
import Loader from "react-loader-spinner";

class LoadingScreen extends Component {
  render() {
    return (
      <div>
        <div className="loading-container">
          <Loader className="loader" type="Puff" color="#00BFFF" />
        </div>
        <LocationCard
          buttonClick={this.props.continueDefault}
          permissionRestricted={this.props.permissionRestricted}
        />
      </div>
    );
  }
}
export default LoadingScreen;
