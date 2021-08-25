export const FETCH_LOCATIONS_SUCCESS = "FETCH_LOCATIONS_SUCCESS";
export const FETCH_LOCATIONS_FAILED = "FETCH_LOCATIONS_FAILED";
export const SORTING_FINISHED = "SORTING_FINISHED";

export const getLocations = () => {
  return async (dispatch) => {
    console.log("location fetching from", process.env.REACT_APP_LOCATION_API);
    const response = await fetch(
      `${process.env.REACT_APP_LOCATION_API}/api/location/stations`
    );
    if (response.ok) {
      const locations = await response.json();
      dispatch({
        type: FETCH_LOCATIONS_SUCCESS,
        payload: locations.locations,
      });
    } else {
      dispatch({
        type: FETCH_LOCATIONS_FAILED,
        payload: response.status,
      });
    }
  };
};

export const sortingFinished = () => {
  return (dispatch) => {
    dispatch({
      type: SORTING_FINISHED,
    });
  };
};
