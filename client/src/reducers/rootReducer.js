import { combineReducers } from "redux";
import locationsReducer from "./locations.reducers";
import flagReducer from "./flags.reducers";
import stationReducer from "./station.reducers";
import sidebarReducer from "./sidebar.reducers";
import authReducer from "./auth.reducers";

export default combineReducers({
  location: locationsReducer,
  flag: flagReducer,
  station: stationReducer,
  sidebar: sidebarReducer,
  auth: authReducer,
});
