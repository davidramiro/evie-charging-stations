import React, { Component } from "react";
import "../login.css";
import { bindActionCreators } from "redux";
import { connect } from "react-redux";
import { getLocations } from "../actions/location.actions";
import {
  selectStation,
  getAddress,
  clearAddress,
  deleteStation,
} from "../actions/station.actions";
import { getFlags, updateFlags } from "../actions/flag.actions";
import { PaginatedList } from "react-paginated-list";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { Redirect } from "react-router-dom";
import {
  faExclamationCircle,
  faExclamationTriangle,
  faLocationArrow,
  faPlug,
  faTools,
  faPencilAlt,
} from "@fortawesome/free-solid-svg-icons";
import {
  Button,
  Input,
  InputGroup,
  InputGroupText,
  InputGroupAddon,
} from "reactstrap";
import { logOut } from "../actions/auth.actions";
import MapWithMarker from "./MapWithMarker";

const containerStyle = {
  width: "34rem",
  height: "34rem",
  position: "inherit",
};

class Administration extends Component {
  state = {
    lat: 51.312801,
    lng: 9.481544,
    stations: [],
    ccs2: undefined,
    chademo: undefined,
    type2: undefined,
    status: undefined,
  };

  mdSize = {};

  componentDidMount() {
    this.getStations();
  }

  getStations = async () => {
    await this.props.getLocations();
    await this.props.getFlags();

    this.mergeStations();
  };

  onMapLoad = (map) => {
    this.map = map;
  };

  locateStation = (lat, lng) => {
    this.map.setCenter({ lat: lat, lng: lng });
    this.map.setZoom(14);
  };

  mergeStations = () => {
    const fetchedStations = this.props.locations
      .filter((loc) => loc.owner === this.props.auth.user)
      .map((item) =>
        Object.assign(
          {},
          {
            ...item,
          },
          this.props.flags.find((flag) => flag.locationid === item.id)
        )
      );

    this.setState({ stations: fetchedStations });
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

  renderStatus = (status) => {
    let color = "";
    let statusText = "";
    let icon = undefined;

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
      <h6>
        Status:{" "}
        <FontAwesomeIcon className="ml-3" size="lg" color={color} icon={icon} />
        <span className="ml-2" style={{ color: color }}>
          {statusText}
        </span>
      </h6>
    );
  };

  renderTable = (type, power) => {
    let image = "";
    let name = "";

    switch (type) {
      case "ccs2":
        image = "images/ccs2-black.png";
        name = "CCS2";
        break;
      case "type2":
        image = "images/type2-black.png";
        name = "Type 2";
        break;
      case "chademo":
        image = "images/chademo-black.png";
        name = "ChaDeMo";
        break;
      default:
        return "";
    }

    return (
      <li className="list-group-item">
        <img
          src={image}
          style={{ height: "20px" }}
          className="img-fluid mr-3"
          alt="Charger"
        />
        {name}: {power}{" "}
      </li>
    );
  };

  handleSelect = (station) => {
    this.props.selectStation(station);
    this.props.getAddress(station.latitude, station.longitude);
    this.setState({
      id: station.id,
      locationid: station.locationid,
      ccs2: station.ccs2,
      chademo: station.chademo,
      type2: station.type2,
      status: station.status,
      lat: station.latitude,
      lng: station.longitude,
    });
  };

  handleCcs2Input = (ev) => {
    this.setState({ ccs2: ev.target.value });
  };

  handleType2Input = (ev) => {
    this.setState({ type2: ev.target.value });
  };

  handleChademoInput = (ev) => {
    this.setState({ chademo: ev.target.value });
  };

  handleStatusInput = (ev) => {
    this.setState({ status: ev.target.value });
  };

  handleUpdateStation = () => {
    this.props.updateFlags({
      id: this.props.station.selectedStation.id,
      locationid: this.props.station.selectedStation.locationid,
      ccs2: this.state.ccs2,
      chademo: this.state.chademo,
      type2: this.state.type2,
      status: this.state.status,
    });

    this.getStations();
  };

  handleDiscard = () => {
    this.setState({
      ccs2: "",
      chademo: "",
      type2: "",
      status: "",
    });
    this.props.selectStation({});
    this.props.clearAddress();
  };

  handleDelete = async () => {
    await this.props.deleteStation({
      id: this.props.station.selectedStation.id,
      locationid: this.props.station.selectedStation.locationid,
    });
    this.getStations();
  };

