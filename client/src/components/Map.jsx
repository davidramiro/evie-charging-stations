import { React, Component } from "react";
import VisitToast from "./VisitToast";
import NearestStationsView from "./NearestStationsView";
import SingleStationView from "./SingleStationView";
import { getLocations, sortingFinished } from "../actions/location.actions";
import { getFlags } from "../actions/flag.actions";
import { openSidebar } from "../actions/sidebar.actions";
import { clearSelection } from "../actions/station.actions";
import { bindActionCreators } from "redux";
import { connect } from "react-redux";
import { Marker } from "@react-google-maps/api";

import { headingDistanceTo } from "geolocation-utils";
import LoadingScreen from "./LoadingScreen";
import MapWithMarker from "./MapWithMarker";
import MapControls from "./MapControls";

const containerStyle = {
  width: "100%",
  height: "100%",
  position: "inherit",
};

class Map extends Component {
  constructor(props) {
    super(props);
    this.autocomplete = null;
    this.geoLoc = navigator.geolocation;
    this.onSearchLoad = this.onSearchLoad.bind(this);
    this.onPlaceChanged = this.onPlaceChanged.bind(this);
  }

  state = {
    // default coordinates for first visit, central germany
    userLat: 52.427547,
    userLng: 10.78042,
    firstLat: 0,
    firstLng: 0,
    located: false,
    sorted: false,
    stations: [],
    leftMenuOpen: false,
    dropdownOpen: false,
    zoomOnClick: true,
    info: false,
    permissionRestricted: false,
  };

  /**
   * Get user location on component mount
   */
  async componentDidMount() {
    this.watchID = this.geoLoc.watchPosition(
      (pos) => {
        this.setState({
          userLat: pos.coords.latitude,
          userLng: pos.coords.longitude,
          firstLat: pos.coords.latitude,
          firstLng: pos.coords.longitude,
          located: true,
        });
        this.sortStations(this.state.firstLat, this.state.firstLng);
      },
      (error) =>
        this.setState({
          permissionRestricted: true,
        })
    );
    await this.initialize();
  }

  /**
   * Fetch and arrange stations
   */
  async initialize() {
    await this.props.getLocations();
    await this.props.getFlags();
  }

  continueDefault = () => {
    this.geoLoc.clearWatch(this.watchID);
    this.setState({
      firstLat: this.state.userLat,
      firstLng: this.state.userLng,
    });
    this.sortStations(this.state.userLat, this.state.userLng);
  };

  /**
   * Sorts the station list in state by distance to the provided coordinates
   *
   * @param {number} lat - Latitude
   * @param {number} lng - Longitude
   */
  sortStations = (lat, lng) => {
    const fetchedStations = this.props.locations
      .map((item) =>
        Object.assign(
          {},
          {
            ...item,
            distance: headingDistanceTo(
              { lat: lat, lon: lng },
              { lat: item.latitude, lon: item.longitude }
            ),
            visible: true,
          },
          this.props.flags.find((flag) => flag.locationid === item.id)
        )
      )
      .sort((s1, s2) => {
        return s1.distance.distance - s2.distance.distance;
      });

    this.setState({ stations: fetchedStations, sorted: true });
  };

  /**
   * Initialize Places API Autocomplete instance
   */
  onSearchLoad(autocomplete) {
    this.autocomplete = autocomplete;
  }

  /**
   * Assign GoogleMap instance to variable
   */
  onMapLoad = (map) => {
    this.map = map;
  };

  onClustererLoad = (clusterer) => {
    this.clusterer = clusterer;
    window.google.maps.event.addListener(this.map, "dragstart", () => {
      this.clusterer.setZoomOnClick(false);
    });
    window.google.maps.event.addListener(this.map, "mouseup", () => {
      setTimeout(() => {
        this.clusterer.setZoomOnClick(true);
      }, 50);
    });
  };

  /**
   * Fired when successfully uses the Places API search, updates the current coordinates in state
   */
  onPlaceChanged = () => {
    if (Object.keys(this.autocomplete.getPlace()).length > 1) {
      const lat = this.autocomplete.getPlace().geometry.location.lat();
      const lng = this.autocomplete.getPlace().geometry.location.lng();

      this.setState({
        userLat: lat,
        userLng: lng,
      });
      this.setMapZoom(12);
      this.sortStations(lat, lng);
    }
  };

  /**
   * Fired when user ends scrolling the map view, updates the current coordinates in state
   */

  mapScrollingEnded = () => {
    if (this.state.located && this.map != null) {
      const lat = this.map.getCenter().lat();
      const lng = this.map.getCenter().lng();
      this.setState({
        userLat: lat,
        userLng: lng,
      });
    }
  };

  setMapZoom = (num) => {
    this.map.setZoom(num);
  };

  zoomInMap = () => {
    this.map.setZoom(this.map.getZoom() + 1);
  };

