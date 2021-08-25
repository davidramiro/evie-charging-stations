import { Component } from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faBars,
  faSearchMinus,
  faSearchPlus,
  faLocationArrow,
} from "@fortawesome/free-solid-svg-icons";
import {
  Button,
  ButtonGroup,
  ButtonDropdown,
  DropdownToggle,
  DropdownMenu,
  DropdownItem,
  InputGroup,
  InputGroupAddon,
  Input,
  ButtonToolbar,
} from "reactstrap";
import { getLocations, sortingFinished } from "../actions/location.actions";
import { getFlags } from "../actions/flag.actions";
import { openSidebar } from "../actions/sidebar.actions";
import { clearSelection } from "../actions/station.actions";

import { bindActionCreators } from "redux";
import { connect } from "react-redux";

import { Autocomplete } from "@react-google-maps/api";
const provider = [
  "NovoFleet",
  "PlugSurfing",
  "RheinEnergie",
  "Naturstrom",
  "Volkswagen We Charge",
  "EnBW",
  "E.ON",
  "TankE",
  "allego",
  "IONITY",
  "EWE",
];

class MapControls extends Component {
  state = {
    filterAllActive: true,
    filterProviderActive: undefined,
    filterAvailableActive: false,
    dropdownOpen: false,
  };

  toggleFilterDropdown = () => {
    this.setState({ dropdownOpen: !this.state.dropdownOpen });
  };

  onFilterApplied = (event, isProvider) => {
    const filterAll = event.target.innerText.toLowerCase() === "all";
    let filterAvailable =
      event.target.innerText.toLowerCase() === "available" ||
      this.state.filterAvailableActive;
    let filterProvider = this.state.filterProviderActive;

    if (isProvider) {
      filterProvider = event.target.innerText.toLowerCase();
    }

    if (filterAll) {
      filterAvailable = false;
      filterProvider = undefined;
    }

    this.props.onFilterApplied(
      event,
      isProvider,
      filterAll,
      filterAvailable,
      filterProvider
    );

    this.setState({
      filterAllActive: filterAll,
      filterAvailableActive: filterAvailable,
      filterProviderActive: filterProvider,
    });
    //this.props.refreshZoom();
  };
  openSidebar = () => {
    this.props.openSidebar();
  };

  render() {
    return (
      <>
        <InputGroup className="hover-search" size="lg">
          <InputGroupAddon addonType="prepend">
            <Button color="primary" onClick={() => this.openSidebar()}>
              <FontAwesomeIcon size="lg" icon={faBars} />
            </Button>
          </InputGroupAddon>
          <Autocomplete
            onLoad={this.props.onSearchLoad}
            onPlaceChanged={this.props.onPlaceChanged}
          >
            <Input
              type="text"
              placeholder="Search for a charging station near you..."
              style={{
                width: `25rem`,
                height: `3.5rem`,
                padding: `0 12px`,
                fontSize: `16px`,
              }}
            />
          </Autocomplete>
        </InputGroup>
        <ButtonGroup className="filter-buttons">
          <Button
            color={this.state.filterAllActive ? "success" : "primary"}
            size="lg"
            onClick={this.onFilterApplied}
          >
            All
          </Button>
          <Button
            color={this.state.filterAvailableActive ? "success" : "primary"}
            size="lg"
            onClick={this.onFilterApplied}
          >
            Available
          </Button>
          <ButtonDropdown
            direction="up"
            size="lg"
            isOpen={this.state.dropdownOpen}
            onClick={this.toggleFilterDropdown}
            toggle={() => null}
          >
            <DropdownToggle
              color={this.state.filterProviderActive ? "success" : "primary"}
              caret
            >
              Provider
            </DropdownToggle>
            <DropdownMenu>
              {provider.map((item) => (
                <DropdownItem
                  key={item}
                  onClick={(event) => this.onFilterApplied(event, true)}
                >
                  {item}
                </DropdownItem>
              ))}
            </DropdownMenu>
          </ButtonDropdown>
        </ButtonGroup>
        <ButtonToolbar className="zoom-buttons">
          <ButtonGroup className="mr-2">
            <Button
              color="primary"
              size="lg"
              onClick={() =>
                this.props.setCenterAndZoom(
                  this.props.firstLat,
                  this.props.firstLng
                )
              }
            >
              <FontAwesomeIcon size="lg" icon={faLocationArrow} />
            </Button>
          </ButtonGroup>
          <ButtonGroup>
            <Button color="primary" size="lg" onClick={this.props.zoomInMap}>
              <FontAwesomeIcon size="lg" icon={faSearchPlus} />
            </Button>
            <Button
              color="primary"
              size="lg"
              onClick={() => this.props.zoomOutMap()}
            >
              <FontAwesomeIcon size="lg" icon={faSearchMinus} />
            </Button>
          </ButtonGroup>
        </ButtonToolbar>
      </>
    );
  }
}

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

export default connect(null, mapDispatchToProps)(MapControls);
