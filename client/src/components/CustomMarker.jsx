import { React, Component } from "react";
import { bindActionCreators } from "redux";
import { selectStation, getAddress } from "../actions/station.actions";
import { connect } from "react-redux";
import { InfoWindow, Marker } from "@react-google-maps/api";
import Emoji from "emoji-dictionary";

class CustomMarker extends Component {
  state = { showInfo: false, address: "" };

  onMarkerClick = () => {
    if (this.props.hasClickAction) {
      this.props.selectStation(this.props.station);
      this.props.getAddress(
        this.props.station.latitude,
        this.props.station.longitude
      );
    }
  };

  onMouseOver = () => {
    //this.props.getAddress(this.props.station.latitude, this.props.station.longitude);
    /* unfourtantly this is way too slow :(
      Preloading the nearest 15 stations is way to expensive
    ) */
    this.setState({ showInfo: true });
  };

  onMouseOut = () => {
    this.setState({ showInfo: false });
  };

  /**
   * Conditionally render marker image depending on station status
   *
   * @param {String} status - Station status
   * @returns {String} - URL of image
   */
  chooseStatus = (status) => {
    switch (status) {
      case "available":
        return "images/map-avail.png";
      case "maintenance":
        return "images/map-warn.png";
      case "in-use":
        return "images/map-inuse.png";
      case "defect":
        return "images/map-defect.png";
      default:
        return undefined;
    }
  };
  render() {
    return (
      <Marker
        onMouseOver={this.onMouseOver}
        onMouseOut={this.onMouseOut}
        onClick={() => this.onMarkerClick()}
        icon={this.chooseStatus(this.props.station.status)}
        zIndex={2}
        cluster={this.props.clusterer}
        visible={this.props.visible}
        {...this.props}
      >
        {this.state.showInfo && this.props.hasClickAction && (
          <InfoWindow>
            <div className="fade-in">
              <br />
              {this.props.station.owner} {Emoji.getUnicode(Emoji.getName("😊"))}
              {Emoji.getUnicode(Emoji.getName("🔌"))}
              <br />
              Distance:{" "}
              {(parseInt(this.props.station.distance.distance) / 1000).toFixed(
                1
              )}{" "}
              km
            </div>
          </InfoWindow>
        )}
      </Marker>
    );
  }
}

const mapStateToProps = (state) => ({
  stationFunc: state.station,
});

const mapDispatchToProps = (dispatch) =>
  bindActionCreators(
    {
      selectStation: selectStation,
      getAddress: getAddress,
    },
    dispatch
  );

export default connect(mapStateToProps, mapDispatchToProps)(CustomMarker);
