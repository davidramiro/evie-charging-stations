import {
  FETCH_FLAGS_SUCCESS,
  FETCH_FLAGS_FAILED,
  UPDATE_FLAGS_SUCCESS,
  UPDATE_FLAGS_FAILED,
} from "../actions/flag.actions";

const initialstate = {
  flags: [],
};

const reducer = (state = initialstate, action) => {
  switch (action.type) {
    case FETCH_FLAGS_SUCCESS:
      return { ...state, flags: action.payload };
    case FETCH_FLAGS_FAILED:
    case UPDATE_FLAGS_SUCCESS:
    case UPDATE_FLAGS_FAILED:
      return state;
    default:
      return state;
  }
};

export default reducer;
