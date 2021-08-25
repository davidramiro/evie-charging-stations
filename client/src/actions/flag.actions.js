import axios from "axios";

export const FETCH_FLAGS_SUCCESS = "FETCH_FLAGS_SUCCESS";
export const FETCH_FLAGS_FAILED = "FETCH_FLAGS_FAILED";
export const UPDATE_FLAGS_SUCCESS = "UPDATE_FLAGS_SUCCESS";
export const UPDATE_FLAGS_FAILED = "UPDATE_FLAGS_FAILED";

export const getFlags = () => {
  return async (dispatch) => {
    console.log("flag fetching from", process.env.REACT_APP_FLAG_API);
    const response = await fetch(
      `${process.env.REACT_APP_FLAG_API}/api/flag/flags`
    );
    if (response.ok) {
      const flags = await response.json();
      dispatch({
        type: FETCH_FLAGS_SUCCESS,
        payload: flags.flags,
      });
    } else {
      dispatch({
        type: FETCH_FLAGS_FAILED,
        payload: response.status,
      });
    }
  };
};

export const updateFlags = (station) => {
  return async (dispatch) => {
    return await axios
      .put(`${process.env.REACT_APP_FLAG_API}/api/flag/flag/${station.id}`, {
        id: station.id,
        locationid: station.locationid,
        status: station.status,
        ccs2: station.ccs2,
        type2: station.type2,
        chademo: station.type2,
        tesla: null,
      })
      .then((message) => {
        dispatch({
          type: UPDATE_FLAGS_SUCCESS,
          //payload: message.data,
        });
      });
  };
};
