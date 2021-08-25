import {
  STATION_SELECTED,
  SELECTION_CLEARED,
  FETCH_ADDRESS,
  CLEAR_ADDRESS,
  DELETE_STATION,
} from "../actions/station.actions";

const initialstate = {
  selectedStation: {},
  address: "",
};

const reducer = (state = initialstate, action) => {
  switch (action.type) {
    case DELETE_STATION: {
      return { selectedStation: {}, address: "" };
    }
    case STATION_SELECTED:
      return { ...state, selectedStation: action.payload };
    case SELECTION_CLEARED:
      return { ...state, selectedStation: {} };
    case FETCH_ADDRESS:
      return { ...state, address: action.payload };
    case CLEAR_ADDRESS:
      return { ...state, address: "" };
    default:
      return state;
  }
};

export default reducer;
