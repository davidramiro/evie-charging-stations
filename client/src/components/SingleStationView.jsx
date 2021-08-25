import { React, Component } from "react";
import { bindActionCreators } from "redux";
import { clearSelection } from "../actions/station.actions";
import { connect } from "react-redux";
import { slide as Sidebar } from "react-burger-menu";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faPlug,
  faSpinner,
  faExclamationCircle,
  faExclamationTriangle,
  faTools,
  faLocationArrow,
} from "@fortawesome/free-solid-svg-icons";
import { Button } from "reactstrap";

class SingleStationView extends Component {
  renderStatus = (status) => {
    let color = "";
    let statusText = "";
    let icon = faSpinner;

    switch (status) {
      case "available":
        color = "#1cd6d9";
        statusText = "Operational";
        icon = faPlug;
        break;
      case "in-use":
        color = "#F6F5AE";
        statusText = "Occupied";
        icon = faExclamationCircle;
        break;
      case "maintenance":
        color = "#f05d5e";
        statusText = "Down for Maintenance";
        icon = faTools;
        break;
      case "defect":
        color = "#f05d5e";
        statusText = "Defect";
        icon = faExclamationTriangle;
        break;
      default:
        break;
    }
    return (
      <h4>
        Status:{" "}
        <FontAwesomeIcon className="ml-3" size="lg" color={color} icon={icon} />
        <span className="ml-2" style={{ color: color }}>
          {statusText}
        </span>
      </h4>
    );
  };

  renderTable = (type, power) => {
    let image = "";
    let name = "";

    switch (type) {
      case "ccs2":
        image = "images/ccs2-white.png";
        name = "CCS2";
        break;
      case "type2":
        image = "images/type2-white.png";
        name = "Type 2";
        break;
      case "chademo":
        image = "images/chademo-white.png";
        name = "ChaDeMo";
        break;
      case "tesla":
        image = "images/tesla-white.png";
        name = "Tesla Proprietary";
        break;
      default:
        return "";
    }

    return (
      <tr>
        <th scope="row" className="w-30">
          {name}
        </th>
        <td className="w-45">
          <img src={image} className="img-fluid" alt="Charger" />
        </td>
        <td className="w-25">
          <h4>{power}</h4> kW
        </td>
      </tr>
    );
  };

  handleNavigate = (station) => {
    window.open(
      `https://www.google.com/maps/dir/?api=1&origin=${this.props.geoLat},${this.props.geoLng}&destination=${this.props.station.selectedStation.latitude},${this.props.station.selectedStation.longitude}`
    );
  };

  render() {
    return (
      <Sidebar
        right
        width={"30%"}
        isOpen={Object.keys(this.props.station.selectedStation).length > 0}
        pageWrapId={"page-wrap"}
        onClose={() => this.props.clearSelection()}
        outerContainerId={"map-wrapper"}
      >
        <main id="page-wrap">
          <h2>{this.props.station.selectedStation.owner}</h2>
          <h3>Station {this.props.station.selectedStation.id}</h3>
          <h6 className="mt-3 mb-3">{this.props.station.address}</h6>

          <div className="mt-3 mb-5">
            {this.renderStatus(this.props.station.selectedStation.status)}
          </div>

          <div className="container">
            <div className="row">
              <div className="col-12">
                <table className="table table-image">
                  <thead>
                    <tr>
                      <th scope="col">Connector</th>
                      <th scope="col"></th>
                      <th scope="col">Power</th>
                    </tr>
                  </thead>
                  <tbody>
                    {"ccs2" in this.props.station.selectedStation
                      ? this.renderTable(
                          "ccs2",
                          this.props.station.selectedStation.ccs2
                        )
                      : undefined}
                    {"chademo" in this.props.station.selectedStation
                      ? this.renderTable(
                          "chademo",
                          this.props.station.selectedStation.chademo
                        )
                      : undefined}
                    {"type2" in this.props.station.selectedStation
                      ? this.renderTable(
                          "type2",
                          this.props.station.selectedStation.type2
                        )
                      : undefined}
                    {"tesla" in this.props.station.selectedStation
                      ? this.renderTable(
                          "tesla",
                          this.props.station.selectedStation.tesla
                        )
                      : undefined}
                  </tbody>
                </table>
                <Button color="primary" onClick={this.handleNavigate}>
                  <FontAwesomeIcon
                    className="mr-3"
                    size="sm"
                    color="white"
                    icon={faLocationArrow}
                  />
                  Get Directions
                </Button>
              </div>
            </div>
          </div>
        </main>
      </Sidebar>
    );
  }
}

const mapStateToProps = (state) => ({
  locations: state.location.locations,
  flags: state.flag.flags,
  station: state.station,
});

const mapDispatchToProps = (dispatch) =>
  bindActionCreators(
    {
      clearSelection: clearSelection,
    },
    dispatch
  );

export default connect(mapStateToProps, mapDispatchToProps)(SingleStationView);