  render() {
    if (!this.props.auth.status) {
      return <Redirect to="/auth" />;
    }
    return (
      <div>
        <div className="mt-5">
          <div className="container">
            <div className="row justify-content-around">
              <div className="col-4-lg">
                <ul className="list-group">
                  <PaginatedList
                    list={this.state.stations}
                    itemsPerPage={5}
                    renderList={(list) => (
                      <>
                        {list.map((station, i) => {
                          return (
                            <li
                              key={i}
                              className={`list-group-item mb-1 ${
                                this.props.station.selectedStation.id ===
                                station.id
                                  ? "active"
                                  : ""
                              }`}
                            >
                              <div className="row ml-2 d-flex justify-content-between">
                                <div className="col-xs-6 px-auto">
                                  <h5>
                                    {station.owner} - Station {station.id}
                                  </h5>
                                  {this.renderStatus(station.status)}

                                  {"ccs2" in station
                                    ? "CCS2: " + station.ccs2 + " kW"
                                    : null}
                                  <br />
                                  {"chademo" in station
                                    ? "ChaDeMo: " + station.chademo + " kW"
                                    : null}
                                  <br />
                                  {"type2" in station
                                    ? "Type 2: " + station.type2 + " kW"
                                    : null}
                                  <br />
                                </div>
                                <div className="col-xs-6 ml-5">
                                  <div className="float-right">
                                    <Button
                                      size="sm"
                                      className="mr-2"
                                      onClick={() =>
                                        this.locateStation(
                                          station.latitude,
                                          station.longitude
                                        )
                                      }
                                    >
                                      <FontAwesomeIcon
                                        size="sm"
                                        icon={faLocationArrow}
                                      />
                                    </Button>
                                    <Button
                                      size="sm"
                                      className="mr-2"
                                      onClick={() => this.handleSelect(station)}
                                    >
                                      <FontAwesomeIcon
                                        size="sm"
                                        icon={faPencilAlt}
                                      />
                                    </Button>
                                  </div>
                                </div>
                              </div>
                            </li>
                          );
                        })}
                      </>
                    )}
                  />
                </ul>
              </div>
              <div className="col-8-lg">
                <div className="card" style={{ width: "34rem" }}>
                  <MapWithMarker
                    containerStyle={containerStyle}
                    onMapLoad={this.onMapLoad}
                    center={{ lat: this.state.lat, lng: this.state.lng }}
                    stations={this.state.stations}
                  />
                  <div className="card-body">
                    <h5 className="card-title mb-0">
                      Edit station{" "}
                      {this.props.station.selectedStation
                        ? this.props.station.selectedStation.id
                        : ""}
                    </h5>
                    {this.props.station.address.length > 0 ? (
                      <h6>{this.props.station.address}</h6>
                    ) : (
                      ""
                    )}
                  </div>
                  <ul className="list-group list-group-flush">
                    <li className="list-group-item">
                      <InputGroup>
                        <InputGroupAddon addonType="prepend">
                          <InputGroupText>CCS2</InputGroupText>
                        </InputGroupAddon>
                        <Input
                          placeholder="Leave blank to disable connector"
                          value={this.state.ccs2}
                          onChange={this.handleCcs2Input}
                        ></Input>
                      </InputGroup>
                    </li>
                    <li className="list-group-item">
                      <InputGroup>
                        <InputGroupAddon addonType="prepend">
                          <InputGroupText>ChaDeMo</InputGroupText>
                        </InputGroupAddon>
                        <Input
                          placeholder="Leave blank to disable connector"
                          value={this.state.chademo}
                          onChange={this.handleChademoInput}
                        ></Input>
                      </InputGroup>
                    </li>
                    <li className="list-group-item">
                      <InputGroup>
                        <InputGroupAddon addonType="prepend">
                          <InputGroupText>Type 2</InputGroupText>
                        </InputGroupAddon>
                        <Input
                          placeholder="Leave blank to disable connector"
                          value={this.state.type2}
                          onChange={this.handleType2Input}
                        ></Input>
                      </InputGroup>
                    </li>
                    <li className="list-group-item">
                      <Input
                        type="select"
                        name="select"
                        id="statusSelect"
                        value={this.state.status}
                        onChange={this.handleStatusInput}
                      >
                        <option value="available">Available</option>
                        <option value="in-use">In Use</option>
                        <option value="maintenance">Maintenance</option>
                        <option value="defect">Defect</option>
                      </Input>
                    </li>
                  </ul>
                  <div className="card-body">
                    <Button
                      color="primary"
                      className="mr-4"
                      onClick={this.handleUpdateStation}
                    >
                      Save
                    </Button>
                    <Button
                      color="secondary"
                      className="mr-4"
                      onClick={this.handleDiscard}
                    >
                      Discard
                    </Button>
                    <Button color="danger" onClick={this.handleDelete}>
                      Delete
                    </Button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    );
  }
}

const mapStateToProps = (state) => ({
  locations: state.location.locations,
  flags: state.flag.flags,
  station: state.station,
  auth: state.auth,
});

const mapDispatchToProps = (dispatch) =>
  bindActionCreators(
    {
      selectStation: selectStation,
      getAddress: getAddress,
      clearAddress: clearAddress,
      getLocations: getLocations,
      getFlags: getFlags,
      logOut: logOut,
      updateFlags: updateFlags,
      deleteStation: deleteStation,
    },
    dispatch
  );

export default connect(mapStateToProps, mapDispatchToProps)(Administration);
