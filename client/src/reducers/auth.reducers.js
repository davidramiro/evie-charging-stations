import { USER_LOGIN, USER_LOGOUT } from "../actions/auth.actions";

const initialState = {
  user: "",
  status: false,
};

const state = (state = initialState, action) => {
  switch (action.type) {
    case USER_LOGIN:
      return {
        ...state,
        status: true,
        user: action.payload,
      };
    case USER_LOGOUT:
      return { ...state, user: null, status: false };
    default:
      return state;
  }
};

export default state;
