import { React, Component } from "react";
import { bindActionCreators } from "redux";
import { selectStation } from "../actions/station.actions";
import { closeSidebar } from "../actions/sidebar.actions";
import { openSidebar } from "../actions/sidebar.actions";
import { connect } from "react-redux";
import { slide as Sidebar } from "react-burger-menu";
import { Button } from "reactstrap";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faChargingStation,
  faLocationArrow,
} from "@fortawesome/free-solid-svg-icons";

class NearestStationsView extends Component {
  constructor(props) {
    super(props);

    this.state = {
      menuOpen: false,
    };

    this.handleStateChange = this.handleStateChange.bind(this);
  }

  handleStateChange(state) {
    if (state.isOpen) {
      this.props.openSidebar();
    } else {
      this.props.closeSidebar();
    }
    this.setState({ menuOpen: state.isOpen });
  }

  renderDistance = (meters) => {
    const km = meters / 1000;
    return km.toFixed(1) + " km";
  };

  handleGoTo = (station) => {
    this.props.setMapZoom(14);
    this.props.setMapCenter(station.latitude, station.longitude);
  };

  handleSelect = (station) => {
    this.props.selectStation(station);
  };

  renderColor = (status) => {
    switch (status) {
      case "available":
        return "#1cd6d9";
      case "in-use":
        return "#F6F5AE";
      case "maintenance":
      case "defect":
        return "#f05d5e";
      default:
        return undefined;
    }
  };

  render() {
    return (
      <Sidebar
        width={"30%"}
        isOpen={this.props.sidebar.isOpen}
        onStateChange={this.handleStateChange}
        pageWrapId={"page-wrap"}
        outerContainerId={"map-wrapper"}
      >
        <main id="page-wrap">
          <h3>
            Nearest stations
            <br />
          </h3>
          <div className="mt-5">
            <div className="container">
              <div className="row">
                <div className="col-12">
                  <table className="table table-image table-list">
                    <tbody>
                      {this.props.stations.slice(0, 10).map((station, i) => {
                        return (
                          <tr key={i}>
                            <th scope="row" className="w-30">
                              {station.owner}
                              <br />
                              <h5
                                style={{
                                  color: this.renderColor(station.status),
                                }}
                              >
                                Station {station.id}
                              </h5>
                            </th>
                            <td className="w-45">
                              {this.renderDistance(station.distance.distance)}
                            </td>
                            <td className="w-25">
                              <Button
                                size="sm"
                                className="mr-2"
                                onClick={() => this.handleGoTo(station)}
                              >
                                <FontAwesomeIcon
                                  size="sm"
                                  icon={faLocationArrow}
                                />
                              </Button>
                              <Button
                                size="sm"
                                onClick={() => this.handleSelect(station)}
                              >
                                <FontAwesomeIcon
                                  size="sm"
                                  icon={faChargingStation}
                                />
                              </Button>
                            </td>
                          </tr>
                        );
                      })}
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          </div>
        </main>
      </Sidebar>
    );
  }
}

const mapStateToProps = (state) => ({
  sidebar: state.sidebar,
});

const mapDispatchToProps = (dispatch) =>
  bindActionCreators(
    {
      selectStation: selectStation,
      closeSidebar: closeSidebar,
      openSidebar: openSidebar,
    },
    dispatch
  );

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(NearestStationsView);
