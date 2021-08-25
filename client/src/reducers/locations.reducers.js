import {
  FETCH_LOCATIONS_SUCCESS,
  FETCH_LOCATIONS_FAILED,
  SORTING_FINISHED,
} from "../actions/location.actions";

const initialstate = {
  locations: [],
  sorting: true,
};

const reducer = (state = initialstate, action) => {
  switch (action.type) {
    case FETCH_LOCATIONS_SUCCESS:
      return { ...state, locations: action.payload };
    case FETCH_LOCATIONS_FAILED:
      return state;
    case SORTING_FINISHED:
      return { ...state, sorting: false };
    default:
      return state;
  }
};

export default reducer;
