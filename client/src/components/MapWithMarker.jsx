import { Component } from "react";
import { GoogleMap, LoadScript, MarkerClusterer } from "@react-google-maps/api";
import CustomMarker from "./CustomMarker";

const gMapsLibraries = ["places"];

class MapWithMarker extends Component {
  render() {
    return (
      <LoadScript
        googleMapsApiKey={process.env.REACT_APP_API_KEY}
        libraries={gMapsLibraries}
      >
        <GoogleMap
          options={{
            disableDefaultUI: true,
          }}
          mapContainerStyle={this.props.containerStyle}
          onDragEnd={this.props.mapScrollingEnded}
          onLoad={this.props.onMapLoad}
          center={this.props.center}
          onCenterChanged={this.props.handleScrollThrottled}
          zoom={this.props.zoom ? this.props.zoom : 6}
          ref={(ref) => {
            this.mapRef = ref;
          }}
        >
          <>{this.props.passDownComponent}</>
          <MarkerClusterer
            options={{
              imagePath: "images/cluster/m",
            }}
            onLoad={this.props.onClustererLoad}
            zIndex={1}
            ignoreHidden={true}
          >
            {(clusterer) =>
              this.props.stations.map((station) => (
                <CustomMarker
                  key={station.id}
                  station={station}
                  hasClickAction={this.props.hasClickAction}
                  id={station.id}
                  enableInfoWindow={false}
                  position={{
                    lat: station.latitude,
                    lng: station.longitude,
                  }}
                  visible={
                    station.visible === undefined ? true : station.visible
                  }
                  clusterer={clusterer}
                />
              ))
            }
          </MarkerClusterer>
        </GoogleMap>
      </LoadScript>
    );
  }
}

export default MapWithMarker;
