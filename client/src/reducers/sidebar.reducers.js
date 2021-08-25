import { SIDEBAR_OPENED, SIDEBAR_CLOSED } from "../actions/sidebar.actions";

const initialstate = {
  isOpen: false,
};

const reducer = (state = initialstate, action) => {
  switch (action.type) {
    case SIDEBAR_OPENED:
      return { ...state, isOpen: true };
    case SIDEBAR_CLOSED:
      return { ...state, isOpen: false };
    default:
      return state;
  }
};

export default reducer;
