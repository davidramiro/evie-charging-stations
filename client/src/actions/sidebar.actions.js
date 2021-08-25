export const SIDEBAR_OPENED = "SIDEBAR_OPENED";
export const SIDEBAR_CLOSED = "SIDEBAR_CLOSED";

export const openSidebar = () => {
  return (dispatch) => {
    dispatch({
      type: SIDEBAR_OPENED,
    });
  };
};

export const closeSidebar = () => {
  return (dispatch) => {
    dispatch({
      type: SIDEBAR_CLOSED,
    });
  };
};