  zoomOutMap = () => {
    this.map.setZoom(this.map.getZoom() - 1);
  };
  refreshZoom = () => {
    this.map.setZoom(this.map.getZoom());
  };

  setMapCenter = (lat, lng) => {
    this.map.setCenter({ lat: lat, lng: lng });
    this.setState({
      userLat: lat,
      userLng: lng,
    });
  };

  setCenterAndZoom = (lat, lng) => {
    this.map.setCenter({ lat: lat, lng: lng });
    this.map.setZoom(13);
  };

  stationStatsRadius = () => {
    const nearStations = this.state.stations.filter(
      (station) => station.distance.distance < 15000
    );

    const availableCount = nearStations.filter(
      (station) => station.status === "available"
    ).length;

    const inUseCount = nearStations.filter(
      (station) => station.status === "in-use"
    ).length;

    return {
      count: nearStations.length,
      availableCount: availableCount,
      inUseCount: inUseCount,
    };
  };

  /**
   * </ Burger menu helper functions >
   */

  toggleFilterDropdown = () => {
    this.map.setCenter({ lat: 52.313051, lng: 10.63504 });
    this.setState({ dropdownOpen: !this.state.dropdownOpen });
  };

  getAllStations = () => {
    return this.state.stations.map((station) =>
      Object.assign(
        {},
        {
          ...station,
          visible: true,
        }
      )
    );
  };

  filterForAvailable = (filteredStations) => {
    if (filteredStations) {
      return filteredStations.map((station) =>
        Object.assign(
          {},
          {
            ...station,
            visible: station.visible ? station.status === "available" : false,
          }
        )
      );
    }
  };

  filterForProvider = (filteredStations, provider) => {
    if (filteredStations) {
      return filteredStations.map((station) =>
        Object.assign(
          {},
          {
            ...station,
            visible: station.visible
              ? station.owner.toLowerCase() === provider
              : false,
          }
        )
      );
    }
  };

  onFilterApplied = (
    event,
    isProvider,
    filterAll,
    filterAvailable,
    filterProvider
  ) => {
    let filteredStations = this.getAllStations();

    if (isProvider) {
      filterProvider = event.target.innerText.toLowerCase();
    }

    if (filterAvailable) {
      filteredStations = this.filterForAvailable(filteredStations);
    }

    if (isProvider || filterProvider) {
      filteredStations = this.filterForProvider(
        filteredStations,
        filterProvider
      );
    }
    if (filterAll) {
      filteredStations = this.getAllStations();
      filterAvailable = false;
      filterProvider = undefined;
    }

    this.setState({
      stations: filteredStations,
    });
    this.refreshZoom();
  };

  render() {
    if (this.state.firstLat === 0) {
      return (
        <LoadingScreen
          continueDefault={this.continueDefault}
          permissionRestricted={this.state.permissionRestricted}
        />
      );
    }
    return (
      <>
        <NearestStationsView
          stations={this.state.stations}
          menuOpen={this.state.leftMenuOpen}
          setMapCenter={this.setMapCenter}
          setMapZoom={this.setMapZoom}
        />
        <SingleStationView
          stations={this.state.stations}
          menuOpen={this.state.rightMenuOpen}
          geoLat={this.state.firstLat}
          geoLng={this.state.firstLng}
        />
        <MapWithMarker
          completeVisible={true}
          containerStyle={containerStyle}
          mapScrollingEnded={this.mapScrollingEnded}
          onMapLoad={this.onMapLoad}
          center={{ lat: this.state.userLat, lng: this.state.userLng }}
          handleScrollThrottled={this.handleScrollThrottled}
          onClustererLoad={this.onClustererLoad}
          stations={this.state.stations}
          zoom={this.state.located ? 11 : 6}
          hasClickAction={true}
          passDownComponent={
            <>
            <MapControls
              toggleFilterDropdown={this.toggleFilterDropdown}
              setCenterAndZoom={this.setCenterAndZoom}
              firstLat={this.state.firstLat}
              firstLng={this.state.firstLng}
              zoomInMap={this.zoomInMap}
              zoomOutMap={this.zoomOutMap}
              refreshZoom={this.refreshZoom}
              onFilterApplied={this.onFilterApplied}
              onSearchLoad={this.onSearchLoad}
              onPlaceChanged={this.onPlaceChanged}
            />
            <Marker
            icon={"images/home.png"}
            zIndex={2}
            position={{ lat: this.state.firstLat, lng: this.state.firstLng }}
          ></Marker>
          </>
          }
        />
        {this.state.located && (
          <VisitToast stationStats={this.stationStatsRadius()} />
        )}
      </>
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
      getLocations: getLocations,
      getFlags: getFlags,
      clearSelection: clearSelection,
      openSidebar: openSidebar,
      sortingFinished: sortingFinished,
    },
    dispatch
  );

export default connect(mapStateToProps, mapDispatchToProps)(Map);
