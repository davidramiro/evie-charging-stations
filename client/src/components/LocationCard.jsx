import React, { Component } from "react";
import Emoji from "emoji-dictionary";

class LocationCard extends Component {
  render() {
    return (
      <div className="card w-50 location-card">
        <div className="card-body">
          <h5 className="card-title">
            Welcome to the EVIE Charging App!{" "}
            {Emoji.getUnicode(Emoji.getName("🔋"))}
          </h5>
          <p className="card-text">
            {this.props.permissionRestricted
              ? `Check your browsers location permissions.${Emoji.getUnicode(
                  Emoji.getName("🌍")
                )}`
              : "The app is loading..."}
          </p>
          {this.props.permissionRestricted && (
            <button
              className="btn btn-primary"
              onClick={this.props.buttonClick}
            >
              Continue with default Location (Wolfsburg)
            </button>
          )}
        </div>
      </div>
    );
  }
}
export default LocationCard;
