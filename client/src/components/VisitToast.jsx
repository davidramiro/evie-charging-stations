import React, { Component } from "react";
import Emoji from "emoji-dictionary";
import { Toast } from "react-bootstrap";

class VisitToast extends Component {
  state = { show: true };
  render() {
    return (
      <Toast
        className="toast-map"
        animation={true}
        onClose={() => this.setState({ show: false })}
        show={this.state.show}
        autohide
        delay={10000}
      >
        <Toast.Header>
          <img src="holder.js/20x20?text=%20" className="rounded mr-2" alt="" />
          <strong className="mr-auto">
            Thanks for using the EV Charging App{" "}
            {Emoji.getUnicode(Emoji.getName("🔋"))}
          </strong>
        </Toast.Header>
        <Toast.Body>
          There are {this.props.stationStats.count} stations in a 15 km radius
          around you.
          <br />
          {this.props.stationStats.availableCount} station are currently
          available {Emoji.getUnicode(Emoji.getName("😊"))}, while{" "}
          {this.props.stationStats.inUseCount} are in use.{" "}
          {Emoji.getUnicode(Emoji.getName("🔌"))}
        </Toast.Body>
      </Toast>
    );
  }
}
export default VisitToast;
