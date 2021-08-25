import axios from "axios";

export const DELETE_STATION = "DELETE_STATION";
export const STATION_SELECTED = "STATION_SELECTED";
export const SELECTION_CLEARED = "SELECTION_CLEARED";
export const FETCH_ADDRESS = "FETCH_ADDRESS";
export const CLEAR_ADDRESS = "CLEAR_ADDRESS";

export const selectStation = (station) => {
  return (dispatch) => {
    dispatch({
      type: STATION_SELECTED,
      payload: station,
    });
  };
};

export const clearSelection = () => {
  return (dispatch) => {
    dispatch({
      type: SELECTION_CLEARED,
    });
  };
};

export const getAddress = (lat, lng) => {
  return async (dispatch) => {
    await axios
      .get(
        `https://api.positionstack.com/v1/reverse?access_key=${process.env.REACT_APP_POSITION_API_KEY}&query=${lat},${lng}`
      )
      .then((response) => {
        dispatch({
          type: FETCH_ADDRESS,
          payload: response.data.data[1].label,
        });
      });
  };
};

export const deleteStation = (station) => {
  return async (dispatch) => {
    await axios
      .delete(
        `${process.env.REACT_APP_LOCATION_API}/api/location/station/${station.locationid}`
      )
      .then(() =>
        axios.delete(
          `${process.env.REACT_APP_FLAG_API}/api/flag/flag/${station.id}`
        )
      )
      .then((response) => {
        dispatch({
          type: DELETE_STATION,
          payload: response,
        });
      });
  };
};

export const clearAddress = () => {
  return (dispatch) => {
    dispatch({
      type: CLEAR_ADDRESS,
      payload: "",
    });
  };
};
